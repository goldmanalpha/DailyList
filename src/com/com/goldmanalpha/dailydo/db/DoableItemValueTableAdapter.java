package com.com.goldmanalpha.dailydo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.goldmanalpha.dailydo.model.DoableItem;
import com.goldmanalpha.dailydo.model.DoableValue;
import com.goldmanalpha.dailydo.model.UnitType;

import java.util.Date;

public class DoableItemValueTableAdapter extends TableAdapterBase<DoableValue> {

    Context context;

    public DoableItemValueTableAdapter(Context context) {
        super(context, DoableItemValueTable.TableName);
        this.context = context;
    }

    @Override
    protected ContentValues createContentValues(DoableValue object) {
        ContentValues values = super.createContentValues(object);

        values.put("appliesToDate", super.DateToTimeStamp(object.getAppliesToDate()));
        values.put("itemId", object.getDoableItemId());

        //values.put("unitType", object.getUnitType().name());
        values.put("description", object.getDescription());

        if (object.getUnitType() != UnitType.time && object.getUnitType() != UnitType.timeSpan)
        {
            values.putNull("fromTime");
            values.putNull("toTime");

            values.put("amount", object.getAmount());
        }
        else
        {
            values.putNull("amount");
            values.put("fromTime", TimeToInt(object.getFromTime()) );
            values.put("toTime", TimeToInt(object.getToTime()));
        }
        return values;
    }


    public static final String ColId = "id";
    public static final String ColItemId = "items_id";

    public static final String ColItemName = "items_name";
    public static final String ColUnitType = "unitType";
    public static final String ColDescription = "description";
    public static final String ColPrivate = "private";
    public static final String ColDateCreated = "dateCreated";
    public static final String ColDateModified = "dateModified";


    //returns a cursor of doable items:
    public Cursor getItems(Date date) {

        open();

        Cursor cursor = db.rawQuery(
                "select "
                        + " vals.id as _id, vals.id, vals.description, "
                        + " vals.fromTime, vals.toTime, vals.amount, "

                        + " vals.dateCreated, vals.dateModified, "

                        + " items.id as items_id, items.name as items_name, items.unitType, items.private"

                        + " from " + DoableItemTable.TableName + " as items "
                        + " left outer join " + this.tableName + " as vals "
                        + " on vals.itemId = items.id "
                        + " and appliesToDate = ?"
                        + " order by vals.dateCreated"

                , new String[]{super.DateToTimeStamp(date)});

        return cursor;
    }
}


