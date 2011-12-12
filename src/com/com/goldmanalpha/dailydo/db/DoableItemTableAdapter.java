package com.com.goldmanalpha.dailydo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.goldmanalpha.dailydo.model.DoableItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        values.put("unitType", object.getUnitType().name());
        values.put("description", object.getDescription());
        values.put("private", object.getPrivate());

        return values;
    }


    public static final String ColId = "id";
    public static final String ColName = "name";
    public static final String ColUnitType = "unitType";
    public static final String ColDescription = "description";
    public static final String ColPrivate = "private";
    public static final String ColDateCreated = "dateCreated";
    public static final String ColDateModified = "dateModified";


    //returns a cursor of doable items:
    public Cursor getItems() {

        open();

        Cursor cursor = db.rawQuery(
                "select "
                        + " id as _id, id, name, unitType, description, private, dateCreated, dateModified"
                        + " from " + this.tableName, new String[]{});

        return cursor;
    }
}


