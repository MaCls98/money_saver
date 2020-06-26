package com.theoffice.moneysaver.data.model;

import java.util.ArrayList;

public class Goal {
    private String goalId;
    private String goalName;
    private int goalValue;
    private String goalDate;
    private String goalPhotoPath;
    private String goalStatus;
    private int goalLikes;
    private int contributionCount;

    public Goal(String goalId, String goalName, int goalValue, String goalDate, String goalPhotoPath,
                String goalStatus, int goalLikes, int contributionCount) {
        this.goalId = goalId;
        this.goalName = goalName;
        this.goalValue = goalValue;
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

    public int getGoalLikes() {
        return goalLikes;
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

    public int getGoalValue() {
        return goalValue;
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
                "goalId=" + goalId +
                ", goalName='" + goalName + '\'' +
                ", goalValue='" + goalValue + '\'' +
                ", goalDate='" + goalDate + '\'' +
                ", goalPhotoPath='" + goalPhotoPath + '\'' +
                ", goalStatus='" + goalStatus + '\'' +
                ", goalLikes=" + goalLikes +
                ", contributionList=" + contributionCount +
                '}';
    }
}
