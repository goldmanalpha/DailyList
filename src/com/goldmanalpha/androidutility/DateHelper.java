package com.goldmanalpha.androidutility;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    public static final Date addMinutes(Date startDate, int minutesToAdd) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(Calendar.MINUTE, minutesToAdd);

        return cal.getTime();
    }

    public static Time getLocalTime(Date date) {
        LocalDateTime localCreated = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        int hours = localCreated.getHour();
        int minutes = localCreated.getMinute();
        int seconds = localCreated.getSecond();

        return new Time(hours, minutes, seconds);
    }
}
