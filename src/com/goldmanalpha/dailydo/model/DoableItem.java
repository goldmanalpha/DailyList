package com.goldmanalpha.dailydo.model;


import android.content.Context;
import com.com.goldmanalpha.dailydo.db.DoableItemValueTableAdapter;

import java.lang.reflect.Array;
import java.sql.Time;
import java.text.ParseException;
import java.util.Date;

public class DoableItem extends DoableBase {

    String name;
    String description;
    UnitType unitType;
    boolean isPrivate;

    DoableValue lastValue;
    TeaSpoons lastTeaSpoons;

    int lastValueId = 0;

    public DoableItem(int id) {
        //To change body of created methods use File | Settings | File Templates.
        super(id);
    }

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
        lastValueId = 0;
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

    public int getLastValueId() {
        return lastValueId;
    }

    public void setLastValueId(int lastValueId) {
        this.lastValueId = lastValueId;

        if (lastValue != null && lastValue.id != lastValueId)
        {
            lastValue = null;
        }
    }


    public DoableValue getLastValue(Context context) throws ParseException {

        if (lastValueId != 0 && lastValue == null)
        {
               lastValue = new DoableItemValueTableAdapter(context).get(lastValueId);
        }

        return lastValue;
    }

    public void setLastValue(DoableValue lastValue) {
        this.lastValue = lastValue;

        if (lastValue != null)
        {
            lastValueId = lastValue.id;
        }
        else
        {
            lastValueId = 0;
        }
    }

    @Override
    public String toString() {
        return super.toString() + ": " + name;    //To change body of overridden methods use File | Settings | File Templates.
    }
    
    
}


