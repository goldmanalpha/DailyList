package com.goldmanalpha.dailydo.model;


import java.lang.reflect.Array;
import java.sql.Time;
import java.util.Date;

public class DoableItem extends DoableBase {

    String description;
    UnitType unitType;
    boolean isPrivate;
    DoableValue lastValue;
    int version;
    TeaSpoons lastTeaSpoons;

    public TeaSpoons getLastTeaSpoons() {
        return lastTeaSpoons;
    }

    public void setLastTeaSpoons(TeaSpoons lastTeaSpoons) {
        this.lastTeaSpoons = lastTeaSpoons;
    }

    public DoableItem(int id, int version) {
        super(id);
        this.version = version;
    }

    public DoableItem(int version) {
        super();
        this.version = version;
    }

    public DoableItem() {
        super();
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UnitType getUnitType() {
        return unitType;
    }

    public void setUnitType(UnitType unitType) {
        this.unitType = unitType;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public DoableValue getLastValue() {
        return lastValue;
    }

    public void setLastValue(DoableValue lastValue) {
        this.lastValue = lastValue;
    }

    public int getVersion() {
        return version;
    }
}


