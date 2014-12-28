package com.application.material.takeacoffee.app.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.*;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.application.material.takeacoffee.app.CoffeeMachineActivity;
import com.application.material.takeacoffee.app.LoginActivity;
import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.application.DataApplication;
import com.application.material.takeacoffee.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.takeacoffee.app.fragments.interfaces.OnLoadViewHandlerInterface;
import com.application.material.takeacoffee.app.models.User;
import com.application.material.takeacoffee.app.services.HttpIntentService;
import com.application.material.takeacoffee.app.sharedPreferences.SharedPreferencesWrapper;
import com.application.material.takeacoffee.app.singletons.BusSingleton;
import com.squareup.otto.Subscribe;

import static com.application.material.takeacoffee.app.sharedPreferences.SharedPreferencesWrapper.LOGGED_USER_ID;

/**
 * Created by davide on 25/12/14.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "LoginFragment";
    private static FragmentActivity loginActivityRef = null;
    public static final String LOGIN_FRAG_TAG = "LOGIN_FRAG_TAG";

    private View settingListView;
    private String coffeeMachineId;
    private Bundle bundle;
    @InjectView(R.id.loginContinueButtonId) View loginContinueButton;
    @InjectView(R.id.loginUsernameEditId) View loginUsernameEditText;
    private DataApplication dataApplication;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof OnLoadViewHandlerInterface)) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLoadViewHandlerInterface");
        }
        if (!(activity instanceof OnChangeFragmentWrapperInterface)) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLoadViewHandlerInterface");
        }
        loginActivityRef =  (LoginActivity) activity;
        dataApplication = ((DataApplication) loginActivityRef.getApplication());

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        settingListView = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.inject(this, settingListView);

        initOnLoadView();
        return settingListView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        //get all bundle
        bundle = getArguments();
    }

    private void initOnLoadView() {
        ((OnLoadViewHandlerInterface) loginActivityRef).initOnLoadView(null);

        //CHECK if user already logged in
        String loggedUserId;
        if((loggedUserId = SharedPreferencesWrapper.getValue(loginActivityRef,
                LOGGED_USER_ID)) != null) {
            //check if valid user
            HttpIntentService.checkUserRequest(loginActivityRef, loggedUserId);
            return;
        }

        initView();
    }

    public void initView() {
        ((OnLoadViewHandlerInterface) loginActivityRef).hideOnLoadView(null);
        loginContinueButton.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        BusSingleton.getInstance().register(this);
        super.onResume();
    }

    @Override
    public void onStop() {
        BusSingleton.getInstance().unregister(this);
        super.onStop();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.review_list_no_edit, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        }
        return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginContinueButtonId:
                String username = ((EditText) loginUsernameEditText).getText().toString();
                if(username.compareTo("") == 0) {
                    Toast.makeText(loginActivityRef, "empty username", Toast.LENGTH_SHORT).show();
                    return;
                }

                HttpIntentService.addUserRequest(loginActivityRef, new User("4nmvMJNk1R", null, username));
                break;
        }
    }

    @Subscribe
    public void onNetworkRespose(User user) {
        Log.d(TAG, "ADD_USER_RESPONSE");
        ((OnLoadViewHandlerInterface) loginActivityRef).hideOnLoadView(null);

        if(user == null) {
            //TODO handle adapter with empty data
            return;
        }

        dataApplication.setUser(user);

        if(SharedPreferencesWrapper.getValue(loginActivityRef, LOGGED_USER_ID) == null) {
            SharedPreferencesWrapper.putString(loginActivityRef,
                    LOGGED_USER_ID, user.getId());
        }

        Intent intent = new Intent(this.getActivity(), CoffeeMachineActivity.class);
        startActivity(intent);

        loginActivityRef.finish();

    }

}
