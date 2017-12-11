package com.example.kharak1.liveupdatestatus.model;

/**
 * Created by kharak1 on 12/3/2017.
 */

public class Status {
    private String userStatus;
    private String userId;
//    private String userFullName;

    public Status(){}

    // parametrized constructor. (Shortcut key is: Alt+Insert)
    public Status(String userStatus, String userId) {
        this.userStatus = userStatus;
        this.userId = userId;
    }

    // getters and setters

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
