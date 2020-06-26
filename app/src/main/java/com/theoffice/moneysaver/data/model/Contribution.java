package com.theoffice.moneysaver.data.model;

public class Contribution {

    private int contributionId;
    private String contributionValue;
    private String contributionDate;

    public Contribution(int contributionId, String contributionValue, String contributionDate) {
        this.contributionId = contributionId;
        this.contributionValue = contributionValue;
        this.contributionDate = contributionDate;
    }

    public int getContributionId() {
        return contributionId;
    }

    public String getContributionValue() {
        return contributionValue;
    }

    public String getContributionDate() {
        return contributionDate;
    }

    @Override
    public String toString() {
        return "Contribution{" +
                "contributionId=" + contributionId +
                ", contributionValue='" + contributionValue + '\'' +
                ", contributionDate='" + contributionDate + '\'' +
                '}';
    }
}
