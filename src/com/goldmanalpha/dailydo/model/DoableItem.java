package com.goldmanalpha.dailydo.model;


import java.sql.Time;
import java.util.Date;

public class DoableItem extends DoableBase {

    String description;
    UnitType unitType;
    boolean isPrivate;

    DoableValue lastValue;
    
    int version;

}


class DoableBase {

    int id;

    Date dateCreated;
    Date dateModified;


}

