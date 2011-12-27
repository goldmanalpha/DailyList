package com.goldmanalpha.androidutility;

import java.util.Date;

//avoid time on a date only meant to represent the day
public class DayOnlyDate extends Date{

    public DayOnlyDate()
    {
        super(new Date().getYear(), new Date().getMonth(), new Date().getDate());
        
    }

    public DayOnlyDate(Date date) {
        //To change body of created methods use File | Settings | File Templates.
        super(date.getYear(), date.getMonth(), date.getDate());
    }
}
