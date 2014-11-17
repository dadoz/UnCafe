package com.application.material.takeacoffee.app.fragments;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.widget.ArrayAdapter;
import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.adapters.ReviewListAdapter;
import com.application.material.takeacoffee.app.models.Setting;

import java.util.ArrayList;

/**
 * Created by davide on 17/11/14.
 */
public class SettingListAdapter extends ArrayAdapter<Setting> {
    public SettingListAdapter(FragmentActivity activity, int resource, ArrayList<Setting> reviewList, String coffeeMachineId) {
        super(activity.getApplicationContext(), resource, R.id.reviewCommentTextId, reviewList);

    }
}
