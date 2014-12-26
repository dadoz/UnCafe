package com.application.material.takeacoffee.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.application.material.takeacoffee.app.fragments.CoffeeMachineFragment;
import com.application.material.takeacoffee.app.fragments.LoginFragment;
import com.application.material.takeacoffee.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.takeacoffee.app.fragments.interfaces.OnLoadViewHandlerInterface;
import com.squareup.otto.Subscribe;


public class LoginActivity extends ActionBarActivity implements
        OnChangeFragmentWrapperInterface, OnLoadViewHandlerInterface{
    private static final String TAG = "LoginActivity";
    private String currentFragTag;
    private String CURRENT_FRAGMENT_TAG;
    @InjectView(R.id.onLoadLayoutId)
    View onLoadLayout;


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
    public void initOnLoadView() {
        onLoadLayout.setVisibility(View.VISIBLE);
    }

    public void hideOnLoadView() {
        onLoadLayout.setVisibility(View.GONE);
    }

    @Override
    public void changeFragment(Fragment fragment, Bundle bundle, String tag) {
        if (fragment == null) {
            Log.e(TAG, "cannot change fragment!");
            return;
        }
//        getResources().putString(CURRENT_FRAGMENT_TAG, CoffeeMachineFragment.COFFEE_MACHINE_FRAG_TAG);
        this.setFragTag(tag);
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
    public void setFragTag(String tag) {
        currentFragTag = tag;
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(CURRENT_FRAGMENT_TAG, currentFragTag);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentFragTag = savedInstanceState.getString(CURRENT_FRAGMENT_TAG);
    }

    @Subscribe
    public void onHandlingError(Throwable cause) {
        String message = cause.getMessage();
        int code = Integer.parseInt(cause.getCause().getMessage());

        Log.e(TAG, "error - " + message + code);
    }


}