package com.application.material.takeacoffee.app.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.RatingBar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import com.application.material.takeacoffee.app.*;
import com.application.material.takeacoffee.app.models.Review;
import com.application.material.takeacoffee.app.presenter.LikePresenter;
import com.application.material.takeacoffee.app.presenter.ReviewCardviewPresenter;
import com.application.material.takeacoffee.app.singletons.EventBusSingleton;
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
    private View reviewView;
    private RatingBar editStatusRatingbarView;
    @Bind(R.id.commentTextId)
    TextView commentTextView;
    @Bind(R.id.reviewEditCardviewLayoutId)
    View reviewEditCardviewLayout;
    @Bind(R.id.reviewCardviewLayoutId)
    View reviewCardviewLayout;
    @Bind(R.id.commentReviewEditTextId)
    TextView commentReviewEditText;
    @Bind(R.id.titleReviewEditTextId)
    TextView titleReviewEditText;
    @Bind(R.id.likeReviewEditIconId)
    View likeReviewEditIcon;
    @Bind(R.id.likeReviewIconId)
    View likeReviewIcon;

    private String reviewContent;
    private boolean reviewLikeStatus;
    private boolean editStatus = false;
    private RebounceMotionHandler rebounceMotionHandler;
    private String reviewId;
    private LikePresenter likePresenter;

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
        reviewView = inflater.inflate(R.layout.fragment_edit_view_review, container, false);
        ButterKnife.bind(this, reviewView);
        setHasOptionsMenu(true);

        rebounceMotionHandler = RebounceMotionHandler.getInstance(new WeakReference<OnSpringMotionHandler>(this));
        likePresenter = LikePresenter.getInstance(new WeakReference<Context>(getActivity()));
        initView();
        return reviewView;
    }

    @Override
    public void onResume(){
        EventBusSingleton.getInstance().register(this);
        super.onResume();
    }

    @Override
    public void onPause(){
        EventBusSingleton.getInstance().unregister(this);
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
        likeReviewEditIcon.setOnClickListener(this);
        likePresenter.initPresenter(reviewLikeStatus);

        //TODO move on presenter
        ((View) reviewEditCardviewLayout.getParent()).setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.likeReviewEditIconId:
                likePresenter.toggleLikeStatus(v);
                break;
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
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
        likePresenter.initLikeStatus(likeReviewEditIcon);
        titleReviewEditText.setText(reviewContent.substring(0, Math.min(reviewContent.length(), 18)) + "...");
        titleReviewEditText.setEllipsize(TextUtils.TruncateAt.END);
        commentReviewEditText.setText(reviewContent);
        commentReviewEditText.requestFocus();
    }

    /**
     *
     */
    private void initReview() {
        ReviewCardviewPresenter.getInstance(new WeakReference<Context>(getActivity())).init(reviewView,
                ReviewCardviewPresenter.REVIEW_VIEW_MODE);
        likePresenter.initLikeStatus(likeReviewIcon);
        commentTextView.setText(reviewContent);
    }

    /**
     * @param isVisible
     */
    private void showEditReview(boolean isVisible) {
        //TODO add animationBuilder :) - move on presenter
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
        HashMap <String,String> map = new HashMap<>();
        map.put(Review.REVIEW_ID_KEY, reviewId);
        map.put(Review.REVIEW_CONTENT_KEY, commentReviewEditText.getText().toString());
        map.put(Review.REVIEW_RATING_KEY, Boolean.toString(likePresenter.isLike()));
        return map;
    }


    /**
     *
     */
    private void updateCardviewData() {
        HashMap<String, String> map = getUpdateDataMapFromUI();
        reviewContent = map.get(Review.REVIEW_CONTENT_KEY);
        reviewLikeStatus = Boolean.getBoolean(map.get(Review.REVIEW_RATING_KEY));
        initReview();
        initEditReview();
    }


    @Override
    public void onUpdateFirebaseSuccessCallback() {
        editStatus = false;
        showEditReview(false);
        updateCardviewData();
        Utils.showSnackbar(reviewView, "Review updated!");
        getActivity().invalidateOptionsMenu();
    }


    @Override
    public void onUpdateFirebaseErrorCallback(FirebaseError firebaseError) {
        Log.e(TAG, firebaseError.getMessage());
        editStatus = false;
        showEditReview(false);
        Utils.showSnackbar(reviewView, "Error! Cannot update review!");
        getActivity().invalidateOptionsMenu();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(Bundle bundle) {
        reviewId = bundle.getString(Review.REVIEW_ID_KEY);
        reviewContent = bundle.getString(Review.REVIEW_CONTENT_KEY);
        reviewLikeStatus = bundle.getBoolean(Review.REVIEW_RATING_KEY);
        initReview();
    }

}
