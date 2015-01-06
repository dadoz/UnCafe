package com.application.material.takeacoffee.app.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.widget.ImageView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.application.material.takeacoffee.app.AddReviewActivity;
import com.application.material.takeacoffee.app.CoffeeMachineActivity;
import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.utils.Utils;
import com.application.material.takeacoffee.app.application.DataApplication;
import com.application.material.takeacoffee.app.fragments.interfaces.OnLoadViewHandlerInterface;
import com.application.material.takeacoffee.app.models.CoffeeMachine;
import com.application.material.takeacoffee.app.models.Review;
import com.application.material.takeacoffee.app.models.Review.ReviewStatus;
import com.application.material.takeacoffee.app.services.HttpIntentService;
import com.application.material.takeacoffee.app.singletons.BusSingleton;
import com.application.material.takeacoffee.app.singletons.ImagePickerSingleton;
import com.squareup.otto.Subscribe;
import org.joda.time.DateTime;


/**
 * Created by davide on 14/11/14.
 */
public class AddReviewFragment extends Fragment implements
        View.OnClickListener {
    private static final String TAG = "AddReviewFragment";
    private AddReviewActivity addActivityRef;
    private Bundle bundle;
    private View addReviewView;
//    private String meUserId = "4nmvMJNk1R";
    private String meUserId;
    @InjectView(R.id.commentTextId) View commentTextView;
    @InjectView(R.id.statusRatingBarId) View statusRatingBarView;
//    @InjectView(R.id.addReviewButtonId) View addReviewButton;
    @InjectView(R.id.pickPictureIconId) View pickPictureButton;
    @InjectView(R.id.pickPictureFromGalleryIconId) View pickPictureFromGalleryButton;
    @InjectView(R.id.imagePreviewViewId) View imagePreviewView;

//    @InjectView(R.id.filledCircleId) View filledCircleView;
//    @InjectView(R.id.seekBarId) View seekBarView;

    private String coffeeMachineId;
    private Review reviewParams;
    private DataApplication dataApplication;
    private boolean isReviewPictureSet = false;
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
        addActivityRef =  (AddReviewActivity) activity;
        dataApplication = (DataApplication) addActivityRef.getApplication();

        meUserId = dataApplication.getUserId();
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        //getArgs
        bundle = getArguments();
        try {
            coffeeMachineId = bundle.getString(CoffeeMachine.COFFEE_MACHINE_ID_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        addReviewView = inflater.inflate(R.layout.fragment_add_review, container, false);
        ButterKnife.inject(this, addReviewView);
        //initOnLoadView();
        if(savedInstance != null) {
            isReviewPictureSet = dataApplication.isReviewPictureSet();
        }

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

    private void initOnLoadView() {
        //initOnLoadView
        addActivityRef.initOnLoadView();
        initView();
    }

    private void initView() {
//        try {
//            ((SeekBar) seekBarView).setProgress((int) ((FilledCircleView) filledCircleView).getValue());
//            ((SeekBar) seekBarView).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                @Override
//                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                    if (fromUser)
//                        ((FilledCircleView) filledCircleView).setValue(progress);
//                }
//
//                @Override
//                public void onStartTrackingTouch(SeekBar seekBar) {
//                }
//
//                @Override
//                public void onStopTrackingTouch(SeekBar seekBar) {
//                }
//            });
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        addActivityRef.hideOnLoadView();
//        addReviewButton.setOnClickListener(this);
        pickPictureButton.setOnClickListener(this);
        pickPictureFromGalleryButton.setOnClickListener(this);
        if(isReviewPictureSet) {
            ((ImageView) imagePreviewView).setImageBitmap(dataApplication.getReviewPictureTemp());
        }
//        Log.e(TAG, "user" + user.getUsername() + "review" + review.toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.addReviewButtonId:
//                if(! addReview()) {
//                    return;
//                }
//
//                Utils.hideKeyboard(addActivityRef, (EditText) commentTextView);
//                addActivityRef.initOnLoadView(null); //get spinner
//                HttpIntentService.addReviewRequest(addActivityRef, reviewParams);
//                break;
            case R.id.pickPictureIconId:
                ImagePickerSingleton imagePicker = ImagePickerSingleton
                        .getInstance(addActivityRef);
                imagePicker.onLaunchCamera();
                Log.e(TAG, "hey");
                break;
            case R.id.pickPictureFromGalleryIconId:
                imagePicker = ImagePickerSingleton
                        .getInstance(addActivityRef);
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
                addReview();

                Utils.hideKeyboard(addActivityRef, (EditText) commentTextView);
                addActivityRef.initOnLoadView(); //get spinner
                HttpIntentService.addReviewRequest(addActivityRef, reviewParams);
                break;
        }
        return true;
    }

    private boolean addReview() {
        addActivityRef.initOnLoadView(); //get spinner

        //on callback
        String comment = ((EditText) commentTextView).getText().toString();
        if(comment.compareTo("") == 0) {
            Toast.makeText(addActivityRef.getApplicationContext(),
                    "Failed - write some comment!", Toast.LENGTH_LONG).show();
            addActivityRef.hideOnLoadView(); //get spinner
            return false;
        }
        ReviewStatus.ReviewStatusEnum status = ReviewStatus.parseStatus(
                ((RatingBar) statusRatingBarView).getRating());

        long timestamp = new DateTime().getMillis();
        reviewParams = new Review(null, comment, ReviewStatus.toString(status),
                timestamp, meUserId, coffeeMachineId);
        return true;
    }


    public void addReviewSuccessCallback(String reviewId) {
        Review review = new Review(reviewId, reviewParams.getComment(),
                reviewParams.getStatus(),
                reviewParams.getTimestamp(), meUserId, coffeeMachineId);

        Intent intent = new Intent();

        intent.putExtra(Review.REVIEW_OBJ_KEY, review);
        addActivityRef.setResult(Activity.RESULT_OK, intent);
        addActivityRef.finish();
    }

    public void addReviewErrorCallback() {
        Intent intent = new Intent();
        intent.putExtra(CoffeeMachineActivity.ERROR_MESSAGE_KEY, Review.ERROR_MESSAGE);
        addActivityRef.setResult(CoffeeMachineActivity.RESULT_FAILED, intent);
        addActivityRef.finish();

    }


    @Subscribe
    public void onNetworkResponse(Review review) {
        Log.d(TAG, "get response from bus - REVIEW_REQUEST");
        ((OnLoadViewHandlerInterface) addActivityRef).hideOnLoadView();

        if(review == null) {
            addReviewErrorCallback();
            //TODO handle adapter with empty data
            return;
        }

        addReviewSuccessCallback(review.getId());
    }

}
