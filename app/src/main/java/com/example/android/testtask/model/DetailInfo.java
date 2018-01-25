package com.example.android.testtask.model;

public class DetailInfo {
    private String date;
    private long timeOfUse;

    public DetailInfo(){}

    public DetailInfo(String date, long timeOfUse){
        this.date = date;
        this.timeOfUse = timeOfUse;
    }

    public String getDate() {
        return date;
    }

    public long getTimeOfUse() {
        return timeOfUse;
    }
}
