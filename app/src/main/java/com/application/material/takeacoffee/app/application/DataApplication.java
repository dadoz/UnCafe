package com.application.material.takeacoffee.app.application;

import android.app.Application;
import android.graphics.Bitmap;
import android.os.Bundle;
import com.application.material.takeacoffee.app.models.CoffeeMachineStatus;
import com.application.material.takeacoffee.app.models.Review;
import com.application.material.takeacoffee.app.models.ReviewDataContainer;
import com.application.material.takeacoffee.app.models.User;
import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Created by davide on 25/12/14.
 */
public class DataApplication extends Application {

    private ArrayList<User> userList;
    private ReviewDataContainer reviewDataContainer;
    private CoffeeMachineStatus coffeeMachineStatus;
    private User user;
    private Bitmap reviewPictureTemp;
    private ParseUser parseUser;
    private User userId;
    private String userProfilePicture;

    public DataApplication() {
//        user = new User("4nmvMJNk1R", null, "John Bla");
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

    public boolean isUserSet() {
        return user != null;
    }
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public void setUserId(String userId) {
        if(user == null) {
            //TODO handle it better
            user = new User(userId, null, null, null);
            return;
        }

        user.setId(userId);
    }

    public String getUserId() {
        return user == null ? null : user.getId();
    }

    public String getUsername() {
        return user == null ? "Guest" : user.getUsername();
    }
    public String getProfilePicturePath() {
        return user == null ? null : user.getProfilePicturePath();
    }

    public void setUsername(String username) {
        this.user.setUsername(username);
    }

    /**PICTURES**/
    public boolean isReviewPictureSet() {
        return reviewPictureTemp != null;
    }

    public void setReviewPictureTemp(Bitmap bitmap) {
        reviewPictureTemp = bitmap;
    }

    public Bitmap getReviewPictureTemp() {
        return reviewPictureTemp;
    }
    public void deleteReviewPictureTemp() {
        reviewPictureTemp = null;
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

}
