package com.application.material.takeacoffee.app.models;
/**
 * Created by davide on 04/12/14.
 */

import android.os.Parcel;
import android.os.Parcelable;

import static com.application.material.takeacoffee.app.models.Review.ReviewStatus.*;

public class CoffeeMachineStatus implements Parcelable {

    public static String COFFEE_MACHINE_STATUS = "COFFEE_MACHINE_STATUS";
    public static String COFFEE_MACHINE_STATUS_STRING_KEY = "COFFEE_MACHINE_STATUS_STRING_KEY";
    private final ReviewStatusEnum status;
    private final String description;
    private final int goodReviewPercentage;

    public CoffeeMachineStatus(String status, String description, int goodReviewPercentage) {
        this.status = parseStatus(status);
        this.description = description;
        this.goodReviewPercentage = goodReviewPercentage;
    }

    public CoffeeMachineStatus(ReviewStatusEnum status, String description,
                               int goodReviewPercentage) {
        this.status = status;
        this.description = description;
        this.goodReviewPercentage = goodReviewPercentage;
    }

    public String getDescription() {
        return description;
    }

    public ReviewStatusEnum getStatus() {
        return status;
    }

    public int getGoodReviewPercentage() {
        return goodReviewPercentage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public class Params {
        private String coffeeMachineId;

        public Params(String coffeeMachineId, double timestamp) {
            this.coffeeMachineId = coffeeMachineId;
        }
    }


}
