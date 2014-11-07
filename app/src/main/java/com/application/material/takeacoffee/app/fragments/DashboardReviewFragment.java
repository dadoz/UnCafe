package com.application.material.takeacoffee.app.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.application.material.takeacoffee.app.CoffeeMachineActivity;
import com.application.material.takeacoffee.app.R;

import static com.application.material.takeacoffee.app.loaders.RetrofitLoader.HTTPActionRequestEnum.COFFEE_MACHINE_REQUEST;

/**
 * Created by davide on 07/11/14.
 */
public class DashboardReviewFragment extends Fragment{
    private CoffeeMachineActivity mainActivityRef;
    private View dashboardReviewView;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        dashboardReviewView = getActivity().getLayoutInflater().inflate(R.layout.fragment_dashboard_review, container, false);
        ButterKnife.inject(this, dashboardReviewView);
        initOnLoadView();
        return dashboardReviewView;
    }

    private void initOnLoadView() {
//        getLoaderManager().initLoader(COFFEE_MACHINE_REQUEST.ordinal(), null, this).forceLoad();
        //initOnLoadView
        mainActivityRef.initOnLoadView();

        //after loading smthing if u need
        initView();
    }

    private void initView() {
        mainActivityRef.hideOnLoadView();

    }

}
