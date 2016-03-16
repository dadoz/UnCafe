package com.application.material.takeacoffee.app.adapters;

import android.content.Context;
import android.content.pm.LabeledIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.models.Setting;


import java.util.ArrayList;

public class SettingListAdapter extends ArrayAdapter<Setting> {
    private final ArrayList<Setting> settingList;

    public SettingListAdapter(Context context, int resource, ArrayList<Setting> settingList) {
        super(context, resource, settingList);
        this.settingList = settingList;
    }

    /**
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = ((LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.settings_template, null);
        Setting settingObj = settingList.get(position);
        ((TextView) convertView.findViewById(R.id.settingLabelId))
                .setText(settingObj.getName());
        return convertView;
    }
}
