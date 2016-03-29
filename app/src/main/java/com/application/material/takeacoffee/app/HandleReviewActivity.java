package com.application.material.takeacoffee.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import com.application.material.takeacoffee.app.fragments.AddReviewFragment;
import com.application.material.takeacoffee.app.fragments.EditViewReviewFragment;


public class HandleReviewActivity extends AppCompatActivity {
    private static final String TYPE = "EDIT_VIEW"; //TODO change this
    @Bind(R.id.addReviewToolbarId)
    public Toolbar toolbar;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_review);
        ButterKnife.bind(this);

        initActionbar();
        initView();
    }

    /**
     * init actionBar
     */
    private void initActionbar() {
        setSupportActionBar(toolbar);
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    /**
     * init view
     */
    private void initView() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.addReviewFragmentContainerId,
                        getFragmentByType(TYPE), TYPE + "_TAG")
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

    /**
     *
     * @param type
     * @return
     */
    public Fragment getFragmentByType(String type) {
        switch (type) {
            case "ADD":
                return new AddReviewFragment();
            case "EDIT_VIEW":
                return new EditViewReviewFragment();
        }
        return null;
    }
}