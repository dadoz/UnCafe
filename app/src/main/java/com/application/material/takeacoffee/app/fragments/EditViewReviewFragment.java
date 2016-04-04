package com.application.material.takeacoffee.app.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import com.application.material.takeacoffee.app.*;
import com.application.material.takeacoffee.app.models.Review;
import com.application.material.takeacoffee.app.models.User;
import com.application.material.takeacoffee.app.singletons.BusSingleton;
import com.application.material.takeacoffee.app.singletons.FirebaseManager;
import com.application.material.takeacoffee.app.singletons.FirebaseManager.OnUpdateFirebaseCallbackInterface;
import com.application.material.takeacoffee.app.utils.RebounceMotionHandler;
import com.application.material.takeacoffee.app.utils.RebounceMotionHandler.OnSpringMotionHandler;
import com.application.material.takeacoffee.app.utils.Utils;
import com.firebase.client.FirebaseError;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Created by davide on 14/11/14.
 */
public class EditViewReviewFragment extends Fragment implements
        View.OnClickListener, OnSpringMotionHandler, View.OnLongClickListener, OnUpdateFirebaseCallbackInterface {
    private static final String TAG = "EditViewReviewFragment";
    private View addReviewView;
    private RatingBar editStatusRatingbarView;
    @Bind(R.id.commentTextId)
    TextView commentTextView;
    @Bind(R.id.reviewEditCardviewLayoutId)
    View reviewEditCardviewLayout;
    @Bind(R.id.reviewCardviewLayoutId)
    View reviewCardviewLayout;
    @Bind(R.id.commentReviewEditTextId)
    TextView commentReviewEditText;
//    @Bind(R.id.reviewEditButtonId)
//    TextView reviewEditButton;
//    @Bind(R.id.reviewShareButtonId)
//    TextView reviewShareButton;
    @Bind(R.id.likeReviewEditIconId)
    View likeReviewEditIcon;
    @Bind(R.id.unlikeReviewEditIconId)
    View unlikeReviewEditIcon;

    private String reviewContent;
    private int reviewRating;
    private boolean editStatus = false;
    private RebounceMotionHandler rebounceMotionHandler;
    private String reviewId;
    private boolean isLiking = false;

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

        rebounceMotionHandler = RebounceMotionHandler.getInstance(new WeakReference<OnSpringMotionHandler>(this));
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
        editStatusRatingbarView.setVisibility(View.GONE);
        reviewCardviewLayout.setOnLongClickListener(this);
//        reviewEditButton.setOnClickListener(this);
//        reviewShareButton.setOnClickListener(this);
        likeReviewEditIcon.setOnClickListener(this);
        unlikeReviewEditIcon.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.reviewEditButtonId:
//                editReview();
//                break;
            case R.id.likeReviewEditIconId:
            case R.id.unlikeReviewEditIconId:
                toggleStatusOnEditMode();
                break;
//            case R.id.reviewShareButtonId:
//                Log.e(TAG, "still to be implemented - share");
//                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.reviewCardviewLayoutId:
                rebounceMotionHandler.translateViewOnY(reviewCardviewLayout.getY());
                break;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.edit_view_review_menu, menu);
        menu.getItem(0).setVisible(editStatus);
        menu.getItem(1).setVisible(!editStatus);
        menu.getItem(2).setVisible(!editStatus);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                break;
            case R.id.action_edit:
                editReview();
                break;
            case R.id.action_save:
                updateReview();
                break;
            case android.R.id.home:
                handleBackPressed();
                break;
        }
        return true;
    }

    /**
     *
     */
    private void handleBackPressed() {
        //edit
        if (editStatus) {
            editStatus = false;
            getActivity().invalidateOptionsMenu();
            showEditReview(false);
            return;
        }

        //deleted
        if (rebounceMotionHandler.isMovedUp()) {
            rebounceMotionHandler.translateViewOnY(0);
            return;
        }
        getActivity().onBackPressed();
    }

    @Override
    public void handleSpringUpdate(float value) {
        reviewCardviewLayout.setY(value);
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
        commentReviewEditText.setText(reviewContent);
        commentReviewEditText.requestFocus();
    }

    /**
     *
     */
    private void initReview() {
        commentTextView.setText(reviewContent);
    }

    /**
     * @param isVisible
     */
    private void showEditReview(boolean isVisible) {
        //TODO add animationBuilder :)
        reviewEditCardviewLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        reviewCardviewLayout.setVisibility(!isVisible ? View.VISIBLE : View.GONE);
    }

    /**
     *
     */
    private void deleteReview() {
        //TODO implement it
    }

    /**
     * update review
     * @return
     */
    public boolean updateReview() {
        Log.e(TAG, "Update");
//        FirebaseManager.getIstance()
//                .updateObjectByType(getUpdateDataMapFromUI(), FirebaseManager.REVIEW_CLASS,
//                        new WeakReference<OnUpdateFirebaseCallbackInterface>(this));
        onUpdateFirebaseSuccessCallback();
        return true;
    }

    //TODO move in a presenter
    /**
     *
     * @return
     */
    public HashMap < String,String > getUpdateDataMapFromUI() {
        HashMap < String,String > map = new HashMap<>();
        map.put(Review.REVIEW_ID_KEY, reviewId);
        map.put(Review.REVIEW_CONTENT_KEY, commentReviewEditText.getText().toString());
        map.put(Review.REVIEW_RATING_KEY, "NO");
        return map;
    }

    /**
     *
     */
    public void toggleStatusOnEditMode() {
        //TODO animationBuilder
        likeReviewEditIcon.setVisibility(isLiking ? View.VISIBLE : View.GONE);
        unlikeReviewEditIcon.setVisibility(isLiking ? View.GONE : View.VISIBLE);
        isLiking = !isLiking;
    }

    /**
     *
     */
    private void updateCardviewData() {
        HashMap<String, String> map = getUpdateDataMapFromUI();
        reviewContent = map.get(Review.REVIEW_CONTENT_KEY);
        initReview();
        initEditReview();
    }


    @Override
    public void onUpdateFirebaseSuccessCallback() {
        editStatus = false;
        showEditReview(false);
        updateCardviewData();
        Utils.showSnackbar(addReviewView, "Review updated!");
        getActivity().invalidateOptionsMenu();
    }


    @Override
    public void onUpdateFirebaseErrorCallback(FirebaseError firebaseError) {
        Log.e(TAG, firebaseError.getMessage());
        editStatus = false;
        showEditReview(false);
        Utils.showSnackbar(addReviewView, "Error! Cannot update review!");
        getActivity().invalidateOptionsMenu();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(Bundle bundle) {
        reviewId = bundle.getString(Review.REVIEW_ID_KEY);
        reviewContent = bundle.getString(Review.REVIEW_CONTENT_KEY);
        reviewRating = bundle.getInt(Review.REVIEW_RATING_KEY);
        initReview();
    }

}
