package com.goldmanalpha.dailydo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DoableItem extends DoableBase {

    String name;
    String description;
    UnitType unitType;
    boolean isPrivate;
    SimpleLookup category;
    boolean alwaysShowAppliesToTime;

    int displayOrder = 0;
    int categoryId;

    public DoableItem(int id) {
        //To change body of created methods use File | Settings | File Templates.
        super(id);
    }

    @Override
    public String toString() {
        return super.toString() + ": " + name;    //To change body of overridden methods use File | Settings | File Templates.
    }


}


