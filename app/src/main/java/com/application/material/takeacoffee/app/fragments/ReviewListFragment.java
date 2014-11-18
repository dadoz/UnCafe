package com.application.material.takeacoffee.app.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
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
import com.application.material.takeacoffee.app.models.User;
import com.application.material.takeacoffee.app.parsers.ParserToJavaObject;
import uk.me.lewisdeane.ldialogs.CustomDialog;

import java.util.ArrayList;

import static com.application.material.takeacoffee.app.loaders.RetrofitLoader.HTTPActionRequestEnum.REVIEW_REQUEST;
import static com.application.material.takeacoffee.app.loaders.RetrofitLoader.HTTPActionRequestEnum.USER_REQUEST;
import static com.application.material.takeacoffee.app.models.ReviewStatus.ReviewStatusEnum;

/**
 * Created by davide on 08/04/14.
 */
public class ReviewListFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<RestResponse>,
        AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener, DialogInterface.OnClickListener {
    private static final String TAG = "ReviewListFragment";
    public static final String REVIEW_LIST_FRAG_TAG = "REVIEW_LIST_FRAG_TAG";
    private static FragmentActivity mainActivityRef = null;

//    private Common.ReviewStatusEnum reviewStatus;
//    private View reviewListView, emptyView, moreReviewLoaderView;
    private View reviewListView;
    private String coffeeMachineId;
    private ArrayList<Review> reviewListDataStorage;
    private Bundle bundle;
    private ReviewStatusEnum reviewStatus;


    @InjectView(R.id.reviewsContainerListViewId) ListView listView;
    private View moreReviewLoaderView;
    private View emptyView;


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
//        moreReviewLoaderView = LayoutInflater.from(mainActivityRef.getApplicationContext())
//                .inflate(R.layout.more_review_loader_layout, listView, false);
//        emptyView = inflater.inflate(R.layout.empty_data_status_layout, container, false);
        setHasOptionsMenu(true);
        initOnLoadView();

        return reviewListView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        //get all bundle
        bundle = getArguments();
//        coffeeMachineId = bundle.getString(CoffeeMachine.COFFEE_MACHINE_ID_KEY);
//        reviewStatus = ReviewStatusEnum.valueOf(bundle
//                .getString(ReviewStatus.REVIEW_STATUS_KEY));

    }

    private void initOnLoadView() {
        ((OnLoadViewHandlerInterface) mainActivityRef).initOnLoadView();
        getLoaderManager().initLoader(REVIEW_REQUEST.ordinal(), null, this)
                .forceLoad();

        //TODO REFACTORIZE IT
/*        long fromTimestamp = bundle.getLong(Common.FROM_TIMESTAMP_KEY);
        long toTimestamp = bundle.getLong(Common.TO_TIMESTAMP_KEY);
        Bundle params = RestResponse.createBundleReview(coffeeMachineId, fromTimestamp, toTimestamp);
        if (getLoaderManager().getLoader(RestLoader.HTTPVerb.POST) == null) {
            getLoaderManager().initLoader(RestLoader.HTTPVerb.POST, params, this)
                    .forceLoad();
        } else {
            initView(reviewListDataStorage, coffeeMachineId);
        }*/

    }

    public void initView(ArrayList<Review> reviewList) {
        ((OnLoadViewHandlerInterface) mainActivityRef).hideOnLoadView();

        //action bar
        ((SetActionBarInterface) mainActivityRef)
                .setActionBarCustomViewById(R.id.customActionBarReviewListLayoutId, null);
        ((SetActionBarInterface) mainActivityRef)
                .setCustomNavigation(ReviewListFragment.class);

        if (reviewList == null) {
            Log.e(TAG, "empty review list");
            listView.setEmptyView(emptyView);
            return;
        }

        ReviewListAdapter reviewListenerAdapter = new ReviewListAdapter(mainActivityRef,
                R.layout.review_template, reviewList, coffeeMachineId);
        listView.setAdapter(reviewListenerAdapter);

        listView.setOnItemLongClickListener(this);
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
        final int REVIEW_REQ = 1; //REVIEW_REQUEST.ordinal();
        final int MORE_REVIEW_REQ = 2; //MORE_REVIEW_REQUEST.ordinal();
        final int USER_REQ = 3; //MORE_REVIEW_REQUEST.ordinal();

//        if(restResponse.getParsedData() == null) {
//            Log.e(TAG, "empty data result - nothing to be done");
//            return;
//        }
        Log.e(TAG, "id review_request" + REVIEW_REQUEST.ordinal());
        try {
            switch (loader.getId()) {
                case REVIEW_REQ:
                    Log.e(TAG, "REVIEW_REQ");
//                    ArrayList<Review> reviewList = (ArrayList<Review>) restResponse.getParsedData();
//                    create new loader for user
//                    Bundle params = new Bundle();
//                    getLoaderManager().initLoader(USER_REQUEST.ordinal(), params, this).forceLoad();
//                    initView(reviewList);

                    ((OnLoadViewHandlerInterface) mainActivityRef).hideOnLoadView();
                    String filename = "reviews.json";
                    String data = RetrofitLoader.getJSONDataMockup(this.getActivity(), filename);
                    ArrayList<Review> reviewList = ParserToJavaObject.getReviewListParser(data);
                    Bundle params = new Bundle();
                    getLoaderManager().initLoader(USER_REQUEST.ordinal(), params, this).forceLoad();
                    initView(reviewList);

                    break;
                case MORE_REVIEW_REQ:
                    Log.e(TAG, "MORE_REVIEW_REQ");
                    reviewList = (ArrayList<Review>) restResponse.getParsedData();
                    //create new loader for user
                    params = new Bundle();
                    getLoaderManager().initLoader(USER_REQUEST.ordinal(), params, this).forceLoad();
                    break;
                case USER_REQ:
                    Log.e(TAG, "USER_REQ");

//                    ArrayList<User> userList = (ArrayList<User>) restResponse.getParsedData();

                    //TODO TEST
                    filename = "user.json";
                    data = RetrofitLoader.getJSONDataMockup(this.getActivity(), filename);
                    ArrayList<User> userList = ParserToJavaObject.getUserListParser(data);

                    ((ReviewListAdapter) listView.getAdapter()).setUserList(userList);
                    //UPDATE DATA on LIST
                    if (listView.getAdapter() != null) {
                        try {
                            ((ReviewListAdapter) ((WrapperListAdapter) listView.getAdapter()).getWrappedAdapter()).notifyDataSetChanged();
                        } catch (Exception e) {
                            ((ReviewListAdapter) listView.getAdapter()).notifyDataSetChanged();
                        }
                    }

                    break;
            }


//            Log.d(TAG, "this are data result" + restResponse.getData());
//            if(restResponse.getRequestType() == null) {
//                reviewResponse(restResponse);
//                return;
//            }
//
//            switch(restResponse.getRequestType()) {
//                case RestLoaderRetrofit.HTTPAction.REVIEW_REQUEST:
//                    reviewResponse(restResponse);
//                    break;
//                case RestLoaderRetrofit.HTTPAction.USER_REQUEST:
//                    userResponse(restResponse);
//                    break;
//                case RestLoaderRetrofit.HTTPAction.MORE_REVIEW_REQUEST:
//                    moreReviewResponse(restResponse);
//                    break;
//                default:
//                    Log.e(TAG, "error - no valid response");
//                    break;
//            }
//
//            reviewResponse(restResponse);
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }


    @Override
    public void onLoaderReset(Loader<RestResponse> restResponseLoader) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivityRef)
                .setView(View.inflate(mainActivityRef,
                        R.layout.review_dialog_template, null))
                .setPositiveButton("Ok", this);
        AlertDialog customDialog = builder.create();
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.show();

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
//            case R.id.action_delete:
//                Toast.makeText(mainActivityRef, "map calling", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.action_edit_icon:
//                Toast.makeText(mainActivityRef, "map calling", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.action_my_review_list:
//                Toast.makeText(mainActivityRef, "my own review list", Toast.LENGTH_SHORT).show();
//                break;
        }
        return true;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Log.e(TAG, "dismiss");
    }




