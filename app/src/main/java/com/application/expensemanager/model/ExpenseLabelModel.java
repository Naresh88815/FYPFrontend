package com.application.expensemanager.model;

public class ExpenseLabelModel {
    String expenseLabel;

    String labelId;


    public ExpenseLabelModel(String expenseLabel, String labelId) {
        this.expenseLabel = expenseLabel;
        this.labelId = labelId;
    }

    public String getExpenseLabel() {
        return expenseLabel;
    }

    public String getLabelId() {
        return labelId;
    }

}
