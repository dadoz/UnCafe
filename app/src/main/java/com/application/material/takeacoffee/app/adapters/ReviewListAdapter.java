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
import android.widget.RatingBar;
import android.widget.TextView;
import com.application.material.takeacoffee.app.R;
import com.application.material.takeacoffee.app.VolleyImageRequestWrapper;
import com.application.material.takeacoffee.app.fragments.interfaces.SetActionBarInterface;
import com.application.material.takeacoffee.app.models.Review;
import com.application.material.takeacoffee.app.models.Review.ReviewStatus;
import com.application.material.takeacoffee.app.models.User;

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
            convertView = LayoutInflater.from(mainActivityRef
                    .getApplicationContext()).inflate(R.layout.review_template, parent, false);
            Review review = reviewList.get(position);
            ViewHolder holder = new ViewHolder();
            holder.usernameTextView = (TextView) convertView.findViewById(R.id.reviewUsernameTextId);
            holder.reviewDateTextView = ((TextView) convertView.findViewById(R.id.reviewDateTextId));
            holder.reviewCommentTextView = ((TextView) convertView.findViewById(R.id.reviewCommentTextId));
            holder.profilePicImageView = ((ImageView) convertView.findViewById(R.id.profilePicReviewTemplateId));
            holder.statusRatingBarView = ((RatingBar) convertView.findViewById(R.id.statusRatingBarId));

//          TODO SET DATA TO VIEW
            holder.reviewCommentTextView.setText(review.getComment());
            holder.reviewDateTextView.setText(review.getFormattedTimestamp());
            holder.statusRatingBarView.setRating(ReviewStatus.parseStatusToRating(review.getStatus()));
//            holder.statusRatingBarView.setVisibility(View.GONE);
//            ((TextView) convertView.findViewById(R.id.statusRatingBarId)).setText(
//                    String.valueOf(ReviewStatus.parseStatusToRating(review.getStatus())));

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

            //TODO check if selectedItem then background color middle grey
            if(((SetActionBarInterface) mainActivityRef).isItemSelected() &&
                ((SetActionBarInterface) mainActivityRef).getSelectedItemPosition() == position + 1) {
                //due to header on listview
                ((SetActionBarInterface) mainActivityRef).setSelectedItemView(convertView);
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

    public boolean setReviewList(ArrayList<Review> reviewList) {
        this.reviewList.clear();
        this.reviewList.addAll(reviewList);
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

    public boolean addReview(Review review) {
        if(review == null) {
            return false;
        }
        reviewList.add(review);
        notifyDataSetChanged();
        return true;
    }

    public void setPrevReview(ArrayList<Review> prevReviewList) {
        prevReviewList.addAll(reviewList);
        reviewList.clear();
        reviewList.addAll(prevReviewList);
    }


    public static class ViewHolder {
        View mainItemView;
        RatingBar statusRatingBarView;
        TextView reviewDateTextView;
        TextView reviewCommentTextView;
        ImageView profilePicImageView;
        TextView usernameTextView;
    }

}
