package com.application.material.takeacoffee.app.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.*;

import butterknife.Bind;
import butterknife.ButterKnife;
import com.application.material.takeacoffee.app.*;
import com.application.material.takeacoffee.app.models.Review;
import com.application.material.takeacoffee.app.models.Review.ReviewStatus;
import com.application.material.takeacoffee.app.singletons.BusSingleton;
import com.application.material.takeacoffee.app.utils.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by davide on 14/11/14.
 */
public class EditViewReviewFragment extends Fragment implements
        View.OnClickListener {
    private static final String TAG = "EditViewReviewFragment";
    private View addReviewView;
    @Bind(R.id.commentTextId)
    TextView commentTextView;
    private RatingBar editStatusRatingbarView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        addReviewView = inflater.inflate(R.layout.fragment_edit_view_review, container, false);
        ButterKnife.bind(this, addReviewView);
        setHasOptionsMenu(true);

        initView();
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

    /**
     * init view
     */
    private void initView() {
        editStatusRatingbarView = (RatingBar) getActivity()
                .findViewById(R.id.statusRatingBarId);
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.saveReviewId:
//                saveReview();
//                break;
//        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.edit_view_review_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveReview();
                break;
        }
        return true;
    }

    /**
     * update review
     * @return
     */
    public boolean updateReview() {
        Log.e(TAG, "Update");
//        String editComment = editReviewCommentText.getText().toString();
//        String editStatus = ReviewStatus.parseStatus(editStatusRatingBar.getRating()).name();

//        if (review.getComment().equals(editComment) &&
//                review.getStatus().equals(editStatus)) {
//            return false;
//        }
//
//        review.setComment(editComment);
//        review.setStatus(editStatus);
        return true;
    }

    /**
     * save review
     */
    private void saveReview() {
        Log.e(TAG, "Save");
        if (!updateReview()) {
            Toast.makeText(getActivity(), "No changes", Toast.LENGTH_SHORT).show();
            return;
        }

//        Utils.hideKeyboard(getActivity(), editReviewCommentText);
//        HttpIntentService.updateReviewRequest(getActivity(), review);
//        saveReviewSuccessCallback();
    }

    /**
     * save review callback
     */
    private void saveReviewSuccessCallback() {
        Log.e(TAG, "Save review callback");
        Intent intent = new Intent();

        //on callback
//        intent.putExtra(CoffeePlacesActivity.ACTION_EDIT_REVIEW_RESULT, "SAVE");
//        getActivity().setResult(Activity.RESULT_OK, intent);
//        getActivity().finish();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(Bundle bundle) {
//        String reviewId = bundle.getString(Review.REVIEW_ID_KEY);
        String reviewContent = bundle.getString(Review.REVIEW_CONTENT_KEY);
        int reviewRating = bundle.getInt(Review.REVIEW_RATING_KEY);

        editStatusRatingbarView.setRating(reviewRating);
        commentTextView.setText(reviewContent);
    }
}
