package com.application.material.takeacoffee.app.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.application.material.takeacoffee.app.CoffeeMachineActivity;
import com.application.material.takeacoffee.app.EditReviewActivity;
import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.takeacoffee.app.fragments.interfaces.OnLoadViewHandlerInterface;
import com.application.material.takeacoffee.app.models.Review;
import com.application.material.takeacoffee.app.models.User;


/**
 * Created by davide on 14/11/14.
 */
public class EditReviewFragment extends Fragment implements View.OnClickListener {
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
        setHasOptionsMenu(true);
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

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.saveReviewButtonId:
                Log.e(TAG, "Save");
                intent.putExtra(CoffeeMachineActivity.ACTION_EDIT_REVIEW_RESULT, "SAVE");
                editActivityRef.setResult(Activity.RESULT_OK, intent);
                editActivityRef.finish();
                break;
//            case R.id.editDeleteIconId:
//                Log.e(TAG, "delete");
//                intent.putExtra(CoffeeMachineActivity.ACTION_EDIT_REVIEW_RESULT, "SAVE");
//                editActivityRef.setResult(CoffeeMachineActivity.ACTION_EDIT_REVIEW, intent);
//                editActivityRef.finish();
//                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.edit_review, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();

        switch (item.getItemId()) {
            case R.id.action_delete:
                intent.putExtra(CoffeeMachineActivity.ACTION_EDIT_REVIEW_RESULT, "SAVE");
                editActivityRef.setResult(Activity.RESULT_OK, intent);
                editActivityRef.finish();
                break;
        }
        return true;
    }

}
