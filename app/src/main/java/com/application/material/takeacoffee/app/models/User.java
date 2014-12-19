package com.application.material.takeacoffee.app.models;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;

public class User implements Parcelable {
    private static final String EMPTY_PIC_PATH = "EMPTY_PIC_PATH";
    public static String USER_OBJ_KEY = "USER_OBJ_KEY";
    public static String USER_ID_KEY = "USER_ID_KEY";
    private String id;
	private String username;
//	private String reviewsListId;
    private String profilePicturePath;

    /*
        public User(String id, String username, ArrayList<Review> reviewsList){
            this.id = id;
            this.username = username;
            this.reviewsList = reviewsList;
        }
    */
    public User(Parcel in) {
        this.id = in.readString();
        this.username = in.readString();
        this.profilePicturePath = in.readString();
    }

	public User(String id, String profilePicturePath, String username) {
		this.id = id;
		this.username = username;
        this.profilePicturePath = profilePicturePath;
	}
	
	public String getId(){
		return this.id;
	}

	public String getUsername(){
		return this.username;
	}

/*	public String getReviewList(){
		return this.reviewsList;
	}

	public void setReviewList(String reviewsListId){
		this.reviewsListId = reviewsListId;
	}
	public void setUsername(String username){
		this.username = username;
	}
*/
    public String getProfilePicturePath() {
        return this.profilePicturePath;
    }

    public void setProfilePicturePath(String profilePicturePath) {
        try {
            if(profilePicturePath != null && profilePicturePath.equals(User.EMPTY_PIC_PATH)) {
                this.profilePicturePath = null;
                return;
            }
            this.profilePicturePath = profilePicturePath;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.username);
        dest.writeString(this.profilePicturePath);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };


    public static class Params {
        ArrayList<String> userIdList;
        public Params(ArrayList<String> list) {
            userIdList = list;
        }

    }
}
