package com.application.material.takeacoffee.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.application.material.takeacoffee.app.fragments.ReviewListFragment;
import com.application.material.takeacoffee.app.models.CoffeePlace;
import com.application.material.takeacoffee.app.models.Review;
import com.application.material.takeacoffee.app.singletons.FirebaseManager;
import com.firebase.client.Firebase;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ReviewListActivity extends AppCompatActivity {
    public static int ACTION_EDIT_REVIEW = 0;

    @Bind(R.id.reviewsToolbarId)
    public Toolbar toolbar;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);
        ButterKnife.bind(this);
        Firebase.setAndroidContext(this);

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
                            new ReviewListFragment(), "hey")
                    .commit();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }
}