package com.application.material.takeacoffee.app.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.*;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.application.material.takeacoffee.app.CoffeeMachineActivity;
import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.takeacoffee.app.fragments.interfaces.OnLoadViewHandlerInterface;
import com.application.material.takeacoffee.app.loaders.RestResponse;
import com.application.material.takeacoffee.app.loaders.RetrofitLoader;
import com.application.material.takeacoffee.app.models.CoffeeMachine;
import static com.application.material.takeacoffee.app.models.Review.ReviewStatus;

import com.application.material.takeacoffee.app.models.CoffeeMachineStatus;
import com.application.material.takeacoffee.app.parsers.ParserToJavaObject;

import static com.application.material.takeacoffee.app.loaders.RetrofitLoader.HTTPActionRequestEnum.*;

/**
 * Created by davide on 07/11/14.
 */
public class DashboardReviewFragment extends Fragment implements
        View.OnClickListener, LoaderManager.LoaderCallbacks<RestResponse>,
        DialogInterface.OnClickListener {
    private static final String TAG = "DashboardReviewFragment";
    public static final String DASHBOARD_REVIEW_FRAG_TAG = "DASHBOARD_REVIEW_FRAG_TAG";
    private CoffeeMachineActivity mainActivityRef;
    private View dashboardReviewView;
    private boolean hasAtLeastOneReview = false;
    private Bundle bundle;
    @InjectView(R.id.coffeeMachineStatusIconId) View coffeeMachineStatusIcon;
    @InjectView(R.id.coffeeMachineStatusTextId) View coffeeMachineStatusTextView;
    @InjectView(R.id.coffeeMachineWeeklyReviewTextId) View coffeeMachineWeeklyReviewTextView;
    @InjectView(R.id.addReviewFabId) View addReviewButton;
//    @InjectView(R.id.coffeeMachineNameHeaderId) View coffeeMachineNameHeaderView;
//    @InjectView(R.id.coffeeMachineLocationHeaderId) View coffeeMachineLocationHeaderView;
//    @InjectView(R.id.addReviewButtonId) View addReviewButton;

    private String coffeeMachineId;
    private CoffeeMachine coffeeMachine;
    private View addReviewDialogTemplate;


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

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        //get all bundle
        bundle = getArguments();
        coffeeMachine = bundle.getParcelable(CoffeeMachine.COFFEE_MACHINE_OBJ_KEY);
        //TODO redundant
        coffeeMachineId = coffeeMachine.getId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        dashboardReviewView = getActivity().getLayoutInflater().inflate(R.layout.fragment_dashboard_review, container, false);
        ButterKnife.inject(this, dashboardReviewView);
        setHasOptionsMenu(true);
        initOnLoadView();
        return dashboardReviewView;
    }

    private void initOnLoadView() {
        //initOnLoadView
        mainActivityRef.initOnLoadView();

       getLoaderManager().initLoader(GET_COFFEE_MACHINE_STATUS.ordinal(), null, this)
                .forceLoad();

        //after loading smthing if u need
//        initView(reviewStatus);
    }

    private void initView(CoffeeMachineStatus reviewStatus) {
        mainActivityRef.hideOnLoadView();
        Log.e(TAG, reviewStatus.toString());
        //set action bar view
        mainActivityRef.setActionBarCustomViewById(R.id.customActionBarCoffeeMachineLayoutId,
                coffeeMachine);
        mainActivityRef.setCustomNavigation(DashboardReviewFragment.class);

        //set coffee machine header
//        ((TextView) coffeeMachineNameHeaderView).setText(coffeeMachine.getName());
//        ((TextView) coffeeMachineLocationHeaderView).setText(coffeeMachine.getAddress());

        //set image status
//        int iconId = reviewStatus.getIconIdByStatus();
        int iconId = -1;
        ((ImageView) coffeeMachineStatusIcon)
                .setImageDrawable(getResources().getDrawable(iconId));

        //set week review name and cnt
//        ((TextView) coffeeMachineStatusTextView).setText(reviewStatus.getDescription());
//        ((TextView) coffeeMachineWeeklyReviewTextView).setText(Integer.toString(reviewStatus.getWeeklyReviewCnt()));

        //set listener if there is sm review
//        hasAtLeastOneReview = reviewStatus.getHasAtLeastOneReview();
        coffeeMachineStatusIcon.setOnClickListener(this);
//        ((FloatingActionButton) addReviewButton).setColor(getResources().getColor(R.color.material_amber));
//        ((FloatingActionButton) addReviewButton).initBackground();
        addReviewButton.setOnClickListener(this);
    }

    @Override
    public Loader<RestResponse> onCreateLoader(int ordinal, Bundle params) {
//        Uri action = Uri.parse(params.getString("action"));
//        String requestType = params.getString("requestType");

        //get action
        String action = RetrofitLoader.getActionByActionRequestEnum(ordinal);
        return new RetrofitLoader(mainActivityRef, action, params);
    }

    @Override
    public void onLoadFinished(Loader<RestResponse> restResponseLoader, RestResponse restResponse) {
        Log.e(TAG, "hey - finish load resources");

        try {
            CoffeeMachineStatus reviewStatus = (CoffeeMachineStatus) restResponse.getParsedData();
            initView(reviewStatus);
        } catch (Exception e) {
            ((OnLoadViewHandlerInterface) mainActivityRef).hideOnLoadView();
            String filename = "review_status.json";
            String data = RetrofitLoader.getJSONDataMockup(this.getActivity(), filename);
            CoffeeMachineStatus reviewStatus = ParserToJavaObject.coffeeMachineStatusParser(data);
            initView(reviewStatus);

//            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    @Override
    public void onClick(View v) {
        Log.e(TAG, "-" + v.getId());
        Dialog customDialog = null;
        switch (v.getId()) {
            case R.id.coffeeMachineStatusIconId:
                if(hasAtLeastOneReview) {
                    //get listview fragment
                    ((OnChangeFragmentWrapperInterface) mainActivityRef)
                            .changeFragment(new ReviewListFragment(), bundle, ReviewListFragment.REVIEW_LIST_FRAG_TAG);
                    break;
                }

                Log.e(TAG, "no review for this coffee machine!");
                Toast.makeText(mainActivityRef, "No review!", Toast.LENGTH_SHORT);
                break;
            case R.id.addReviewFabId:
                addReviewDialogTemplate = View.inflate(mainActivityRef,
                        R.layout.fragment_add_review, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(mainActivityRef)
                        .setView(addReviewDialogTemplate)
                        .setPositiveButton("Add", this);
                customDialog = builder.create();
                customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                customDialog.setOnShowListener(this);
                customDialog.show();
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.dashboard_review, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_status:
                ((OnChangeFragmentWrapperInterface) mainActivityRef)
                        .changeFragment(new StatusFragment(), bundle,
                                StatusFragment.STATUS_FRAG_TAG);
                break;
        }
        return true;
    }

//    @Override
//    public void onShow(final DialogInterface dialog) {
//        //ADD REVIEW BY PARAMS
//        try {
//            Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
//            positiveButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    try {
//                        //add review function
//                        String comment = ((EditText) addReviewDialogTemplate
//                                .findViewById(R.id.reviewCommentTextId)).getText().toString();
//                        float rating = ((RatingBar) addReviewDialogTemplate
//                                .findViewById(R.id.setStatusRatingBarViewId)).getRating();
//
//                        if(comment == null || comment.compareTo("") == 0) {
//                            Toast.makeText(mainActivityRef.getApplicationContext(),
//                                    "Failed - write some comment!", Toast.LENGTH_LONG).show();
//                            return;
//                        }
//
//                        dialog.dismiss();
//                        Log.e(TAG, comment + " " + rating);
//                        Bundle params = new Bundle();
//                        params.putParcelable(Review.REVIEW_KEY, new Review(Review.ID_NOT_SET,
//                                comment, Review.parseStatus(rating),
//                                (long) 123456,
//                                "USER_ID", coffeeMachineId));
////                        getLoaderManager().initLoader(ADD_REVIEW_BY_PARAMS.ordinal(), params,
////                                (LoaderManager.LoaderCallbacks<Object>) getFragmentManager().getFragment(bundle, "TAG"))
////                                .forceLoad();
//                        //NOTIFY USER
//                        Toast.makeText(mainActivityRef.getApplicationContext(),
//                                "Added review with success", Toast.LENGTH_LONG).show();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        //EMPTY - handled by onShow
    }
}
