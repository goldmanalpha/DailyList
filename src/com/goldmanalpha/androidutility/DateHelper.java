package com.goldmanalpha.androidutility;

import com.com.goldmanalpha.dailydo.db.TableAdapterBase;
import com.goldmanalpha.dailydo.model.DoableBase;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by IntelliJ IDEA.
 * User: Aaron
 * Date: 1/1/12
 * Time: 11:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class DateHelper {

    public static final SimpleDateFormat simpleDateFormatGmt = createSimpleDateFormat("GMT");
    public static final SimpleDateFormat simpleDateFormatLocal = createSimpleDateFormat(null);

    public static final SimpleDateFormat shortMonthDateFormat = new SimpleDateFormat("MMM-dd");
    public static final SimpleDateFormat short24TimeFormat = new SimpleDateFormat("HH:mm");

    public static final Date addMinutes(Date startDate, int minutesToAdd) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(Calendar.MINUTE, minutesToAdd);

        return cal.getTime();
    }

    public static Time getLocalTime(Date date) {
        LocalDateTime localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return toTime(localDate);
    }

    //given a GMT date, retrieves the same date, but as local
    // ex: midnight GMT is 7pm NY, this will return a midnight NY date
    public static Date sameTimeGmt(Date date) {
        String midnightDate = simpleDateFormatLocal.format(date);
        LocalDateTime localDateTime = LocalDateTime.parse(midnightDate, DateTimeFormatter.ofPattern(DATE_FORMAT));

        Date gmtDate = new Date(localDateTime.atZone(TimeZone.getTimeZone("GMT").toZoneId()).toEpochSecond() * 1000);

        return gmtDate;
    }

    public static Time toTime(LocalDateTime localCreated) {
        int hours = localCreated.getHour();
        int minutes = localCreated.getMinute();
        int seconds = localCreated.getSecond();

        return new Time(hours, minutes, seconds);
    }

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static SimpleDateFormat createSimpleDateFormat(String timeZone) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        format.setTimeZone(timeZone == null ? TimeZone.getDefault() : TimeZone.getTimeZone(timeZone));
        return format;
    }

    public static Date TimeStampToDate(String timestamp) throws ParseException {
        return TimeStampToDate(timestamp, simpleDateFormatLocal);
    }

    public static Date TimeStampToDate(String timestamp, SimpleDateFormat zoneFormatter) throws ParseException {
        return zoneFormatter.parse(timestamp);
    }

    public static Integer TimeToInt(Time t) {
        if (t == null) {
            //null time getHasValue only for defaults
            Date now = new Date();
            t = new Time(now.getHours(), 0, 0);
        }

        return t.getHours() * 10000 + t.getMinutes() * 100 + t.getSeconds();
    }

    public static Time IntToTime(Integer time) {
        Integer seconds = time % 100;

        time = (time - seconds) / 100;

        Integer minutes = time % 100;
        Integer hours = (time - minutes) / 100;

        Time t = new Time(hours, minutes, seconds);

        return t;
    }

    public static <T extends DoableBase> float totalHours(TableAdapterBase<T> tableAdapterBase, Integer time1, Integer time2) {

        if (time1 > time2) {

            float diff24 = totalHours(time1, 240000, true);
            float diff2 = totalHours(1, time2, true);

            float total = diff24 + diff2;

            return (float) Math.round(total * 10) / 10f;
        }

        return totalHours(time1, time2, false);
    }

    static float totalHours(Integer time1, Integer time2, boolean exact) {

        float hours1 = (float) Math.floor(time1 / 10000);
        float hours2 = (float) Math.floor(time2 / 10000);

        float minutes1Pct = (time1 % 10000f) / 6000f;
        float minutes2Pct = (time2 % 10000f) / 6000f;

        hours1 += minutes1Pct;
        hours2 += minutes2Pct;

        float diff = hours2 - hours1;

        if (exact)
            return diff;
        else
            return Math.round(diff * 10) / 10.0f;
    }

    public static Date addDays(Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);  // number of days to add
        return c.getTime();  // dt is now the new date
    }

    public static String LongDateString(Date date) {
        return LongDateString(date, "EEE. MMM d, yyyy");
    }

    protected static String LongDateString(Date date, String dateFormat) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        return format.format(date);
    }
}
