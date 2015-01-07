package com.application.material.takeacoffee.app.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.*;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.application.material.takeacoffee.app.AddReviewActivity;
import com.application.material.takeacoffee.app.CoffeeMachineActivity;
import com.application.material.takeacoffee.app.EditReviewActivity;
import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.adapters.ReviewListAdapter;
import com.application.material.takeacoffee.app.application.DataApplication;
import com.application.material.takeacoffee.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.takeacoffee.app.fragments.interfaces.OnLoadViewHandlerInterface;
import com.application.material.takeacoffee.app.fragments.interfaces.SetActionBarInterface;
import com.application.material.takeacoffee.app.models.*;
import com.application.material.takeacoffee.app.services.HttpIntentService;
import com.application.material.takeacoffee.app.singletons.BusSingleton;
import com.neopixl.pixlui.components.textview.TextView;
import com.squareup.otto.Subscribe;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by davide on 08/04/14.
 */
public class ReviewListFragment extends Fragment
        implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener,
        DialogInterface.OnClickListener, View.OnClickListener,
        OnRefreshListener, AbsListView.OnScrollListener {
    private static final String TAG = "ReviewListFragment";
    public static final String REVIEW_LIST_FRAG_TAG = "REVIEW_LIST_FRAG_TAG";
    private static FragmentActivity mainActivityRef = null;
    public static String EDIT_REVIEW_STRING = "Edit";

    private View reviewListView;
    private String coffeeMachineId;
    private Bundle bundle;
    private CoffeeMachineStatus coffeeMachineStatus;
    private String meUserId;

    @InjectView(R.id.reviewsContainerListViewId) ListView listView;
    @InjectView(R.id.addReviewFabId) View addReviewFabButton;
    @InjectView(R.id.goodReviewPercentageTextId) TextView goodReviewPercentageView;
    @InjectView(R.id.statusCoffeeIconId) ImageView statusCoffeeIcon;
    @InjectView(R.id.swipeRefreshLayoutId) SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.statusHeaderLayoutId) View statusHeaderLayout;
    @InjectView(R.id.periodTextId) View statusPeriodView;
    @InjectView(android.R.id.empty) View emptyView;


    private View moreReviewLoaderView;
//    private View emptyView;
    private ArrayList<Review> reviewList;
    private ArrayList<User> userList;
    private CoffeeMachine coffeeMachine;
    private boolean hasMoreReviews;
    private View oldReviewView;
    private boolean isAllowToEdit = false;
    private View headerView;
    private boolean isMoreReviewRequest = false;
    private DataApplication dataApplication;
    private boolean isRefreshAction = false;
    private int REVIEW_MAX_LINES = 5; //max line number of review
    private int REVIEW_MIN_LINES = 2; //max line number of review

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
        dataApplication = ((DataApplication) mainActivityRef.getApplication());
        meUserId = dataApplication.getUserId();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        reviewListView = inflater.inflate(R.layout.fragment_review_list, container, false);
        ButterKnife.inject(this, reviewListView);
