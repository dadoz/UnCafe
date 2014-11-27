package com.application.material.takeacoffee.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.LruCache;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.application.material.takeacoffee.app.fragments.EditReviewFragment;
import com.application.material.takeacoffee.app.fragments.interfaces.OnLoadViewHandlerInterface;
import com.application.material.takeacoffee.app.models.User;


public class EditReviewActivity extends ActionBarActivity implements
        OnLoadViewHandlerInterface, ImageLoader.ImageCache,
        VolleyImageRequestWrapper {
    private static final String TAG = "CoffeeMachineActivity";
    private static String EDIT_REVIEW_FRAG_TAG = "EDIT_REVIEW_FRAG_TAG";
    @InjectView(R.id.onLoadLayoutId) View onLoadLayout;
    //Volley lib
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(20);
    public static final String CURRENT_FRAGMENT_TAG = "CURRENT_FRAGMENT_TAG";
    private static String currentFragTag = null;
    private Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_review);
        ButterKnife.inject(this);

        bundle = getIntent().getExtras();
//        User user = bundle.getParcelable(User.USER_OBJ_KEY);
        //TODO ALWAYS recreating this stuff - check it out
        //ACTION BAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            setSupportActionBar(toolbar);

            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setCustomView(R.layout.custom_action_bar_template);
        }
        this.setActionBarCustomViewById(R.id.customActionBarUserLayoutId);

        //VOLLEY stuff
        requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        imageLoader = new ImageLoader(requestQueue, this);

        //INIT VIEW
        if(savedInstanceState != null) {
            //already init app - try retrieve frag from manager
            Fragment fragment = getSupportFragmentManager()
                    .findFragmentByTag(currentFragTag);
            initView(fragment);
            return;
        }

        currentFragTag = EditReviewActivity.EDIT_REVIEW_FRAG_TAG;
        initView(new EditReviewFragment());
    }

    private void initView(Fragment fragment) {
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.editReviewFragmentContainerId, fragment, currentFragTag)
                .commit();
    }

    public void setActionBarCustomViewById(int id) {
        //        invalidateOptionsMenu();
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        ((TextView) actionBar.getCustomView().findViewById(R.id.cActBarTitleId))
                .setText("Edit mode");

        //current view
        View currentView = actionBar.getCustomView().findViewById(id);

        currentView.setVisibility(View.VISIBLE);
        ((TextView) currentView.findViewById(R.id.usernameTextId))
                .setText("fake david");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.take_a_coffee, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                Log.d(TAG, "hey home button");
                getSupportFragmentManager().popBackStack();
                return true;
//            case R.id.action_settings:
//                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void initOnLoadView() {
        onLoadLayout.setVisibility(View.VISIBLE);
    }

    public void hideOnLoadView() {
        onLoadLayout.setVisibility(View.GONE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void volleyImageRequest(String profilePicturePath, ImageView profilePicImageView, int defaultIconId) {
        this.imageLoader.get(profilePicturePath, ImageLoader
                .getImageListener(profilePicImageView, defaultIconId, defaultIconId));
    }

    @Override
    public Bitmap getBitmap(String s) {
        return cache.get(s);
    }

    @Override
    public void putBitmap(String s, Bitmap bitmap) {
        cache.put(s, bitmap);
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


}