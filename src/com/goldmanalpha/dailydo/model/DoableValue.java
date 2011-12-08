package com.goldmanalpha.dailydo.model;

import java.sql.Time;

public class DoableValue extends DoableBase{
    String description;

    //tsp saved in 64ths (which is half the min I can measure)
    UnitType unitType;

    //used as start or only time
    Time fromTime;
    Time toTime;

    Float amount;

}
