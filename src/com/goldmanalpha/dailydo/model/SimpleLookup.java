package com.goldmanalpha.dailydo.model;

public class SimpleLookup extends DoableBase {
    String name;
    String description;

    public static final int ALL_ID = -2;
    public static final int UNSET_ID = -1;

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
