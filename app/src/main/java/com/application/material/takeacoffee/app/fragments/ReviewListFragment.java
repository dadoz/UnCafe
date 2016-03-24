package com.application.material.takeacoffee.app.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;
import android.widget.*;
import butterknife.Bind;
import butterknife.ButterKnife;

import com.application.material.takeacoffee.app.BuildConfig;
import com.application.material.takeacoffee.app.ReviewListActivity;
import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.adapters.ReviewRecyclerViewAdapter;
import com.application.material.takeacoffee.app.models.*;
import com.application.material.takeacoffee.app.singletons.BusSingleton;
import com.application.material.takeacoffee.app.singletons.PlaceApiManager;
import com.application.material.takeacoffee.app.utils.CacheManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.lang.ref.WeakReference;
import java.util.*;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.*;


/**
 * Created by davide on 08/04/14.
 */
public class ReviewListFragment extends Fragment implements AdapterView.OnItemLongClickListener,
        ReviewRecyclerViewAdapter.CustomItemClickListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
        PlaceApiManager.OnHandlePlaceApiResult, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "ReviewListFragment";
    private static AppCompatActivity mainActivityRef = null;
    private ArrayList<Review> reviewList;
    private String coffeePlaceId;
    private PlaceApiManager placesApiManager;
    private GoogleApiClient mGoogleApiClient;

    @Bind(R.id.reviewRecyclerViewId)
    RecyclerView reviewRecyclerView;
    @Bind(R.id.swipeRefreshLayoutId)
    SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<CoffeePlace> coffeePlacesList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivityRef =  (ReviewListActivity) context;
    }

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstance
     * @return
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View reviewListView = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_review_list, container, false);
        ButterKnife.bind(this, reviewListView);

        //TODO NOTHING - waiting for bus response :)
        initView();
        return reviewListView;
    }

    @Override
    public void onResume() {
//        BusSingleton.getInstance().register(this);
        super.onResume();
    }

    @Override
    public void onPause() {
//        BusSingleton.getInstance().unregister(this);
        super.onPause();
    }

    /**
     * init view to handle review data
     */
    public void initView() {
        if (BuildConfig.DEBUG) {
            reviewList = getReviewListTest();
            coffeePlacesList = getCoffeePlacesListTest();
            CoffeePlace coffeePlace = coffeePlacesList.get(0);
            initActionbar(coffeePlace.getName());
        }
        swipeRefreshLayout.setOnRefreshListener(this);
        mainActivityRef.findViewById(R.id.addReviewFabId)
                .setOnClickListener(this);
        initListView();
    }

    /**
     * samplePlacesApi
     */
    private void initGooglePlaces() {
        if (mGoogleApiClient != null) {
            return;
        }

        mGoogleApiClient = new GoogleApiClient
                .Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(), this)
                .build();

        placesApiManager = PlaceApiManager.getInstance(new WeakReference<PlaceApiManager.OnHandlePlaceApiResult>(this),
                mGoogleApiClient);
    }


    /**
     *
     */
    private void initActionbar(String name) {
        ActionBar actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setTitle(name);
        }
    }

    /**
     * init list view
     */
    private void initListView() {
        ReviewRecyclerViewAdapter adapter = new ReviewRecyclerViewAdapter(
                new WeakReference<Context>(getActivity()), reviewList);
        adapter.setOnItemClickListener(this);        //TODO booooo ????
        LinearLayoutManager layoutManager= new LinearLayoutManager(getContext());

        reviewRecyclerView.setAdapter(adapter);
        reviewRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onRefresh() {

    }

    /**
     *
     * @return
     */
    public ArrayList<Review> getReviewListTest() {
        ArrayList<Review> list = new ArrayList<Review>();
        list.add(new Review("0", "heheeheheheh", "balal", 1111, "1", "1", null, null));
        list.add(new Review("1", "blalallalll", "balal", 1111, "1", "1", null, null));
        list.add(new Review("2", "blalallalll2", "balal", 1111, "1", "1", null, null));
        list.add(new Review("3", "blalallall3", "balal", 1111, "1", "1", null, null));
        list.add(new Review("4", "blalallall4", "balal", 1111, "1", "1", null, null));
        list.add(new Review("5", "blalallall5", "balal", 1111, "1", "1", null, null));
        list.add(new Review("6", "blalallall6", "balal", 1111, "1", "1", null, null));
        list.add(new Review("7", "blalallall6", "balal", 1111, "1", "1", null, null));
        list.add(new Review("7", "blalallall7", "balal", 1111, "1", "1", null, null));
        list.add(new Review("8", "blalallall8", "balal", 1111, "1", "1", null, null));
        list.add(new Review("9", "blalallall9", "balal", 1111, "1", "1", null, null));
        list.add(new Review("10", "blalallall10", "balal", 1111, "1", "1", null, null));
        return list;
    }

    /**
     *
     * @return
     */
    public ArrayList<CoffeePlace> getCoffeePlacesListTest() {
        ArrayList<CoffeePlace> tmp = new ArrayList<CoffeePlace>();
        tmp.add(new CoffeePlace("0", "Caffe Vergnano Torino spa Bologna", "Corso Gramsci 7 alesessanrdia", null));
        tmp.add(new CoffeePlace("1", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeePlace("2", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeePlace("3", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeePlace("4", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeePlace("0", "Caffe Vergnano Torino spa Bologna", "Corso Gramsci 7 alesessanrdia", null));
        tmp.add(new CoffeePlace("1", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeePlace("2", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeePlace("3", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeePlace("4", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeePlace("0", "Caffe Vergnano Torino spa Bologna", "Corso Gramsci 7 alesessanrdia", null));
        tmp.add(new CoffeePlace("1", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeePlace("2", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeePlace("3", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeePlace("4", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeePlace("0", "Caffe Vergnano Torino spa Bologna", "Corso Gramsci 7 alesessanrdia", null));
        tmp.add(new CoffeePlace("1", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeePlace("2", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeePlace("3", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeePlace("4", "Caffe Vergnano Torino spa Bologna", "hey", null));
        return tmp;
    }

//    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
//    public void onEvent(Bundle bundle) {
//        coffeePlaceId = bundle.getString(CoffeePlace.COFFEE_PLACE_ID_KEY);
//        initGooglePlaces();
//        placesApiManager.getInfo(coffeePlaceId, true);
//    }

    @Override
    public void onSetCoffeePlaceInfoOnListCallback(Place place) {
        initActionbar(place.getName().toString());
    }

    @Override
    public void onUpdatePhotoOnListCallback() {
        //handle picture
        Bitmap cachedPic = CacheManager.getInstance().getBitmapFromMemCache(coffeePlaceId);
        if (cachedPic != null) {
            ((ImageView) getActivity().findViewById(R.id.coffeePlaceImageViewId))
                    .setImageBitmap(cachedPic);
        }
    }

    @Override
    public void handleLatestItem() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onItemClick(int pos, View v) {
        Log.e("TAG", "hey click -> " + pos);
    }
}

