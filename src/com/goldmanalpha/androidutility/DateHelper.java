package com.goldmanalpha.androidutility;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Aaron
 * Date: 1/1/12
 * Time: 11:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class DateHelper {

    public static final Date addMinutes(Date startDate, int minutesToAdd)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(Calendar.MINUTE, minutesToAdd);

        return cal.getTime();
    }
}
