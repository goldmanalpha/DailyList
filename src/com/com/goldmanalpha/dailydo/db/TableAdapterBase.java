package com.com.goldmanalpha.dailydo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.goldmanalpha.dailydo.model.DoableBase;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public abstract class TableAdapterBase<T> {


    private Context context;
    protected SQLiteDatabase db;
    protected String tableName;
    DailyDoDatabaseHelper dbHelper;
    private boolean opened;

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
    
    protected String DateToTimeStamp(Date d)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        return format.format(d);
    }

    public Integer TimeToInt(Time t)
    {
        return t.getHours() * 10000 + t.getMinutes() * 100 + t.getSeconds();
    }
    
    public Time IntToTime(Integer time)
    {
        Integer seconds = time % 100;

        time = (time - seconds) / 100;
        
        Integer minutes = time % 100;
        Integer hours = (time - minutes) / 100;
        
        Time t = new Time(hours, minutes, seconds);
        
        return t;
    }
}
