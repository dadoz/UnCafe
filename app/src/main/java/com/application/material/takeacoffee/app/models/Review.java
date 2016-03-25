package com.application.material.takeacoffee.app.models;

import android.util.Log;
import com.google.gson.annotations.SerializedName;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Review {
    private static final String TAG = "Review";
    public static String REVIEW_OBJ_KEY = "REVIEW_OBJ_KEY";

    private String id;
	private String status;
    private String comment;
    private String timestamp;
    private String placeId;
    private User user;

	public Review(String id, String placeId, String comment, String status,
                  long timestamp, User user) {
        this.id = id;
        this.status = status;
        this.comment = comment;
        this.timestamp = Long.toString(timestamp);
        this.placeId = placeId;
        this.user = user;
    }

	public String getPlaceId() {
		return placeId;
	}

    public String getId(){
        return this.id;
    }

    public String getComment() {
        return this.comment;
    }

    public User getUser(){
        return this.user;
    }

    public void setComment(String value) {
        this.comment = value;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public long getTimestamp() {
        return Long.parseLong(this.timestamp);
    }

    public String getFormattedTimestamp() {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm")
                .format(new Date(Long.parseLong(timestamp)));
    }





    
    //TODO why in here??
    public static class ReviewStatus {
        private static final String TAG = "ReviewStatus";
        public static final String REVIEW_STATUS_KEY = "REVIEW_STATUS_KEY";

        public static String toString(ReviewStatusEnum status) {
            return status.name();
        }

        public enum ReviewStatusEnum {
            GOOD,
            NOTSOBAD,
            NOTSET,
            WORST
        }
        private ReviewStatusEnum status;

        public ReviewStatus(String status) {
            this.status = parseStatus(status);
        }

        public ReviewStatus(ReviewStatusEnum status) {
            this.status = status;
        }

        public ReviewStatusEnum getStatus() {
            return this.status;
        }

        public static ReviewStatusEnum parseStatus(String reviewStatus) {
            if(reviewStatus == null) {
                Log.e(TAG, "Hey - null status");
                return ReviewStatusEnum.NOTSET;
            }
            if(reviewStatus.equals("GOOD")) {
                return ReviewStatusEnum.GOOD;
            } else if(reviewStatus.equals("NOTSOBAD")) {
                return ReviewStatusEnum.NOTSOBAD;
            } else if(reviewStatus.equals("WORST")) {
                return ReviewStatusEnum.WORST;
            }
            Log.e(TAG, reviewStatus);
            Log.e(TAG, "Hey - status not set");
            return ReviewStatusEnum.NOTSET;
        }

        public static ReviewStatusEnum parseStatus(float rating) {
            switch ((int) rating) {
                case 0:
                case 1:
                    return ReviewStatusEnum.WORST;
                case 2:
                    return ReviewStatusEnum.NOTSOBAD;
                case 3:
                    return ReviewStatusEnum.GOOD;
                default:
                    return ReviewStatusEnum.NOTSET;
            }

        }

        public static float parseStatusToRating(ReviewStatusEnum status) {
            if(status == ReviewStatusEnum.WORST) {
                return 1;
            }
            if(status == ReviewStatusEnum.NOTSOBAD) {
                return 2;
            }
            if(status == ReviewStatusEnum.GOOD) {
                return 3;
            }
            return 0;
        }

    }

    public static class Params {
        private String coffeeMachineId;
        private long timestamp;

        public Params(String coffeeMachineId, long timestamp) {
            this.coffeeMachineId = coffeeMachineId;
            this.timestamp = timestamp;
        }
    }
}
