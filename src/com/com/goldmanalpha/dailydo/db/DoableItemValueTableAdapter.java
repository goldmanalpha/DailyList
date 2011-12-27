package com.com.goldmanalpha.dailydo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.goldmanalpha.dailydo.model.DoableItem;
import com.goldmanalpha.dailydo.model.DoableValue;
import com.goldmanalpha.dailydo.model.UnitType;

import java.text.ParseException;
import java.util.Date;

public class DoableItemValueTableAdapter 
        extends TableAdapterBase<DoableValue> {

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

        values.put("description", object.getDescription());

        if (object.getItem(context).getUnitType() != UnitType.time
                && object.getItem(context).getUnitType() != UnitType.timeSpan)
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
    public static final String ColAmount = "amount";

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
                        + " order by vals.dateCreated desc"

                , new String[]{super.DateToTimeStamp(date)});

        return cursor;
    }
    
    @Override
    public DoableValue get(int id) throws ParseException {
        Cursor c = getSingle(id);

        DoableValue val = new DoableValue();

        if (c.moveToFirst())
        {

            val = new DoableValue(c.getInt(c.getColumnIndex("id")));

            super.setCommonValues(val, c);


            val.setAmount(c.getInt(c.getColumnIndex("amount")));
            val.setAppliesToDate(simpleDateFormat.parse(c.getString(c.getColumnIndex("appliesToDate"))));

            val.setFromTime(IntToTime(c.getInt(c.getColumnIndex("fromTime"))));
            val.setToTime(IntToTime(c.getInt(c.getColumnIndex("toTime"))));

            val.setDoableItemId(c.getInt(c.getColumnIndex("itemId")));


        }


        return val;
    }
}


