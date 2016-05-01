package com.application.material.takeacoffee.app.models;
import com.google.gson.annotations.SerializedName;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Review {

    private String id;
    @SerializedName("time")
    private long timestamp;
    @SerializedName("author_name")
    private String user;
    @SerializedName("profile_photo_url")
    private String profilePhotoUrl;
    @SerializedName("text")
    private String comment;
    private int rating;
//    private User user;

	public Review(String id, String text, int rating, long time, String user, String profilePhotoUrl) {
        this.id = id;
        this.comment = text;
        this.timestamp = time;
        this.user = user;
        this.profilePhotoUrl = profilePhotoUrl;
        this.rating = rating;
    }

    /**
     *
     * @return
     */
    public String getId(){
        return this.id;
    }

    /**
     *
     * @return
     */
    public String getComment() {
        return this.comment;
    }

    /**
     *
     * @return
     */
    public String getUser(){
        return this.user;
    }

    /**
     *
     * @return
     */
    public long getTimestamp() {
        return this.timestamp;
    }

    /**
     *
     * @return
     */
    public String getProfilePhotoUrl() {
        return "http:" + profilePhotoUrl;
    }

    /**
     *
     * @param profilePhotoUrl
     */
    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
