package com.sasr.medbudfinal.model;

public class Inventory {
    private int id;
    private Pill pill;
    private int quantity;
    private int warningQuantity;
    private int quantityUnit;

    public Inventory(int id, Pill pill, int quantity, int warningQuantity, int quantityUnit) {
        this.id = id;
        this.pill = pill;
        this.quantity = quantity;
        this.warningQuantity = warningQuantity;
        this.quantityUnit = quantityUnit;
    }

    public Inventory() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Pill getPill() {
        return pill;
    }

    public void setPill(Pill pill) {
        this.pill = pill;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getWarningQuantity() {
        return warningQuantity;
    }

    public void setWarningQuantity(int warningQuantity) {
        this.warningQuantity = warningQuantity;
    }

    public int getQuantityUnit() {
        return quantityUnit;
    }

    public void setQuantityUnit(int quantityUnit) {
        this.quantityUnit = quantityUnit;
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "id=" + id +
                ", pill=" + pill +
                ", quantity=" + quantity +
                ", warningQuantity=" + warningQuantity +
                ", quantityUnit=" + quantityUnit +
                '}';
    }
}
