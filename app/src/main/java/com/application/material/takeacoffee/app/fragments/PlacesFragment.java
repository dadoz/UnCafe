package com.application.material.takeacoffee.app.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.*;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import icepick.Icepick;
import icepick.State;

import com.application.material.takeacoffee.app.*;
import com.application.material.takeacoffee.app.adapters.PlacesGridViewAdapter;
import com.application.material.takeacoffee.app.decorators.ItemOffsetDecoration;
import com.application.material.takeacoffee.app.helper.BundleHelper;
import com.application.material.takeacoffee.app.helper.MaterialSearchManager;
import com.application.material.takeacoffee.app.models.CoffeePlace;
import com.application.material.takeacoffee.app.singletons.EventBusSingleton;
import com.application.material.takeacoffee.app.utils.Utils;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class PlacesFragment extends Fragment implements PlacesGridViewAdapter.CustomItemClickListener,
        SwipeRefreshLayout.OnRefreshListener {
    public static final String COFFEE_MACHINE_FRAG_TAG = "COFFEE_MACHINE_FRAG_TAG";

    @BindView(R.id.coffeePlacesRecyclerViewId)
    RecyclerView coffeePlacesRecyclerview;
    @BindView(R.id.coffeePlacesProgressId)
    ProgressBar coffeePlacesProgress;


    @BindView(R.id.coffeePlaceSwipeRefreshLayoutId)
    SwipeRefreshLayout coffeePlaceSwipeRefreshLayout;

    private Unbinder unbinder;

    @State
    public ArrayList<CoffeePlace> placeList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_coffee_places_layout, container, false);

        Icepick.restoreInstanceState(this, savedInstance);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @NonNull Bundle savedInstance) {
//        changePlaceAutocompletePresenter = ChangeLocationAutocompleteFilterPresenter
//                .getInstance(new WeakReference<>(getContext()),
//                        new WeakReference<OnHandlePlaceApiResult>(this),
//                        new View[] {changePlaceFilterAutocompleteTextview, changePlaceTextInputLayout});
        initView(savedInstance);
    }

    @Override
    public void onResume() {
        super.onResume();
//        presenter.init();
    }

    @Override
    public void onStop() {
        super.onStop();
//        unsubscribeObservable();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        unsubscribeObservable();
        if (unbinder != null)
            unbinder.unbind();
    }

    /**
     * init view
     * @param savedInstance
     */
    public void initView(Bundle savedInstance) {
        initActionBar();
        setHasOptionsMenu(true);

//        emptyResultButton.setOnClickListener(this);
        coffeePlaceSwipeRefreshLayout.setOnRefreshListener(this);
//        initFilters();
        initGridViewAdapter();
//        initGooglePlaces();
        initViewFromSavedInstance(savedInstance);
//        initPermissionChainResponsibility();
    }

    /**
     * init action bar
     */
    public void initActionBar() {
        ActionBar actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(false);
            actionbar.setTitle(getResources().getString(R.string.coffee_place_actionbar));
        }
    }
    /**
     *
     * @param savedInstance
     */
    private void initViewFromSavedInstance(Bundle savedInstance) {
        if (savedInstance == null) {
            return;
        }

        if (placeList.size() != 0) {
//            handleInfo(placeList, false);
            return;
        }

        ((PlacesGridViewAdapter) coffeePlacesRecyclerview.getAdapter()).setEmptyResult(true);
        coffeePlacesRecyclerview.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onItemClick(int pos, View v) {
        CoffeePlace place = ((PlacesGridViewAdapter) coffeePlacesRecyclerview.getAdapter())
            .getItem(pos);
        Bundle bundle = BundleHelper.createBundleByPlacePosition(place);
        changeActivity(bundle);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.coffee_place, menu);
        MaterialSearchView searchView = ((MaterialSearchView) getView().getRootView()
                .findViewById(R.id.searchViewId));
        new MaterialSearchManager().initSearchView(menu.findItem(R.id.action_search), searchView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_policy:
                showInfoDialog();
                break;
            case R.id.action_settings:
                //force to edit mode
//                placeFilterPresenter.setEditMode();
//                handleFilterBackPressed();
                changeFragment(new SettingListFragment(),
                        SettingListFragment.SETTING_LIST_FRAG_TAG);
                break;
        }
        return true;
    }

    /**
     * init grid view adapter
     */
    private void initGridViewAdapter() {
        PlacesGridViewAdapter adapter = new PlacesGridViewAdapter(new WeakReference<>(getContext()),
                new ArrayList<CoffeePlace>());
//        adapter.registerAdapterDataObserver(new CoffeePlaceAdapterObserver(new WeakReference<>(adapter),
//                coffeePlacesProgress, coffeePlacesEmptyResult));
        adapter.setOnItemClickListener(this);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        coffeePlacesRecyclerview.setLayoutManager(layoutManager);
        coffeePlacesRecyclerview.setAdapter(adapter);
        coffeePlacesRecyclerview.addItemDecoration(new ItemOffsetDecoration(getContext(), R.dimen.small_padding));
//        initScrollListener(layoutManager);
//        coffeePlacesRecyclerview.addOnScrollListener(scrollListener);
        //TODO add footer or header to handle more review spinner and also get map button!
    }

//    /**
//     * TODO rm it
//     */
//    private void unsubscribeObservable() {
//        if (obsSubscription != null) {
//            obsSubscription.unsubscribe();
//        }
//    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Icepick.saveInstanceState(this, savedInstanceState);
    }

    /**
     *
     * @param fragment
     * @param tag
     */
    private void changeFragment(Fragment fragment, String tag) {
        getActivity()
                .getSupportFragmentManager().beginTransaction()
                .replace(R.id.coffeeAppFragmentContainerId, fragment, tag)
                .addToBackStack("TAG")
                .commit();
    }

    /**
     * change activity on reviewList
     * @param bundle
     *
     */
    private void changeActivity(Bundle bundle) {
        EventBusSingleton.getInstance().postSticky(bundle);
        startActivity(new Intent(getActivity(), ReviewListActivity.class));
    }

    /**
     *
     */
    private void showErrorMessage() {
        try {
            Utils.showSnackbar(new WeakReference<>(getContext()), getView(), R.string.no_place_found);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     *
     */
    private void setActionbarHomeButtonEnabled(boolean isEnabled) {
        if (getActivity() == null) {
            return;
        }

        ActionBar actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(isEnabled);
        }
    }

    @Override
    public void onRefresh() {
        //TODO need to be implemented
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                coffeePlaceSwipeRefreshLayout.setRefreshing(false);
            }
        }, 1500);
    }

    /**
     *
     */
    public void showInfoDialog() {
        AlertDialog dialog = new AlertDialog.Builder(getContext(),
                R.style.CustomAlertDialogStyle)
                .setView(R.layout.dialog_info_layout)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

}
