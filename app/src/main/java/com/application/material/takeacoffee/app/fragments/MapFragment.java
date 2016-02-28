package com.application.material.takeacoffee.app.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
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
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;

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

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        mainActivityRef = getActivity();
        mapView = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, mapView);

        initView();
        return mapView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        bundle = getArguments();
    }

    public void initActionBar() {
        setHasOptionsMenu(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle("Map");
    }

    /**
     * init view
     */
    public void initView() {
        initActionBar();
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

    /**
     * destroy view
     */
    public void onDestroyView() {
        super.onDestroyView();
    }

}
