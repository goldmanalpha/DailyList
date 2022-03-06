package com.com.goldmanalpha.dailydo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DailyDoDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "dailydodata.db";

    private static final int DATABASE_VERSION = DBVersions.DBVersion;

    private List<TableBase> tables;

    public DailyDoDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        tables = new ArrayList<TableBase>();
        tables.add(new DoableItemTable());
        tables.add(new DoableItemValueTable());
        tables.add(LookupTable.getItemCategoryTable());
        tables.add(new ItemSortingTable());
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        for (TableBase t : tables) {
            t.onCreate(sqLiteDatabase);
        }

        this.onUpgrade(sqLiteDatabase, 0, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        for (TableBase t : tables) {
            t.onUpgrade(sqLiteDatabase, oldVersion, newVersion);
        }
    }
}
