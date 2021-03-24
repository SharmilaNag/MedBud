package com.sasr.medbudfinal.model;

public class MedDisplay {
    private String medName;
    private int medIcon;

    public MedDisplay(String medName, int medIcon) {
        this.medName = medName;
        this.medIcon = medIcon;
    }

    public MedDisplay() {
    }

    public String getMedName() {
        return medName;
    }

    public void setMedName(String medName) {
        this.medName = medName;
    }

    public int getMedIcon() {
        return medIcon;
    }

    public void setMedIcon(int medIcon) {
        this.medIcon = medIcon;
    }
}
