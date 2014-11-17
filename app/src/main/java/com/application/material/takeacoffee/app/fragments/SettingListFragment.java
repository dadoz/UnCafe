package com.application.material.takeacoffee.app.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.WrapperListAdapter;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.application.material.takeacoffee.app.CoffeeMachineActivity;
import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.adapters.ReviewListAdapter;
import com.application.material.takeacoffee.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.takeacoffee.app.fragments.interfaces.OnLoadViewHandlerInterface;
import com.application.material.takeacoffee.app.fragments.interfaces.SetActionBarInterface;
import com.application.material.takeacoffee.app.loaders.RestResponse;
import com.application.material.takeacoffee.app.loaders.RetrofitLoader;
import com.application.material.takeacoffee.app.models.Review;
import com.application.material.takeacoffee.app.models.Setting;
import com.application.material.takeacoffee.app.models.User;
import com.application.material.takeacoffee.app.parsers.ParserToJavaObject;

import java.util.ArrayList;

import static com.application.material.takeacoffee.app.loaders.RetrofitLoader.HTTPActionRequestEnum.REVIEW_REQUEST;
import static com.application.material.takeacoffee.app.loaders.RetrofitLoader.HTTPActionRequestEnum.USER_REQUEST;
import static com.application.material.takeacoffee.app.models.ReviewStatus.ReviewStatusEnum;

/**
 * Created by davide on 08/04/14.
 */
public class SettingListFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<RestResponse>,
        AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener, DialogInterface.OnClickListener {
    private static final String TAG = "ReviewListFragment";
    private static FragmentActivity mainActivityRef = null;

    private View reviewListView;
    private String coffeeMachineId;
//    private ArrayList<Review> reviewListDataStorage;
    private Bundle bundle;
//    private ReviewStatusEnum reviewStatus;


    @InjectView(R.id.reviewsContainerListViewId) ListView listView;
//    private View moreReviewLoaderView;
//    private View emptyView;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof OnLoadViewHandlerInterface)) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLoadViewHandlerInterface");
        }
        if (!(activity instanceof OnChangeFragmentWrapperInterface)) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLoadViewHandlerInterface");
        }
        mainActivityRef =  (CoffeeMachineActivity) activity;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        reviewListView = inflater.inflate(R.layout.fragment_review_list, container, false);
        ButterKnife.inject(this, reviewListView);
        setHasOptionsMenu(true);
        initOnLoadView();
        return reviewListView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        //get all bundle
        bundle = getArguments();
    }

    private void initOnLoadView() {
//        ((OnLoadViewHandlerInterface) mainActivityRef).initOnLoadView();
//        getLoaderManager().initLoader(REVIEW_REQUEST.ordinal(), null, this)
//                .forceLoad();
        ArrayList<Setting> settingList = new ArrayList<Setting>();
        initView(settingList);
    }

    public void initView(ArrayList<Setting> reviewList) {
        ((OnLoadViewHandlerInterface) mainActivityRef).hideOnLoadView();

        //action bar
        ((SetActionBarInterface) mainActivityRef)
                .setActionBarCustomViewById(R.id.customActionBarReviewListLayoutId, null);
        ((SetActionBarInterface) mainActivityRef)
                .setCustomNavigation(SettingListFragment.class);


        SettingListAdapter reviewListenerAdapter = new SettingListAdapter(mainActivityRef,
                R.layout.review_template, reviewList, coffeeMachineId);
        listView.setAdapter(reviewListenerAdapter);

        listView.setOnItemClickListener(this);
    }

    @Override
    public Loader<RestResponse> onCreateLoader(int id, Bundle params) {
        try {
            String action = RetrofitLoader.getActionByActionRequestEnum(id);
            return new RetrofitLoader(this.getActivity(), action, params);
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<RestResponse> loader,
                               RestResponse restResponse) {
    }


    @Override
    public void onLoaderReset(Loader<RestResponse> restResponseLoader) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.review_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        }
        return true;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Log.e(TAG, "dismiss");
    }

}

