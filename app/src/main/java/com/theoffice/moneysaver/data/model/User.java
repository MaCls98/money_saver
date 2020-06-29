package com.theoffice.moneysaver.data.model;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    private String userId;
    private String userName;
    private String userPhotoUrl;
    private ArrayList<Goal> goalList;

    public User() {

    }

    public User(String userId, String userName, String userPhotoUrl) {
        this.userId = userId;
        this.userName = userName;
        this.goalList = new ArrayList<>();
        this.userPhotoUrl = userPhotoUrl;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public void setGoalList(ArrayList<Goal> goalList) {
        this.goalList.clear();
        this.goalList.addAll(goalList);
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public ArrayList<Goal> getGoalList() {
        return goalList;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", goalList=" + goalList +
                '}';
    }
}
