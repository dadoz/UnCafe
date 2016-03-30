package com.application.material.takeacoffee.app.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.*;
import android.util.Log;
import android.view.*;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

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
        View.OnClickListener, Toolbar.OnMenuItemClickListener {
    private static final String TAG = "EditViewReviewFragment";
    private View addReviewView;
    private RatingBar editStatusRatingbarView;
    @Bind(R.id.commentTextId)
    TextView commentTextView;
    @Bind(R.id.reviewCardviewToolbarId)
    Toolbar reviewCardviewToolbar;
    @Bind(R.id.reviewEditCardviewLayoutId)
    View reviewEditCardviewLayout;
    @Bind(R.id.reviewCardviewLayoutId)
    View reviewCardviewLayout;
    @Bind(R.id.commentReviewEditTextId)
    TextView commentReviewEditText;
    private String reviewContent;
    private int reviewRating;
    private boolean editStatus = false;

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
        reviewCardviewToolbar.inflateMenu(R.menu.edit_review_menu);
        reviewCardviewToolbar.setOnMenuItemClickListener(this);
        editStatusRatingbarView = (RatingBar) getActivity()
                .findViewById(R.id.statusRatingBarId);
        editStatusRatingbarView.setVisibility(View.GONE);
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
        menuInflater.inflate(R.menu.save_review_menu, menu);
        MenuItem saveItem = menu.getItem(0);
        saveItem.setVisible(editStatus);
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

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_icon:
                editReview();
                break;
            case R.id.action_delete:
                deleteReview();
                break;
        }
        return true;
    }


    /**
     *
     */
    private void editReview() {
        editStatus = true;
        showEditReview(true);
        initEditReview();
        getActivity().invalidateOptionsMenu();
    }

    /**
     *
     */
    private void initEditReview() {
        editStatusRatingbarView.setRating(reviewRating);
        commentReviewEditText.setText(reviewContent);
        commentReviewEditText.requestFocus();
    }
    
    /**
     * @param isVisible
     */
    private void showEditReview(boolean isVisible) {
        reviewEditCardviewLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        editStatusRatingbarView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        reviewCardviewLayout.setVisibility(isVisible ? View.GONE : View.VISIBLE);
    }

    /**
     *
     */
    private void deleteReview() {

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
        reviewContent = bundle.getString(Review.REVIEW_CONTENT_KEY);
        reviewRating = bundle.getInt(Review.REVIEW_RATING_KEY);

        commentTextView.setText(reviewContent);
    }


}
