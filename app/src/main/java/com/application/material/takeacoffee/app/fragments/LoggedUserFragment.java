package com.application.material.takeacoffee.app.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.*;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.application.material.takeacoffee.app.CoffeeMachineActivity;
import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.application.DataApplication;
import com.application.material.takeacoffee.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.takeacoffee.app.fragments.interfaces.OnLoadViewHandlerInterface;
import com.application.material.takeacoffee.app.fragments.interfaces.SetActionBarInterface;
import com.application.material.takeacoffee.app.models.User;

/**
 * Created by davide on 27/12/14.
 */
public class LoggedUserFragment extends Fragment
        implements View.OnClickListener {

    public static String LOGGED_USER_FRAG_TAG = "LOGGED_USER_FRAG_TAG";

    private static final String TAG = "LoggedUserFragment";
    private static FragmentActivity mainActivityRef = null;

    private View settingListView;
    private String coffeeMachineId;
    private Bundle bundle;
    @InjectView(R.id.loginUsernameEditId)
    EditText loginUsernameEdit;
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
        mainActivityRef =  (CoffeeMachineActivity) activity;
        dataApplication = ((DataApplication) mainActivityRef.getApplication());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        settingListView = inflater.inflate(R.layout.logged_user_fragment, container, false);
        ButterKnife.inject(this, settingListView);
        setHasOptionsMenu(true);
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
        initView();
    }

    public void initView() {
        ((OnLoadViewHandlerInterface) mainActivityRef).hideOnLoadView();

        //action bar
        ((SetActionBarInterface) mainActivityRef)
                .setActionBarCustomViewById(R.id.customActionSettingsLayoutId, null);
        ((SetActionBarInterface) mainActivityRef)
                .setCustomNavigation(LoggedUserFragment.class);

        loginUsernameEdit.setText(dataApplication.getUsername());
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.logged_user, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_logged_user:
//                save user
                hideKeyboard();
                boolean isUserSaved = saveUser();
                if(! isUserSaved) {
                    Log.e(TAG, "error on save user");
                    break;
                }
                mainActivityRef.onBackPressed();
                break;
        }
        return true;
    }

    private void hideKeyboard() {

    }

    private boolean saveUser() {
        String username = loginUsernameEdit.getText().toString();
        if(username.compareTo("") == 0) {
            return false;
        }

        //HTTPIntent service call
        dataApplication.setUsername(username);
        return true;
    }

    @Override
    public void onClick(View v) {

    }
}
