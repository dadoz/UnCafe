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
import com.application.material.takeacoffee.app.fragments.interfaces.SetActionBarInterface;
import com.application.material.takeacoffee.app.models.EllipsizedComment;
import com.application.material.takeacoffee.app.models.Review;
import com.application.material.takeacoffee.app.models.Review.ReviewStatus;
import com.application.material.takeacoffee.app.models.User;
import com.application.material.takeacoffee.app.parsers.JSONParserToObject;
import com.application.material.takeacoffee.app.singletons.VolleySingleton;

import java.util.ArrayList;

public class ReviewListAdapter extends ArrayAdapter<Review> implements View.OnClickListener {
    private static final String TAG = "ReviewListAdapter";
    private ArrayList<Review> reviewList;
    private Bitmap defaultIcon;
    private String coffeeMachineId;
    private FragmentActivity mainActivityRef;
    private ArrayList<User> userList = new ArrayList<User>();
    ;
    private int MAX_CHAR_COMMENT = 30;
    private View.OnClickListener onClickListenerRef;

    public ReviewListAdapter(FragmentActivity activity, View.OnClickListener onClickListenerRef, int resource, ArrayList<Review> reviewList,
                               String coffeeMachineId) {
        //this constructor to handle empty getView function
        super(activity.getApplicationContext(), resource, R.id.reviewCommentTextId, reviewList);
        this.mainActivityRef = activity;
        this.reviewList = reviewList;
        this.coffeeMachineId = coffeeMachineId;
        //SAVE MEMORY - DEFAULT ICON ALLOCATION
        this.defaultIcon = BitmapFactory.decodeResource(mainActivityRef.getResources(), R.drawable.user_icon);
        this.onClickListenerRef = onClickListenerRef;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(mainActivityRef
                .getApplicationContext()).inflate(R.layout.review_template, parent, false);

        try {
            Review review = reviewList.get(position);
            boolean hasReviewPicture = review.getReviewPictureUrl() != null;

            ViewHolder holder = new ViewHolder();
            holder.usernameTextView = (TextView) view.findViewById(R.id.reviewUsernameTextId);
            holder.reviewDateTextView = ((TextView) view.findViewById(R.id.reviewDateTextId));
            holder.reviewCommentTextView = ((TextView) view.findViewById(R.id.reviewCommentTextId));
            holder.expandDescriptionTextView = ((TextView) view.findViewById(R.id.expandDescriptionTextId));
            holder.reviewPictureButton = ((ImageView) view.findViewById(R.id.reviewPictureButtonId));
            holder.profilePicImageView = ((ImageView) view.findViewById(R.id.profilePicReviewTemplateId));
            holder.statusRatingBarView = ((RatingBar) view.findViewById(R.id.statusRatingBarId));
            holder.reviewPictureImageView = ((ImageView) view.findViewById(R.id.reviewPictureImageViewId));

            EllipsizedComment comment = getReviewCommentEllipsized(review.getComment());
            view.setTag(comment);
            boolean isCommentEllipsized = comment.isEllipsized();

//          TODO SET DATA TO VIEW
            holder.reviewCommentTextView.setText(isCommentEllipsized ?
                    comment.getEllipsizedComment() : review.getComment());
            holder.reviewDateTextView.setText(review.getFormattedTimestamp());
            holder.statusRatingBarView.setRating(
                    ReviewStatus.parseStatusToRating(
                            Review.ReviewStatus.parseStatus(review.getStatus())));
            holder.expandDescriptionTextView.setVisibility(isCommentEllipsized || hasReviewPicture ? View.VISIBLE : View.GONE);

            //add swiping view to show picture
            holder.reviewPictureButton.setVisibility(hasReviewPicture ? View.VISIBLE : View.INVISIBLE);
            holder.reviewPictureImageView.setOnClickListener(hasReviewPicture ? onClickListenerRef : null);
            //with VOLLEY u can do it :) without no - cos u got out of memory
//            holder.reviewPictureImageView.setImageBitmap(! hasReviewPicture ? null :
//                    JSONParserToObject.getMockupPicture(mainActivityRef, review.getReviewPictureUrl()));


//            holder.statusRatingBarView.setVisibility(View.GONE);
//            ((TextView) view.findViewById(R.id.statusRatingBarId)).setText(
//                    String.valueOf(ReviewStatus.parseStatusToRating(review.getStatus())));

            if(userList == null) {
                return view;
            }

            User user = getUserByUserId(review.getUserId());
            if(user != null) {
                holder.usernameTextView.setText(user.getUsername());

                int defaultIconId = R.drawable.user_icon;
                VolleySingleton volleySingleton = VolleySingleton.getInstance(mainActivityRef);
                volleySingleton.imageRequest( user.getProfilePicturePath(), holder.profilePicImageView,
                        defaultIconId);
            }

            //TODO check if selectedItem then background color middle grey
            if(((SetActionBarInterface) mainActivityRef).isItemSelected() &&
                ((SetActionBarInterface) mainActivityRef).getSelectedItemPosition() == position + 1) {
                //due to header on listview
                ((SetActionBarInterface) mainActivityRef).setSelectedItemView(view);
            }
        } catch (Exception e) {
            Log.e(TAG, " - " + e.getMessage());
            e.printStackTrace();
        }
        return view;
    }

    private EllipsizedComment getReviewCommentEllipsized(String comment) {
        if(comment.length() > MAX_CHAR_COMMENT) {
            //ellipsize comment
            return new EllipsizedComment(comment, comment.substring(0, MAX_CHAR_COMMENT) + "...", true);
        }
        return new EllipsizedComment(comment, comment, false);
    }

    public boolean setUserList(ArrayList<User> userList) {
        this.userList.addAll(userList);
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
        if(data.getId() == null) {
            return false;
        }

        try {
            for(Review review : reviewList) {
                if(review.getId().equals(data.getId())) {
                    review.setComment(data.getComment());
                    review.setStatus(data.getStatus());
                    notifyDataSetChanged();
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
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
        TextView expandDescriptionTextView;
        ImageView reviewPictureButton;
        ImageView reviewPictureImageView;
    }

}
