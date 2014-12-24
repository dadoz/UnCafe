package com.application.material.takeacoffee.app.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.application.material.takeacoffee.app.CoffeeMachineActivity;
import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.adapters.SettingListAdapter;
import com.application.material.takeacoffee.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.takeacoffee.app.fragments.interfaces.OnLoadViewHandlerInterface;
import com.application.material.takeacoffee.app.fragments.interfaces.SetActionBarInterface;
import com.application.material.takeacoffee.app.models.Setting;

import java.util.ArrayList;

/**
 * Created by davide on 08/04/14.
 */
public class SettingListFragment extends Fragment
        implements AdapterView.OnItemLongClickListener,
        AdapterView.OnItemClickListener, DialogInterface.OnClickListener {
    private static final String TAG = "ReviewListFragment";
    private static FragmentActivity mainActivityRef = null;
    public static String SETTING_LIST_FRAG_TAG = "SETTING_LIST_FRAG_TAG";

    private View settingListView;
    private String coffeeMachineId;
    private Bundle bundle;
    @InjectView(R.id.settingsContainerListViewId) ListView listView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof OnLoadViewHandlerInterface)) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLoadViewHandlerInterface");
        }
        if (!(activity instanceof OnChangeFragmentWrapperInterface)) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLoadViewHandlerInterface");
        }
        mainActivityRef =  (CoffeeMachineActivity) activity;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        settingListView = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.inject(this, settingListView);
        setHasOptionsMenu(true);
        initOnLoadView();
        return settingListView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        //get all bundle
        bundle = getArguments();
    }

    private void initOnLoadView() {
        ArrayList<Setting> settingList = new ArrayList<Setting>();
        //STATIC DATA :D load form asset even if you want or provide it by this list as u wish
        settingList.add(new Setting("ID", 0, R.drawable.monsieur_icon, "setting 1"));
        settingList.add(new Setting("ID", 1, R.drawable.drink_icon, "setting 2"));
        settingList.add(new Setting("ID", 2, R.drawable.crown_icon, "setting 3"));
        settingList.add(new Setting("ID", 3, R.drawable.barometer_icon, "setting 4"));
        initView(settingList);
    }

    public void initView(ArrayList<Setting> reviewList) {
        ((OnLoadViewHandlerInterface) mainActivityRef).hideOnLoadView();

        //action bar
        ((SetActionBarInterface) mainActivityRef)
                .setActionBarCustomViewById(R.id.customActionSettingsLayoutId, null);
        ((SetActionBarInterface) mainActivityRef)
                .setCustomNavigation(SettingListFragment.class);


        SettingListAdapter settingListAdapter = new SettingListAdapter(mainActivityRef,
                R.layout.review_template, reviewList);
        listView.setAdapter(settingListAdapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.review_list_no_edit, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        }
        return true;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Log.e(TAG, "dismiss");
    }

}

