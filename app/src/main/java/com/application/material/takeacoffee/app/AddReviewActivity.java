package com.application.material.takeacoffee.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import com.application.material.takeacoffee.app.fragments.AddReviewFragment;
import com.application.material.takeacoffee.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.takeacoffee.app.fragments.interfaces.OnLoadViewHandlerInterface;
import com.application.material.takeacoffee.app.fragments.interfaces.SetActionBarInterface;
import com.application.material.takeacoffee.app.singletons.BusSingleton;
import com.application.material.takeacoffee.app.singletons.ImagePickerSingleton;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;


public class AddReviewActivity extends AppCompatActivity implements
        OnLoadViewHandlerInterface, SetActionBarInterface, OnChangeFragmentWrapperInterface {
    private static final String TAG = "CoffeePlacesActivity";
    public static final String CURRENT_FRAGMENT_TAG = "CURRENT_FRAGMENT_TAG";
    private static String currentFragTag = null;

    @Bind(R.id.addReviewToolbarId)
    public Toolbar toolbar;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);
        ButterKnife.bind(this);

        initActionbar();
        initView(savedInstanceState);

//        String coffeeMachineId = getIntent().getStringExtra(CoffeePlacesActivity.EXTRA_DATA);
//        bundle = new Bundle();
//        bundle.putString(CoffeePlace.COFFEE_MACHINE_ID_KEY, coffeeMachineId);

        //custom actionBar
//        getSupportActionBar().setCustomView(R.layout.action_bar_custom_template);
//        getSupportActionBar().setDisplayShowCustomEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        setActionBarCustomViewById(-1, "Add new review");
        //INIT VIEW
//        dataApplication = (CoffeePlacesApplication) this.getApplication();

//        setCurrentFragTag(AddReviewActivity.ADD_REVIEW_FRAG_TAG);
//        initView(new AddReviewFragment(), null);
    }

    /**
     * init actionBar
     */
    private void initActionbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setDisplayShowCustomEnabled(true);
//        getSupportActionBar().setCustomView(R.layout.actionbar_add_review_layout);
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
     *
     * @param savedInstanceState
     */
    private void initView(Bundle savedInstanceState) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.addReviewFragmentContainerId,
                        new AddReviewFragment(), getCurrentFragTag())
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
//        getMenuInflater().inflate(R.menu.take_a_coffee, menu);
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
    }

    @Override
    public void hideOnLoadView(View view) {
    }

    @Override
    public void hideOnLoadView() {
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

//            dataApplication.setReviewPictureTemp(picture);

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