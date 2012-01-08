package com.goldmanalpha.dailydo.model;

public class SimpleLookup extends DoableBase {
    String name;
    String description;

    //matches any:
    public static final int ALL_ID = -2;

    //lookup value has not been applied:
    public static final int UNSET_ID = -1;

    //matches only where value is applied, not nulls
    public static final int HAS_VALUE_ID = -3;

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
