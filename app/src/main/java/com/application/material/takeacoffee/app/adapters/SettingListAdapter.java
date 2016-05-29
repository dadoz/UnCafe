package com.application.material.takeacoffee.app.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.models.Setting;


import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class SettingListAdapter extends ArrayAdapter<Setting> implements View.OnClickListener {
    private final ArrayList<Setting> settingList;
    private final WeakReference<CustomItemClickListener> listener;
    private final String location;

    public SettingListAdapter(Context context, int resource, ArrayList<Setting> settingList,
                              WeakReference<CustomItemClickListener> lst, String location) {
        super(context, resource, settingList);
        this.settingList = settingList;
        this.listener = lst;
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
                .inflate(position == 0 ? R.layout.item_location_setting : R.layout.item_setting, null);
        ((TextView) convertView.findViewById(R.id.settingLabelId))
                .setText(settingList.get(position).getName());

        initViews(position, convertView);
        return convertView;
    }

    /**
     * @param position
     * @param convertView
     */
    private void initViews(int position, View convertView) {
        switch (position) {
            case 0:
                initResetLocationView(convertView);
                break;
        }
    }

    /**
     *
     * @param convertView
     */
    private void initResetLocationView(View convertView) {
        ((TextView) convertView.findViewById(R.id.settingLocationId)).setText(location);
        Button clearButton = (Button) convertView.findViewById(R.id.settingClearButtonId);
        clearButton .setOnClickListener(this);
        clearButton.getBackground().setColorFilter(ContextCompat.getColor(getContext(),
                R.color.material_brown800), PorterDuff.Mode.MULTIPLY);
    }

    @Override
    public void onClick(View v) {
        listener.get().onItemClick();
    }

    /**
     *
     */
    public interface CustomItemClickListener {
        void onItemClick();
    }

}
