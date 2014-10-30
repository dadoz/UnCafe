package com.application.material.takeacoffee.app.models;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.application.material.takeacoffee.app.models.ReviewStatus.ReviewStatusEnum;


public class Review {

    private static final String TAG = "Review";
    private String id;
//    private boolean feedback;
	private ReviewStatusEnum status;
    private String comment;
    private long timestamp;
    private String userId;
    private String coffeeMachineId;

	public Review(String id, String comment, ReviewStatusEnum status,
                  long timestamp, String userId, String coffeeMachineId) {

        this.id = id;
        this.userId = userId;
        this.status = status;
        this.comment = comment;
        this.timestamp = timestamp;
        this.coffeeMachineId = coffeeMachineId;
    }

	public String getCoffeeMachineId() {
		return coffeeMachineId;
	}

    public void setCoffeeMachineId(String coffeeMachineId) {
        this.coffeeMachineId = coffeeMachineId;
    }

    public String getId(){
        return this.id;
    }

    public String getComment() {
        return this.comment;
    }

    public String getUserId(){
        return this.userId;
    }

    public void setComment(String value) {
        this.comment = value;
    }

    public String getFormattedTimestamp(){
        return new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date(timestamp));
    }

    public ReviewStatusEnum getStatus() {
        return this.status;
    }


    public long getTimestamp() {
        return this.timestamp;
    }


    public static Date parseDate(long timestamp) {
        return new Date();
    }

    @Override
    public String toString() {
        return "id: " + this.id +
            "userId: " + this.userId +
            "status: " + this.status +
            "comment: " + this.comment +
            "timestamp: " + this.timestamp +
            "coffeeMachineId: " + this.coffeeMachineId;
    }


}
