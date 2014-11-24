package com.application.material.takeacoffee.app.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.*;
import butterknife.ButterKnife;
import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.takeacoffee.app.fragments.interfaces.OnLoadViewHandlerInterface;
import com.application.material.takeacoffee.app.fragments.interfaces.SetActionBarInterface;
import com.application.material.takeacoffee.app.models.CoffeeMachine;

/**
 * Created by davide on 20/11/14.
 */
public class StatusFragment extends Fragment {

    private Activity mainActivityRef;
    private View statusView;
    public static final String STATUS_FRAG_TAG = "STATUS_FRAG_TAG";
    private CoffeeMachine coffeeMachine;
    private Bundle bundle;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (! (activity instanceof OnLoadViewHandlerInterface)) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLoadViewHandlerInterface");
        }

        if(! (activity instanceof OnChangeFragmentWrapperInterface)) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLoadViewHandlerInterface");
        }

        mainActivityRef = activity;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //get all bundle
        bundle = getArguments();
        coffeeMachine = bundle.getParcelable(CoffeeMachine.COFFEE_MACHINE_OBJ_KEY); //NULL

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        statusView = inflater.inflate(R.layout.fragment_status, container, false);
        ButterKnife.inject(statusView);
        initOnLoadView();

        return statusView;
    }

    private void initOnLoadView() {
//        //initOnLoadView
//        ((OnLoadViewHandlerInterface) mainActivityRef).initOnLoadView();
//
//        getLoaderManager().initLoader(COFFEE_MACHINE_REQUEST.ordinal(), null, this)
//                .forceLoad();

        initView();
    }

    private void initView() {
//        coffeeMachine = new CoffeeMachine("1", "fake0", "address", null);
        ((SetActionBarInterface) mainActivityRef)
                .setActionBarCustomViewById(R.id.customActionBarCoffeeMachineLayoutId,
                        coffeeMachine);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
