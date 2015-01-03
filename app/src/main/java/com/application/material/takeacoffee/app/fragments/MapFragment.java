package com.application.material.takeacoffee.app.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.application.material.takeacoffee.app.CoffeeMachineActivity;
import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.takeacoffee.app.fragments.interfaces.OnLoadViewHandlerInterface;
import com.application.material.takeacoffee.app.fragments.interfaces.SetActionBarInterface;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by davide on 19/11/14.
 */
public class MapFragment extends Fragment {
    private static final String TAG = "MapFragment";
    private static FragmentActivity mainActivityRef = null;
    public static String MAP_FRAG_TAG = "MAP_FRAG_TAG";
    private View mapView;
    private Bundle bundle;
//    private GoogleMap mMap;

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
        mapView = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.inject(this, mapView);
        setHasOptionsMenu(true);
        initView(); //NO LOAD DATA IS REQUIRED
        return mapView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        //get all bundle
        bundle = getArguments();
    }

//    private void initOnLoadView() {
//        initView();
//    }

    public void initView() {
        //action bar
        ((SetActionBarInterface) mainActivityRef)
                .setActionBarCustomViewById(R.id.customActionBarMapLayoutId, null);
        ((SetActionBarInterface) mainActivityRef)
                .setCustomNavigation(MapFragment.class);

//        try {
//            SupportMapFragment supportMapFragment = new SupportMapFragment() {
//                @Override
//                public void onActivityCreated(Bundle savedInstanceState) {
//                    //modify your map here
//                    Log.e(TAG, "map loaded");
////                initView(this.getMap());
//                }
//            };
//            GoogleMap map = ((SupportMapFragment) supportMapFragment.getFragmentManager().findFragmentById(R.id.map)).getMap();
//        if(mainActivityRef.getSupportFragmentManager().findFragmentById(R.id.map) == null) {
//            Log.e(TAG, "hey it seems you have not added map frag!");
//            return;
//        }
//        mMap = ((SupportMapFragment) mainActivityRef.getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
//        if((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map) == null) {
//            Log.e(TAG, "hey it seems you have not added map frag!");
//            return;
//        }
//
//
//        GoogleMap map = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
//
//            ArrayList<LatLng> markerList = new ArrayList<LatLng>();
//            markerList.add(new LatLng(45.0631, 7.6611));
//
//            if (map == null) {
//                Log.e(TAG, "map is null sorry - empty view");
//                return;
//            }
//            Log.e(TAG, " - empty view");
//
//            //set marker and settings on map
//            map.setMyLocationEnabled(true);
//            map.addMarker(new MarkerOptions()
//                    .position(markerList.get(0))
//                    .draggable(true));

//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void onDestroyView() {
        super.onDestroyView();
//        FragmentManager fm = getActivity().getSupportFragmentManager();
//        Fragment fragment = (fm.findFragmentById(R.id.map));
//        FragmentTransaction ft = fm.beginTransaction();
//        ft.remove(fragment);
//        ft.commit();
    }

}
