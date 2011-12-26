package com.goldmanalpha.dailydo.model;

import java.sql.Time;
import java.util.Date;

public class DoableValue extends DoableBase{
    private String description;

    //tsp saved in 64ths (which is half the min I can measure)
    private UnitType unitType;

    //used as start or only time
    private Time fromTime;
    private Time toTime;

    private Integer amount;

    private Date appliesToDate;
    private Integer doableItemId;

    public Date getAppliesToDate() {
        return appliesToDate;
    }

    public void setAppliesToDate(Date appliesToDate) {
        this.appliesToDate = appliesToDate;
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

    public Time getFromTime() {
        return fromTime;
    }

    public void setFromTime(Time fromTime) {
        this.fromTime = fromTime;
    }

    public Time getToTime() {
        return toTime;
    }

    public void setToTime(Time toTime) {
        this.toTime = toTime;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getDoableItemId() {
        return doableItemId;
    }

    public void setDoableItemId(Integer doableItemId) {
        this.doableItemId = doableItemId;
    }
}
