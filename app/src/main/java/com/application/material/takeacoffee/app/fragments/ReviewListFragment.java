package com.application.material.takeacoffee.app.fragments;

import android.content.Context;
import android.content.Intent;
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

import com.application.material.takeacoffee.app.HandleReviewActivity;
import com.application.material.takeacoffee.app.BuildConfig;
import com.application.material.takeacoffee.app.ReviewListActivity;
import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.adapters.ReviewRecyclerViewAdapter;
import com.application.material.takeacoffee.app.decorator.DividerItemDecoration;
import com.application.material.takeacoffee.app.models.*;
import com.application.material.takeacoffee.app.singletons.BusSingleton;
import com.application.material.takeacoffee.app.singletons.FirebaseManager;
import com.application.material.takeacoffee.app.singletons.PlaceApiManager;
import com.application.material.takeacoffee.app.utils.CacheManager;
import com.firebase.client.FirebaseError;
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
        ReviewRecyclerViewAdapter.CustomItemClickListener, View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener, PlaceApiManager.OnHandlePlaceApiResult,
        GoogleApiClient.OnConnectionFailedListener, FirebaseManager.OnRetrieveFirebaseDataInterface {
    private static final String TAG = "ReviewListFragment";
    private static AppCompatActivity mainActivityRef = null;
    private ArrayList<Review> reviewList = new ArrayList<>();
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
        initView();
        return reviewListView;
    }

    @Override
    public void onResume() {
        BusSingleton.getInstance().register(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        BusSingleton.getInstance().unregister(this);
        super.onPause();
    }

    /**
     * init view to handle review data
     */
    public void initView() {
        swipeRefreshLayout.setOnRefreshListener(this);
        mainActivityRef.findViewById(R.id.addReviewFabId).setOnClickListener(this);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addReviewFabId:

                Bundle bundle = new Bundle();
                bundle.putString(Review.REVIEW_ID_KEY, "090Xg3rDmx");
                bundle.putString(Review.REVIEW_CONTENT_KEY, "balsdlllasldlflalsl llsadf lalsll sdlfl lalsd");
                BusSingleton.getInstance().postSticky(bundle);
                Intent intent = new Intent(getActivity(), HandleReviewActivity.class);
                startActivity(intent);
                break;
        }
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

        if (BuildConfig.DEBUG) {
            Log.e("TAG", "BLA");
            reviewList = getReviewListTest();
            ((ReviewRecyclerViewAdapter) reviewRecyclerView.getAdapter()).addAllItems(reviewList);
//            reviewRecyclerView.getAdapter().notifyDataSetChanged();
            return;
        }

        //review
        reviewList.clear();
        FirebaseManager.getIstance()
                .getReviewListAsync(new WeakReference<FirebaseManager.OnRetrieveFirebaseDataInterface>(this), "kFFMaPaytU");
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
    public void emptyFirebaseDataCallback() {
        ((ReviewRecyclerViewAdapter) reviewRecyclerView.getAdapter()).setEmptyResult(true);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onItemClick(int pos, View v) {
        //TODO move
        Bundle bundle = new Bundle();
        bundle.putString(Review.REVIEW_ID_KEY, "000000");
        bundle.putString(Review.REVIEW_CONTENT_KEY, "Hey content review, you can handle this by bla");
        bundle.putInt(Review.REVIEW_RATING_KEY, 4);
        BusSingleton.getInstance().postSticky(bundle);

        Intent intent = new Intent(getActivity(), HandleReviewActivity.class);
        startActivity(intent);

    }

    @Override
    public void retrieveFirebaseDataSuccessCallback(String type, ArrayList<Review> list) {
        coffeePlacesProgress.setVisibility(View.GONE);
        reviewList.addAll(list);
        reviewRecyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void retrieveFirebaseDataErrorCallback(FirebaseError error) {
        Log.e(TAG, "error" + error.getMessage());
        //TODO change view
        ((ReviewRecyclerViewAdapter) reviewRecyclerView.getAdapter()).setEmptyResult(true);
    }

    /**
     *
     * @return
     */
    public ArrayList<Review> getReviewListTest() {
        ArrayList<Review> list = new ArrayList<Review>();
        list.add(new Review("0", "1", "heheeheheheh", "hadshfjefhsejkfhakejh", 1234342, null));
//        list.add(new Review("1", "blalallalll", "hadshfjefhsejkfhakejh adfaklsjd kj klsdfkj alskjd lksdj flksd fa", 1234342, "Guest Bla", "1", null, null));
//        list.add(new Review("2", "blalallalll2", "hadshfjefhsejkfhakejh adfaklsjd kj klsdfkj alskjd lksdj flksd fa", 1234342, "Davide", "1", null, null));
//        list.add(new Review("3", "blalallall3", "hadshfjefhsejkfhakejh adfaklsjd kj klsdfkj alskjd lksdj flksd fa", 1234342, "Andrea", "1", null, null));
//        list.add(new Review("4", "blalallall4", "balal", 1234342, "Davide", "1", null, null));
//        list.add(new Review("5", "blalallall5", "hadshfjefhsejkfhakejh adfaklsjd kj klsdfkj alskjd lksdj flksd fa", 1234342, "Guest Hey", "1", null, null));
//        list.add(new Review("6", "blalallall6", "balal", 1234342, "1", "1", null, null));
//        list.add(new Review("7", "blalallall6", "hadshfjefhsejkfhakejh adfaklsjd kj klsdfkj alskjd lksdj flksd fa", 1234342, "1", "1", null, null));
//        list.add(new Review("7", "blalallall7", "balal", 1234342, "1", "1", null, null));
//        list.add(new Review("8", "blalallall8", "hadshfjefhsejkfhakejh adfaklsjd kj klsdfkj alskjd lksdj flksd fa", 1234342, "1", "1", null, null));
//        list.add(new Review("9", "blalallall9", "balal", 1234342, "1", "1", null, null));
//        list.add(new Review("10", "blalallall10", "balal", 1234342, "1", "1", null, null));
        return list;
    }
}

