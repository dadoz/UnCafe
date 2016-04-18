package com.application.material.takeacoffee.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.application.material.takeacoffee.app.fragments.PickPositionFragment;
import com.application.material.takeacoffee.app.fragments.ReviewListFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PickPositionActivity extends AppCompatActivity {
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

        initActionBar();
        initView();
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

    /**
     * init action bar
     */
    private void initActionBar() {
        setSupportActionBar(toolbar);
    }

    /**
     *
     */
    private void initView() {
        try {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.coffeeAppFragmentContainerId,
                            new PickPositionFragment(), "hey")
                    .commit();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }
}