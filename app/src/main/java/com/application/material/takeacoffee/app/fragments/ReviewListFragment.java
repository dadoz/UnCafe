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
import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.adapters.ReviewRecyclerViewAdapter;
import com.application.material.takeacoffee.app.decorator.DividerItemDecoration;
import com.application.material.takeacoffee.app.models.*;
import com.application.material.takeacoffee.app.singletons.EventBusSingleton;
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


/**
 * Created by davide on 08/04/14.
 */
public class ReviewListFragment extends Fragment implements AdapterView.OnItemLongClickListener,
        ReviewRecyclerViewAdapter.CustomItemClickListener,
        SwipeRefreshLayout.OnRefreshListener, PlaceApiManager.OnHandlePlaceApiResult,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private static final String TAG = "ReviewListFragment";
    private String coffeePlaceId;
    private PlaceApiManager placesApiManager;
    private GoogleApiClient mGoogleApiClient;

    @Bind(R.id.reviewRecyclerViewId)
    RecyclerView reviewRecyclerView;
    @Bind(R.id.swipeRefreshLayoutId)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.coffeePlacesProgressId)
    ProgressBar coffeePlacesProgress;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View reviewListView = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_review_list, container, false);
        ButterKnife.bind(this, reviewListView);
        initView();
        return reviewListView;
    }

    @Override
    public void onResume() {
        EventBusSingleton.getInstance().register(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        EventBusSingleton.getInstance().unregister(this);
        super.onPause();
    }

    /**
     * init view to handle review data
     */
    public void initView() {
        swipeRefreshLayout.setOnRefreshListener(this);
        getActivity().findViewById(R.id.addReviewFabId).setOnClickListener(this);
        initListView();
        initGooglePlaces();
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

        placesApiManager = PlaceApiManager.getInstance(new WeakReference<PlaceApiManager.OnHandlePlaceApiResult>(this)
        );
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
                new WeakReference<Context>(getActivity()), new ArrayList<Review>());
        adapter.setOnItemClickListener(this);        //TODO booooo ????
        LinearLayoutManager layoutManager= new LinearLayoutManager(getContext());

        reviewRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                LinearLayoutManager.VERTICAL));
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
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(Bundle bundle) {
        //TODO refactor
        coffeePlaceId = bundle.getString(CoffeePlace.COFFEE_PLACE_ID_KEY);
        String placeName = bundle.getString(CoffeePlace.COFFEE_PLACE_NAME_KEY);

        onSetCoffeePlaceInfoOnListCallback(placeName);
        onUpdatePhotoOnListCallback();

        if (false && BuildConfig.DEBUG) {
            ((ReviewRecyclerViewAdapter) reviewRecyclerView.getAdapter())
                    .addAllItems(getReviewListTest());
            return;
        }

        //review
        ArrayList<Review> reviewList = placesApiManager.getReviewByPlaceId(coffeePlaceId);
        ((ReviewRecyclerViewAdapter) reviewRecyclerView.getAdapter())
                .addAllItems(reviewList);
    }

    /**
     *
     * @param title
     */
    public void onSetCoffeePlaceInfoOnListCallback(String title) {
        initActionbar(title);
    }

    /**
     *
     * @param place
     */
    @Override
    public void onSetCoffeePlaceInfoOnListCallback(Place place) {
        initActionbar(place.getName().toString());
    }

    /**
     *
     */
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
    public void handleEmptyList() {
        ((ReviewRecyclerViewAdapter) reviewRecyclerView.getAdapter()).setEmptyResult(true);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onItemClick(int pos, View v) {
        //TODO smthing with this
        Toast.makeText(getContext(), "hey yu're clicking review content", Toast.LENGTH_LONG).show();
        Log.e(TAG, "HANDLE on click review");
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(getContext(), "hey yu're clicking share content", Toast.LENGTH_LONG).show();
        Log.e(TAG, "HANDLE share review :)");
    }


    /**
     *
     * @return
     */
    public ArrayList<Review> getReviewListTest() {
        ArrayList<Review> list = new ArrayList<Review>();
        list.add(new Review("0", "1", "heheeheheheh", "hadshfjefhsejkfhakejh", 1234342, null));
        return list;
    }


}

