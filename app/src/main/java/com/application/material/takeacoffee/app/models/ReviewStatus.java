package com.application.material.takeacoffee.app.models;

import android.util.Log;
import com.application.material.takeacoffee.app.R;

/**
 * Created by davide on 29/05/14.
 */
public class ReviewStatus {
    private static final String TAG = "ReviewStatus";
    public static final String REVIEW_STATUS_KEY = "REVIEW_STATUS_KEY";
    public static final String REVIEW_KEY = "REVIEW_KEY";

    public enum ReviewStatusEnum {
        GOOD,
        NOTSOBAD,
        NOTSET,
        WORST
    }
    private String name;
    private ReviewStatusEnum status;
    private int weeklyReviewCnt;
    private boolean hasAtLeastOneReview;

    public ReviewStatus(String status) {
        this.status = parseStatus(status);
    }

    public ReviewStatus(ReviewStatusEnum status) {
        this.status = status;
    }

    public ReviewStatus(String status, String name, int weeklyReviewCnt, boolean hasAtLeastOneReview) {
        this.status = parseStatus(status);
        this.name = name;
        this.hasAtLeastOneReview = hasAtLeastOneReview;
        this.weeklyReviewCnt = weeklyReviewCnt;
    }
    public ReviewStatus(ReviewStatusEnum status, String name, int weeklyReviewCnt, boolean hasAtLeastOneReview) {
        this.status = status;
        this.name = name;
        this.hasAtLeastOneReview = hasAtLeastOneReview;
        this.weeklyReviewCnt = weeklyReviewCnt;
    }

    public ReviewStatusEnum getStatus() {
        return this.status;
    }
    public String getName() {
        return this.name;
    }
    public boolean getHasAtLeastOneReview() {
        return this.hasAtLeastOneReview;
    }
    public int getWeeklyReviewCnt() {
        return this.weeklyReviewCnt;
    }

    public static ReviewStatusEnum parseStatus(String reviewStatus) {
        if(reviewStatus == null) {
            Log.e(TAG, "status not set -");
            return ReviewStatusEnum.NOTSET;
        }
        if(reviewStatus.equals("GOOD")) {
            return ReviewStatusEnum.GOOD;
        } else if(reviewStatus.equals("NOTSOBAD")) {
            return ReviewStatusEnum.NOTSOBAD;
        } else if(reviewStatus.equals("WORST")) {
            return ReviewStatusEnum.WORST;
        }
        Log.e(TAG, "status not set -");
        return ReviewStatusEnum.NOTSET;
    }

    public static ReviewStatusEnum parseStatusFromPageNumber(int pageNumber) {
        ReviewStatusEnum status;
        switch (pageNumber) {
            case 0:
                status = ReviewStatusEnum.GOOD;
                break;
            case 1:
                status = ReviewStatusEnum.NOTSOBAD;
                break;
            case 2:
                status = ReviewStatusEnum.WORST;
                break;
            default:
                status = ReviewStatusEnum.NOTSET;
        }
        return status;
    }

    public int getIconIdByStatus() {
        switch (this.status) {
            case GOOD:
                return R.drawable.crown_icon;
            case NOTSOBAD:
                return R.drawable.drink_icon;
            case WORST:
                return R.drawable.skull_icon;
            default:
                return R.drawable.coffee_cup_icon;
        }
    }


}
