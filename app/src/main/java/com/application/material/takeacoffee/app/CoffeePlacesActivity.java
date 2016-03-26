package com.application.material.takeacoffee.app;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import com.application.material.takeacoffee.app.fragments.PlacesFragment;
import com.application.material.takeacoffee.app.utils.PermissionManager;

public class CoffeePlacesActivity extends AppCompatActivity {
    public static final int RESULT_FAILED = -1;
    public static String EXTRA_DATA = "EXTRA_DATA";
    public static String ACTION_EDIT_REVIEW_RESULT = "EDIT_RESULT";
    public static final String ERROR_MESSAGE_KEY = "EMK";
    private PermissionManager permissionManager;

    @Bind(R.id.coffeeToolbarId)
    public Toolbar toolbar;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coffee_machine);
        ButterKnife.bind(this);

        permissionManager = PermissionManager.getInstance();
        initView();
    }

    /**
     * init action bar - set only toolbar
     */
    private void initActionBar() {
        setSupportActionBar(toolbar);
    }

    /**
     * pre init view
     */
    private void initView() {
        initActionBar();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.coffeeAppFragmentContainerId, new PlacesFragment(),
                        PlacesFragment.COFFEE_MACHINE_FRAG_TAG)
                .commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PermissionManager.ACTION_LOCATION_SOURCE_SETTINGS) {
//            if (resultCode == RESULT_OK) {
                permissionManager.onEnablePositionResult();
//            }
        }
    }
}