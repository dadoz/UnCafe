//package com.application.material.takeacoffee.app.adapters;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.*;
//import com.application.commons.Common;
//import com.application.models.Setting;
//import com.application.takeacoffee.R;
//
//import java.util.ArrayList;
//
///**
// * Created by davide on 02/10/14.
// */
//public class SettingListAdapter extends ArrayAdapter<Setting> {
//    private final Context context;
//    private ArrayList<Setting> settingList;
//
//    public SettingListAdapter(Context context, int resource, ArrayList<Setting> settingList) {
//        super(context, resource, settingList);
//        this.settingList = settingList;
//        this.context = context;
//    }
//
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        Setting settingObj = settingList.get(position);
//
//        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        final View rowView = inflater.inflate(R.layout.settings_template, parent, false);
//
//        ((ImageView) ((ViewGroup) rowView).getChildAt(0)).setImageDrawable(context.getResources().getDrawable(settingObj.getIconResourceId()));
//        ((TextView) ((ViewGroup) rowView).getChildAt(1)).setText(settingObj.getName());
//
//        switch (settingObj.getPosition()) {
//            case 0:
//                CheckBox checkBox = new CheckBox(getContext());
//                ((ViewGroup) rowView.findViewById(R.id.checkboxContainerLayoutId)).addView(checkBox);
//                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton compoundButton, boolean status) {
//                        compoundButton.setChecked(status);
//                        if(compoundButton.isChecked()) {
//                            Common.displayError(context.getApplicationContext(), "Notification enabled");
//                        } else {
//                            Common.displayError(context.getApplicationContext(), "Notification disabled");
//                        }
//                    }
//                });
//                break;
//            case 1:
//                break;
//            case 2:
//                break;
//            case 3:
//                break;
//        }
//
//        Common.setCustomFont(rowView, context.getAssets());
//        return rowView;
//    }
//}
