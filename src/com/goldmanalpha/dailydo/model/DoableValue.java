package com.goldmanalpha.dailydo.model;

import android.content.Context;
import com.com.goldmanalpha.dailydo.db.DoableItemTableAdapter;
import com.com.goldmanalpha.dailydo.db.DoableItemValueTableAdapter;

import java.sql.Time;
import java.text.ParseException;
import java.util.Date;

public class DoableValue extends DoableBase {
    private String description;

    //used as start or only time
    private Time fromTime;
    private Time toTime;

    private Float amount = 0f;

    private Date appliesToDate;
    private Integer doableItemId = 0;
    private boolean hasAnotherDayInstance = false;
    private Integer potency = 0;

    private Time appliesToTime;

    public DoableValue(DoableValue copyItem) {
        super();

        this.setAppliesToDate(copyItem.getAppliesToDate());
        this.setTeaspoons(copyItem.teaspoons);
        this.setPotency(copyItem.potency);
        this.setDoableItemId(copyItem.getItem().getId());
    }


    public DoableValue(int id) {
        super(id);
    }


    public DoableValue() {
    }



    public boolean getHasAnotherDayInstance() {
        return hasAnotherDayInstance;
    }

    public void setHasAnotherDayInstance(boolean hasAnotherDayInstance) {
        this.hasAnotherDayInstance = hasAnotherDayInstance;
    }

    /**
     * can be null,
     * in which case caller might want to default to
     * crDate
     * @return
     */
    public Time getAppliesToTime() {
        return appliesToTime;
    }

    public void setAppliesToTime(Time appliesToTime) {
        this.appliesToTime = appliesToTime;
    }
    public TeaSpoons getTeaspoons() {
        return teaspoons;
    }

    public void setTeaspoons(TeaSpoons teaspoons) {
        this.teaspoons = teaspoons;
    }

    private TeaSpoons teaspoons = TeaSpoons.unset;

    public Date getAppliesToDate() {
        return appliesToDate;
    }

    public void setAppliesToDate(Date appliesToDate) {
        this.appliesToDate = new Date(appliesToDate.getYear(), appliesToDate.getMonth(), appliesToDate.getDate());
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Integer getDoableItemId() {
        return doableItemId;
    }

    public void setDoableItemId(Integer doableItemId) {
        this.doableItemId = doableItemId;
    }


    DoableItem item;

    public DoableItem getItem() {
        if (item == null) {
            item = new DoableItemTableAdapter().get(doableItemId);
        }

        return item;
    }

    public Integer getPotency() {
        return potency;
    }

    public void setPotency(Integer potency) {
        this.potency = potency;
    }
}
