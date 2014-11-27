package com.application.material.takeacoffee.app.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.VolleyImageRequestWrapper;
import com.application.material.takeacoffee.app.models.Review;
import com.application.material.takeacoffee.app.models.User;
import com.neopixl.pixlui.components.textview.TextView;

import java.util.ArrayList;

public class ReviewListAdapter extends ArrayAdapter<Review> implements View.OnClickListener {
    private static final String TAG = "ReviewListAdapter";
    private ArrayList<Review> reviewList;
    private Bitmap defaultIcon;
    private String coffeeMachineId;
    private FragmentActivity mainActivityRef;
    private ArrayList<User> userList;

    public ReviewListAdapter(FragmentActivity activity, int resource, ArrayList<Review> reviewList,
                               String coffeeMachineId) {
        //this constructor to handle empty getView function
        super(activity.getApplicationContext(), resource, R.id.reviewCommentTextId, reviewList);
        this.mainActivityRef = activity;
        this.reviewList = reviewList;
        this.coffeeMachineId = coffeeMachineId;
        //SAVE MEMORY - DEFAULT ICON ALLOCATION
        this.defaultIcon = BitmapFactory.decodeResource(mainActivityRef.getResources(), R.drawable.user_icon);
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        try {
            convertView = LayoutInflater.from(mainActivityRef.getApplicationContext()).inflate(R.layout.review_template, parent, false);
            Review review = reviewList.get(position);
            ViewHolder holder = new ViewHolder();
            holder.mainItemView = convertView.findViewById(R.id.mainItemViewId);
//            holder.extraMenuItemView = convertView.findViewById(R.id.extraMenuItemViewId);
            holder.usernameTextView = (TextView) convertView.findViewById(R.id.reviewUsernameTextId);
            holder.reviewDateTextView = ((TextView) convertView.findViewById(R.id.reviewDateTextId));
            holder.reviewCommentTextView = ((TextView) convertView.findViewById(R.id.reviewCommentTextId));
            holder.profilePicImageView = ((ImageView) convertView.findViewById(R.id.profilePicReviewTemplateId));

//          TODO SET DATA TO VIEW
            holder.reviewCommentTextView.setText(review.getComment());
            holder.reviewDateTextView.setText(review.getFormattedTimestamp());

            //show extra menu
            //TODO on action bar - so might add an interface to handle in main
/*            if(selectedItemIndex == position) {
                //set extra menu visibility
                holder.mainItemView.setVisibility(View.GONE);
                holder.extraMenuItemView.setVisibility(View.VISIBLE);
//            setReviewListHeaderBackgroundLabel(holder.extraMenuItemView, false); TODO replace this
                initExtraMenuAction(holder.extraMenuItemView);
            }*/

            if(userList == null) {
                return convertView;
            }

            User user = getUserByUserId(review.getUserId());
            if(user != null) {
                holder.usernameTextView.setText(user.getUsername());

                int defaultIconId = R.drawable.user_icon;
                ((VolleyImageRequestWrapper) mainActivityRef).volleyImageRequest(
                        user.getProfilePicturePath(), holder.profilePicImageView, defaultIconId);
            }
        } catch (Exception e) {
            Log.e(TAG, " - " + e.getMessage());
            e.printStackTrace();
        }


        return convertView;
    }

    public boolean setUserList(ArrayList<User> userList) {
        this.userList = userList;
        return true;
    }

    //TODO refactor it
    public User getUserByUserId(String userId) {
        for(User user : userList) {
            if(user.getId().compareTo(userId) == 0) {
                return user;
            }
        }
        return null;
    }

    @Override
    public void onClick(View v) {

    }

    public boolean updateReview(Review data) {
        for(Review review : reviewList) {
            if(review.getId().equals(data.getId())) {
                review.setComment(data.getComment());
                review.setStatus(data.getStatus());
                notifyDataSetChanged();
                return true;
            }
        }
        return false;
    }

    public boolean deleteReview(String id) {
        for(int i = 0; i < reviewList.size(); i ++) {
            if(reviewList.get(i).getId().equals(id)) {
                reviewList.remove(i);
                notifyDataSetChanged();
                return true;
            }
        }
        return false;
    }

