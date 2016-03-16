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

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by davide on 25/12/14.
 */
public class CoffeePlacesApplication extends Application {

    private static final String FONT_PATH = "fonts/chimphand-regular.ttf";
    private ArrayList<User> userList = new ArrayList<User>();
    private ReviewDataContainer reviewDataContainer;
    private CoffeeMachineStatus coffeeMachineStatus;
    private User user;
    private Bitmap reviewPictureTemp;

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig
                .Builder().setDefaultFontPath(FONT_PATH).build());
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
                    userList = new ArrayList<User>();
                    break;
            }
        }
    }

}
