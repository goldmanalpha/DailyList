package com.com.goldmanalpha.dailydo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.goldmanalpha.dailydo.model.DoableItem;


public class DoableItemTable extends TableBase {

    public static final String TableName="DoableItem";

    // Database creation SQL statement
    @Override
    protected String databaseCreateSql() {
        return super.databaseCreateSql().replace("?", TableName)
                + "name text not null, "
                + "unitType text not null, "
                + "description text null, "
                + "private integer, "
                + "lastValueId integer null"
                + ");";
    }



}


