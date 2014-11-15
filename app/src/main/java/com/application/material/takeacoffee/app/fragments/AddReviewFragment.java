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
import com.application.material.takeacoffee.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.takeacoffee.app.fragments.interfaces.OnLoadViewHandlerInterface;
import com.application.material.takeacoffee.app.models.CoffeeMachine;


/**
 * Created by davide on 14/11/14.
 */
public class AddReviewFragment extends Fragment {
    private CoffeeMachineActivity mainActivityRef;
    private Bundle bundle;
    private CoffeeMachine coffeeMachine;
    private String coffeeMachineId;
    private View addReviewView;

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
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        //get all bundle
        bundle = getArguments();
        coffeeMachine = bundle.getParcelable(CoffeeMachine.COFFEE_MACHINE_OBJ_KEY);
        //TODO redundant
        coffeeMachineId = coffeeMachine.getId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        addReviewView = getActivity().getLayoutInflater().inflate(R.layout.card_view_appcomapat_v7, container, false);
        ButterKnife.inject(this, addReviewView);
        initOnLoadView();
        return addReviewView;
    }

    private void initOnLoadView() {
        //initOnLoadView
        mainActivityRef.initOnLoadView();
        initView();
    }

    private void initView() {
        mainActivityRef.hideOnLoadView();
    }
}
