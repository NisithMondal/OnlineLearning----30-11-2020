package com.nisith.onlinelearning.Model;

public class User {
    private String userName;
    private String profileImageUrl;
    private String accountCreatedDate;
    public User(){}

    public User(String userName, String profileImageUrl, String accountCreatedDate) {
        this.userName = userName;
        this.profileImageUrl = profileImageUrl;
        this.accountCreatedDate = accountCreatedDate;
    }

    public String getUserName() {
        return userName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getAccountCreatedDate() {
        return accountCreatedDate;
    }
}
