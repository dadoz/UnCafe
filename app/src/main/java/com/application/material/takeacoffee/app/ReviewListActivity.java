package com.application.material.takeacoffee.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.application.material.takeacoffee.app.fragments.ReviewListFragment;
import com.application.material.takeacoffee.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.takeacoffee.app.fragments.interfaces.OnLoadViewHandlerInterface;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ReviewListActivity extends AppCompatActivity implements
        OnLoadViewHandlerInterface,
        OnChangeFragmentWrapperInterface {

    public static int ACTION_ADD_REVIEW = 0;
    public static int ACTION_EDIT_REVIEW = 0;

    @Bind(R.id.reviewsToolbarId)
    public android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);
        ButterKnife.bind(this);

        initActionBar();
        initView();
    }

    /**
     * init action bar
     */
    private void initActionBar() {
        setSupportActionBar(toolbar);
//        toolbar.setTitle(getResources().getString(R.string.review_actionbar_title));
    }
    /**
     *
     */
    private void initView() {
        try {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.coffeeAppFragmentContainerId,
                            new ReviewListFragment(), "hey")
                    .commit();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void changeFragment(Fragment fragment, Bundle bundle, String tag) {

    }

    @Override
    public void pushCurrentFragTag(String tag) {

    }

    @Override
    public void setCurrentFragTag(String tag) {

    }

    @Override
    public String popCurrentFragTag() {
        return null;
    }

    @Override
    public String getCurrentFragTag() {
        return null;
    }

    @Override
    public void startActivityWrapper(Class activityClassName, int requestCode, Bundle bundle) {

    }

    @Override
    public void initOnLoadView(View view) {

    }

    @Override
    public void initOnLoadView() {

    }

    @Override
    public void hideOnLoadView(View view) {

    }

    @Override
    public void hideOnLoadView() {

    }

}