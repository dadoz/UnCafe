package com.application.material.takeacoffee.app.fragments.interfaces;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by davide on 07/11/14.
 */
public interface OnChangeFragmentWrapperInterface {
    public void changeFragment(Fragment fragment, Bundle bundle, String tag);
    public void setFragTag(String tag);
    public void startActivityWrapper(Class activityClassName, int requestCode, Bundle bundle);

}
