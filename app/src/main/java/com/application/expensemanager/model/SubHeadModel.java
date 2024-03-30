package com.application.expensemanager.model;

public class SubHeadModel {
    String subHeadID;
    String headId;
    String subHeadName;
    String status;
    String imgStatus;

    public SubHeadModel(String subHeadID, String headId, String subHeadName, String status, String imgStatus) {
        this.subHeadID = subHeadID;
        this.headId = headId;
        this.subHeadName = subHeadName;
        this.status = status;
        this.imgStatus = imgStatus;
    }

    public String getSubHeadID() {
        return subHeadID;
    }

    public String getHeadId() {
        return headId;
    }

    public String getSubHeadName() {
        return subHeadName;
    }

    public String getStatus() {
        return status;
    }

    public String getImgStatus() {
        return imgStatus;
    }
}
