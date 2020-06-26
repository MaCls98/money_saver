package com.theoffice.moneysaver.data.model;

import java.util.ArrayList;

public class Goal {
    private int goalId;
    private String goalName;
    private String goalValue;
    private String goalDate;
    private String goalPhotoPath;
    private String goalStatus;
    private int goalLikes;
    private ArrayList<Contribution> contributionList;

    public Goal(int goalId, String goalName, String goalValue, String goalDate, String goalPhotoPath,
                String goalStatus, int goalLikes, ArrayList<Contribution> contributionList) {
        this.goalId = goalId;
        this.goalName = goalName;
        this.goalValue = goalValue;
        this.goalDate = goalDate;
        this.goalPhotoPath = goalPhotoPath;
        this.goalStatus = goalStatus;
        this.goalLikes = goalLikes;
        this.contributionList = contributionList;
    }

    public void addContribution(Contribution contribution){
        contributionList.add(contribution);
    }

    public ArrayList<Contribution> getContributionList() {
        return contributionList;
    }

    public int getGoalLikes() {
        return goalLikes;
    }

    public String getGoalStatus() {
        return goalStatus;
    }

    public int getGoalId() {
        return goalId;
    }

    public String getGoalName() {
        return goalName;
    }

    public String getGoalValue() {
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
                ", contributionList=" + contributionList.size() +
                '}';
    }
}
