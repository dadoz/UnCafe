package com.application.material.takeacoffee.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.application.material.takeacoffee.app.fragments.ReviewListFragment;
import com.firebase.client.Firebase;

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