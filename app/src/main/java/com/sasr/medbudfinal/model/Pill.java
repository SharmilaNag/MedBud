package com.sasr.medbudfinal.model;

public class Pill {
    private int id;
    private String name;
    private int type;

    public Pill(int id, String name, int type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public Pill() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Pill{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}
