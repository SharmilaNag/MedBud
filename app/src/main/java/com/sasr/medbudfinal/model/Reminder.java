package com.sasr.medbudfinal.model;

public class Reminder {
    private int id;
    private Inventory inventory;
    private String time;
    private int dosage;
    private int instruction;
    private int duration;
    private String fromDate;
    private String toDate;
    private String dayMarker;

    public Reminder(int id, Inventory inventory, String time, int dosage, int instruction,
                    int duration,String fromDate,String toDate,String dayMarker) {
        this.id = id;
        this.inventory = inventory;
        this.time = time;
        this.dosage = dosage;
        this.instruction = instruction;
        this.duration = duration;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.dayMarker = dayMarker;
    }

    public Reminder() {
    }

    @Override
    public String toString() {
        return "Reminder{" +
                "id=" + id +
                ", inventory=" + inventory +
                ", time='" + time + '\'' +
                ", dosage=" + dosage +
                ", instruction='" + instruction + '\'' +
                ", duration=" + duration +
                ", fromDate='" + fromDate + '\'' +
                ", toDate='" + toDate + '\'' +
                ", dayMarker='" + dayMarker + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getDosage() {
        return dosage;
    }

    public void setDosage(int dosage) {
        this.dosage = dosage;
    }

    public int getInstruction() {
        return instruction;
    }

    public void setInstruction(int instruction) {
        this.instruction = instruction;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
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

    public String getDayMarker() {
        return dayMarker;
    }

    public void setDayMarker(String dayMarker) {
        this.dayMarker = dayMarker;
    }
}
