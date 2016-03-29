package com.application.material.takeacoffee.app.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.*;

import butterknife.Bind;
import butterknife.ButterKnife;
import com.application.material.takeacoffee.app.AddViewReviewActivity;
import com.application.material.takeacoffee.app.CoffeePlacesActivity;
import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.application.CoffeePlacesApplication;
import com.application.material.takeacoffee.app.models.CoffeePlace;
import com.application.material.takeacoffee.app.singletons.FirebaseManager;
import com.application.material.takeacoffee.app.utils.Utils;
import com.application.material.takeacoffee.app.fragments.interfaces.OnLoadViewHandlerInterface;
import com.application.material.takeacoffee.app.models.Review;
import com.application.material.takeacoffee.app.models.Review.ReviewStatus;
import com.application.material.takeacoffee.app.services.HttpIntentService;
import com.application.material.takeacoffee.app.singletons.BusSingleton;
import com.parse.ParseFile;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.joda.time.DateTime;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.util.Timer;


/**
 * Created by davide on 14/11/14.
 */
public class AddViewReviewFragment extends Fragment implements
        View.OnClickListener {
    private static final String TAG = "AddViewReviewFragment";
    private String coffeePlaceId;
    private AddViewReviewActivity activityRef;
    @Bind(R.id.commentTextId)
    View commentTextView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activityRef =  (AddViewReviewActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View addReviewView = inflater.inflate(R.layout.fragment_add_review, container, false);
        ButterKnife.bind(this, addReviewView);

//        setHasOptionsMenu(true);
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
    }

    @Override
    public void onClick(View v) {
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
