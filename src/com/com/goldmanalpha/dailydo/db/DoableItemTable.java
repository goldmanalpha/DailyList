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
    protected String databaseCreateSql() {
        return super.databaseCreateSql().replace("?", TableName)
                + "name text not null, "
                + "description text null, "
                + "private integer"
                + ");";
    }


    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                          int newVersion) {
        super.onUpgrade(database, oldVersion, newVersion);


        database.execSQL("DROP TABLE IF EXISTS todo");
        onCreate(database);
    }


}


public class DoableItemTableAdapter extends TableAdapterBase<DoableItem> {

    Context context;

    public DoableItemTableAdapter(Context context) {
        super(context, DoableItemTable.TableName);
        this.context = context;
    }


    @Override
    protected ContentValues createContentValues(DoableItem object) {
        ContentValues values = super.createContentValues(object);

        values.put("name", object.getName());
        values.put("description", object.getDescription());
        values.put("private", object.getPrivate());

        return values;
    }

}