//        emptyView = inflater.inflate(R.layout.empty_data_status_layout, listView, false);
        headerView = inflater.inflate(R.layout.header_listview_template, listView, false);
        oldReviewView = headerView.findViewById(R.id.oldReviewTemplateId);
        moreReviewLoaderView = headerView.findViewById(R.id.moreReviewTemplateId);
        setHasOptionsMenu(true);

        try {
            bundle = getArguments();
            coffeeMachine = bundle.getParcelable(CoffeeMachine.COFFEE_MACHINE_OBJ_KEY);
            coffeeMachineId = coffeeMachine.getId();
        //        reviewStatus = ReviewStatus.ReviewStatusEnum.valueOf(bundle
        //                .getString(ReviewStatus.REVIEW_STATUS_KEY));

        } catch (Exception e) {
            Log.e(TAG, "" + e.getMessage());
        }

        if(savedInstance != null) {
            restoreData();
            initView();
            return reviewListView;
        }

        //TODO mmmmmm
        if(reviewList != null) {
            initView();
            return reviewListView;
        }

        initOnLoadView();
        return reviewListView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
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

    private void initOnLoadView() {
        ((OnLoadViewHandlerInterface) mainActivityRef).initOnLoadView();
        Log.w(TAG, "call initLoader getCoffeeMachineStatus");

        //get timestamp
        long timestamp = new DateTime().getMillis();
        HttpIntentService.coffeeMachineStatusRequest(mainActivityRef.getApplicationContext(),
                coffeeMachineId, timestamp);

        HttpIntentService.reviewListRequest(this.getActivity(),
                coffeeMachineId, timestamp);

        //TODO REFACTORING
/*        long fromTimestamp = bundle.getLong(Common.FROM_TIMESTAMP_KEY);
        long toTimestamp = bundle.getLong(Common.TO_TIMESTAMP_KEY);
        Bundle params = RestResponse.createBundleReview(coffeeMachineId, fromTimestamp, toTimestamp);
*/
    }

    private void restoreData() {
        try {
            userList = dataApplication.restoreUserList();
            ReviewDataContainer reviewDataContainer = dataApplication.restoreReviewDataContainer();
            if(reviewDataContainer != null) {
                reviewList = reviewDataContainer.getReviewList();
                hasMoreReviews = reviewDataContainer.getHasMoreReviews();
            }
            coffeeMachineStatus = dataApplication.restoreCoffeeMachineStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshView() {
        ((OnLoadViewHandlerInterface) mainActivityRef).hideOnLoadView();

        if(coffeeMachineStatus != null) {
            goodReviewPercentageView.setText(coffeeMachineStatus.getGoodReviewPercentage() + " %");
//            statusCoffeeIcon.setImageDrawable();
        }
        //action bar
        setActionBarData();

        if (reviewList == null) {
            Log.w(TAG, "empty review list");
            reviewList = new ArrayList<Review>(); //empty review list
        }

        setHasMoreReviewView();
        Collections.reverse(reviewList);
        getAdapterWrapper().setReviewList(reviewList);
//        listView.setVisibility(reviewList.size() == 0 ? View.GONE : View.VISIBLE);
        //retrieve user if are not null
        if(userList != null) {
            getAdapterWrapper().setUserList(userList);
            getAdapterWrapper().notifyDataSetChanged();
        }

        swipeRefreshLayout.setOnRefreshListener(this);
    }

    public void initView() {
        ((OnLoadViewHandlerInterface) mainActivityRef).hideOnLoadView();
        boolean isReviewListEmpty = false;
        if(coffeeMachineStatus != null) {
            goodReviewPercentageView.setText(coffeeMachineStatus.getGoodReviewPercentage() + " %");
//            statusCoffeeIcon.setImageDrawable();
        }

        setActionBarData();

        if (reviewList == null) {
            Log.w(TAG, "empty review list");
            reviewList = new ArrayList<Review>();// empty review list
        }
        isReviewListEmpty = reviewList.size() == 0;


//        leftArrowIcon.setOnClickListener(this);
        Collections.reverse(reviewList);
        ReviewListAdapter reviewListenerAdapter = new ReviewListAdapter(mainActivityRef,
                R.layout.review_template, reviewList, coffeeMachineId);

        statusHeaderLayout.setVisibility(! isReviewListEmpty ? View.VISIBLE : View.GONE);

        listView.setEmptyView(emptyView);
        listView.addHeaderView(headerView);
        setHasMoreReviewView();

        listView.setAdapter(reviewListenerAdapter);

        listView.setOnItemLongClickListener(! isReviewListEmpty ? this : null);
        listView.setOnItemClickListener(! isReviewListEmpty ? this : null);
        listView.setOnScrollListener(! isReviewListEmpty ? this : null);
        swipeRefreshLayout.setOnRefreshListener(! isReviewListEmpty ? this : null);
        swipeRefreshLayout.setProgressViewOffset(true, 100, 200); //TODO replace please with dimen size
        addReviewFabButton.setOnClickListener(this);

        //STATUS header
        statusHeaderLayout.setOnClickListener(this);
        //retrieve user if are not null
        if(userList != null) {
            getAdapterWrapper().setUserList(userList);
            getAdapterWrapper().notifyDataSetChanged();
        }
    }

    private void setActionBarData() {
        Bundle actionbarBundle = new Bundle();
        actionbarBundle.putString(CoffeeMachineStatus.COFFEE_MACHINE_STATUS_STRING_KEY,
                coffeeMachine.getAddress());
        actionbarBundle.putString(CoffeeMachine.COFFEE_MACHINE_STRING_KEY,
                coffeeMachine.getName());
        ((SetActionBarInterface) mainActivityRef)
                .setActionBarCustomViewById(R.id.customActionBarReviewListLayoutId,
                        actionbarBundle);
        ((SetActionBarInterface) mainActivityRef)
                .setCustomNavigation(ReviewListFragment.class);
    }

    private ReviewListAdapter getAdapterWrapper() {
        return listView.getAdapter().getClass() == ReviewListAdapter.class ?
                ((ReviewListAdapter) listView.getAdapter()) :
                ((ReviewListAdapter) ((HeaderViewListAdapter) listView.getAdapter())
                    .getWrappedAdapter());
    }

    public ListView getListView() {
        return listView;
    }

    private void setHasMoreReviewView() {
        if(hasMoreReviews) {
            oldReviewView.setVisibility(View.GONE);
            moreReviewLoaderView.setVisibility(View.VISIBLE);
            return;
        }
        moreReviewLoaderView.setVisibility(View.GONE);
        oldReviewView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        //get more review
        switch (view.getId()) {
            case R.id.headerTemplateId :
                if(view.findViewById(R.id.moreReviewTemplateId).getVisibility() == View.VISIBLE) {
                    Log.e(TAG, "get previous review");
                    swipeRefreshLayout.setRefreshing(true);

                    //request review
                    String fromReviewId = null;
                    HttpIntentService.moreReviewListRequest(this.getActivity(),
                            coffeeMachineId, fromReviewId);
                    isMoreReviewRequest = true;

                    //remove item selected
                    ((SetActionBarInterface) mainActivityRef).updateSelectedItem(this,
                            listView, null, -1);
                    return;
                }

                if(view.findViewById(R.id.oldReviewTemplateId).getVisibility() == View.VISIBLE) {
                    Log.e(TAG, "get old review");
                    Toast.makeText(mainActivityRef, "load old review", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.reviewLayoutId:
                EllipsizedComment comment = (EllipsizedComment) view.getTag();

                if(! comment.isEllipsized()) {
                    (view.findViewById(R.id.expandDescriptionTextId))
                            .setVisibility(View.GONE);
                    return;
                }

                int maxLines = comment.isHidden() ? REVIEW_MAX_LINES : REVIEW_MIN_LINES;
                ((TextView) view.findViewById(R.id.reviewCommentTextId))
                        .setMaxLines(maxLines);
                (view.findViewById(R.id.expandDescriptionTextId))
                        .setVisibility(comment.isHidden() ? View.GONE : View.VISIBLE);

                ((TextView) view.findViewById(R.id.reviewCommentTextId))
                        .setText(comment.isHidden() ?
                                comment.getPlainComment() :
                                comment.getEllipsizedComment());
                //UPDATE
                comment.setHidden(! comment.isHidden());
                view.setTag(comment);

                break;
        }



    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        if(view.getId() == R.id.headerTemplateId) {
            Log.e(TAG, "moreReviewTemplateId");
            return true;
        }
        try {
            Review review = (Review) adapterView.getItemAtPosition(position);
            User user = getAdapterWrapper().getUserByUserId(review.getUserId());

            //clear prev bundle
            bundle.putParcelable(Review.REVIEW_OBJ_KEY, null);
            bundle.putParcelable(User.USER_OBJ_KEY, null);
            isAllowToEdit = false;
            //set
            if (user != null &&
                    user.getId().equals(meUserId)) {
                bundle.putParcelable(Review.REVIEW_OBJ_KEY, review);
                bundle.putParcelable(User.USER_OBJ_KEY, user);
                isAllowToEdit = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //disable onItemLongClick (only one edit per time)
        ((SetActionBarInterface) mainActivityRef)
                .updateSelectedItem(this, listView, view, position);
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        Log.e(TAG, "create option menu");
        if(((SetActionBarInterface) mainActivityRef).isItemSelected()) {
            menuInflater.inflate(isAllowToEdit ? R.menu.edit_review : R.menu.clipboard_review,
                    menu);
            return;
        }
        menuInflater.inflate(R.menu.review_list, menu);
//        listView.setOnItemLongClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_coffee_machine_position:
                    Toast.makeText(mainActivityRef, "get machine pos", Toast.LENGTH_SHORT).show();
                ((OnChangeFragmentWrapperInterface) mainActivityRef)
                        .changeFragment(new MapFragment(),
                                bundle, MapFragment.MAP_FRAG_TAG);
                break;

            case R.id.action_edit_icon:
                Toast.makeText(mainActivityRef, "change", Toast.LENGTH_SHORT).show();
                ((OnChangeFragmentWrapperInterface) mainActivityRef)
                        .startActivityWrapper(EditReviewActivity.class,
                                CoffeeMachineActivity.ACTION_EDIT_REVIEW, bundle);
                //deselect Item
                ((SetActionBarInterface) mainActivityRef)
                        .updateSelectedItem(this, listView, null, -1);
                break;
            case R.id.action_delete:
                Toast.makeText(mainActivityRef, "change", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(mainActivityRef)
                        .setMessage("Sure to delete this review?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Review review = (Review) bundle.get(Review.REVIEW_OBJ_KEY);
                                HttpIntentService.deleteReviewRequest(mainActivityRef, review.getId());
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                Dialog dialog = builder.create();
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.show();


                //deselect Item
                ((SetActionBarInterface) mainActivityRef)
                        .updateSelectedItem(this, listView, null, -1);
                break;

        }
        return true;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Log.e(TAG, "dismiss");
    }

    @Override
    public void onClick(View v) {
        //dialog button
        switch (v.getId()) {
            case R.id.addReviewFabId:
                ((OnChangeFragmentWrapperInterface) mainActivityRef)
                        .startActivityWrapper(AddReviewActivity.class,
                                CoffeeMachineActivity.ACTION_ADD_REVIEW, bundle);
                break;
//            case R.id.leftArrowIconId:
//                Toast.makeText(mainActivityRef, "got statistics on machine",
//                        Toast.LENGTH_SHORT).show();
//                ((OnChangeFragmentWrapperInterface) mainActivityRef)
//                        .startActivityWrapper(AddReviewActivity.class,
//                                CoffeeMachineActivity.ACTION_ADD_REVIEW, null);
//                break;

        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setOnRefreshListener(null);

        Toast.makeText(mainActivityRef, "refresh", Toast.LENGTH_SHORT).show();
        isRefreshAction = true;
        //clear data on application
        int [] dataLabels = {0, 1, 2};
        dataApplication.clearData(dataLabels);

        long timestamp = -1;
        HttpIntentService.coffeeMachineStatusRequest(this.getActivity(),
                coffeeMachineId, timestamp);
        //request review
//        long timestamp = 123456; //TODO replace with joda time
        HttpIntentService.reviewListRequest(this.getActivity(),
                coffeeMachineId, timestamp);

        isMoreReviewRequest = false;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        try {
            if(firstVisibleItem == 0) {
//                Animation jumpUp = AnimationUtils.loadAnimation(mainActivityRef, R.anim.slide_up);
//                Animation jumpDown =AnimationUtils.loadAnimation(mainActivityRef, R.anim.slide_down);
                if(view.getChildAt(firstVisibleItem) == null) {
                    return;
                }
                int viewHeight = view.getChildAt(firstVisibleItem).getHeight();
                int offset = viewHeight * 30 / 100;

                //hide and show element
                if(Math.abs(view.getChildAt(firstVisibleItem).getY()) > offset) {
                    statusHeaderLayout.setVisibility(View.GONE);
                    return;
                }
                statusHeaderLayout.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private ArrayList<String> getUserIdListFromReviewList(ArrayList<Review> reviewList) {
        if(reviewList == null ||
                reviewList.size() == 0) {
            return null;
        }
        ArrayList<String> userIdList = new ArrayList<String>();
        for(Review review : reviewList) {
            userIdList.add(review.getUserId());
        }

        return userIdList;
    }

    @Subscribe
    public void onNetworkRespose(CoffeeMachineStatus response){
        Log.d(TAG, "Response  COFFEE_MACHINE_STATUS");
        ((OnLoadViewHandlerInterface) mainActivityRef).hideOnLoadView();
        if(response == null) {
            return;
        }
        coffeeMachineStatus = response;
        dataApplication.saveCoffeeMachineStatus(coffeeMachineStatus);

        ((TextView) statusPeriodView).setText("01.12 - today");
        goodReviewPercentageView.setText(coffeeMachineStatus.getGoodReviewPercentage() + " %");
    }

    @Subscribe
    public void onNetworkRespose(ReviewDataContainer reviewDataContainer){
        Log.d(TAG, "get response from bus - REVIEW_REQUEST");
        ((OnLoadViewHandlerInterface) mainActivityRef).hideOnLoadView();

        if(reviewDataContainer == null ||
                reviewDataContainer.getReviewList() == null) {
            Log.e(TAG, "empty review list data");
            //TODO handle adapter with empty data
            return;
        }
        dataApplication.saveReviewDataContainer(reviewDataContainer);

        reviewList = reviewDataContainer.getReviewList();
        hasMoreReviews = reviewDataContainer.getHasMoreReviews();

        if(reviewList.size() != 0) {
            ArrayList<String> userIdList = getUserIdListFromReviewList(reviewList);
            HttpIntentService.userListRequest(mainActivityRef, userIdList);
        }

        //notify changes
//        setHasMoreReviewView();
//        getAdapterWrapper().setReviewList(reviewList);
//        getAdapterWrapper().notifyDataSetChanged();

        //or init view
        if(isMoreReviewRequest) {
            isMoreReviewRequest = false;
            swipeRefreshLayout.setRefreshing(false);

            setHasMoreReviewView();
            Collections.reverse(reviewList);
            getAdapterWrapper().setPrevReview(reviewList);
            return;
        }

//        if() //TODO add refreshstatus -
        if(isRefreshAction) {
            refreshView();
            isRefreshAction = false;
            return;
        }

        initView();
    }

    @Subscribe
    public void onNetworkRespose(ArrayList<User> userList) {
        Log.d(TAG, "get response from bus - USER_REQUEST");
        ((OnLoadViewHandlerInterface) mainActivityRef).hideOnLoadView();

        if(userList == null) {
            //TODO handle adapter with empty data
            return;
        }
        dataApplication.saveUserList(userList);

        getAdapterWrapper().setUserList(userList);
        getAdapterWrapper().notifyDataSetChanged();
//                    swipeRefreshLayout.setRefreshing(false);
    }

    @Subscribe
    public void onNetworkRespose(Review.DeletedResponse deleteReviewResponse) {
        Log.d(TAG, "get response from bus - DELETE_REVIEW_REQ");
        ((OnLoadViewHandlerInterface) mainActivityRef).hideOnLoadView();

        if(deleteReviewResponse == null) {
            //TODO handle adapter with empty data
            Log.e(TAG, "error - not able to delete review");
            return;
        }

        Review review = (Review) bundle.get(Review.REVIEW_OBJ_KEY);
        getAdapterWrapper().deleteReview(review.getId());
        getAdapterWrapper().notifyDataSetChanged();
        return;
    }


    @Subscribe
    public void onHandlingError(Throwable cause) {
        String message = cause.getMessage();
        int code = Integer.parseInt(cause.getCause().getMessage());

        Log.e(TAG, "error - " + message + code);
    }


/*    @Override
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
        //TODO REFACTORIZE IT - add a new class with an interface
        //TODO FIX IT
        final int REVIEW_REQ = 0; //REVIEW_REQUEST.ordinal();
        final int MORE_REVIEW_REQ = 1; //MORE_REVIEW_REQUEST.ordinal();
        final int USER_REQ = 6; //USER_REQUEST.ordinal();
        final int COFFEE_MACHINE_STATUS_REQ = 5; //.ordinal();
        Log.i(TAG, "request id - " + loader.getId());
        try {
            switch (loader.getId()) {
                case COFFEE_MACHINE_STATUS_REQ:
                    Log.i(TAG, "STATUS_REQ");

                    ((OnLoadViewHandlerInterface) mainActivityRef).hideOnLoadView();
//                    reviewStatus = (ReviewStatus) restResponse.getParsedData();

                    //MOCKUP
                    String filename = "review_status.json";
                    String data = RetrofitLoader.getJSONDataMockup(this.getActivity(), filename);
                    coffeeMachineStatus = JSONParserToObject.coffeeMachineStatusParser(data);

                    //real json data
//                    coffeeMachineStatus = (CoffeeMachineStatus) restResponse.getParsedData();

                    if(getLoaderManager().getLoader(REVIEW_REQUEST.ordinal()) != null) {
                        getLoaderManager().restartLoader(REVIEW_REQUEST.ordinal(), null, this).forceLoad();
                        goodReviewPercentageView.setText(coffeeMachineStatus.getGoodReviewPercentage() + " %");
                        return;
                    }

                    getLoaderManager().initLoader(REVIEW_REQUEST.ordinal(), null, this)
                            .forceLoad();
                    break;
                case REVIEW_REQ:
                    Log.i(TAG, "REVIEW_REQ");

                    ((OnLoadViewHandlerInterface) mainActivityRef).hideOnLoadView();

                    filename = "reviews.json";
                    data = RetrofitLoader.getJSONDataMockup(this.getActivity(), filename);
                    ReviewDataContainer reviewDataContainer = JSONParserToObject.getReviewListParser(data);
                    //real json data
//                    reviewList = (ArrayList<Review>) restResponse.getParsedData();

                    reviewList = reviewDataContainer.getReviewList();
                    hasMoreReviews = reviewDataContainer.getHasMoreReviews();

                    Bundle params = new Bundle();
                    if(getLoaderManager().getLoader(USER_REQUEST.ordinal()) != null) {
                        setHasMoreReviewView();
                        getLoaderManager().restartLoader(USER_REQUEST.ordinal(), params, this).forceLoad();

                        getAdapterWrapper().setReviewList(reviewList);
                        getAdapterWrapper().notifyDataSetChanged();
                        return;
                    }

                    getLoaderManager().initLoader(USER_REQUEST.ordinal(), params, this).forceLoad();
                    initView();
                    break;
                case MORE_REVIEW_REQ:
                    Log.i(TAG, "MORE_REVIEW_REQ");
                    swipeRefreshLayout.setRefreshing(false);

                    filename = "prev_reviews.json";
                    data = RetrofitLoader.getJSONDataMockup(this.getActivity(), filename);
                    reviewDataContainer = JSONParserToObject.getReviewListParser(data);
                    prevReviewList = reviewDataContainer.getReviewList();
                    hasMoreReviews = reviewDataContainer.getHasMoreReviews();
                    setHasMoreReviewView();
//                    prevReviewList = (ArrayList<Review>) restResponse.getParsedData();

                    //TODO add review to adapter
                    getAdapterWrapper().setPrevReview(prevReviewList);
                    //create new loader for user
                    params = new Bundle();
                    if(getLoaderManager().getLoader(USER_REQUEST.ordinal()) != null) {
                        getLoaderManager().restartLoader(USER_REQUEST.ordinal(), params, this).forceLoad();
                        getAdapterWrapper().notifyDataSetChanged();
                        return;
                    }

                    getLoaderManager().initLoader(USER_REQUEST.ordinal(), params, this).forceLoad();
                    break;
                case USER_REQ:
                    Log.i(TAG, "USER_REQ");

                    //TODO TEST
                    filename = "user.json";
                    data = RetrofitLoader.getJSONDataMockup(this.getActivity(), filename);
                    userList = JSONParserToObject.getUserListParser(data);

//                    ArrayList<User> userList = (ArrayList<User>) restResponse.getParsedData();
                    getAdapterWrapper().setUserList(userList);
                    getAdapterWrapper().notifyDataSetChanged();
//                    swipeRefreshLayout.setRefreshing(false);
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(Loader<RestResponse> restResponseLoader) {
        //called on release resources - on back press and when loader is deleted/abandoned
        Log.e(TAG, "reset loader");
    }

*/

}

