package com.application.material.takeacoffee.app.adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.adapters.ReviewListAdapter;
import com.application.material.takeacoffee.app.models.Setting;
import com.neopixl.pixlui.components.textview.TextView;

import java.util.ArrayList;

/**
 * Created by davide on 17/11/14.
 */
public class SettingListAdapter extends ArrayAdapter<Setting> {
    private final ArrayList<Setting> settingList;
    private final Context context;
    private String TAG = "SettingListAdapter";

    public SettingListAdapter(Context context, int resource, ArrayList<Setting> settingList) {
        super(context, resource, settingList);
        this.settingList = settingList;
        this.context = context;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        Setting settingObj = settingList.get(position);

        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.settings_template, parent, false);
        }

        ((ImageView) ((ViewGroup) convertView).getChildAt(0))
                .setImageDrawable(context
                        .getResources()
                        .getDrawable(settingObj.getIconResourceId()));
        ((TextView) ((ViewGroup) convertView).getChildAt(1)).setText(settingObj.getName());

        switch (settingObj.getPosition()) {
            case 0:
                Log.e(TAG, "case 0");
                break;
            case 1:
                Log.e(TAG, "case 1");
                break;
            case 2:
                Log.e(TAG, "case 2");
                break;
            case 3:
                Log.e(TAG, "case 3");
                break;
        }

        return convertView;
    }
}
