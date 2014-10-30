package com.application.material.takeacoffee.app.models;

import android.util.Log;

/**
 * Created by davide on 29/05/14.
 */
public class ReviewStatus {
    private static final String TAG = "ReviewStatus";

    public enum ReviewStatusEnum {
        GOOD,
        NOTSOBAD,
        NOTSET,
        WORST
    }

    private int position;
    private String name;
    private int iconId;


    public ReviewStatus(int position,String name, int iconId) {
        this.iconId = iconId;
        this.name = name;
        this.position = position;
    }

    public int getIconId() {
        return iconId;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
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

}
