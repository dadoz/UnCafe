package com.application.material.takeacoffee.app.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.application.material.takeacoffee.app.models.ReviewStatus.ReviewStatusEnum;


public class Review implements Parcelable {
    private static final String TAG = "Review";
    public static final String ID_NOT_SET = "ID_NOT_SET";
    public static final String REVIEW_KEY = "REVIEW_KEY";
    public static String REVIEW_OBJ_KEY = "REVIEW_OBJ_KEY";
    private String objectId;
	private ReviewStatusEnum status;
    private String comment;
    private long timestamp;
    private String userId;
    private String coffeeMachineId;

    public Review(Parcel in) {
        this.objectId = in.readString();
        this.userId = in.readString();
        this.status = ReviewStatus.parseStatus(in.readString());
        this.comment = in.readString();
        this.timestamp = in.readLong();
        this.coffeeMachineId = in.readString();
    }

	public Review(String objectId, String comment, ReviewStatusEnum status,
                  long timestamp, String userId, String coffeeMachineId) {
        this.objectId = objectId;
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
        return this.objectId;
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

    public void setStatus(ReviewStatusEnum status) {
        this.status = status;
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

    public String getFormattedTimestamp() {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date(timestamp));
    }

    @Override
    public String toString() {
        return "id: " + this.objectId +
            "userId: " + this.userId +
            "status: " + this.status +
            "comment: " + this.comment +
            "timestamp: " + this.timestamp +
            "coffeeMachineId: " + this.coffeeMachineId;
    }


    public static ReviewStatusEnum parseStatus(float rating) {
        switch (Float.floatToIntBits(rating)) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.objectId);
        dest.writeString(this.userId);
        dest.writeString(this.status.name());
        dest.writeString(comment);
        dest.writeLong(this.timestamp);
        dest.writeString(this.coffeeMachineId);
    }

    public static Creator CREATOR = new Creator() {
        @Override
        public Review createFromParcel(Parcel source) {
            return new Review(source);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
}
