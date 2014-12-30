package com.application.material.takeacoffee.app.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.*;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.application.material.takeacoffee.app.*;
import com.application.material.takeacoffee.app.application.DataApplication;
import com.application.material.takeacoffee.app.fragments.interfaces.OnLoadViewHandlerInterface;
import com.application.material.takeacoffee.app.models.CoffeeMachine;
import com.application.material.takeacoffee.app.models.Review;
import com.application.material.takeacoffee.app.models.Review.ReviewStatus;
import com.application.material.takeacoffee.app.models.User;
import com.application.material.takeacoffee.app.services.HttpIntentService;
import com.application.material.takeacoffee.app.singletons.BusSingleton;
import com.squareup.otto.Subscribe;

/**
 * Created by davide on 14/11/14.
 */
public class EditReviewFragment extends Fragment implements
        View.OnClickListener {
    private static final String TAG = "EditReviewFragment";
    private EditReviewActivity editActivityRef;
    private Bundle bundle;
    private View addReviewView;
    private User user;
    private Review review;
    @InjectView(R.id.editReviewCommentTextId) View editReviewCommentText;
    @InjectView(R.id.editStatusRatingBarViewId) View editStatusRatingBarView;
    @InjectView(R.id.saveReviewButtonId) View saveReviewButton;
    private DataApplication dataApplication;
    private String meUserId;
//    @InjectView(R.id.usernameTextId) View usernameText;
//    @InjectView(R.id.userIconId) View userIconView;
//    @InjectView(R.id.editDeleteIconId) View editDeleteIcon;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof OnLoadViewHandlerInterface)) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLoadViewHandlerInterface");
        }
        editActivityRef =  (EditReviewActivity) activity;

        //getArgs
        dataApplication = (DataApplication) editActivityRef.getApplication();

        //getArgs
        bundle = getArguments();
        meUserId = dataApplication.getUserId();
        try {
//            user = bundle.getParcelable(User.USER_OBJ_KEY);
            review = bundle.getParcelable(Review.REVIEW_OBJ_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        //get all bundle
//        coffeeMachineId = coffeeMachine.getId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        addReviewView = inflater.inflate(R.layout.fragment_edit_review, container, false);
        ButterKnife.inject(this, addReviewView);
        initOnLoadView();
        setHasOptionsMenu(true);
        return addReviewView;
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
        //initOnLoadView
        editActivityRef.initOnLoadView(null);
        initView();
    }

    private void initView() {
        editActivityRef.hideOnLoadView(null);
        ((RatingBar) editStatusRatingBarView).setRating(
                ReviewStatus.parseStatusToRating(
                        Review.ReviewStatus.parseStatus(review.getStatus())));
        ((EditText) editReviewCommentText).setText(review.getComment());
        saveReviewButton.setOnClickListener(this);
//        Log.e(TAG, "user" + user.getUsername() + "review" + review.toString());
    }

    public boolean updateReview() {
        String editComment = ((EditText) editReviewCommentText)
                .getText().toString();
        String editStatus = ReviewStatus.parseStatus(
                ((RatingBar) editStatusRatingBarView).getRating()).name();

        if(review.getComment().equals(editComment) &&
               review.getStatus().equals(editStatus)) {
            return false;
        }

        review.setComment(editComment);
        review.setStatus(editStatus);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveReviewButtonId:
                saveReview();
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.save_edit_review, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e(TAG, "save review");
        switch (item.getItemId()) {
            case R.id.action_save:
                saveReview();
                break;
        }
        return true;
    }

    private void saveReview() {
        Log.e(TAG, "Save");
        if(! updateReview()) {
            Toast.makeText(editActivityRef, "No changes", Toast.LENGTH_SHORT).show();
            return;
        }

        ((OnLoadViewHandlerInterface) editActivityRef).initOnLoadView(null);
        Utils.hideKeyboard(editActivityRef, (EditText) editReviewCommentText);

        //reqeust service
        HttpIntentService.updateReviewRequest(editActivityRef, review);

        //TODO move on callback
//        saveReviewSuccessCallback();
    }

    private void saveReviewSuccessCallback() {
        Intent intent = new Intent();

        //on callback
        intent.putExtra(CoffeeMachineActivity.ACTION_EDIT_REVIEW_RESULT, "SAVE");
        intent.putExtra(Review.REVIEW_OBJ_KEY, review);
        editActivityRef.setResult(Activity.RESULT_OK, intent);
        editActivityRef.finish();
    }

    @Subscribe
    public void onNetworkResponse(Review updateReviewResponse) {
        Log.d(TAG, "get response from bus - REVIEW_REQUEST");
        ((OnLoadViewHandlerInterface) editActivityRef).hideOnLoadView(null);

        if(updateReviewResponse == null) {
            //TODO handle adapter with empty data
            return;
        }
        //TODO handle adapter with empty data
        saveReviewSuccessCallback();
    }


}
