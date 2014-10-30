package com.application.material.takeacoffee.app.fragments;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import butterknife.InjectView;
import com.application.material.takeacoffee.app.*;
import com.application.material.takeacoffee.app.adapters.CoffeeMachineGridAdapter;
import com.application.material.takeacoffee.app.loaders.RestLoaderRetrofit;
import com.application.material.takeacoffee.app.loaders.RestResponse;
import com.application.material.takeacoffee.app.models.CoffeeMachine;

import java.util.ArrayList;


/**
 * Created by davide on 3/13/14.
 */
public class CoffeeMachineFragment extends Fragment implements AdapterView.OnItemClickListener,
        LoaderManager.LoaderCallbacks<RestResponse> {
    private static final String TAG = "coffeeMachineFragment";
    private static FragmentActivity mainActivityRef;
    private View coffeeMachineView;

    @InjectView(R.id.settingsLayoutId) LinearLayout settingsLayout;
    @InjectView(R.id.emptyCoffeeMachineLayoutId) View emptyCoffeeMachineView;
    @InjectView(R.id.coffeeMachineGridLayoutId) GridView coffeeMachineGridLayout;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityRef =  (CoffeeMachineActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        coffeeMachineView = getActivity().getLayoutInflater().inflate(R.layout.fragment_coffee_machine, container, false);
        return coffeeMachineView;
    }

    private void initOnLoadView() {
    }

    public void initView(ArrayList<CoffeeMachine> coffeeMachineList) {
//        coffeeAppController.getCoffeeMachineList()
        coffeeMachineGridLayout.setAdapter(new CoffeeMachineGridAdapter(this.getActivity(),
                R.layout.coffe_machine_template, coffeeMachineList));
        coffeeMachineGridLayout.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }


    @Override
    public Loader<RestResponse> onCreateLoader(int verb, Bundle params) {
//        Uri action = Uri.parse(params.getString("action"));
//        String requestType = params.getString("requestType");

        String action = "action";
        return new RestLoaderRetrofit(this.getActivity(), action, params);
    }

    @Override
    public void onLoadFinished(Loader<RestResponse> restResponseLoader, RestResponse restResponse) {
        Log.e(TAG, "hey - finish load resources");

        try {
            String filename = "coffee_machines.json";
            String data = RestResponse.getJSONDataMockup(this.getActivity(), filename);
//            String data = restResponse.getData();
            ArrayList<CoffeeMachine> coffeeMachinesList = restResponse.coffeeMachineParser(data);
//            coffeeAppController.setCoffeeMachineList(coffeeMachinesList);
            initView(coffeeMachinesList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }




}
