package com.application.material.takeacoffee.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import butterknife.ButterKnife;
import com.application.material.takeacoffee.app.facebookServices.FacebookLogin;
import com.application.material.takeacoffee.app.fragments.LoginFragment;
import com.application.material.takeacoffee.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.takeacoffee.app.fragments.interfaces.OnLoadViewHandlerInterface;
import com.application.material.takeacoffee.app.models.User;
import com.application.material.takeacoffee.app.singletons.BusSingleton;
import com.application.material.takeacoffee.app.singletons.ImagePickerSingleton;
import com.parse.ParseFacebookUtils;
import com.squareup.otto.Subscribe;

import java.io.IOException;


public class LoginActivity extends ActionBarActivity implements
        OnChangeFragmentWrapperInterface, OnLoadViewHandlerInterface {
    private static final String TAG = "LoginActivity";
    private String currentFragTag;
    private String CURRENT_FRAGMENT_TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        getSupportActionBar().hide();

        //INIT VIEW
        if(savedInstanceState != null) {
            //already init app - try retrieve frag from manager
            //String fragTag = savedInstanceState.getString(CURRENT_FRAGMENT_TAG);
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(
                    currentFragTag);
            initView(fragment);
            return;
        }

        currentFragTag = LoginFragment.LOGIN_FRAG_TAG;
        initView(new LoginFragment());
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

    private void initView(Fragment fragment) {
        if(fragment == null) {
            //if sm error init again app
            fragment = new LoginFragment();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.coffeeAppFragmentContainerId, fragment, currentFragTag)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.take_a_coffee, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void initOnLoadView(View view) {
        view.setVisibility(View.VISIBLE);
    }

    @Override
    public void initOnLoadView() {
    }

    @Override
    public void hideOnLoadView(View view) {
        view.setVisibility(View.GONE);
    }

    @Override
    public void hideOnLoadView() {
    }

    @Override
    public void changeFragment(Fragment fragment, Bundle bundle, String tag) {
        if (fragment == null) {
            Log.e(TAG, "cannot change fragment!");
            return;
        }
        setCurrentFragTag(tag);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.coffeeAppFragmentContainerId, fragment, tag)
                .addToBackStack("TAG")
                .commit();
    }


    @Override
    public void startActivityWrapper(Class activityClassName, int requestCode, Bundle bundle) {
        Intent intent = new Intent(this, activityClassName);
        //EditReviewActivity
        if(activityClassName.equals(EditReviewActivity.class)) {
            try {
                intent.putExtras(bundle);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        startActivityForResult(intent, requestCode);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "" + requestCode);
        switch (requestCode) {
            case FacebookLogin.REQUEST_CODE_FB:
                ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
                return;
            case ImagePickerSingleton.PICK_PHOTO_CODE:
                try {
                    super.onActivityResult(requestCode, resultCode, data);
                    ImagePickerSingleton imagePickerSingleton = ImagePickerSingleton
                            .getInstance(this.getApplicationContext());

                    Bitmap picture = imagePickerSingleton.onActivityResultWrapped(requestCode, resultCode, data);
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag(getCurrentFragTag());

                    ((ImageView) fragment.getView().findViewById(R.id.profilePictureViewId))
                            .setImageBitmap(picture);
                    (fragment.getView().findViewById(R.id.profilePictureViewId))
                            .setTag(imagePickerSingleton.getPictureUrl());
                    picture = null;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
        }

        super.onActivityResult(requestCode, resultCode, data);

//        switch (resultCode) {
//            case RESULT_OK:
//                Bundle bundle;
//                ReviewListAdapter adapter;
//                try {
//                    Fragment fragment = getSupportFragmentManager().findFragmentByTag(
//                            currentFragTag);
//                    adapter = getAdapterByFragment(fragment);
//                    //get data
//                    bundle = data.getExtras();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return;
//                }
//
//                switch (requestCode) {
//                    case LoginActivity.ACTION_EDIT_REVIEW:
//                break;
//            case RESULT_CANCELED:
//                break;
//        }
    }

    @Override
    public void setCurrentFragTag(String tag) {
        currentFragTag = tag;
    }
    @Override
    public String getCurrentFragTag() {
        return currentFragTag;
    }

    @Override
    public void pushCurrentFragTag(String tag) {
    }

    @Override
    public String popCurrentFragTag() {
        return null;
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(CURRENT_FRAGMENT_TAG, getCurrentFragTag());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        setCurrentFragTag(savedInstanceState.getString(CURRENT_FRAGMENT_TAG));
    }

    @Subscribe
    public void onHandlingError(Throwable cause) {
        String message = cause.getMessage();
        int code = Integer.parseInt(cause.getCause().getMessage());

        Log.e(TAG, "error - " + message + code);
        switch (code) {
            case 500:
                Toast.makeText(this.getApplicationContext(),
                        getResources().getString(R.string.HTTP_generic_error),
                        Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(this.getApplicationContext(),
                        getResources().getString(R.string.generic_error),
                        Toast.LENGTH_LONG).show();
                break;
        }
        hideOnLoadView();
        finish();
    }



}