package com.application.material.takeacoffee.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.application.material.takeacoffee.app.utils.SharedPrefManager;

import java.lang.ref.WeakReference;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SplashScreenActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen_layout);

        initView();
    }

    /**
     *
     */
    private void initView() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startPlaceActivity();
            }
        }, 1500);
    }

    /**
     *
     * @return
     */
    public boolean isLocationSet() {
        return SharedPrefManager.getInstance(new WeakReference<>(getApplicationContext()))
                .getValueByKey(SharedPrefManager.LATLNG_SHAREDPREF_KEY) != null;
    }

    /**
     *
     */
    private void startPlaceActivity() {
        startActivity(new Intent(getApplicationContext(), PlacesActivity.class));
        finish();
    }

}