package com.application.material.takeacoffee.app.fragments;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.GridView;

import butterknife.Bind;
import butterknife.ButterKnife;
import com.application.material.takeacoffee.app.*;
import com.application.material.takeacoffee.app.adapters.CoffeeMachineGridAdapter;
import com.application.material.takeacoffee.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.takeacoffee.app.fragments.interfaces.OnLoadViewHandlerInterface;
import com.application.material.takeacoffee.app.models.CoffeeMachine;
import com.application.material.takeacoffee.app.services.HttpIntentService;
import com.application.material.takeacoffee.app.singletons.BusSingleton;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

/**
 * Created by davide on 3/13/14.
 */
public class CoffeePlacesFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String TAG = "coffeeMachineFragment";
    public static final String COFFEE_MACHINE_FRAG_TAG = "COFFEE_MACHINE_FRAG_TAG";
    private static FragmentActivity mainActivityRef;
    private View coffeeMachineView;

//    @InjectView(R.id.settingsLayoutId) LinearLayout settingsLayout;
//    @InjectView(R.id.emptyCoffeeMachineLayoutId) View emptyCoffeeMachineView;
    @Bind(R.id.coffeeMachineGridLayoutId) GridView coffeeMachineGridLayout;
    private ArrayList<CoffeeMachine> coffeeMachineList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof OnLoadViewHandlerInterface)) {
            throw new ClassCastException(context.toString()
                    + " must implement OnLoadViewHandlerInterface");
        }
        if (!(context instanceof OnChangeFragmentWrapperInterface)) {
            throw new ClassCastException(context.toString()
                    + " must implement OnLoadViewHandlerInterface");
        }
        mainActivityRef = (CoffeePlacesActivity) context;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        coffeeMachineView = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_coffee_machine, container, false);
        ButterKnife.bind(this, coffeeMachineView);
        initView();
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

        //request coffee machine list
        HttpIntentService.coffeeMachineRequest(mainActivityRef);
    }

    public void initView() {
        initActionBar();
        setHasOptionsMenu(true);

        if (BuildConfig.DEBUG) {
            coffeeMachineList = getCoffeeMachineListTest();
        }
//        //initOnLoadView
//        ((OnLoadViewHandlerInterface) mainActivityRef).hideOnLoadView();
//
//        //set action bar view
//        ((SetActionBarInterface) mainActivityRef)
//                .setActionBarCustomViewById(R.id.customActionBarUserLayoutId, null);
//        ((SetActionBarInterface) mainActivityRef)
//                .setCustomNavigation(CoffeePlacesFragment.class);

        coffeeMachineGridLayout.setAdapter(new CoffeeMachineGridAdapter(this.getActivity(),
                R.layout.coffee_machine_template, coffeeMachineList));
        coffeeMachineGridLayout.setOnItemClickListener(this);
    }

    /**
     * init action bar
     */
    public void initActionBar() {
        ((AppCompatActivity) getActivity()).getSupportActionBar()
            .setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle(getString(R.string.app_name));

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        CoffeeMachine coffeeMachine = (CoffeeMachine) adapterView.getAdapter().getItem(position);
        Bundle bundle = new Bundle();
//        bundle.putString(CoffeeMachine.COFFEE_MACHINE_ID_KEY, coffeeMachine.getId());
        bundle.putParcelable(CoffeeMachine.COFFEE_MACHINE_OBJ_KEY, coffeeMachine);

//        ((OnChangeFragmentWrapperInterface) mainActivityRef)
//                .changeFragment(new ReviewListFragment(), bundle, ReviewListFragment.REVIEW_LIST_FRAG_TAG);
        changeActivity();
    }

    /**
     * change actiity on reviewList
     */
    private void changeActivity() {
        startActivity(new Intent(getActivity(), ReviewListActivity.class));
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

        this.coffeeMachineList = coffeeMachinesList;
        initView();
    }

    /**
     *
     * @return
     */
    public ArrayList<CoffeeMachine> getCoffeeMachineListTest() {
        ArrayList<CoffeeMachine> tmp = new ArrayList<CoffeeMachine>();
        tmp.add(new CoffeeMachine("0", "balllala", "hey", null));
        tmp.add(new CoffeeMachine("1", "balllala", "hey", null));
        tmp.add(new CoffeeMachine("2", "balllala", "hey", null));
        tmp.add(new CoffeeMachine("3", "balllala", "hey", null));
        tmp.add(new CoffeeMachine("4", "balllala", "hey", null));
        return tmp;
    }
}
