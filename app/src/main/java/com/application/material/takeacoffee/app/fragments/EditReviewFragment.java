package com.application.material.takeacoffee.app.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.*;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.application.material.takeacoffee.app.CoffeeMachineActivity;
import com.application.material.takeacoffee.app.EditReviewActivity;
import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.adapters.ReviewListAdapter;
import com.application.material.takeacoffee.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.takeacoffee.app.fragments.interfaces.OnLoadViewHandlerInterface;
import com.application.material.takeacoffee.app.loaders.RestResponse;
import com.application.material.takeacoffee.app.loaders.RetrofitLoader;
import com.application.material.takeacoffee.app.models.CoffeeMachine;
import com.application.material.takeacoffee.app.models.Review;
import com.application.material.takeacoffee.app.models.ReviewStatus;
import com.application.material.takeacoffee.app.models.User;
import com.application.material.takeacoffee.app.parsers.ParserToJavaObject;

import java.util.ArrayList;

import static com.application.material.takeacoffee.app.loaders.RetrofitLoader.HTTPActionRequestEnum.*;


/**
 * Created by davide on 14/11/14.
 */
public class EditReviewFragment extends Fragment implements
        View.OnClickListener, LoaderManager.LoaderCallbacks<RestResponse> {
    private static final String TAG = "EditReviewFragment";
    private EditReviewActivity editActivityRef;
    private Bundle bundle;
    private View addReviewView;
    private User user;
    private Review review;
    @InjectView(R.id.editReviewCommentTextId) View editReviewCommentText;
    @InjectView(R.id.editStatusRatingBarViewId) View editStatusRatingBarView;
    @InjectView(R.id.saveReviewButtonId) View saveReviewButton;
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
        bundle = getArguments();
        try {
            user = bundle.getParcelable(User.USER_OBJ_KEY);
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
        return addReviewView;
    }

    private void initOnLoadView() {
        //initOnLoadView
        editActivityRef.initOnLoadView();
        initView();
    }

    private void initView() {
        editActivityRef.hideOnLoadView();
        ((RatingBar) editStatusRatingBarView).setRating(Review.parseStatusToRating(review.getStatus()));
        ((EditText) editReviewCommentText).setText(review.getComment());
        saveReviewButton.setOnClickListener(this);
//        Log.e(TAG, "user" + user.getUsername() + "review" + review.toString());
    }

    public boolean updateReview() {
        String editComment = ((EditText) editReviewCommentText)
                .getText().toString();
        ReviewStatus.ReviewStatusEnum editStatus = Review.parseStatus(
                ((RatingBar) editStatusRatingBarView).getRating());

        if(review.getComment().equals(editComment) &&
               review.getStatus().name().equals(editStatus.name())) {
            return false;
        }

        review.setComment(editComment);
        review.setStatus(editStatus);
        return true;
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
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.saveReviewButtonId:
                Log.e(TAG, "Save");
                if(! updateReview()) {
                    Toast.makeText(editActivityRef, "No changes", Toast.LENGTH_SHORT).show();
                    break;
                }

                //crete loader to save
//                getLoaderManager().initLoader(SAVE_EDIT_REVIEW.ordinal(), null, this)
//                        .forceLoad();

                //on callback
                intent.putExtra(CoffeeMachineActivity.ACTION_EDIT_REVIEW_RESULT, "SAVE");
                intent.putExtra(Review.REVIEW_OBJ_KEY, review);
                editActivityRef.setResult(Activity.RESULT_OK, intent);
                editActivityRef.finish();
                break;
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        Intent intent = new Intent();
//        Log.e(TAG, "Delete");
//        switch (item.getItemId()) {
//            case R.id.action_delete:
////                getLoaderManager().initLoader(DELETE_REVIEW.ordinal(), null, this)
////                        .forceLoad();
//
//                //on callback
//                intent.putExtra(CoffeeMachineActivity.ACTION_EDIT_REVIEW_RESULT, "DELETE");
//                intent.putExtra(Review.REVIEW_OBJ_KEY, review);
//                editActivityRef.setResult(Activity.RESULT_OK, intent);
//                editActivityRef.finish();
//                break;
//        }
        return true;
    }

}
