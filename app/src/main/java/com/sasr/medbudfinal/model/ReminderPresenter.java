package com.sasr.medbudfinal.model;

import java.util.ArrayList;

public class ReminderPresenter {
    private String medName;
    private String fromDate;
    private String toDate;
    private String onDays;
    private ArrayList<Reminder> reminder = new ArrayList<>();

    public ReminderPresenter() {
    }

    public String getMedName() {
        return medName;
    }

    public void setMedName(String medName) {
        this.medName = medName;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getOnDays() {
        return onDays;
    }

    public void setOnDays(String onDays) {
        this.onDays = onDays;
    }

    public Reminder getLastReminder() {
        return reminder.get(reminder.size()-1);
    }

    public void setLastReminder(Reminder reminder) {
        this.reminder.add(reminder);
    }

    public ArrayList<Reminder> getReminders () {return this.reminder;}

    @Override
    public String toString() {
        return "ReminderPresenter{" +
                "medName='" + medName + '\'' +
                ", fromDate='" + fromDate + '\'' +
                ", toDate='" + toDate + '\'' +
                ", onDays='" + onDays + '\'' +
                ", reminder=" + reminder +
                '}';
    }
}
