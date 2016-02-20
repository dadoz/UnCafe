package com.application.material.takeacoffee.app.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
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
import com.application.material.takeacoffee.app.views.FilledCircleView;
import com.parse.ParseFile;
import com.squareup.otto.Subscribe;
import org.joda.time.DateTime;

import java.io.ByteArrayOutputStream;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by davide on 14/11/14.
 */
public class AddReviewFragment extends Fragment implements
        View.OnClickListener {
    private static final String TAG = "AddReviewFragment";
    private AddReviewActivity addActivityRef;
    private String meUserId;
    @Bind(R.id.commentTextId) View commentTextView;
    @Bind(R.id.statusRatingBarId) View statusRatingBarView;
//    @Bind(R.id.addReviewButtonId) View addReviewButton;
    @Bind(R.id.pickPictureIconId) View pickPictureButton;
    @Bind(R.id.pickPictureFromGalleryIconId) View pickPictureFromGalleryButton;
    @Bind(R.id.imagePreviewViewId) View imagePreviewView;
    @Bind(R.id.setRatingButtonId) View setRatingButton;

    @Bind(R.id.filledCircleId) View filledCircleView;
//    @Bind(R.id.seekBarId) View seekBarView;

    private String coffeeMachineId;
    private Review reviewParams;
    private DataApplication dataApplication;
    private boolean isReviewPictureSet = false;
    private boolean mItemPressed = false;

//    @Bind(R.id.usernameTextId) View usernameText;
//    @Bind(R.id.userIconId) View userIconView;
//    @Bind(R.id.editDeleteIconId) View editDeleteIcon;


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
        Bundle bundle = getArguments();
        try {
            coffeeMachineId = bundle.getString(CoffeeMachine.COFFEE_MACHINE_ID_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View addReviewView = inflater.inflate(R.layout.fragment_add_review, container, false);
        ButterKnife.bind(this, addReviewView);
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
        setRatingButton.setOnTouchListener(setRatingTouchListener);

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
                reviewParams = addReview();

                if(reviewParams != null) {
                    Utils.hideKeyboard(addActivityRef, (EditText) commentTextView);
                    addActivityRef.initOnLoadView(); //get spinner

                    if(imagePreviewView.getTag() != null) {
                        String pictureUrlLocal = (String) imagePreviewView.getTag();
                        ParseFile file = saveFile(pictureUrlLocal);
                        reviewParams.setReviewPictureUrl(file.getUrl());
                        reviewParams.setReviewPictureName(file.getUrl());
                    }
                    HttpIntentService.addReviewRequest(addActivityRef, reviewParams);
                }
                break;
        }
        return true;
    }

    private Review addReview() {
        addActivityRef.initOnLoadView(); //get spinner

        String comment = ((EditText) commentTextView).getText().toString();
        if(comment.compareTo("") == 0) {
            Toast.makeText(addActivityRef.getApplicationContext(),
                    "Failed - write some comment!", Toast.LENGTH_LONG).show();
            addActivityRef.hideOnLoadView(); //get spinner
            return null;
        }

        ReviewStatus.ReviewStatusEnum status = ReviewStatus.parseStatus(
                ((RatingBar) statusRatingBarView).getRating());

        long timestamp = new DateTime().getMillis();
        return new Review(null, comment, ReviewStatus.toString(status),
                timestamp, meUserId, coffeeMachineId, null, null);
    }

    private ParseFile saveFile(String url) {
        ParseFile file;
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(url);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] image = stream.toByteArray();

            file = new ParseFile("reviewPic.png", image);

            file.save();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addReviewSuccessCallback(String reviewId) {
        Review review = new Review(reviewId, reviewParams.getComment(),
                reviewParams.getStatus(),
                reviewParams.getTimestamp(),
                meUserId,
                coffeeMachineId,
                reviewParams.getReviewPictureName(),
                reviewParams.getReviewPictureUrl());

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

    Timer timer = new Timer();
    int counter = 0;

    private View.OnTouchListener setRatingTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(final View v, MotionEvent event) {
            //start counter
            final double MAX_VALUE = ((FilledCircleView) filledCircleView).getMaxValue();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    timer = new Timer();
                    counter = (int) ((FilledCircleView) filledCircleView).getCurrentValue();
                    Log.e(TAG, "down");
                    if (mItemPressed) {
                        // Multi-item swipes not handled
                        timer.cancel();
                        return false;
                    }

                    mItemPressed = true;
                            timer.scheduleAtFixedRate(new TimerTask() {
                                @Override
                                public void run() {
                                    counter --;
                                    Log.e(TAG, "expand coffee cup - " + counter);
                                    if(counter < 0 ) {
                                        counter = (int) MAX_VALUE;
                                    }

                                    addActivityRef.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ((FilledCircleView) filledCircleView).setValue(counter);
                                        }
                                    });
                                }
                            }, 20, 20);
                    return true;
                case MotionEvent.ACTION_UP:
                    Log.e(TAG, "up");
                    timer.cancel();
                    ((FilledCircleView) filledCircleView).setCurrentValue(counter);
                    mItemPressed = false;
                    return true;
            }
            return true;
        }
    };

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
