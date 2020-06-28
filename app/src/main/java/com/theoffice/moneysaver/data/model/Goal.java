package com.theoffice.moneysaver.data.model;

import java.io.Serializable;
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
    /*
    public void addContribution(Contribution contribution){
        contributionList.add(contribution);
    }*/

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
                ", goalPhotoPath='" + goalPhotoPath + '\'' +
                ", goalLikes=" + Arrays.toString(goalLikes) +
                ", contributionCount=" + contributionCount +
                '}';
    }
}
