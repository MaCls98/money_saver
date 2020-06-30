package com.theoffice.moneysaver.data.model;

public class Contribution {

    private String contributionId;
    private int contributionValue;
    private String contributionDate;

    public Contribution(String contributionId, int contributionValue, String contributionDate) {
        this.contributionId = contributionId;
        this.contributionValue = contributionValue;
        this.contributionDate = contributionDate;
    }

    public String getContributionId() {
        return contributionId;
    }

    public int getContributionValue() {
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
