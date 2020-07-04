package com.theoffice.moneysaver.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class Goal implements Serializable {
    private String goalId;
    private String goalName;
    private int goalCost;
    private int goalActualMoney;
    private String goalDate;
    private String goalPhotoPath;
    private String goalStatus;
    private String[] goalLikes;
    private int contributionCount;

    public Goal(String goalId, String goalName,int goalCost, int goalActualMoney, String goalDate, String goalPhotoPath,
                String goalStatus, String[] goalLikes, int contributionCount) {
        this.goalId = goalId;
        this.goalName = goalName;
        this.goalActualMoney = goalActualMoney;
        this.goalCost = goalCost;
        this.goalDate = goalDate;
        this.goalPhotoPath = goalPhotoPath;
        this.goalStatus = goalStatus;
        this.goalLikes = goalLikes;
        this.contributionCount = contributionCount;
    }

    public void setGoalId(String goalId) {
        this.goalId = goalId;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public void setGoalCost(int goalCost) {
        this.goalCost = goalCost;
    }

    public void setGoalActualMoney(int goalActualMoney) {
        this.goalActualMoney = goalActualMoney;
    }

    public void setGoalDate(String goalDate) {
        this.goalDate = goalDate;
    }

    public void setGoalStatus(String goalStatus) {
        this.goalStatus = goalStatus;
    }

    public void setGoalLikes(String[] goalLikes) {
        this.goalLikes = goalLikes;
    }

    public void setContributionCount(int contributionCount) {
        this.contributionCount = contributionCount;
    }

    public void setGoalPhotoPath(String goalPhotoPath) {
        this.goalPhotoPath = goalPhotoPath;
    }

    public int getContributionCount() {
        return contributionCount;
    }

    public String[] getGoalLikes() {
        return goalLikes;
    }

    public int getGoalCost() {
        return goalCost;
    }

    public String getGoalStatus() {
        return goalStatus;
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
                ", goalCost=" + goalCost +
                ", goalActualMoney=" + goalActualMoney +
                ", goalDate='" + goalDate + '\'' +
                ", goalPhotoPath='" + goalPhotoPath + '\'' +
                ", goalStatus='" + goalStatus + '\'' +
                ", goalLikes=" + goalLikes.length +
                ", contributionCount=" + contributionCount +
                '}';
    }

    public void increaseContribution() {
        this.contributionCount++;
    }

    public void increaseActualMoney(int money){
        this.goalActualMoney += money;
    }
}
