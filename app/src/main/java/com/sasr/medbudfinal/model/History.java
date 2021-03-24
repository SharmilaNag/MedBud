package com.sasr.medbudfinal.model;

public class History {
    private int id;
    private String medName;
    private int medType;
    private String quantityTaken;
    private String dueTime;
    private String dueDate;
    private String dueDay;
    private String takeTime;
    private String takeDate;
    private String takeDay;

    public History() {
    }

    @Override
    public String toString() {
        return "History{" +
                "id='" + id + '\'' +
                ", medName='" + medName + '\'' +
                ", medType='" + medType + '\'' +
                ", quantityTaken='" + quantityTaken + '\'' +
                ", dueTime='" + dueTime + '\'' +
                ", dueDate='" + dueDate + '\'' +
                ", dueDay='" + dueDay + '\'' +
                ", takeTime='" + takeTime + '\'' +
                ", takeDate='" + takeDate + '\'' +
                ", takeDay='" + takeDay + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMedName() {
        return medName;
    }

    public void setMedName(String medName) {
        this.medName = medName;
    }

    public int getMedType() {
        return medType;
    }

    public void setMedType(int medType) {
        this.medType = medType;
    }

    public String getQuantityTaken() {
        return quantityTaken;
    }

    public void setQuantityTaken(String quantityTaken) {
        this.quantityTaken = quantityTaken;
    }

    public String getDueTime() {
        return dueTime;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getDueDay() {
        return dueDay;
    }

    public void setDueDay(String dueDay) {
        this.dueDay = dueDay;
    }

    public String getTakeTime() {
        return takeTime;
    }

    public void setTakeTime(String takeTime) {
        this.takeTime = takeTime;
    }

    public String getTakeDate() {
        return takeDate;
    }

    public void setTakeDate(String takeDate) {
        this.takeDate = takeDate;
    }

    public String getTakeDay() {
        return takeDay;
    }

    public void setTakeDay(String takeDay) {
        this.takeDay = takeDay;
    }
}
