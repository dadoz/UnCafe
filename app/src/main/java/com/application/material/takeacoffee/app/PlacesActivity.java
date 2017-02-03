package com.application.material.takeacoffee.app;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import icepick.Icepick;
import icepick.State;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import com.application.material.takeacoffee.app.fragments.PlacesFragment;
import com.application.material.takeacoffee.app.utils.PermissionManager;
import com.flurry.android.FlurryAgent;

public class PlacesActivity extends AppCompatActivity {
    private PermissionManager permissionManager;
    @State
    public String currentFragmentTag = PlacesFragment.COFFEE_MACHINE_FRAG_TAG;

    @BindView(R.id.toolbarId)
    public Toolbar toolbar;
    private Unbinder unbinder;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        setContentView(R.layout.activity_coffee_places);
        unbinder = ButterKnife.bind(this);
        FlurryAgent.onStartSession(this);

        permissionManager = PermissionManager.getInstance();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
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
        injectFrag();
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
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PermissionManager.ACTION_LOCATION_SOURCE_SETTINGS) {
            //TODO handle ok or no from dialog
            if (resultCode == RESULT_OK) {
                permissionManager.onEnablePositionResult();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }


    @Override
    public void onBackPressed() {
        this.currentFragmentTag = PlacesFragment.COFFEE_MACHINE_FRAG_TAG;
        Fragment frag;
        if ((frag = getSupportFragmentManager().findFragmentByTag(currentFragmentTag)) != null &&
                ((OnHandleFilterBackPressedInterface) frag).handleFilterBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    /**
     *
     * @param val
     */
    public void setCurrentFragmentTag(String val) {
        this.currentFragmentTag = val;
    }

    /**
     *
     */
    private void injectFrag() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.coffeeAppFragmentContainerId, getSuitableFragment(), currentFragmentTag)
                .commit();
    }

    /**
     *
     * @return
     */
    public Fragment getSuitableFragment() {
        Fragment frag;
        return (frag = getSupportFragmentManager().findFragmentByTag(this.currentFragmentTag)) == null ?
                new PlacesFragment() : frag;
    }

    /**
     *
     */
    public interface OnHandleFilterBackPressedInterface {
        boolean handleFilterBackPressed();
    }
}