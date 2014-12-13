package com.application.material.takeacoffee.app.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.*;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.application.material.takeacoffee.app.AddReviewActivity;
import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.fragments.interfaces.OnLoadViewHandlerInterface;
import com.application.material.takeacoffee.app.loaders.RestResponse;
import com.application.material.takeacoffee.app.loaders.RetrofitLoader;
import com.application.material.takeacoffee.app.models.Review;
import com.application.material.takeacoffee.app.models.Review.ReviewStatus;
import com.application.material.takeacoffee.app.views.FilledCircleView;

import java.util.Date;


/**
 * Created by davide on 14/11/14.
 */
public class AddReviewFragment extends Fragment implements
        View.OnClickListener, LoaderManager.LoaderCallbacks<RestResponse> {
    private static final String TAG = "EditReviewFragment";
    private AddReviewActivity addActivityRef;
    private Bundle bundle;
    private View addReviewView;
    private String meUserId = "4nmvMJNk1R";
    @InjectView(R.id.commentTextId) View commentTextView;
    @InjectView(R.id.statusRatingBarId) View statusRatingBarView;
    @InjectView(R.id.addReviewButtonId) View addReviewButton;
    @InjectView(R.id.filledCircleId) View filledCircleView;
    @InjectView(R.id.seekBarId) View seekBarView;

    private String coffeeMachineId;
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

        //getArgs
        bundle = getArguments();
//        try {
//            meUserId = bundle.getString(User.USER_ID_KEY);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        //get all bundle
//        coffeeMachineId = coffeeMachine.getId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        addReviewView = inflater.inflate(R.layout.fragment_add_review, container, false);
        ButterKnife.inject(this, addReviewView);
        initOnLoadView();
        return addReviewView;
    }

    private void initOnLoadView() {
        //initOnLoadView
        addActivityRef.initOnLoadView();
        initView();
    }

    private void initView() {
        try {
            ((SeekBar) seekBarView).setProgress((int) ((FilledCircleView) filledCircleView).getValue());
            ((SeekBar) seekBarView).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser)
                        ((FilledCircleView) filledCircleView).setValue(progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        addActivityRef.hideOnLoadView();
        addReviewButton.setOnClickListener(this);
//        Log.e(TAG, "user" + user.getUsername() + "review" + review.toString());
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
            case R.id.addReviewButtonId:
                //on callback
                String comment = ((EditText) commentTextView).getText().toString();
                if(comment == null || comment.compareTo("") == 0) {
                    Toast.makeText(addActivityRef.getApplicationContext(),
                            "Failed - write some comment!", Toast.LENGTH_LONG).show();
                    return;
                }

//                getLoaderManager().initLoader(ADD_REVIEW_BY_PARAMS.ordinal(), null, this)
//                        .forceLoad();
                //move this on Success callback
                ReviewStatus.ReviewStatusEnum status = ReviewStatus.parseStatus(
                        ((RatingBar) statusRatingBarView).getRating());
                long timestamp = new Date().getTime();
                Review review = new Review("FAKE_ID", comment,
                        status, timestamp, meUserId, null);//TODO this is wrong
                intent.putExtra(Review.REVIEW_OBJ_KEY, review);
                addActivityRef.setResult(Activity.RESULT_OK, intent);
                addActivityRef.finish();
                break;
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

}