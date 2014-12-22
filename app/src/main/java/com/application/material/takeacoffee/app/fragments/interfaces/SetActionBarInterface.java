package com.application.material.takeacoffee.app.fragments.interfaces;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by davide on 07/11/14.
 */
public interface SetActionBarInterface {
    public void setActionBarCustomViewById(int id, Object data);
    public void setCustomNavigation(Class<?> id);
    public void setActionBarEditSelection(boolean itemSelected);
    public boolean isItemSelected();
    public void updateSelectedItem(AdapterView.OnItemLongClickListener listener,
                                   ListView listView, View view);


    }
