package com.application.material.takeacoffee.app.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.*;
import android.util.Log;
import android.view.*;
import android.widget.*;
import butterknife.Bind;
import butterknife.ButterKnife;

import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.adapters.ReviewRecyclerViewAdapter;
import com.application.material.takeacoffee.app.application.CoffeePlacesApplication;
import com.application.material.takeacoffee.app.decorators.DividerItemDecoration;
import com.application.material.takeacoffee.app.models.*;
import com.application.material.takeacoffee.app.singletons.EventBusSingleton;
import com.application.material.takeacoffee.app.singletons.PicassoSingleton;
import com.application.material.takeacoffee.app.singletons.PicassoSingleton.PicassoCallbacksInterface;
import com.application.material.takeacoffee.app.singletons.PlaceApiManager;
import com.application.material.takeacoffee.app.singletons.RetrofitManager;
import com.application.material.takeacoffee.app.utils.ExpandableTextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, PicassoCallbacksInterface {
    private static final String TAG = "ReviewListFragment";
    private String coffeePlaceId;
    private String placeName;
    private PlaceApiManager placesApiManager;
    private String photoReference;

    @Bind(R.id.reviewRecyclerViewId)
    RecyclerView reviewRecyclerView;
    @Bind(R.id.swipeRefreshLayoutId)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.coffeePlacesProgressId)
    ProgressBar coffeePlacesProgress;
    private ImageView coffeePlaceImageView;
    private CollapsingToolbarLayout collapsingToolbar;

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
        collapsingToolbar = (CollapsingToolbarLayout) getActivity()
                .findViewById(R.id.collapsingToolbarLayoutId);
        coffeePlaceImageView = ((ImageView) getActivity().findViewById(R.id.coffeePlaceImageViewId));

        swipeRefreshLayout.setOnRefreshListener(this);
        getActivity().findViewById(R.id.addReviewFabId).setOnClickListener(this);
        initListView();
        initGooglePlaces();
    }

    /**
     * samplePlacesApi
     */
    private void initGooglePlaces() {
        placesApiManager = PlaceApiManager
                .getInstance(new WeakReference<PlaceApiManager.OnHandlePlaceApiResult>(this), new WeakReference<>(getContext()));

    }

    /**
     *
     */
    private void initActionbar(String name) {
        ActionBar actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (getActivity() != null &&
                actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setTitle(name);
            setCoolapsingToolbarTitleFont();
        }
    }

    /**
     *
     */
    private void setCoolapsingToolbarTitleFont() {
        final Typeface tf = Typeface
                .createFromAsset(getActivity().getAssets(), CoffeePlacesApplication.FONT_BOLD_PATH);
        collapsingToolbar.setCollapsedTitleTypeface(tf);
        collapsingToolbar.setExpandedTitleTypeface(tf);

    }
    /**
     *
     */
    private void setCoolapsingToolbarTitleColor() {
        try {
            new Palette.Builder(((BitmapDrawable) coffeePlaceImageView.getDrawable()).getBitmap())
                    .generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    int grey = getActivity().getResources().getColor(R.color.material_grey400);
                    int brown = getActivity().getResources().getColor(R.color.material_brown800);
                    collapsingToolbar.setContentScrimColor(palette.getMutedColor(brown));
                    collapsingToolbar.setCollapsedTitleTextColor(palette.getLightVibrantColor(grey));
                    collapsingToolbar.setExpandedTitleColor(palette.getLightVibrantColor(grey));
                }
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
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

    /**
     *
     */
    private void setPlaceName() {
        initActionbar(placeName);
    }

    /**
     *
     */
    private void setPlacePhotoByUrl() {
        Drawable defaultIcon = getResources()
                .getDrawable(R.drawable.ic_local_see_black_48dp);
        if (photoReference == null) {
            coffeePlaceImageView.setImageDrawable(defaultIcon);
            return;
        }
        PicassoSingleton.getInstance(new WeakReference<>(getContext()),
                new WeakReference<PicassoCallbacksInterface>(this))
                .setPhotoAsync(coffeePlaceImageView, photoReference, defaultIcon);
    }


    /**
     *
     * @param list
     */
    public void handleReviewOnListCallback(ArrayList<Review> list) {
        coffeePlacesProgress.setVisibility(View.GONE);
        ((ReviewRecyclerViewAdapter) reviewRecyclerView.getAdapter())
                .addAllItems(list);
    }


    @Override
    public void onPlaceApiSuccess(Object result, PlaceApiManager.RequestType type) {
        if (type == PlaceApiManager.RequestType.PLACE_REVIEWS) {
            handleReviewOnListCallback((ArrayList<Review>) result);
        }
    }

    @Override
    public void onEmptyResult() {
        Log.e("ReviewsFrag", "empty");
        ((ReviewRecyclerViewAdapter) reviewRecyclerView.getAdapter()).setEmptyResult(true);
        coffeePlacesProgress.setVisibility(View.GONE);
    }

    @Override
    public void onErrorResult() {
        Log.e("ReviewsFrag", "error");
        ((ReviewRecyclerViewAdapter) reviewRecyclerView.getAdapter()).setEmptyResult(true);
        coffeePlacesProgress.setVisibility(View.GONE);
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onItemClick(int pos, View v) {
        ((ExpandableTextView) v.findViewById(R.id.reviewTextId)).toggleEllipsize();
    }

    @Override
    public void onClick(View v) {
        //TODO handle it
        Toast.makeText(getContext(), "hey yu're clicking share content", Toast.LENGTH_LONG).show();
        Log.e(TAG, "HANDLE share review :)");
    }


    /**
     *
     * @return
     */
    public ArrayList<Review> getReviewListTest() {
        ArrayList<Review> list = new ArrayList<Review>();
//        list.add(new Review("0", "1", "heheeheheheh", "hadshfjefhsejkfhakejh", 1234342, null));
        return list;
    }

    /**
     *
     * @param bundle
     */
    private void parseBundle(Bundle bundle) {
        coffeePlaceId = bundle.getString(CoffeePlace.COFFEE_PLACE_ID_KEY);
        placeName = bundle.getString(CoffeePlace.COFFEE_PLACE_NAME_KEY);
        photoReference = bundle.getString(CoffeePlace.COFFEE_PLACE_PHOTO_REFERENCE_KEY);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(Bundle bundle) {
        parseBundle(bundle);

        setPlaceName();
        setPlacePhotoByUrl();
        placesApiManager.retrieveReviewsAsync(coffeePlaceId);
    }


    @Override
    public void onPicassoSuccessCallback() {
        if (getActivity() != null) {
            setCoolapsingToolbarTitleFont();
            setCoolapsingToolbarTitleColor();
        }

    }

    @Override
    public void onPicassoErrorCallback() {

    }
}

