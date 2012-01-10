package com.com.goldmanalpha.dailydo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.goldmanalpha.dailydo.model.DoableItem;
import com.goldmanalpha.dailydo.model.DoableValue;
import com.goldmanalpha.dailydo.model.UnitType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DoableItemTableAdapter extends TableAdapterBase<DoableItem> {


    public DoableItemTableAdapter() {
        super(DoableItemTable.TableName);
    }

    public void updateOrder(int id, int order) {

        open();

        ContentValues values = new ContentValues();
        values.put("displayOrder", order);

        int rows = db.update(DoableItemTable.TableName, values, "id = " + id, null);

        if (rows == 0) {
            throw new IndexOutOfBoundsException("Unexpected no rows affected for order update of: " + DoableItemTable.TableName);
        }

        if (rows > 1) {
            throw new IndexOutOfBoundsException("Unexpected " + rows + " rows affected for update of: " + DoableItemTable.TableName);
        }

    }


    @Override
    protected ContentValues createContentValues(DoableItem object) {
        ContentValues values = super.createContentValues(object);

        values.put("name", object.getName());
        values.put("unitType", object.getUnitType().name());
        values.put("description", object.getDescription());
        values.put("private",
                object.getPrivate() ? 1 : 0);

        values.put("displayOrder", object.getDisplayOrder());


        values.put("categoryId", object.getCategoryId());

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
                        + " id as _id, id, name, unitType, description, private, dateCreated, dateModified, displayOrder, categoryId "
                        + " from " + this.tableName, new String[]{});

        return cursor;
    }

    @Override
    public DoableItem get(int id) {
        DoableItem item = new DoableItem();

        Cursor c = getSingle(id);

        if (c.moveToFirst()) {
            item = new DoableItem(c.getInt(c.getColumnIndex("id")));

            super.setCommonValues(item, c);

            item.setName(c.getString(c.getColumnIndex("name")));
            item.setUnitType(UnitType.valueOf(c.getString(c.getColumnIndex("unitType"))));
            item.setDescription(c.getString(c.getColumnIndex("description")));
            item.setPrivate(
                    c.getInt(c.getColumnIndex("private")) == 0 ? Boolean.FALSE : Boolean.TRUE
            );

            item.setDisplayOrder(c.getInt(c.getColumnIndex("displayOrder")));
            item.setCategoryId(c.getInt(c.getColumnIndex("categoryId")));
        }

        c.close();
        return item;

    }
}


