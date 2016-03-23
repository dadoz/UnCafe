package com.application.material.takeacoffee.app.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.*;
import android.widget.*;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.application.material.takeacoffee.app.BuildConfig;
import com.application.material.takeacoffee.app.ReviewListActivity;
import com.application.material.takeacoffee.app.EditReviewActivity;
import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.adapters.ReviewListAdapter;
import com.application.material.takeacoffee.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.takeacoffee.app.fragments.interfaces.SetActionBarInterface;
import com.application.material.takeacoffee.app.models.*;
import com.application.material.takeacoffee.app.services.HttpIntentService;
import com.application.material.takeacoffee.app.singletons.BusSingleton;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.*;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.*;


/**
 * Created by davide on 08/04/14.
 */
public class ReviewListFragment extends Fragment implements AdapterView.OnItemLongClickListener,
        AdapterView.OnItemClickListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "ReviewListFragment";
    private static AppCompatActivity mainActivityRef = null;
    private ArrayList<Review> reviewList;
    private Bundle bundle;
    private String coffeePlaceId;

    @Bind(R.id.reviewsContainerListViewId)
    ListView listView;
    @Bind(R.id.swipeRefreshLayoutId)
    SwipeRefreshLayout swipeRefreshLayout;
    private View addReviewFab;
    private ArrayList<CoffeePlace> coffeePlacesList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivityRef =  (ReviewListActivity) context;
    }

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstance
     * @return
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View reviewListView = inflater.inflate(R.layout.fragment_review_list, container, false);
        ButterKnife.bind(this, reviewListView);
        //TODO NOTHING - waiting for bus response :)
        return reviewListView;
    }

    @Override
    public void onResume() {
        BusSingleton.getInstance().register(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        BusSingleton.getInstance().unregister(this);
        super.onPause();
    }

    /**
     * init view to handle review data
     */
    public void initView() {
        if (BuildConfig.DEBUG) {
            reviewList = getReviewListTest();
            coffeePlacesList = getCoffeePlacesListTest();
        }

        CoffeePlace coffeePlace = coffeePlacesList.get(Integer.parseInt(coffeePlaceId));
        initActionbar(coffeePlace.getName());

        addReviewFab = mainActivityRef.findViewById(R.id.addReviewFabId);
        addReviewFab.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        initListView();
    }

    /**
     *
     */
    private void initActionbar(String name) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(name);
    }

    /**
     * init list view
     */
    private void initListView() {
        //        Collections.reverse(reviewList);
        ReviewListAdapter reviewListenerAdapter = new ReviewListAdapter(mainActivityRef,
                this, R.layout.review_template, reviewList, coffeePlaceId);

        //TODO replace with recyclerView :)
//        listView.setEmptyView(emptyView);
        listView.setAdapter(reviewListenerAdapter);

        listView.setOnItemLongClickListener(this);
        listView.setOnItemClickListener(this);
        if (SDK_INT >= LOLLIPOP) {
            listView.setNestedScrollingEnabled(true);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        if(((SetActionBarInterface) mainActivityRef).isItemSelected()) {
            boolean isAllowToEdit = false;
            menuInflater.inflate(isAllowToEdit ? R.menu.edit_review : R.menu.clipboard_review,
                    menu);
            return;
        }
        menuInflater.inflate(R.menu.review_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_coffee_machine_position:
                    Toast.makeText(mainActivityRef, "get machine pos", Toast.LENGTH_SHORT).show();
                ((OnChangeFragmentWrapperInterface) mainActivityRef)
                        .changeFragment(new MapFragment(),
                                bundle, MapFragment.MAP_FRAG_TAG);
                break;

            case R.id.action_edit_icon:
                Toast.makeText(mainActivityRef, "change", Toast.LENGTH_SHORT).show();
                ((OnChangeFragmentWrapperInterface) mainActivityRef)
                        .startActivityWrapper(EditReviewActivity.class,
                                ReviewListActivity.ACTION_EDIT_REVIEW, bundle);
                //deselect Item
//                ((SetActionBarInterface) mainActivityRef)
//                        .updateSelectedItem(this, listView, null, -1);
                break;
            case R.id.action_delete:
                Toast.makeText(mainActivityRef, "change", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(mainActivityRef)
                        .setMessage("Sure to delete this review?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Review review = (Review) bundle.get(Review.REVIEW_OBJ_KEY);
                                HttpIntentService.deleteReviewRequest(mainActivityRef, review.getId());
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                Dialog dialog = builder.create();
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.show();


                //deselect Item
//                ((SetActionBarInterface) mainActivityRef)
//                        .updateSelectedItem(this, listView, null, -1);
                break;

        }
        return true;
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onRefresh() {

    }

    /**
     *
     * @return
     */
    public ArrayList<Review> getReviewListTest() {
        ArrayList<Review> list = new ArrayList<Review>();
        list.add(new Review("0", "heheeheheheh", "balal", 1111, "1", "1", null, null));
        list.add(new Review("1", "blalallalll", "balal", 1111, "1", "1", null, null));
        list.add(new Review("2", "blalallalll2", "balal", 1111, "1", "1", null, null));
        list.add(new Review("3", "blalallall3", "balal", 1111, "1", "1", null, null));
        list.add(new Review("4", "blalallall4", "balal", 1111, "1", "1", null, null));
        list.add(new Review("5", "blalallall5", "balal", 1111, "1", "1", null, null));
        list.add(new Review("6", "blalallall6", "balal", 1111, "1", "1", null, null));
        list.add(new Review("7", "blalallall6", "balal", 1111, "1", "1", null, null));
        list.add(new Review("7", "blalallall7", "balal", 1111, "1", "1", null, null));
        list.add(new Review("8", "blalallall8", "balal", 1111, "1", "1", null, null));
        list.add(new Review("9", "blalallall9", "balal", 1111, "1", "1", null, null));
        list.add(new Review("10", "blalallall10", "balal", 1111, "1", "1", null, null));
        return list;
    }

    /**
     *
     * @return
     */
    public ArrayList<CoffeePlace> getCoffeePlacesListTest() {
        ArrayList<CoffeePlace> tmp = new ArrayList<CoffeePlace>();
        tmp.add(new CoffeePlace("0", "Caffe Vergnano Torino spa Bologna", "Corso Gramsci 7 alesessanrdia", null));
        tmp.add(new CoffeePlace("1", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeePlace("2", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeePlace("3", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeePlace("4", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeePlace("0", "Caffe Vergnano Torino spa Bologna", "Corso Gramsci 7 alesessanrdia", null));
        tmp.add(new CoffeePlace("1", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeePlace("2", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeePlace("3", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeePlace("4", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeePlace("0", "Caffe Vergnano Torino spa Bologna", "Corso Gramsci 7 alesessanrdia", null));
        tmp.add(new CoffeePlace("1", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeePlace("2", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeePlace("3", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeePlace("4", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeePlace("0", "Caffe Vergnano Torino spa Bologna", "Corso Gramsci 7 alesessanrdia", null));
        tmp.add(new CoffeePlace("1", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeePlace("2", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeePlace("3", "Caffe Vergnano Torino spa Bologna", "hey", null));
        tmp.add(new CoffeePlace("4", "Caffe Vergnano Torino spa Bologna", "hey", null));
        return tmp;
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(Bundle bundle) {
        coffeePlaceId = bundle.getString(CoffeePlace.COFFEE_PLACE_ID_KEY);
        initView();
    }
}

