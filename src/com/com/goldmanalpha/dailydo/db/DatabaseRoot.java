package com.com.goldmanalpha.dailydo.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public abstract class DatabaseRoot {
    protected static Context context;

    protected static SQLiteDatabase db;
        static DailyDoDatabaseHelper dbHelper;
        private static boolean opened;


        protected void open() throws SQLException {

            if (!opened) {
                dbHelper = new DailyDoDatabaseHelper(context);
                db = dbHelper.getWritableDatabase();
                opened = true;
            }
        }

        public static void close() {
            if (opened) {
                dbHelper.close();
                opened = false;
            }
        }

    public static void setContext(Context contextIn) {
        context = contextIn;

    }
}
