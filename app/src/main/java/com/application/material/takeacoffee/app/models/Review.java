package com.application.material.takeacoffee.app.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.application.material.takeacoffee.app.models.Review.ReviewStatus.ReviewStatusEnum;


public class Review implements Parcelable {
    private static final String TAG = "Review";
    public static final String ID_NOT_SET = "ID_NOT_SET";
    public static final String REVIEW_KEY = "REVIEW_KEY";
    public static final String ERROR_MESSAGE = "Sorry! there's a problem to save or retrieve review data! Try again later";
    public static String REVIEW_OBJ_KEY = "REVIEW_OBJ_KEY";
    public static String REVIEW_PARAMS_KEY = "REVIEW_PARAMS_KEY";

//    @SerializedName("objectId")
    private String id;
	private String status;
    private String comment;
    private String timestamp;
    private String userIdString;
    private String coffeeMachineIdString;
    private String reviewPictureName;
    private String reviewPictureUrl;

    public Review(Parcel in) {
        this.id = in.readString();
        this.userIdString = in.readString();
        this.status = in.readString();
        this.comment = in.readString();
        this.timestamp = in.readString();
        this.coffeeMachineIdString = in.readString();
        this.reviewPictureName = in.readString();
        this.reviewPictureUrl = in.readString();
    }

	public Review(String id, String comment, String status,
                  long timestamp, String userId, String coffeeMachineId,
                  String reviewPictureName, String reviewPictureUrl) {
        this.id = id;
        this.userIdString = userId;
        this.status = status;
        this.comment = comment;
        this.timestamp = Long.toString(timestamp);
        this.coffeeMachineIdString = coffeeMachineId;
        this.reviewPictureName = reviewPictureName;
        this.reviewPictureUrl = reviewPictureUrl;
    }

	public String getCoffeeMachineId() {
		return coffeeMachineIdString;
	}

    public void setCoffeeMachineId(String coffeeMachineId) {
        this.coffeeMachineIdString = coffeeMachineId;
    }

    public String getId(){
        return this.id;
    }

    public String getComment() {
        return this.comment;
    }

    public String getUserId(){
        return this.userIdString;
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

    @Override
    public String toString() {
        return "id: " + this.id +
            "userIdString: " + this.userIdString +
            "status: " + this.status +
            "comment: " + this.comment +
            "timestamp: " + this.timestamp +
            "coffeeMachineIdString: " + this.coffeeMachineIdString +
            "reviewPictureName: " + this.reviewPictureName +
            "reviewPictureUrl: " + this.reviewPictureUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.userIdString);
        dest.writeString(this.status);
        dest.writeString(comment);
        dest.writeString(this.timestamp);
        dest.writeString(this.coffeeMachineIdString);
        dest.writeString(this.reviewPictureName);
        dest.writeString(this.reviewPictureUrl);
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

    public void setId(String id) {
        this.id = id;
    }

    public void setTimestamp() {
    }

    public void setReviewPictureName(String reviewPictureName) {
        this.reviewPictureName = reviewPictureName;
    }

    public void setReviewPictureUrl(String reviewPictureUrl) {
        this.reviewPictureUrl = reviewPictureUrl;
    }

    public String getReviewPictureUrl() {
        return reviewPictureUrl;
    }

    public String getReviewPictureName() {
        return reviewPictureName;
    }


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

    public static class Params implements Parcelable {
        private String coffeeMachineId;
        private long timestamp;

        public Params(String coffeeMachineId, long timestamp) {
            this.coffeeMachineId = coffeeMachineId;
            this.timestamp = timestamp;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {

        }
    }
/*
    public static class AddReviewParams implements Parcelable {
        private String status;
        private String comment;
        private long timestamp;
        private String userId;
        private String coffeeMachineId;

        public AddReviewParams(String comment, String status,
                               long timestamp, String userId, String coffeeMachineId) {
            this.status = status;
            this.comment = comment;
            this.timestamp = timestamp;
            this.userId = userId;
            this.coffeeMachineId = coffeeMachineId;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {

        }

        public String getStatus() {
            return status;
        }

        public String getComment() {
            return comment;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
*/
    public static class MoreReviewsParams {
        private String coffeeMachineId;
        private String fromReviewId;
        public MoreReviewsParams(String coffeeMachineId, String fromReviewId) {
            this.coffeeMachineId = coffeeMachineId;
            this.fromReviewId = fromReviewId;
        }
    }

    public static class DeletedResponse {
        Object response;
        public DeletedResponse(Object response) {
            this.response = response;
        }
    }
}
