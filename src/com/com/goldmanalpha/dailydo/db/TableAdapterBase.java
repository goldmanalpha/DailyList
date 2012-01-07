package com.com.goldmanalpha.dailydo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.goldmanalpha.dailydo.model.DoableBase;
import com.goldmanalpha.dailydo.model.DoableValue;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public abstract class TableAdapterBase<T extends DoableBase> {


    private Context context;
    protected static SQLiteDatabase db;
    static DailyDoDatabaseHelper dbHelper;
    private static boolean opened;

    protected String tableName;


    //todo: set context from application object and remove context param
    public TableAdapterBase(Context context, String tableName) {
        this.context = context;
        this.tableName = tableName;
    }

    protected void open() throws SQLException {

        if (!opened) {
            dbHelper = new DailyDoDatabaseHelper(context);
            db = dbHelper.getWritableDatabase();
            opened = true;
        }
    }

    public void close() {
        if (opened) {
            dbHelper.close();
            opened = false;
        }
    }

    //returns id or -1 if fail
    public long save(T object) {

        ContentValues values = createContentValues(object);
        DoableBase b = (DoableBase) object;
        long retVal = b.getId();

        open();

        if (b.getId() == 0) {
            retVal = db.insert(tableName, null, values);
        } else {
            int rows = db.update(tableName, values, "id = " + b.getId(), null);
            if (rows == 0) {
                throw new IndexOutOfBoundsException("Unexpected no rows affected for update of: " + object.toString());
            }

            if (rows > 1) {
                throw new IndexOutOfBoundsException("Unexpected " + rows + " rows affected for update of: " + object.toString());
            }

        }

        return retVal;
    }

    protected ContentValues createContentValues(T object) {
        ContentValues values = new ContentValues();

        DoableBase b = (DoableBase) object;
        //YYYY-MM-DD HH:MM:SS.SSS

        if (b.getId() > 0) {
            //its an update
            values.put("id", b.getId());
        }

        values.put("dateModified", DateToTimeStamp(new Date()));

        return values;
    }

    public abstract T get(int id) throws ParseException;

    protected Cursor getSingle(int id) {
        open();

        Cursor cursor = db.rawQuery("select * from "
                + tableName
                + " where id = ?"
                , new String[]{"" + id});

        return cursor;
    }

    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    protected String DateToTimeStamp(Date d) {
        return simpleDateFormat.format(d);
    }

    public Integer TimeToInt(Time t) {
        if (t == null) {
            //null time is only for defaults
            Date now = new Date();
            t = new Time(now.getHours(), 0, 0);
        }

        return t.getHours() * 10000 + t.getMinutes() * 100 + t.getSeconds();
    }

    public Time IntToTime(Integer time) {
        Integer seconds = time % 100;

        time = (time - seconds) / 100;

        Integer minutes = time % 100;
        Integer hours = (time - minutes) / 100;

        Time t = new Time(hours, minutes, seconds);

        return t;
    }

    public void setCommonValues(T val, Cursor c) {

        try {
            val.setDateCreated(
                    simpleDateFormat.parse(c.getString(c.getColumnIndex("dateCreated"))));
            val.setDateModified(simpleDateFormat.parse(c.getString(c.getColumnIndex("dateModified"))));
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
}
