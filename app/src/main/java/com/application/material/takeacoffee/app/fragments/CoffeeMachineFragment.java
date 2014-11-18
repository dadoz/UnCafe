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
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.application.material.takeacoffee.app.*;
import com.application.material.takeacoffee.app.adapters.CoffeeMachineGridAdapter;
import com.application.material.takeacoffee.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.takeacoffee.app.fragments.interfaces.OnLoadViewHandlerInterface;
import com.application.material.takeacoffee.app.fragments.interfaces.SetActionBarInterface;
import com.application.material.takeacoffee.app.loaders.RetrofitLoader;
import com.application.material.takeacoffee.app.loaders.RestResponse;
import com.application.material.takeacoffee.app.models.CoffeeMachine;
import com.application.material.takeacoffee.app.parsers.ParserToJavaObject;

import java.util.ArrayList;
import static com.application.material.takeacoffee.app.loaders.RetrofitLoader.HTTPActionRequestEnum.*;


/**
 * Created by davide on 3/13/14.
 */
public class CoffeeMachineFragment extends Fragment implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<RestResponse> {
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

    public void initOnLoadView() {
        //initOnLoadView
        ((OnLoadViewHandlerInterface) mainActivityRef).initOnLoadView();

        getLoaderManager().initLoader(COFFEE_MACHINE_REQUEST.ordinal(), null, this)
                .forceLoad();
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
                .changeFragment(new DashboardReviewFragment(), bundle, DashboardReviewFragment.DASHBOARD_REVIEW_FRAG_TAG);
    }

    @Override
    public Loader<RestResponse> onCreateLoader(int ordinal, Bundle params) {
//        Uri action = Uri.parse(params.getString("action"));
//        String requestType = params.getString("requestType");

        //get action
        String action = RetrofitLoader.getActionByActionRequestEnum(ordinal);
        return new RetrofitLoader(mainActivityRef, action, params);
    }

    @Override
    public void onLoadFinished(Loader<RestResponse> restResponseLoader, RestResponse restResponse) {
        Log.e(TAG, "hey - finish load resources");

        try {
//            String filename = "coffee_machines.json";
//            String data = RetrofitLoader.getJSONDataMockup(this.getActivity(), filename);
//            ArrayList<CoffeeMachine> coffeeMachinesList = ParserToJavaObject.coffeeMachineParser(data);

            ArrayList<CoffeeMachine> coffeeMachinesList = (ArrayList<CoffeeMachine>) restResponse.getParsedData();
            initView(coffeeMachinesList);
/*            initView(coffeeMachinesList);*/
        } catch (Exception e) {
            ((OnLoadViewHandlerInterface) mainActivityRef).hideOnLoadView();
            String filename = "coffee_machines.json";
            String data = RetrofitLoader.getJSONDataMockup(this.getActivity(), filename);
            ArrayList<CoffeeMachine> coffeeMachinesList = ParserToJavaObject.coffeeMachineParser(data);
            initView(coffeeMachinesList);
//            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

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
                Toast.makeText(mainActivityRef, "map calling", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
//        savedInstanceState.putString(CoffeeMachineActivity.CURRENT_FRAGMENT_TAG,
//                CoffeeMachineFragment.COFFEE_MACHINE_FRAG_TAG);
    }

}
