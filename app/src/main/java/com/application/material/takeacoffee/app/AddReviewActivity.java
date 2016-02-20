package com.application.material.takeacoffee.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.LruCache;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import com.application.material.takeacoffee.app.application.DataApplication;
import com.application.material.takeacoffee.app.fragments.AddReviewFragment;
import com.application.material.takeacoffee.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.takeacoffee.app.fragments.interfaces.OnLoadViewHandlerInterface;
import com.application.material.takeacoffee.app.fragments.interfaces.SetActionBarInterface;
import com.application.material.takeacoffee.app.models.CoffeeMachine;
import com.application.material.takeacoffee.app.singletons.BusSingleton;
import com.application.material.takeacoffee.app.singletons.ImagePickerSingleton;
import com.squareup.otto.Subscribe;

import java.io.IOException;


public class AddReviewActivity extends AppCompatActivity implements
        OnLoadViewHandlerInterface, SetActionBarInterface, OnChangeFragmentWrapperInterface {
    private static final String TAG = "CoffeeMachineActivity";
    private static String ADD_REVIEW_FRAG_TAG = "ADD_REVIEW_FRAG_TAG";
    @Bind(R.id.onLoadLayoutId) View onLoadLayout;
    //Volley lib
    private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(20);
    public static final String CURRENT_FRAGMENT_TAG = "CURRENT_FRAGMENT_TAG";
    private static String currentFragTag = null;
    private Bundle bundle;
    private DataApplication dataApplication;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);
        ButterKnife.bind(this);

        String coffeeMachineId = getIntent().getStringExtra(CoffeeMachineActivity.EXTRA_DATA);
        bundle = new Bundle();
        bundle.putString(CoffeeMachine.COFFEE_MACHINE_ID_KEY, coffeeMachineId);
//        User user = bundle.getParcelable(User.USER_OBJ_KEY);

        //custom actionBar
        getSupportActionBar().setCustomView(R.layout.action_bar_custom_template);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setActionBarCustomViewById(-1, "Add new review");
        //INIT VIEW
        dataApplication = (DataApplication) this.getApplication();

        if(savedInstanceState != null) {
            //already init app - try retrieve frag from manager
            Fragment fragment = getSupportFragmentManager()
                    .findFragmentByTag(getCurrentFragTag());
            initView(fragment, savedInstanceState);
            return;
        }

        setCurrentFragTag(AddReviewActivity.ADD_REVIEW_FRAG_TAG);
        initView(new AddReviewFragment(), null);
    }

    @Override
    public void onResume(){
        BusSingleton.getInstance().register(this);
        super.onResume();
    }

    @Override
    public void onPause(){
        BusSingleton.getInstance().unregister(this);
        super.onPause();
    }

    private void initView(Fragment fragment, Bundle savedInstanceState) {
        if(savedInstanceState == null) {
            fragment.setArguments(bundle);
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.addReviewFragmentContainerId,
                        fragment, getCurrentFragTag())
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
        getMenuInflater().inflate(R.menu.take_a_coffee, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                Log.d(TAG, "hey home button");
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void setCurrentFragTag(String tag) {
        currentFragTag = tag;
    }
    @Override
    public String getCurrentFragTag() {
        return currentFragTag;
    }

    @Override
    public void startActivityWrapper(Class activityClassName, int requestCode, Bundle bundle) {

    }

    @Override
    public void changeFragment(Fragment fragment, Bundle bundle, String tag) {

    }

    @Override
    public void pushCurrentFragTag(String tag) {
    }

    @Override
    public String popCurrentFragTag() {
        return null;
    }



    @Override
    public void initOnLoadView(View view) {
    }

    @Override
    public void initOnLoadView() {
        onLoadLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideOnLoadView(View view) {
    }

    @Override
    public void hideOnLoadView() {
        onLoadLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Fragment fragment = getSupportFragmentManager()
                    .findFragmentByTag(getCurrentFragTag());

            ImagePickerSingleton imagePicker = ImagePickerSingleton
                    .getInstance(this.getApplicationContext());
            Bitmap picture = imagePicker.onActivityResultWrapped(requestCode, resultCode, data);
            String pictureUrl = imagePicker.getPictureUrl();

            ((ImageView) fragment.getView().findViewById(R.id.imagePreviewViewId))
                    .setImageBitmap(picture);
            (fragment.getView().findViewById(R.id.imagePreviewViewId))
                    .setTag(pictureUrl);

            dataApplication.setReviewPictureTemp(picture);

            picture = null;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(CURRENT_FRAGMENT_TAG, getCurrentFragTag());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        setCurrentFragTag(savedInstanceState.getString(CURRENT_FRAGMENT_TAG));
    }

    @Override
    public void setActionBarCustomViewById(int id, Object data) {
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar == null) {
            return;
        }

        View currentView = actionBar.getCustomView();

        currentView.findViewById(R.id.customActionBarReviewListLayoutId).setVisibility(View.GONE);
        currentView.findViewById(R.id.cActBarTitleId).setVisibility(View.VISIBLE);
        ((TextView) currentView.findViewById(R.id.cActBarTitleId)).setText((String) data);
    }

    @Override
    public void setCustomNavigation(Class<?> id) {
        //TODO not implemented
    }

    @Override
    public boolean isItemSelected() {
        //TODO not implemented
        return false;
    }

    @Override
    public int getSelectedItemPosition() {
        //TODO not implemented
        return 0;
    }

    @Override
    public void setSelectedItemView(View selectedItemView) {
        //TODO not implemented
    }

    @Override
    public void updateSelectedItem(AdapterView.OnItemLongClickListener listener, ListView listView, View view, int itemPos) {

    }

    @Subscribe
    public void onHandlingError(Throwable cause) {
        String message = cause.getMessage();
        int code = Integer.parseInt(cause.getCause().getMessage());

        Log.e(TAG, "error - " + message + code);
        switch (code) {
            case 500:
                Toast.makeText(this.getApplicationContext(),
                        getResources().getString(R.string.HTTP_generic_error),
                        Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(this.getApplicationContext(),
                        getResources().getString(R.string.generic_error),
                        Toast.LENGTH_LONG).show();
                break;
        }
        hideOnLoadView();
        finish();
    }

}