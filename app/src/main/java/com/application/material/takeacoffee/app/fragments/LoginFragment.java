package com.application.material.takeacoffee.app.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.*;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import com.application.material.takeacoffee.app.CoffeePlacesActivity;
import com.application.material.takeacoffee.app.LoginActivity;
import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.application.CoffeePlacesApplication;
import com.application.material.takeacoffee.app.facebookServices.FacebookLogin;
import com.application.material.takeacoffee.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.takeacoffee.app.fragments.interfaces.OnLoadViewHandlerInterface;
import com.application.material.takeacoffee.app.models.User;
import com.application.material.takeacoffee.app.services.HttpIntentService;
import com.application.material.takeacoffee.app.sharedPreferences.SharedPreferencesWrapper;
import com.application.material.takeacoffee.app.singletons.BusSingleton;
import com.application.material.takeacoffee.app.singletons.ImagePickerSingleton;

import android.widget.ImageView;

import com.parse.ParseFile;

import org.greenrobot.eventbus.Subscribe;

import java.io.ByteArrayOutputStream;

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
    @Bind(R.id.loginContinueButtonId) View loginContinueButton;
    @Bind(R.id.loginUsernameEditId) View loginUsernameEditText;
    @Bind(R.id.loginMainLayoutId) View loginMainView;
    @Bind(R.id.loaderLayoutId) View loaderView;
    @Bind(R.id.profilePictureViewId)
    ImageView profilePictureView;
    @Bind(R.id.facebookLoginButtonId) View facebookLoginButton;
    @Bind(R.id.userIdDebug) View userIdDebugButton;
    private CoffeePlacesApplication coffeePlacesApplication;
    private FacebookLogin facebookLogin;
    private ImagePickerSingleton imagePicker;

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
        coffeePlacesApplication = ((CoffeePlacesApplication) loginActivityRef.getApplication());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        settingListView = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, settingListView);

        // Fetch Facebook user info if the session is active
        facebookLogin = FacebookLogin.getInstance(loginActivityRef);
//        Session session = ParseFacebookUtils.getSession();
//        if (session != null &&
//                session.isOpened()) {
//            session.close();
//        }


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
        //CHECK if user already logged in
        String loggedUserId;
        if((loggedUserId = SharedPreferencesWrapper.getValue(loginActivityRef,
                LOGGED_USER_ID)) != null) {
            loginMainView.setVisibility(View.GONE);
            ((OnLoadViewHandlerInterface) loginActivityRef).initOnLoadView(loaderView);

            //check if valid user
            HttpIntentService.checkUserRequest(loginActivityRef, loggedUserId);
            return;
        }

        //new
        initView();
    }

    public void initView() {
        loginMainView.setVisibility(View.VISIBLE);
        ((OnLoadViewHandlerInterface) loginActivityRef).hideOnLoadView(loaderView);
        loginContinueButton.setOnClickListener(this);
        facebookLoginButton.setOnClickListener(this);
        profilePictureView.setOnClickListener(this);
        userIdDebugButton.setOnClickListener(this);
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
            case R.id.profilePictureViewId:
                imagePicker = ImagePickerSingleton.getInstance(loginActivityRef);
                imagePicker.onPickPhoto();
                break;
            case R.id.loginContinueButtonId:
                //TODO DEBUG
                if(! ((EditText) userIdDebugButton).getText().toString().equals("")) {
                    SharedPreferencesWrapper.putString(loginActivityRef,
                            LOGGED_USER_ID, ((EditText) userIdDebugButton).getText().toString());
                    loginMainView.setVisibility(View.GONE);
                    ((OnLoadViewHandlerInterface) loginActivityRef).initOnLoadView(loaderView);

                    //check if valid user
                    HttpIntentService.checkUserRequest(loginActivityRef,
                            ((EditText) userIdDebugButton).getText().toString());
                    return;
                }
                //TODO END OF DEBUG

                String username = ((EditText) loginUsernameEditText).getText().toString();
                if(username.compareTo("") == 0) {
                    Toast.makeText(loginActivityRef, "empty username", Toast.LENGTH_SHORT).show();
                    return;
                }

                ParseFile file = null;
                if(profilePictureView.getTag() != null) {
                    String profilePictureUrlLocal = (String) profilePictureView.getTag();
                    file = saveFile(profilePictureUrlLocal);
                }

                User user = new User(
                        null,
                        file != null ? file.getUrl() : null,
                        file != null ? file.getName() : null,
                        username);
                coffeePlacesApplication.setUser(user);
                HttpIntentService.addUserRequest(loginActivityRef, user);
//                HttpIntentService.addUserRequest(loginActivityRef, new User("4nmvMJNk1R", null, username));
                break;
            case R.id.facebookLoginButtonId:
                FacebookLogin facebookLogin = FacebookLogin.getInstance(loginActivityRef);
                facebookLogin.setUserProfilePictureView(profilePictureView);
                facebookLogin.setUsernameTextView((EditText) loginUsernameEditText);
                facebookLogin.onLoginButtonClicked();
                break;
        }
    }

    private ParseFile saveFile(String url) {
        ParseFile file;
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(url);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] image = stream.toByteArray();

            file = new ParseFile("profilePicture.png", image);

            file.save();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Subscribe
    public void onNetworkRespose(User user) {
        Log.d(TAG, "CHECK_USER_RESPONSE");
        ((OnLoadViewHandlerInterface) loginActivityRef).hideOnLoadView(loaderView);

        if(user == null) {
            //TODO handle adapter with empty data
            return;
        }

        //check
        coffeePlacesApplication.setUser(user);

        Intent intent = new Intent(this.getActivity(), CoffeePlacesActivity.class);
        startActivity(intent);

        loginActivityRef.finish();

    }

    @Subscribe
    public void onNetworkRespose(String userId) {
        Log.d(TAG, "ADD_USER_RESPONSE");
        ((OnLoadViewHandlerInterface) loginActivityRef).hideOnLoadView(loaderView);

        if(userId.equals(User.EMPTY_ID)) {
            //TODO handle adapter with empty data
            return;
        }

        coffeePlacesApplication.setUserId(userId); //update user :)

        if(SharedPreferencesWrapper.getValue(loginActivityRef, LOGGED_USER_ID) == null) {
            SharedPreferencesWrapper.putString(loginActivityRef,
                    LOGGED_USER_ID, userId);
        }

        Intent intent = new Intent(this.getActivity(), CoffeePlacesActivity.class);
        startActivity(intent);

        loginActivityRef.finish();
    }

}
