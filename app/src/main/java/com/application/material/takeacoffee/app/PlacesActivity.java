package com.application.material.takeacoffee.app;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import com.application.material.takeacoffee.app.fragments.PlacesFragment;
import com.application.material.takeacoffee.app.utils.PermissionManager;

public class PlacesActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_coffee_places);
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
//            if (resultCode == RESULT_OK) {
                permissionManager.onEnablePositionResult();
//            }
        }
    }

    /**
    * Don't forget to call setResult(Activity.RESULT_OK) in the returning
    * activity or else this method won't be called!
    */
    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        ActivityCompat.postponeEnterTransition(this);

        // TODO: Call the "scheduleStartPostponedTransition()" method
        // above when you know for certain that the shared element is
        // ready for the transition to begin.
    }
}