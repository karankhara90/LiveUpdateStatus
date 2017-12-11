package com.example.kharak1.liveupdatestatus.model;

/**
 * Created by kharak1 on 12/3/2017.
 */

// created this User class for POJO (Plain Old Java Object).

public class User {

    private String displayName;
    private String email;
    private String photoUrl;
    private String userId;

    // constructor with no arguments
    public User(){
    }

    // constructor with arguments
    public User(String displayName, String email, String photoUrl, String userId) {
        this.displayName = displayName;
        this.email = email;
        this.photoUrl = photoUrl;
        this.userId = userId;
    }

    /**  getter and setter methods  **/
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
