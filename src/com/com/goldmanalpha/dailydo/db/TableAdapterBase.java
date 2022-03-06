package com.com.goldmanalpha.dailydo.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.goldmanalpha.androidutility.DateHelper;
import com.goldmanalpha.dailydo.model.DoableBase;

import java.text.ParseException;
import java.util.Date;

public abstract class TableAdapterBase<T extends DoableBase>
        extends DatabaseRoot {

    protected String tableName;

    public TableAdapterBase(String tableName) {
        this.tableName = tableName;
    }

    //returns id or -1 if fail
    public long save(T object) {

        ContentValues values = createContentValues(object);
        DoableBase b = object;
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

        DoableBase b = object;
        //YYYY-MM-DD HH:MM:SS.SSS

        if (b.getId() > 0) {
            //its an update
            values.put("id", b.getId());
        }

        values.put("dateModified", DateHelper.simpleDateFormatGmt.format(new Date()));

        return values;
    }

    public abstract T get(int id) throws ParseException;

    public void delete(int id) throws ParseException {
        open();

        String sql = "delete from " + tableName + " where id = " + id;

        db.execSQL(sql);
    }

    protected Cursor getSingle(int id) {
        open();

        Cursor cursor = db.rawQuery("select * from "
                        + tableName
                        + " where id = ?"
                , new String[]{"" + id});

        return cursor;
    }

    public void setCommonValues(T val, Cursor c) {

        try {
            val.setDateCreated(
                    DateHelper.TimeStampToDate(c.getString(c.getColumnIndex("dateCreated"))));
            val.setDateModified(DateHelper.simpleDateFormatGmt.parse(c.getString(c.getColumnIndex("dateModified"))));
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
