package com.application.material.takeacoffee.app.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.application.material.takeacoffee.app.models.Review.ReviewStatus.ReviewStatusEnum;


public class ReviewDataContainer {
    private static final String TAG = "ReviewDataContainer";
    private final ArrayList<Review> reviewList;
    private final boolean hasMoreReviews;

    public ReviewDataContainer(boolean hasMoreReviews, ArrayList<Review> reviewList) {
        this.reviewList = reviewList;
        this.hasMoreReviews = hasMoreReviews;
    }

    public ArrayList<Review> getReviewList() {
        return reviewList;
    }

    public boolean getHasMoreReviews() {
        return hasMoreReviews;
    }
}
