package com.application.material.takeacoffee.app.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;

import butterknife.Bind;
import butterknife.ButterKnife;
import com.application.material.takeacoffee.app.HandleReviewActivity;
import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.models.Review;
import com.application.material.takeacoffee.app.singletons.BusSingleton;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * Created by davide on 14/11/14.
 */
public class AddReviewFragment extends Fragment implements
        View.OnClickListener {
    private static final String TAG = "AddReviewFragment";
    private String coffeePlaceId;
    @Bind(R.id.commentReviewEditTextId)
    View commentReviewEditText;
    @Bind(R.id.reviewSaveButtonId)
    View reviewSaveButton;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View addReviewView = inflater.inflate(R.layout.review_edit_cardview_item, container, false);
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
        getActivity().findViewById(R.id.statusRatingBarId)
                .setVisibility(View.VISIBLE);
        reviewSaveButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.add_review_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e(TAG, "save review");
        switch (item.getItemId()) {
            case R.id.action_save:
        }
        return true;
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(Bundle bundle) {
        String reviewId = bundle.getString(Review.REVIEW_ID_KEY);
        String reviewContent = bundle.getString(Review.REVIEW_CONTENT_KEY);
        Log.e(TAG, reviewId + " --- " + reviewContent);
    }
}
