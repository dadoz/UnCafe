package com.application.material.takeacoffee.app.fragments;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.GridView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.application.material.takeacoffee.app.*;
import com.application.material.takeacoffee.app.adapters.CoffeeMachineGridAdapter;
import com.application.material.takeacoffee.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.takeacoffee.app.fragments.interfaces.OnLoadViewHandlerInterface;
import com.application.material.takeacoffee.app.fragments.interfaces.SetActionBarInterface;
import com.application.material.takeacoffee.app.models.CoffeeMachine;
import com.application.material.takeacoffee.app.models.CoffeeMachineStatus;
import com.application.material.takeacoffee.app.parsers.JSONParserToObject;
import com.application.material.takeacoffee.app.services.HttpIntentService;
import com.application.material.takeacoffee.app.singletons.BusSingleton;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by davide on 3/13/14.
 */
public class CoffeeMachineFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String TAG = "coffeeMachineFragment";
    public static final String COFFEE_MACHINE_FRAG_TAG = "COFFEE_MACHINE_FRAG_TAG";
    private static FragmentActivity mainActivityRef;
    private View coffeeMachineView;

//    @InjectView(R.id.settingsLayoutId) LinearLayout settingsLayout;
//    @InjectView(R.id.emptyCoffeeMachineLayoutId) View emptyCoffeeMachineView;
    @InjectView(R.id.coffeeMachineGridLayoutId) GridView coffeeMachineGridLayout;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (! (activity instanceof OnLoadViewHandlerInterface)) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLoadViewHandlerInterface");
        }
        if (! (activity instanceof OnChangeFragmentWrapperInterface)) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLoadViewHandlerInterface");
        }

        mainActivityRef =  (CoffeeMachineActivity) activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        coffeeMachineView = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_coffee_machine, container, false);
        ButterKnife.inject(this, coffeeMachineView);
        setHasOptionsMenu(true);
        initOnLoadView();
        return coffeeMachineView;
    }

    @Override
    public void onResume(){
        BusSingleton.getInstance().register(this);
        super.onResume();
    }

    @Override
    public void onPause(){
        BusSingleton.getInstance().unregister(this);
        super.onPause();
    }

    public void initOnLoadView() {
        //initOnLoadView
        ((OnLoadViewHandlerInterface) mainActivityRef).initOnLoadView();

//        getLoaderManager().initLoader(COFFEE_MACHINE_REQUEST.ordinal(), null, this)
//                .forceLoad();

        //request coffee machine list
        HttpIntentService.coffeeMachineRequest(mainActivityRef);
    }

    public void initView(ArrayList<CoffeeMachine> coffeeMachineList) {
        //initOnLoadView
        ((OnLoadViewHandlerInterface) mainActivityRef).hideOnLoadView();

        //set action bar view
        ((SetActionBarInterface) mainActivityRef)
                .setActionBarCustomViewById(R.id.customActionBarUserLayoutId, null);
        ((SetActionBarInterface) mainActivityRef)
                .setCustomNavigation(CoffeeMachineFragment.class);

        if(coffeeMachineList == null) {
            Log.e(TAG, "empty data - show empty list");
            return;
        }

        coffeeMachineGridLayout.setAdapter(new CoffeeMachineGridAdapter(this.getActivity(),
                R.layout.coffee_machine_template, coffeeMachineList));
        coffeeMachineGridLayout.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        CoffeeMachine coffeeMachine = (CoffeeMachine) adapterView.getAdapter().getItem(position);
        Bundle bundle = new Bundle();
//        bundle.putString(CoffeeMachine.COFFEE_MACHINE_ID_KEY, coffeeMachine.getId());
        bundle.putParcelable(CoffeeMachine.COFFEE_MACHINE_OBJ_KEY, coffeeMachine);

        ((OnChangeFragmentWrapperInterface) mainActivityRef)
                .changeFragment(new ReviewListFragment(), bundle, ReviewListFragment.REVIEW_LIST_FRAG_TAG);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.coffee_machine, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_position:
                ((OnChangeFragmentWrapperInterface) mainActivityRef)
                        .changeFragment(new MapFragment(), null,
                                MapFragment.MAP_FRAG_TAG);
                break;
            case R.id.action_settings:
                ((OnChangeFragmentWrapperInterface) mainActivityRef)
                        .changeFragment(new SettingListFragment(), null,
                                SettingListFragment.SETTING_LIST_FRAG_TAG);
                break;
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Subscribe
    public void onNetworkRespose(ArrayList<CoffeeMachine> coffeeMachinesList){
        Log.d(TAG, "get response from bus");
        ((OnLoadViewHandlerInterface) mainActivityRef).hideOnLoadView();

        if(coffeeMachinesList == null) {
            //TODO handle adapter with empty data
            return;
        }

        initView(coffeeMachinesList);
    }

}
