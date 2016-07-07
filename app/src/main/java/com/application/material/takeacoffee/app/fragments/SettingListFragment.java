package com.application.material.takeacoffee.app.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.application.material.takeacoffee.app.PlacesActivity;
import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.adapters.SettingListAdapter;
import com.application.material.takeacoffee.app.models.Setting;
import com.application.material.takeacoffee.app.utils.SharedPrefManager;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class SettingListFragment extends Fragment
        implements AdapterView.OnItemClickListener {
    public static final String SETTING_LIST_FRAG_TAG = "SETTING_LIST_FRAG_TAG";
    @Bind(R.id.settingsContainerListViewId)
    ListView listView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
    
    @Override
    public void onViewCreated(View view, @NonNull Bundle savedInstance) {
        ButterKnife.bind(this, view);

        ((PlacesActivity) getActivity()).setCurrentFragmentTag(SETTING_LIST_FRAG_TAG);
        initView();
    }

    /**
     *
     */
    public void initView() {
        ActionBar actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setTitle(getString(R.string.setting_menu));
        }

        ArrayList<Setting> settingList = getData();
        initListView(settingList);
    }

    /**
     * init data
     */
    public ArrayList<Setting> getData() {
        ArrayList<Setting> settingList = new ArrayList<>();
        settingList.add(new Setting("ID", 0, R.drawable.ic_check_white_48dp,
                getString(R.string.location_settings)));
        settingList.add(new Setting("ID", 1, R.drawable.ic_check_white_48dp,
                getString(R.string.last_sync_settings)));
        settingList.add(new Setting("ID", 2, R.drawable.ic_check_white_48dp,
                getString(R.string.rate_now_settings)));
        settingList.add(new Setting("ID", 3, R.drawable.ic_check_white_48dp,
                getString(R.string.contact_settings)));
        settingList.add(new Setting("ID", 4, R.drawable.ic_check_white_48dp,
                getString(R.string.language_settings)));
        settingList.add(new Setting("ID", 5, R.drawable.ic_check_white_48dp,
                getString(R.string.version_settings) + " " +getVersionName()));
        return settingList;
    }

    /**
     * init list view
     * @param settingList
     */
    public void initListView(ArrayList<Setting> settingList) {
        SettingListAdapter settingListAdapter = new SettingListAdapter(getActivity(),
                R.layout.item_review, settingList,
                SharedPrefManager.getInstance(new WeakReference<>(getContext()))
                        .getValueByKey(SharedPrefManager.LOCATION_NAME_SHAREDPREF_KEY));
        listView.setAdapter(settingListAdapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Log.e("SETTING", "hey click item");
        switch (position) {
            case 2:
                rateByIntent();
                break;
            case 3:
                sendEmailByIntent();
                break;
        }
    }

    /**
     * rete on market by intent
     */
    private void rateByIntent() {
        Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" +
                getContext().getPackageName()));
        boolean marketFound = false;

        // find all applications able to handle our rateIntent
        final List<ResolveInfo> otherApps =
                getContext().getPackageManager().queryIntentActivities(rateIntent, 0);
        for (ResolveInfo otherApp : otherApps) {
            // look for Google Play application
            if (otherApp.activityInfo.applicationInfo.packageName.equals("com.android.vending")) {

                ActivityInfo otherAppActivity = otherApp.activityInfo;
                ComponentName componentName = new ComponentName(
                        otherAppActivity.applicationInfo.packageName,
                        otherAppActivity.name
                );
                rateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                rateIntent.setComponent(componentName);
                getContext().startActivity(rateIntent);
                marketFound = true;
                break;

            }
        }

        // if GP not present on device, open web browser
        if (!marketFound) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" +
                            getContext().getPackageName()));
            getContext().startActivity(webIntent);
        }
    }
    /**
     * send email by intent
     */
    private void sendEmailByIntent() {
        ShareCompat.IntentBuilder.from(getActivity())
                .setType("message/rfc822")
                .addEmailTo(getResources().getString(R.string.contact_email))
                .setSubject(getResources().getString(R.string.contact_subject))
                .setChooserTitle(getString(R.string.sending_email))
                .startChooser();
    }

    /**
     *
     * @return
     */
    public String getVersionName() {
        try {
            return getActivity().getPackageManager()
                    .getPackageInfo(getActivity().getPackageName(), 0)
                    .versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "-";
    }


}

