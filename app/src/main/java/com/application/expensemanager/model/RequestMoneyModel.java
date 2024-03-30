package com.application.expensemanager.model;

public class RequestMoneyModel {
    String id;
    String requestLabelTV;
    String headsTV;
    String amountTV;
    String statusTV;
    String dateTV;

    String image;
    public RequestMoneyModel(String requestLabelTV,String headsTV, String amountTV, String statusTV, String dateTV, String image, String id) {
        this.requestLabelTV = requestLabelTV;
        this.headsTV = headsTV;
        this.amountTV = amountTV;
        this.statusTV = statusTV;
        this.dateTV = dateTV;
        this.image=image;
        this.id = id;
    }

//    public RequestMoneyModel(String requestLabelTV, String amountTV, String dateTV, String image) {
//        this.requestLabelTV = requestLabelTV;
//        this.amountTV = amountTV;
//        this.dateTV = dateTV;
//        this.image = image;
//    }

    public String getRequestLabelTV() {
        return requestLabelTV;
    }

    public String getHeadsTV() {
        return headsTV;
    }

    public void setHeadsTV(String headsTV) {
        this.headsTV = headsTV;
    }

    public String getAmountTV() {
        return amountTV;
    }

    public String getStatusTV() {
        return statusTV;
    }

    public String getDateTV() {
        return dateTV;
    }

    public String getImage() {
        return image;
    }

    public String getId() {
        return id;
    }
}
