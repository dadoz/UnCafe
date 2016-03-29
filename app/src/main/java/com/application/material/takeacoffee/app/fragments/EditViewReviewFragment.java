package com.application.material.takeacoffee.app.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.*;

import butterknife.Bind;
import butterknife.ButterKnife;
import com.application.material.takeacoffee.app.*;
import com.application.material.takeacoffee.app.application.CoffeePlacesApplication;
import com.application.material.takeacoffee.app.fragments.interfaces.OnLoadViewHandlerInterface;
import com.application.material.takeacoffee.app.models.Review;
import com.application.material.takeacoffee.app.models.Review.ReviewStatus;
import com.application.material.takeacoffee.app.parsers.JSONParserToObject;
import com.application.material.takeacoffee.app.services.HttpIntentService;
import com.application.material.takeacoffee.app.singletons.BusSingleton;
import com.application.material.takeacoffee.app.singletons.ImagePickerSingleton;
import com.application.material.takeacoffee.app.utils.Utils;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by davide on 14/11/14.
 */
public class EditViewReviewFragment extends Fragment implements
        View.OnClickListener {
    private static final String TAG = "EditViewReviewFragment";
    private EditReviewActivity editActivityRef;
    private Bundle bundle;
    private View addReviewView;
    private Review review;
    @Bind(R.id.editReviewCommentTextId) View editReviewCommentText;
    @Bind(R.id.editStatusRatingBarViewId) View editStatusRatingBarView;
    @Bind(R.id.deletePictureIconId) View deletePictureButton;
    @Bind(R.id.pickPictureFromCameraIconId) View pickPictureFromCameraButton;
    @Bind(R.id.pickPictureFromGalleryIconId) View pickPictureFromGalleryButton;
    @Bind(R.id.imagePreviewViewId) View imagePreviewView;
    private CoffeePlacesApplication coffeePlacesApplication;
    private boolean isReviewPictureSet = false;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof OnLoadViewHandlerInterface)) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLoadViewHandlerInterface");
        }
        editActivityRef =  (EditReviewActivity) activity;

        //getArgs
        coffeePlacesApplication = (CoffeePlacesApplication) editActivityRef.getApplication();

        //getArgs
        bundle = getArguments();
//        meUserId = coffeePlacesApplication.getUserId();
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
        ButterKnife.bind(this, addReviewView);
        if(savedInstance != null) {
            isReviewPictureSet = coffeePlacesApplication.isReviewPictureSet();
        }
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
        editActivityRef.initOnLoadView();
        initView();
    }

    private void initView() {
        editActivityRef.hideOnLoadView();
        ((RatingBar) editStatusRatingBarView).setRating(
                ReviewStatus.parseStatusToRating(
                        Review.ReviewStatus.parseStatus(review.getStatus())));
        ((EditText) editReviewCommentText).setText(review.getComment());

        deletePictureButton.setOnClickListener(this);
        pickPictureFromCameraButton.setOnClickListener(this);
        pickPictureFromGalleryButton.setOnClickListener(this);

        //if pic is set
        if(isReviewPictureSet) {
            ((ImageView) imagePreviewView).setImageBitmap(coffeePlacesApplication.getReviewPictureTemp());
            return;
        }

//        //retrieve pic from review
//        if(review.getReviewPictureUrl() != null) {
//            Bitmap pic = JSONParserToObject.
//                    getMockupPicture(editActivityRef, review.getReviewPictureUrl());
//            coffeePlacesApplication.setReviewPictureTemp(pic);
//            ((ImageView) imagePreviewView).setImageBitmap(pic);
//        }
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
//            case R.id.saveReviewButtonId:
//                saveReview();
//                break;
            case R.id.deletePictureIconId:
                coffeePlacesApplication.deleteReviewPictureTemp();
                ((ImageView) imagePreviewView).setImageBitmap(null);
                Log.e(TAG, "hey");
                break;
            case R.id.pickPictureFromCameraIconId:
                ImagePickerSingleton imagePicker = ImagePickerSingleton
                        .getInstance(editActivityRef);
                imagePicker.onLaunchCamera();
                Log.e(TAG, "hey");
                break;
            case R.id.pickPictureFromGalleryIconId:
                imagePicker = ImagePickerSingleton
                        .getInstance(editActivityRef);
                imagePicker.onPickPhoto();
                Log.e(TAG, "hey");
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

        ((OnLoadViewHandlerInterface) editActivityRef).initOnLoadView();
        Utils.hideKeyboard(editActivityRef, (EditText) editReviewCommentText);

        //reqeust service
        HttpIntentService.updateReviewRequest(editActivityRef, review);

        //TODO move on callback
//        saveReviewSuccessCallback();
    }

    private void saveReviewSuccessCallback() {
        Intent intent = new Intent();

        //on callback
        intent.putExtra(CoffeePlacesActivity.ACTION_EDIT_REVIEW_RESULT, "SAVE");
//        intent.putExtra(Review.REVIEW_OBJ_KEY, review);
        editActivityRef.setResult(Activity.RESULT_OK, intent);
        editActivityRef.finish();
    }

    @Subscribe
    public void onNetworkResponse(Review updateReviewResponse) {
        Log.d(TAG, "get response from bus - REVIEW_REQUEST");
        ((OnLoadViewHandlerInterface) editActivityRef).hideOnLoadView();

        if(updateReviewResponse == null) {
            //TODO handle adapter with empty data
            return;
        }
        //TODO handle adapter with empty data
        saveReviewSuccessCallback();
    }
}
