package com.application.material.takeacoffee.app.adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.application.material.takeacoffee.app.models.CoffeeMachine;
import com.application.material.takeacoffee.app.*;
import com.application.material.takeacoffee.app.singletons.VolleySingleton;

import java.util.ArrayList;

/****ADAPTER****/
public class CoffeeMachineGridAdapter extends ArrayAdapter<CoffeeMachine> {
    private static final String TAG = "ReviewListAdapter";
    private ArrayList<CoffeeMachine> coffeeMachineList;
    private FragmentActivity mainActivityRef;

    public CoffeeMachineGridAdapter(FragmentActivity activity, int resource,
                                    ArrayList<CoffeeMachine> list) {
        super(activity.getApplicationContext(), resource, list);
        mainActivityRef = activity;
        coffeeMachineList = list;
        //SAVE MEMORY DEFAULT ICON ALLOCATION
//        this.defaultIcon = BitmapFactory.decodeResource(mainActivityRef.getResources(), R.drawable.user_icon);
    }

    public ArrayList<CoffeeMachine> getList() {
        return this.coffeeMachineList;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        CoffeeMachine coffeeMachine = coffeeMachineList.get(position);

        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.coffee_machine_template, null);

        holder = new ViewHolder();
        holder.nameTextView = ((TextView) convertView.findViewById(R.id.coffeeMachineNameTextId));
        holder.addressTextView = ((TextView) convertView.findViewById(R.id.coffeeMachineAddressTextId));
        holder.iconImageView = (ImageView) convertView.findViewById(R.id.coffeeIconId);
        convertView.setTag(holder);

        holder.nameTextView.setText(coffeeMachine.getName());
        holder.addressTextView.setText(coffeeMachine.getAddress());
//        holder.nameTextView.setTextColor(mainActivityRef
//                .getResources().getColor(R.color.light_black));
        holder.iconImageView.setImageResource(R.drawable.coffee_cup_icon);

        //retrieve icon from server volley
        int defaultIconId = R.drawable.coffee_cup_icon;

        //TODO save mb in this way :D
        holder.iconImageView.setImageDrawable(mainActivityRef.
                getResources().getDrawable(defaultIconId));
//        try {
//            VolleySingleton volleySingleton = VolleySingleton.getInstance(mainActivityRef);
//            volleySingleton.imageRequest(coffeeMachine.getIconPath(),
//                    holder.iconImageView, defaultIconId);
////            ((VolleyImageRequestWrapper) mainActivityRef).volleyImageRequest(
////                    coffeeMachine.getIconPath(), holder.iconImageView, defaultIconId);
////
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        return convertView;
    }

    public static class ViewHolder {
        public TextView nameTextView;
        public TextView addressTextView;
        public ImageView iconImageView;
    }
}