/*
    private void moreReviewResponse(RestResponse restResponse) {
        if(! restResponse.getHasMoreReviews()) {
            listView.removeHeaderView(moreReviewLoaderView);
        }

        String filename = "reviews.json";
        String data = RestResponse.getJSONDataMockup(this.getActivity(), filename);
        ArrayList<Review> reviewList = restResponse.getReviewListParser(data);

        Collections.reverse(reviewList);

        reviewListDataStorage.addAll(0, reviewList);

        ArrayList<String> userIdList = new ArrayList<>();
        for (Review review : reviewList) {
            userIdList.add(review.getUserId());
        }
        Bundle bundle = RestResponse.createBundleUser(userIdList);
        Log.d(TAG, "hey " + bundle.getString("requestType"));
        getLoaderManager().restartLoader(RestLoader.HTTPVerb.POST, bundle, this).forceLoad();

        if (listView.getAdapter() != null) {
            try {
                ((ReviewListAdapter) ((WrapperListAdapter) listView.getAdapter()).getWrappedAdapter()).notifyDataSetChanged();
            } catch (Exception e) {
                ((ReviewListAdapter) listView.getAdapter()).notifyDataSetChanged();
            }
        }

    }

    private void userResponse(RestResponse restResponse) {
        ArrayList<User> userList = restResponse.getUserListParser();
        //TODO move in coffeeAppLogic
        coffeeAppController.addUserOnLocalListByList(userList);
        if (listView.getAdapter() != null) {
            try {
                ((ReviewListAdapter) ((WrapperListAdapter) listView.getAdapter()).getWrappedAdapter()).notifyDataSetChanged();
            } catch (Exception e) {
                ((ReviewListAdapter) listView.getAdapter()).notifyDataSetChanged();
            }
        }
    }

    private void reviewResponse(RestResponse restResponse) {
        if(restResponse.getHasMoreReviews()) {
            listView.addHeaderView(moreReviewLoaderView);
        }


        String filename = "reviews.json";
        String data = RestResponse.getJSONDataMockup(this.getActivity(), filename);

        ArrayList<Review> reviewList = RestResponse.getReviewListParser(data);

        reviewListDataStorage = reviewList;

        ArrayList<String> userIdList = new ArrayList<>();
        for (Review review : reviewList) {
            userIdList.add(review.getUserId());
        }
        Bundle bundle = RestResponse.createBundleUser(userIdList);
        Log.d(TAG, "hey " + bundle.getString("requestType"));
        getLoaderManager().restartLoader(RestLoader.HTTPVerb.POST, bundle, this).forceLoad();

        initView(reviewList, coffeeMachineId);
    }









    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        //USEFUL
        Log.e(TAG, "on item click " + position + "id - " + view.getId());
        if(view.getId() == R.id.linearLayout) {
            Log.e(TAG, " id: linearLayout");
        }
        //more review click
        if(view.getId() == R.id.loadOlderReviewLayoutId) {
            try{
                Review firstReview = reviewListDataStorage.get(0);
                String latestReviewId = firstReview.getId();
                DateTime dateTime = new DateTime(firstReview.getTimestamp());
                long fromTimestamp = TimestampHandler.getOneWeekAgoTimestamp(dateTime);
                Bundle bundle = RestResponse.createBundleMoreReview(coffeeMachineId, latestReviewId, fromTimestamp);
                Log.d(TAG, "hey " + bundle.getString("requestType"));
                getLoaderManager().restartLoader(RestLoader.HTTPVerb.POST, bundle, this).forceLoad();
            } catch (Exception e) {
                Log.e(TAG, "failed to load more review");
            }
        }
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        if(view.getId() == R.id.loadOlderReviewLayoutId) {
            return true;
        }

        Review reviewObj = (Review) adapterView.getItemAtPosition(position);
        final View mainItemView = view.findViewById(R.id.mainItemViewId);

        //TODO CHECK THIS STATEMENT
        if (mainItemView != null &&
                mainItemView.getVisibility() == View.VISIBLE &&
                coffeeAppController.checkIsMe(reviewObj.getUserId())) {
            try {
                final View extraMenuItemView = view.findViewById(R.id.extraMenuItemViewId);
                ReviewListAdapter adapter = ((ReviewListAdapter) adapterView.getAdapter());

                mainItemView.setVisibility(View.GONE);
                extraMenuItemView.setVisibility(View.VISIBLE);
                setReviewListHeaderBackgroundLabel(extraMenuItemView, false);

                int prevSelectedItemPosition = adapter
                        .getSelectedItemIndex();

                //DESELECT prev item
                if (prevSelectedItemPosition != Common.ITEM_NOT_SELECTED) {
                    int index = prevSelectedItemPosition - adapterView.getFirstVisiblePosition();
                    View v = adapterView.getChildAt(index);
                    v.findViewById(R.id.mainItemViewId).setVisibility(View.VISIBLE);
                    v.findViewById(R.id.extraMenuItemViewId).setVisibility(View.GONE);
                }

                adapter.setSelectedItemIndex(position);
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }*/


}

