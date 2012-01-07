package com.goldmanalpha.dailydo.model;

public class SimpleLookup extends DoableBase {
    String name;
    String description;

    public SimpleLookup(int id) {
        super(id);
    }

    public SimpleLookup() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return name;
    }
}
