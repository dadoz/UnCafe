package com.application.material.takeacoffee.app.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.models.Setting;
import com.application.material.takeacoffee.app.utils.SharedPrefManager;
import com.application.material.takeacoffee.app.utils.Utils;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Locale;

public class SettingListAdapter extends ArrayAdapter<Setting> {
    private final ArrayList<Setting> settingList;
    private final String location;

    public SettingListAdapter(Context context, int resource, ArrayList<Setting> settingList,
                              String location) {
        super(context, resource, settingList);
        this.settingList = settingList;
        this.location = location;
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
                .inflate(getLayoutResourceByPos(position), null);
        ((TextView) convertView.findViewById(R.id.settingLabelId))
                .setText(settingList.get(position).getName());

        initViews(position, convertView);
        return convertView;
    }

    /**
     *
     * @param position
     * @return
     */
    private int getLayoutResourceByPos(int position) {
        switch (position) {
            case 0:
                return R.layout.item_location_setting;
            default:
                return R.layout.item_setting;
        }
    }

    /**
     * @param position
     * @param convertView
     */
    private void initViews(int position, View convertView) {
        switch (position) {
            case 0:
                initLocationView(convertView);
                break;
            case 1:
                initLastSyncView(convertView);
                break;
            case 4:
                initLastLanguage(convertView);
                break;
            default:
                hideDescriptionLabel(convertView);
        }
    }

    /**
     *
     * @param convertView
     */
    private void initLastLanguage(View convertView) {
        ((TextView) convertView.findViewById(R.id.settingDescriptionTextviewId))
                .setText(Locale.getDefault().getDisplayLanguage());
    }

    /**
     *
     * @param convertView
     */
    private void hideDescriptionLabel(View convertView) {
        convertView.findViewById(R.id.settingDescriptionTextviewId).setVisibility(View.GONE);
    }

    /**
     *
     * @param convertView
     */
    private void initLastSyncView(View convertView) {
        String timestamp = SharedPrefManager.getInstance(new WeakReference<>(getContext()))
                .getValueByKey(SharedPrefManager.TIMESTAMP_REQUEST_KEY);
        ((TextView) convertView.findViewById(R.id.settingDescriptionTextviewId))
                .setText(Utils.convertLastUpdateFromTimestamp(timestamp));
    }

    /**
     *
     * @param convertView
     */
    private void initLocationView(View convertView) {
        ((TextView) convertView.findViewById(R.id.settingLocationId)).setText(location);
    }
}
