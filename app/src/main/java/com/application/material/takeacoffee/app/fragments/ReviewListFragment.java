package com.application.material.takeacoffee.app.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.*;
import android.view.animation.Animation;
import android.widget.*;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import com.application.material.takeacoffee.app.AddReviewActivity;
import com.application.material.takeacoffee.app.BuildConfig;
import com.application.material.takeacoffee.app.ReviewListActivity;
import com.application.material.takeacoffee.app.EditReviewActivity;
import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.adapters.ReviewListAdapter;
import com.application.material.takeacoffee.app.application.CoffeePlacesApplication;
import com.application.material.takeacoffee.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.takeacoffee.app.fragments.interfaces.OnLoadViewHandlerInterface;
import com.application.material.takeacoffee.app.fragments.interfaces.SetActionBarInterface;
import com.application.material.takeacoffee.app.models.*;
import com.application.material.takeacoffee.app.services.HttpIntentService;
import com.application.material.takeacoffee.app.singletons.BusSingleton;


import org.joda.time.DateTime;

import java.util.*;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.*;


/**
 * Created by davide on 08/04/14.
 */
public class ReviewListFragment extends Fragment
        implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener,
        DialogInterface.OnClickListener, View.OnClickListener,
        OnRefreshListener, AbsListView.OnScrollListener {
    private static final String TAG = "ReviewListFragment";
    public static final String REVIEW_LIST_FRAG_TAG = "REVIEW_LIST_FRAG_TAG";
    private static AppCompatActivity mainActivityRef = null;
    public static String EDIT_REVIEW_STRING = "Edit";

    private View reviewListView;
    private String coffeeMachineId;
    private Bundle bundle;
    private CoffeeMachineStatus coffeeMachineStatus;
    private String meUserId;

    @Bind(R.id.reviewsContainerListViewId) ListView listView;
//    @Bind(R.id.addReviewFabId) View addReviewFabButton;
    public View addReviewFabButton;
//    @Bind(R.id.goodReviewPercentageTextId) TextView goodReviewPercentageView;
//    @Bind(R.id.statusCoffeeIconId) ImageView statusCoffeeIcon;
    @Bind(R.id.swipeRefreshLayoutId) SwipeRefreshLayout swipeRefreshLayout;
//    @Bind(R.id.statusHeaderLayoutId) View statusHeaderLayout;
//    @Bind(R.id.periodTextId) View statusPeriodView;
//    @Bind(android.R.id.empty) View emptyView;


    private View moreReviewLoaderView;
    private ArrayList<Review> reviewList;
    private ArrayList<User> userList;
    private CoffeeMachine coffeeMachine;
    private boolean hasMoreReviews;
    private View oldReviewView;
    private boolean isAllowToEdit = false;
    private View headerView;
    private boolean isMoreReviewRequest = false;
    private CoffeePlacesApplication coffeePlacesApplication;
    private boolean isRefreshAction = false;
    private int REVIEW_MAX_LINES = 5; //max line number of review
    private int REVIEW_MIN_LINES = 2; //max line number of review

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof OnLoadViewHandlerInterface)) {
            throw new ClassCastException(context.toString()
                    + " must implement OnLoadViewHandlerInterface");
        }
        if (!(context instanceof OnChangeFragmentWrapperInterface)) {
            throw new ClassCastException(context.toString()
                    + " must implement OnLoadViewHandlerInterface");
        }
        mainActivityRef =  (ReviewListActivity) context;
        coffeePlacesApplication = ((CoffeePlacesApplication) mainActivityRef.getApplication());
        meUserId = coffeePlacesApplication.getUserId();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        reviewListView = inflater.inflate(R.layout.fragment_review_list, container, false);
        ButterKnife.bind(this, reviewListView);
        initView();
        return reviewListView;
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

    /**
     * init view to handle review data
     */
    public void initView() {
        if (BuildConfig.DEBUG) {
            reviewList = getReviewListTest();
        }
        addReviewFabButton = mainActivityRef.findViewById(R.id.addReviewFabId);
        addReviewFabButton.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        initListView();
//        if (userList != null) {
//            getAdapterWrapper().setUserList(userList);
//            getAdapterWrapper().notifyDataSetChanged();
//        }
    }

    /**
     * init list view
     */
    private void initListView() {
        //        Collections.reverse(reviewList);
        ReviewListAdapter reviewListenerAdapter = new ReviewListAdapter(mainActivityRef,
                this, R.layout.review_template, reviewList, coffeeMachineId);

        //TODO replace with recyclerView :)
//        listView.setEmptyView(emptyView);
        listView.setAdapter(reviewListenerAdapter);

        listView.setOnItemLongClickListener(this);
        listView.setOnItemClickListener(this);
        listView.setOnScrollListener(this);
        if (SDK_INT >= LOLLIPOP) {
            listView.setNestedScrollingEnabled(true);
        }
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
    }

    private void restoreData() {
        try {
            userList = coffeePlacesApplication.restoreUserList();
            ReviewDataContainer reviewDataContainer = coffeePlacesApplication.restoreReviewDataContainer();
            if(reviewDataContainer != null) {
                reviewList = reviewDataContainer.getReviewList();
                hasMoreReviews = reviewDataContainer.getHasMoreReviews();
            }
            coffeeMachineStatus = coffeePlacesApplication.restoreCoffeeMachineStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshView() {
        ((OnLoadViewHandlerInterface) mainActivityRef).hideOnLoadView();

        if(coffeeMachineStatus != null) {
//            goodReviewPercentageView.setText(coffeeMachineStatus.getGoodReviewPercentage() + " %");
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
        if(userList != null) {
            getAdapterWrapper().setUserList(userList);
            getAdapterWrapper().notifyDataSetChanged();
        }

        swipeRefreshLayout.setOnRefreshListener(this);
    }

    public View preInitViewOld(LayoutInflater inflater, Bundle savedInstance) {
                headerView = inflater.inflate(R.layout.header_listview_template, listView, false);
        oldReviewView = headerView.findViewById(R.id.oldReviewTemplateId);
        moreReviewLoaderView = headerView.findViewById(R.id.moreReviewTemplateId);
        setHasOptionsMenu(true);
        try {
            bundle = getArguments();
            coffeeMachine = bundle.getParcelable(CoffeeMachine.COFFEE_MACHINE_OBJ_KEY);
            coffeeMachineId = coffeeMachine.getId();

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
            initViewOld();
            return reviewListView;
        }
        initOnLoadView();
        return null;
    }

    public void initViewOld() {
        ((OnLoadViewHandlerInterface) mainActivityRef).hideOnLoadView();
        boolean isReviewListEmpty = false;
        if(coffeeMachineStatus != null) {
//            goodReviewPercentageView.setText(coffeeMachineStatus.getGoodReviewPercentage() + " %");
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
                this, R.layout.review_template, reviewList, coffeeMachineId);

//        statusHeaderLayout.setVisibility(!isReviewListEmpty ? View.VISIBLE : View.GONE);

//        listView.setEmptyView(emptyView);
        listView.addHeaderView(headerView);
        setHasMoreReviewView();

        listView.setAdapter(reviewListenerAdapter);

        listView.setOnItemLongClickListener(!isReviewListEmpty ? this : null);
        listView.setOnItemClickListener(!isReviewListEmpty ? this : null);
        listView.setOnScrollListener(!isReviewListEmpty ? this : null);
        swipeRefreshLayout.setOnRefreshListener(!isReviewListEmpty ? this : null);
        swipeRefreshLayout.setProgressViewOffset(true, 100, 200); //TODO replace please with dimen size
        addReviewFabButton.setOnClickListener(this);

        //STATUS header
//        statusHeaderLayout.setOnClickListener(this);
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
                //MORE REVIEW
                if(view.findViewById(R.id.moreReviewTemplateId).getVisibility() == View.VISIBLE) {
                    Log.e(TAG, "get previous review");
                    swipeRefreshLayout.setRefreshing(true);

                    //request review
                    String fromReviewId = reviewList.size() == 0 ?
                            null :
                            reviewList.get(0).getId();

                    HttpIntentService.moreReviewListRequest(this.getActivity(),
                            coffeeMachineId, fromReviewId);
                    isMoreReviewRequest = true;

                    //remove item selected
                    ((SetActionBarInterface) mainActivityRef).updateSelectedItem(this,
                            listView, null, -1);
                    return;
                }

                //OLD REVIEW
                if(view.findViewById(R.id.oldReviewTemplateId).getVisibility() == View.VISIBLE) {
                    Log.e(TAG, "get old review");
                    Toast.makeText(mainActivityRef, "load old review", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.reviewLayoutId:
//                try {
//                    Review review = (Review) adapterView.getAdapter().getItem(position);
//                    boolean hasReviewPicture = review.getReviewPictureUrl() != null;
//                    EllipsizedComment comment = (EllipsizedComment) view.getTag();
//                    boolean isReviewHidden = comment.isHidden();
//                    final ViewFlipper viewFlipper = (ViewFlipper) view.findViewById(R.id.reviewIconImagesLayoutId);
//
//                    if(! comment.isEllipsized() &&
//                            ! hasReviewPicture) {
//                        //set review rating on tap
//                        flipView(viewFlipper);
//
//                        //UPDATE - TOGGLE
//                        comment.setHidden(! isReviewHidden);
//                        view.setTag(comment);
//
//                        (view.findViewById(R.id.expandDescriptionTextId))
//                                .setVisibility(View.GONE);
//                        return;
//                    }
//
//
//                    int maxLines = isReviewHidden ? REVIEW_MAX_LINES : REVIEW_MIN_LINES;
//                    ((TextView) view.findViewById(R.id.reviewCommentTextId))
//                            .setMaxLines(maxLines);
//                    (view.findViewById(R.id.expandDescriptionTextId))
//                            .setVisibility(isReviewHidden ? View.GONE : View.VISIBLE);
//
//                    ((TextView) view.findViewById(R.id.reviewCommentTextId))
//                            .setText(isReviewHidden ?
//                                    comment.getPlainComment() :
//                                    comment.getEllipsizedComment());
//
//                    //set reviewPicture
//                    if(hasReviewPicture) {
//                        ((ImageView) view.findViewById(R.id.reviewPictureImageViewId)).setImageBitmap(! isReviewHidden ?
//                                null :
//                                JSONParserToObject.getMockupPicture(mainActivityRef, review.getReviewPictureUrl()));
//                        (view.findViewById(R.id.reviewPictureImageViewId)).setVisibility(isReviewHidden ? View.VISIBLE : View.GONE);
//                    }
//
//                    //set review rating on tap
//                    flipView(viewFlipper);
//
//                    //UPDATE - TOGGLE
//                    comment.setHidden(! isReviewHidden);
//                    view.setTag(comment);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                break;
        }



    }

    private void flipView(final ViewFlipper viewFlipper) {
        viewFlipper.setOutAnimation(null);
        viewFlipper.setInAnimation(mainActivityRef, R.anim.shrink_to_middle);
        viewFlipper.startAnimation(viewFlipper.getInAnimation());
        viewFlipper.getInAnimation().setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                viewFlipper.setInAnimation(null);
                viewFlipper.setInAnimation(mainActivityRef, R.anim.grow_from_middle);
                viewFlipper.showNext();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

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

//        ((SetActionBarInterface) mainActivityRef)
//                .updateSelectedItem(this, listView, view, position);
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
                                ReviewListActivity.ACTION_EDIT_REVIEW, bundle);
                //deselect Item
//                ((SetActionBarInterface) mainActivityRef)
//                        .updateSelectedItem(this, listView, null, -1);
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
//                ((SetActionBarInterface) mainActivityRef)
//                        .updateSelectedItem(this, listView, null, -1);
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
                startActivity(new Intent(mainActivityRef, AddReviewActivity.class));

//                ((OnChangeFragmentWrapperInterface) mainActivityRef)
//                        .startActivityWrapper(AddReviewActivity.class,
//                                ReviewListActivity.ACTION_ADD_REVIEW, bundle);
                break;
            case R.id.reviewPictureImageViewId:
                Log.e(TAG, "expand pic view");
                ImageView reviewPictureView = new ImageView(mainActivityRef);
                reviewPictureView.setImageDrawable(((ImageView) v).getDrawable());
                AlertDialog.Builder builder = new AlertDialog.Builder(mainActivityRef).
                        setView(reviewPictureView).
                        setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                Dialog dialog = builder.create();
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.show();



                break;
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
        coffeePlacesApplication.clearData(dataLabels);

        long timestamp = -1;
        HttpIntentService.coffeeMachineStatusRequest(this.getActivity(),
                coffeeMachineId, timestamp);
        //request review
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
                if(view.getChildAt(firstVisibleItem) == null) {
                    return;
                }
                int viewHeight = view.getChildAt(firstVisibleItem).getHeight();
                int offset = viewHeight * 30 / 100;

                //hide and show element
                if(Math.abs(view.getChildAt(firstVisibleItem).getY()) > offset) {
//                    statusHeaderLayout.setVisibility(View.GONE);
                    return;
                }
//                statusHeaderLayout.setVisibility(View.VISIBLE);
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

//    @Subscribe
//    public void onNetworkRespose(CoffeeMachineStatus response){
//        Log.d(TAG, "Response  COFFEE_MACHINE_STATUS");
//        if(response == null) {
//            return;
//        }
//        coffeeMachineStatus = response;
//        coffeePlacesApplication.saveCoffeeMachineStatus(coffeeMachineStatus);
//
//        ((TextView) statusPeriodView).setText("01.12 - today");
//        goodReviewPercentageView.setText(coffeeMachineStatus.getGoodReviewPercentage() + " %");
//
////        ((OnLoadViewHandlerInterface) mainActivityRef).hideOnLoadView();
//    }
//
//    @Subscribe
//    public void onNetworkRespose(ReviewDataContainer reviewDataContainer){
//        Log.d(TAG, "get response from bus - REVIEW_REQUEST");
//        if(reviewDataContainer == null ||
//                reviewDataContainer.getReviewList() == null) {
//            Log.e(TAG, "empty review list data");
//            //TODO handle adapter with empty data
//            return;
//        }
//        coffeePlacesApplication.saveReviewDataContainer(reviewDataContainer);
//
//        reviewList = reviewDataContainer.getReviewList();
//        hasMoreReviews = reviewDataContainer.getHasMoreReviews();
//
//        if(reviewList.size() != 0) {
//            ArrayList<String> userIdList = getUserIdListFromReviewList(reviewList);
//            HttpIntentService.userListRequest(mainActivityRef, userIdList);
//        }
//
//        //or init view
//        if(isMoreReviewRequest) {
//            isMoreReviewRequest = false;
//            swipeRefreshLayout.setRefreshing(false);
//
//            setHasMoreReviewView();
//            Collections.reverse(reviewList);
//            getAdapterWrapper().setPrevReview(reviewList);
//            return;
//        }
//
////        if() //TODO add refreshstatus -
//        if(isRefreshAction) {
//            refreshView();
//            isRefreshAction = false;
//            return;
//        }
//
//        initView();
//
//        ((OnLoadViewHandlerInterface) mainActivityRef).hideOnLoadView(); //sync with review
//    }
//
//    @Subscribe
//    public void onNetworkRespose(ArrayList<User> userList) {
//        Log.d(TAG, "get response from bus - USER_REQUEST");
//        ((OnLoadViewHandlerInterface) mainActivityRef).hideOnLoadView();
//
//        if(userList == null) {
//            //TODO handle adapter with empty data
//            return;
//        }
//        coffeePlacesApplication.saveUserList(userList);
//
//        getAdapterWrapper().setUserList(userList);
//        getAdapterWrapper().notifyDataSetChanged();
////                    swipeRefreshLayout.setRefreshing(false);
//    }
//
//    @Subscribe
//    public void onNetworkRespose(Review.DeletedResponse deleteReviewResponse) {
//        Log.d(TAG, "get response from bus - DELETE_REVIEW_REQ");
//        ((OnLoadViewHandlerInterface) mainActivityRef).hideOnLoadView();
//
//        if(deleteReviewResponse == null) {
//            //TODO handle adapter with empty data
//            Log.e(TAG, "error - not able to delete review");
//            return;
//        }
//
//        Review review = (Review) bundle.get(Review.REVIEW_OBJ_KEY);
//        getAdapterWrapper().deleteReview(review.getId());
//        getAdapterWrapper().notifyDataSetChanged();
//        return;
//    }
//
//
//    @Subscribe
//    public void onHandlingError(Throwable cause) {
//        String message = cause.getMessage();
//        int code = Integer.parseInt(cause.getCause().getMessage());
//
//        Log.e(TAG, "error - " + message + code);
//    }
}

