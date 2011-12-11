package com.goldmanalpha.dailydo.model;


import java.lang.reflect.Array;
import java.sql.Time;
import java.util.Date;

public class DoableItem extends DoableBase {

    String name;
    String description;
    UnitType unitType;
    boolean isPrivate;
    DoableValue lastValue;
    TeaSpoons lastTeaSpoons;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TeaSpoons getLastTeaSpoons() {
        return lastTeaSpoons;
    }

    public void setLastTeaSpoons(TeaSpoons lastTeaSpoons) {
        this.lastTeaSpoons = lastTeaSpoons;
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


    public boolean getPrivate() {
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

    @Override
    public String toString() {
        return super.toString() + ": " + name;    //To change body of overridden methods use File | Settings | File Templates.
    }
}


