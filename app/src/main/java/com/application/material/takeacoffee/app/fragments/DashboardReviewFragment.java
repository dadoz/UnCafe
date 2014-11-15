package com.application.material.takeacoffee.app.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.application.material.takeacoffee.app.CoffeeMachineActivity;
import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.takeacoffee.app.fragments.interfaces.OnLoadViewHandlerInterface;
import com.application.material.takeacoffee.app.fragments.interfaces.SetActionBarInterface;
import com.application.material.takeacoffee.app.loaders.RestResponse;
import com.application.material.takeacoffee.app.loaders.RetrofitLoader;
import com.application.material.takeacoffee.app.models.CoffeeMachine;
import com.application.material.takeacoffee.app.models.ReviewStatus;
import com.application.material.takeacoffee.app.parsers.ParserToJavaObject;

import static com.application.material.takeacoffee.app.loaders.RetrofitLoader.HTTPActionRequestEnum.REVIEW_COUNT_REQUEST;

/**
 * Created by davide on 07/11/14.
 */
public class DashboardReviewFragment extends Fragment implements
        View.OnClickListener, LoaderManager.LoaderCallbacks<RestResponse> {
    private static final String TAG = "DashboardReviewFragment";
    private CoffeeMachineActivity mainActivityRef;
    private View dashboardReviewView;
    private boolean hasAtLeastOneReview = false;
    private Bundle bundle;
    @InjectView(R.id.coffeeMachineStatusIconId) View coffeeMachineStatusIcon;
    @InjectView(R.id.coffeeMachineStatusTextId) View coffeeMachineStatusTextView;
    @InjectView(R.id.coffeeMachineWeeklyReviewTextId) View coffeeMachineWeeklyReviewTextView;

//    @InjectView(R.id.coffeeMachineNameHeaderId) View coffeeMachineNameHeaderView;
//    @InjectView(R.id.coffeeMachineLocationHeaderId) View coffeeMachineLocationHeaderView;
    @InjectView(R.id.addReviewButtonId) View addReviewButton;

    private String coffeeMachineId;
    private CoffeeMachine coffeeMachine;


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
        initOnLoadView();
        return dashboardReviewView;
    }

    private void initOnLoadView() {
        //initOnLoadView
        mainActivityRef.initOnLoadView();

       getLoaderManager().initLoader(REVIEW_COUNT_REQUEST.ordinal(), null, this)
                .forceLoad();

        //after loading smthing if u need
//        initView(reviewStatus);
    }

    private void initView(ReviewStatus reviewStatus) {
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
        int iconId = reviewStatus.getIconIdByStatus();
        ((ImageView) coffeeMachineStatusIcon)
                .setImageDrawable(getResources().getDrawable(iconId));

        //set week review name and cnt
        ((TextView) coffeeMachineStatusTextView).setText(reviewStatus.getName());
        ((TextView) coffeeMachineWeeklyReviewTextView).setText(Integer.toString(reviewStatus.getWeeklyReviewCnt()));

        //set listener if there is sm review
        hasAtLeastOneReview = reviewStatus.getHasAtLeastOneReview();
        coffeeMachineStatusIcon.setOnClickListener(this);
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
            ReviewStatus reviewStatus = (ReviewStatus) restResponse.getParsedData();
            initView(reviewStatus);
        } catch (Exception e) {
            ((OnLoadViewHandlerInterface) mainActivityRef).hideOnLoadView();
            String filename = "review_status.json";
            String data = RetrofitLoader.getJSONDataMockup(this.getActivity(), filename);
            ReviewStatus reviewStatus = ParserToJavaObject.reviewStatusParser(data);
            initView(reviewStatus);

//            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.coffeeMachineStatusIconId:
                if(hasAtLeastOneReview) {
                    //get listview fragment
                    ((OnChangeFragmentWrapperInterface) mainActivityRef)
                            .changeFragment(new ReviewListFragment(), bundle, null);
                    break;
                }

                Log.e(TAG, "no review for this coffee machine!");
                Toast.makeText(mainActivityRef, "No review!", Toast.LENGTH_SHORT);
                break;
            case R.id.addReviewButtonId:
                //use library to handle this dialog with material design
                Toast.makeText(mainActivityRef.getApplicationContext(),
                        "hey add fragment", Toast.LENGTH_SHORT).show();
                break;

        }

    }
}
