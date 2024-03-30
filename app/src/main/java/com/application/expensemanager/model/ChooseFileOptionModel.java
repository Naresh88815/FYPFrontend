package com.application.expensemanager.model;

public class ChooseFileOptionModel {
    private String text;
    private int iconResId;

    public ChooseFileOptionModel(String text, int iconResId) {
        this.text = text;
        this.iconResId = iconResId;
    }

    public String getText() {
        return text;
    }

    public int getIconResId() {
        return iconResId;
    }
}
