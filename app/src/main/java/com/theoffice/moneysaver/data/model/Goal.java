package com.theoffice.moneysaver.data.model;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;

public class Goal implements Serializable {
    private String goalId;
    private String goalName;
    private int goalCost;
    private int goalActualMoney;
    private String goalType;
    private String goalDate;
    private String goalPhotoPath;
    private String goalStatus;
    private ArrayList<String> goalLikes;
    private int contributionCount;
    private Double latitude;
    private Double longitude;

    public Goal(String goalId, String goalName, int goalCost, int goalActualMoney, String goalDate, String goalPhotoPath,
                String goalStatus, ArrayList<String> goalLikes, int contributionCount, String goalType, Double lat, Double lon) {
        this.goalId = goalId;
        this.goalName = goalName;
        this.goalActualMoney = goalActualMoney;
        this.goalCost = goalCost;
        this.goalDate = goalDate;
        this.goalPhotoPath = goalPhotoPath;
        this.goalStatus = goalStatus;
        this.goalLikes = goalLikes;
        this.contributionCount = contributionCount;
        this.goalType = goalType;
        this.latitude = lat;
        this.longitude = lon;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setGoalActualMoney(int goalActualMoney) {
        this.goalActualMoney = goalActualMoney;
    }

    public void setContributionCount(int contributionCount) {
        this.contributionCount = contributionCount;
    }

    public String getGoalType() {
        return goalType;
    }

    public void setGoalLikes(ArrayList<String> goalLikes) {
        this.goalLikes = goalLikes;
    }

    public void setGoalPhotoPath(String goalPhotoPath) {
        this.goalPhotoPath = goalPhotoPath;
    }

    public int getContributionCount() {
        return contributionCount;
    }

    public ArrayList<String> getGoalLikes() {
        return goalLikes;
    }

    public int getGoalCost() {
        return goalCost;
    }

    public String getGoalId() {
        return goalId;
    }

    public String getGoalName() {
        return goalName;
    }

    public int getGoalActualMoney() {
        return goalActualMoney;
    }

    public String getGoalDate() {
        return goalDate;
    }

    public String getGoalPhotoPath() {
        return goalPhotoPath;
    }

    @Override
    public String toString() {
        return "Goal{" +
                "goalId='" + goalId + '\'' +
                ", goalName='" + goalName + '\'' +
                ", goalType='" + goalType + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    public void increaseContribution() {
        this.contributionCount++;
    }
}
