package com.application.material.takeacoffee.app;

import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
import com.application.material.takeacoffee.app.fragments.CoffeeMachineFragment;
import com.application.material.takeacoffee.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.takeacoffee.app.fragments.interfaces.OnLoadViewHandlerInterface;
import com.application.material.takeacoffee.app.fragments.interfaces.SetActionBarInterface;
import com.application.material.takeacoffee.app.models.CoffeeMachine;


public class CoffeeMachineActivity extends ActionBarActivity implements
        OnLoadViewHandlerInterface, OnChangeFragmentWrapperInterface,
        ImageLoader.ImageCache, VolleyImageRequestWrapper, SetActionBarInterface {
    private static final String TAG = "CoffeeMachineActivity";
    @InjectView(R.id.onLoadLayoutId)
    View onLoadLayout;
    //Volley lib
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(20);
    public static final String CURRENT_FRAGMENT_TAG = "CURRENT_FRAGMENT_TAG";
    private static String currentFragTag = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coffee_machine);
        ButterKnife.inject(this);

        //TODO ALWAYS recreating this stuff - checkit out
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

        //VOLLEY stuff
        requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        imageLoader = new ImageLoader(requestQueue, this);

        //INIT VIEW
        if(savedInstanceState != null) {
            //already init app - try retrieve frag from manager
            //String fragTag = savedInstanceState.getString(CURRENT_FRAGMENT_TAG);
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(
                    this.currentFragTag);
            initView(fragment);
            return;
        }

        this.currentFragTag = CoffeeMachineFragment.COFFEE_MACHINE_FRAG_TAG;
        initView(new CoffeeMachineFragment());
    }

    private void initView(Fragment fragment) {
        if(fragment == null) {
            //if sm error init again app
            fragment = new CoffeeMachineFragment();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.coffeeAppFragmentContainerId, fragment, currentFragTag)
                .commit();
    }

//    public static void launch(MainActivity activity, View transitionView, String url) {
//        ActivityOptionsCompat options =
//                ActivityOptionsCompat.makeSceneTransitionAnimation(
//                        activity, transitionView, EXTRA_IMAGE);
//        Intent intent = new Intent(activity, DetailActivity.class);
//        intent.putExtra(EXTRA_IMAGE, url);
//        ActivityCompat.startActivity(activity, intent, options.toBundle());
//    }

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
    public void changeFragment(Fragment fragment, Bundle bundle, String tag) {
        if (fragment == null) {
            Log.e(TAG, "cannot change fragment!");
            return;
        }
//        getResources().putString(CURRENT_FRAGMENT_TAG, CoffeeMachineFragment.COFFEE_MACHINE_FRAG_TAG);
        this.setFragTag(tag);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.coffeeAppFragmentContainerId, fragment, tag)
                .addToBackStack("TAG")
                .commit();
    }

    @Override
    public void setFragTag(String tag) {
        currentFragTag = tag;
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
    public void setActionBarCustomViewById(int id, Object data) {
//        invalidateOptionsMenu();
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        //HIDE all template
        actionBar.getCustomView().findViewById(R.id.customActionBarUserLayoutId).setVisibility(View.GONE);
        actionBar.getCustomView().findViewById(R.id.customActionBarCoffeeMachineLayoutId).setVisibility(View.GONE);
        actionBar.getCustomView().findViewById(R.id.customActionBarReviewListLayoutId).setVisibility(View.GONE);

        //current view
        View currentView = actionBar.getCustomView().findViewById(id);
        currentView.setVisibility(View.VISIBLE);
        switch (id) {
            case R.id.customActionBarCoffeeMachineLayoutId:
                ((TextView) actionBar.getCustomView().findViewById(R.id.cActBarTitleId))
                .setText(((CoffeeMachine) data).getName());
                ((TextView) actionBar.getCustomView().findViewById(R.id.cActBarCoffeeMachineLocationId))
                        .setText(((CoffeeMachine) data).getAddress());
                break;
            case R.id.customActionBarUserLayoutId:
                ((TextView) actionBar.getCustomView().findViewById(R.id.cActBarTitleId))
                        .setText(getResources().getText(R.string.titleTakeACoffee));
                break;
            case R.id.customActionBarReviewListLayoutId:
                ((TextView) actionBar.getCustomView().findViewById(R.id.cActBarTitleId))
                        .setText("01.01.15 - 30.01.15");
                break;

        }
    }

    @Override
    public void setCustomNavigation(Class<?> id) {
        ActionBar actionBar = this.getSupportActionBar();
        if(actionBar == null) {
            return;
        }

        if(id.equals(CoffeeMachineFragment.class)) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            return;
        }
        actionBar.setDisplayHomeAsUpEnabled(true);
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