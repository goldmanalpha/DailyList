package com.com.goldmanalpha.dailydo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.goldmanalpha.dailydo.model.DoableBase;
import com.goldmanalpha.dailydo.model.DoableItem;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class TableBase {

    protected String databaseCreateSql() {
        return "create table ? "
                + "(id integer primary key, "
                + "dateCreated  TIMESTAMP NOT NULL DEFAULT current_timestamp, "
                + "dateModified  TIMESTAMP NOT NULL DEFAULT current_timestamp, ";

    }

    protected String LogTag = this.getClass().getName();

    protected String databaseUpgradeSql(int newVersion) {
        return null;
    }

    public void onCreate(SQLiteDatabase database) {
        try {
            database.execSQL(databaseCreateSql());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                          int newVersion) {

        Log.i(this.getClass().getName(), "upgrading from version "
                + oldVersion + " to " + newVersion
        );

        for (int i = oldVersion + 1; i <= newVersion; i++) {
            Log.i(LogTag, "applying version " + i);

            String sql = databaseUpgradeSql(i);

            if (sql != null) {
                database.execSQL(sql);
            } else {
                Log.v(LogTag, "no update");
            }
        }

    }

}

public abstract class TableAdapterBase<T> {


    private Context context;
    private SQLiteDatabase db;
    private String tableName;
    DailyDoDatabaseHelper dbHelper;

    public TableAdapterBase(Context context, String tableName) {
        this.context = context;
        this.tableName = tableName;
    }

    public TableAdapterBase<T> open() throws SQLException {
        dbHelper = new DailyDoDatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }



    //returns id or -1 if fail
    public long save(T object) {

        ContentValues values = createContentValues(object);
        DoableBase b = (DoableBase) object;
        long retVal = b.getId();
        
        if (b.getId() == 0)
        {
            retVal = db.insert(tableName, null, values);
        }
        else
        {
            int rows = db.update(tableName, values, "id = " + b.getId(), null);
            if (rows == 0)
            {
                throw new IndexOutOfBoundsException("Unexpected no rows affected for update of: " + object.toString());
            }

            if (rows > 1)
            {
                throw new IndexOutOfBoundsException("Unexpected " + rows + " rows affected for update of: " + object.toString());
            }

        }

        return retVal;
    }

    protected ContentValues createContentValues(T object) {
        ContentValues values = new ContentValues();

        DoableBase b = (DoableBase) object;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        //YYYY-MM-DD HH:MM:SS.SSS

        if (b.getId() > 0) {
            //its an update
            values.put("id", b.getId());
        }

        values.put("dateModified", format.format(new Date()));

        return values;
    }
}
