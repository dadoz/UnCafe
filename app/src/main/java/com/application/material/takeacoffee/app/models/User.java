package com.application.material.takeacoffee.app.models;


public class User {
    private static final String EMPTY_PIC_PATH = "EMPTY_PIC_PATH";
    private String id;
	private String username;
//	private String reviewsListId;
    private String profilePicturePath;
    private String userId;

    /*
        public User(String id, String username, ArrayList<Review> reviewsList){
            this.id = id;
            this.username = username;
            this.reviewsList = reviewsList;
        }
    */
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

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
