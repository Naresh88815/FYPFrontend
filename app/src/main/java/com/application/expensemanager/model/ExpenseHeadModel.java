package com.application.expensemanager.model;

public class ExpenseHeadModel {
    String head_id;
    String head_name;
    String head_imgstatus;

    public ExpenseHeadModel(String head_id, String head_name,String head_imgstatus) {
        this.head_id = head_id;
        this.head_name = head_name;
        this.head_imgstatus = head_imgstatus;
    }

    public String getHead_name() {
        return head_name;
    }

    public String getHead_id() {
        return head_id;
    }

}
