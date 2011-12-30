package com.goldmanalpha.dailydo.model;

import android.content.Context;
import com.com.goldmanalpha.dailydo.db.DoableItemTableAdapter;
import com.com.goldmanalpha.dailydo.db.DoableItemValueTableAdapter;

import java.sql.Time;
import java.text.ParseException;
import java.util.Date;

public class DoableValue extends DoableBase{
    private String description;

    //used as start or only time
    private Time fromTime;
    private Time toTime;

    private Float amount = 0f;

    private Date appliesToDate;
    private Integer doableItemId = 0;
    /*int previousValueId = 0;
    DoableValue previousValue;
*/

    public TeaSpoons getTeaspoons() {
        return teaspoons;
    }

    public void setTeaspoons(TeaSpoons teaspoons) {
        this.teaspoons = teaspoons;
    }

    private TeaSpoons teaspoons = TeaSpoons.unset;

    public DoableValue()
    {
    }

    public DoableValue(int id)
    {
        super(id);
    }

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
    public DoableItem getItem(Context context) {
        if (item == null)
        {
            item = new DoableItemTableAdapter(context).get(doableItemId);
        }

        return item;
    }


/*
    public DoableValue getPreviousValue(Context context) throws ParseException {

        if (previousValueId != 0 && previousValue == null)
        {
            previousValue = new DoableItemValueTableAdapter(context).get(previousValueId);
        }

        return previousValue == null ? this : previousValue;
    }


    public int getPreviousValueId() {
        return previousValueId;
    }

    public void setPreviousValueId(int previousValueId) {
        this.previousValueId = previousValueId;

        if (previousValue != null && previousValue.id != previousValueId)
        {
            previousValue = null;
        }
    }


    public void setPreviousValue(DoableValue previousValue) {
        this.previousValue = previousValue;

        if (previousValue != null)
        {
            previousValueId = previousValue.id;
        }
        else
        {
            previousValueId = 0;
        }
    }
    */
}
