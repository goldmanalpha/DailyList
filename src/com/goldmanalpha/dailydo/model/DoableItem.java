package com.goldmanalpha.dailydo.model;


import android.content.Context;
import com.com.goldmanalpha.dailydo.db.DoableItemValueTableAdapter;
import com.com.goldmanalpha.dailydo.db.LookupTableAdapter;

import java.lang.reflect.Array;
import java.sql.Time;
import java.text.ParseException;
import java.util.Date;

public class DoableItem extends DoableBase {

    String name;
    String description;
    UnitType unitType;
    boolean isPrivate;
    SimpleLookup category;
    boolean  alwaysShowAppliesToTime;

    int displayOrder = 0;

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }


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


    @Override
    public String toString() {
        return super.toString() + ": " + name;    //To change body of overridden methods use File | Settings | File Templates.
    }

    public SimpleLookup getCategory() throws ParseException {

        if (category == null) {
            if (categoryId == 0) {
                category = new SimpleLookup();
                category.setName("Unset");
            } else {
                LookupTableAdapter adapter = LookupTableAdapter.getItemCategoryTableAdapter();

                category = adapter.get(categoryId);

            }

        }

        return category;
    }

    int categoryId;

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;

        category = null;
    }

    public int getCategoryId() {
        return this.categoryId;
    }

    public boolean getAlwaysShowAppliesToTime() {
        return alwaysShowAppliesToTime;
    }

    public void setAlwaysShowAppliesToTime(boolean alwaysShowAppliesToTime) {
        this.alwaysShowAppliesToTime = alwaysShowAppliesToTime;
    }

}


