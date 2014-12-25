package com.application.material.takeacoffee.app.application;

import android.app.Application;
import android.os.Bundle;
import com.application.material.takeacoffee.app.models.CoffeeMachineStatus;
import com.application.material.takeacoffee.app.models.Review;
import com.application.material.takeacoffee.app.models.ReviewDataContainer;
import com.application.material.takeacoffee.app.models.User;

import java.util.ArrayList;

/**
 * Created by davide on 25/12/14.
 */
public class DataApplication extends Application {

    private ArrayList<User> userList;
    private ReviewDataContainer reviewDataContainer;
    private CoffeeMachineStatus coffeeMachineStatus;
    private User user;

    public DataApplication() {
        user = new User("4nmvMJNk1R", null, "John Bla");
    }

    public ArrayList<User> restoreUserList() {
        return userList;
    }

    public ReviewDataContainer restoreReviewDataContainer() {
        return reviewDataContainer;
    }

    public CoffeeMachineStatus restoreCoffeeMachineStatus() {
        return coffeeMachineStatus;
    }

    public void saveReviewDataContainer(ReviewDataContainer reviewDataContainer) {
        this.reviewDataContainer = reviewDataContainer;
    }

    public void saveCoffeeMachineStatus(CoffeeMachineStatus coffeeMachineStatus) {
        this.coffeeMachineStatus = coffeeMachineStatus;
    }

    public void saveUserList(ArrayList<User> userList) {
        this.userList = userList;
    }

    public void clearData(int[] dataLabels) {
        for (int i = 0; i< dataLabels.length; i ++) {
            switch (dataLabels[i]) {
                case 0:
                    coffeeMachineStatus = null;
                    break;
                case 1:
                    reviewDataContainer = null;
                    break;
                case 2:
                    userList = null;
                    break;
            }
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUserId() {
        return user == null ? null : user.getId();
    }
}
