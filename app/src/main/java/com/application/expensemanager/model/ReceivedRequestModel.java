package com.application.expensemanager.model;

public class ReceivedRequestModel {
    String exp_id;
    String requestByTV;
    String headsTV;
    String requestLabelTV;
    String amountTV;
    String statusTV;
    String dateTV;

    String image;
    boolean image_status;

    public ReceivedRequestModel(String exp_id, String requestByTV, String requestLabelTV, String headsTV,String amountTV, String statusTV, String dateTV, String image) {
        this.exp_id = exp_id;
        this.requestByTV = requestByTV;
        this.requestLabelTV = requestLabelTV;
        this.headsTV = headsTV;
        this.amountTV = amountTV;
        this.statusTV = statusTV;
        this.dateTV = dateTV;
        this.image = image;
    }

    public void setImage_status(boolean image_status) {
        this.image_status = image_status;
    }

    public String getRequestByTV() {
        return requestByTV;
    }
    public String getRequestLabelTV() {
        return requestLabelTV;
    }

    public String getAmountTV() {
        return amountTV;
    }

    public String getHeadsTV() {
        return headsTV;
    }

    public void setHeadsTV(String headsTV) {
        this.headsTV = headsTV;
    }

    public String getStatusTV() {
        return statusTV;
    }

    public String getDateTV() {
        return dateTV;
    }

    public String getExp_id() {
        return exp_id;
    }

    public String getImage() {
        return image;
    }
}