    public static class ViewHolder {
        View mainItemView;
        View extraMenuItemView;
        TextView reviewDateTextView;
        TextView reviewCommentTextView;
        ImageView profilePicImageView;
        TextView usernameTextView;
    }

//    public ArrayList<Review> getList(){
//        return this.reviewList;
//    }
//
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        Review reviewObj = reviewList.get(position);
////        CoffeeAppController coffeeAppLogic = new CoffeeAppController(mainActivityRef.getApplicationContext());
//        User userOnReview = coffeeAppController.getUserByIdInListview(reviewObj.getUserId());
//        ViewHolder holder;
////            if(convertView == null) {
//        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        convertView = inflater.inflate(R.layout.review_template, null);
//
//        holder = new ViewHolder();
//        holder.mainItemView = convertView.findViewById(R.id.mainItemViewId);
//        holder.extraMenuItemView = convertView.findViewById(R.id.extraMenuItemViewId);
//        holder.usernameTextView = (TextView) convertView.findViewById(R.id.reviewUsernameTextId);
//        holder.reviewDateTextView = ((TextView) convertView.findViewById(R.id.reviewDateTextId));
//        holder.reviewCommentTextView = ((TextView) convertView.findViewById(R.id.reviewCommentTextId));
//        holder.profilePicImageView = ((ImageView) convertView.findViewById(R.id.profilePicReviewTemplateId));
//        convertView.setTag(holder);
////            } else {
////                holder = (ViewHolder) convertView.getTag();
////            }
//
//        holder.reviewCommentTextView.setText(reviewObj.getComment());
//        holder.reviewDateTextView.setText(reviewObj.getFormattedTimestamp());
//
//        //they call volley lib to load profile picture
//        coffeeAppController.setUsernameToUserOnReview(holder.usernameTextView,
//                userOnReview.getUsername(), userOnReview.getId());
//        //set extra menu visibility
//        coffeeAppController.setProfilePictureToUserOnReview(holder.profilePicImageView,
//                userOnReview.getProfilePicturePath(), this.defaultIcon,
//                userOnReview.getId());
//        holder.extraMenuItemView.setVisibility(View.GONE);
//
//        //show extra menu
//        if(selectedItemIndex == position) {
//            //set extra menu visibility
//            holder.mainItemView.setVisibility(View.GONE);
//            holder.extraMenuItemView.setVisibility(View.VISIBLE);
////            setReviewListHeaderBackgroundLabel(holder.extraMenuItemView, false); TODO replace this
//            initExtraMenuAction(holder.extraMenuItemView);
//        }
//
//        //TODO this gave me problem
//        Common.setCustomFont(convertView, mainActivityRef.getAssets());
//        return convertView;
//    }
//
///*        @Override
//        public void notifyDataSetChanged() {
//            super.notifyDataSetChanged();
//        }*/
//
//    public void setSelectedItemIndex(int position) {
//        this.selectedItemIndex = position;
//    }
//
//    public int getSelectedItemIndex() {
//        return selectedItemIndex;
//    }
//
////        public long getFromTimestamp() {
////            return fromTimestamp;
////        }
//
//
//    public static class ViewHolder {
//        View mainItemView;
//        View extraMenuItemView;
//        TextView reviewDateTextView;
//        TextView reviewCommentTextView;
//        ImageView profilePicImageView;
//        TextView usernameTextView;
//    }
//
//
//    public void initExtraMenuAction(final View extraMenuItemView) {
//        extraMenuItemView.findViewById(R.id.modifyReviewEditLayoutId)
//                .setOnClickListener(this);
//        extraMenuItemView.findViewById(R.id.modifyReviewDeleteLayoutId)
//                .setOnClickListener(this);
//        extraMenuItemView.findViewById(R.id.modifyReviewBackImageViewId)
//                .setOnClickListener(this);
//
//    }
//
//    @Override
//    public void onClick(View view) {
//        if(view.getId() == R.id.modifyReviewBackImageViewId) {
//            Log.e(TAG, " id: modifyReviewBackImageViewId");
//        }
////        ArrayList<Review> reviewList = this.getList();
////        int selectedItemIndex = this.getSelectedItemIndex();
//        Review review = this.getList().get(this.getSelectedItemIndex());
//
//        switch (view.getId()) {
//            case R.id.modifyReviewEditLayoutId:
//                ExtraMenuController.getEditReviewFragment(mainActivityRef, review.getId(),
//                        coffeeMachineId, review.getStatus());
//                break;
//            case R.id.modifyReviewDeleteLayoutId:
//                ExtraMenuController.alertDialogDeleteReview(mainActivityRef,
//                        coffeeMachineId, this, review);
//                break;
//            case R.id.modifyReviewBackImageViewId:
//                ExtraMenuController.extra(view, this);
//                break;
//        }
//
//    }

}
