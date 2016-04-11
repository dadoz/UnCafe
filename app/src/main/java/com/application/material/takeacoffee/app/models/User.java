package com.application.material.takeacoffee.app.models;

public class User {
    private String id;
    private String username;

    /**
     *
     * @param id
     * @param username
     */
	public User(String id, String username) {
		this.id = id;
        this.username = username;
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
	public String getUsername() {
		return this.username;
	}
}
