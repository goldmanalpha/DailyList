package com.com.goldmanalpha.dailydo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.goldmanalpha.dailydo.model.DoableItem;
import com.goldmanalpha.dailydo.model.DoableValue;
import com.goldmanalpha.dailydo.model.TeaSpoons;
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
    public long save(DoableValue object) {
        long id = super.save(object);
        return id;
    }

    @Override
    protected ContentValues createContentValues(DoableValue object) {
        ContentValues values = super.createContentValues(object);

        values.put("appliesToDate", super.DateToTimeStamp(object.getAppliesToDate()));
        values.put("itemId", object.getDoableItemId());

        values.put("description", object.getDescription());

        if (object.getItem(context).getUnitType() != UnitType.time
                && object.getItem(context).getUnitType() != UnitType.timeSpan) {
            values.putNull("fromTime");
            values.putNull("toTime");

            values.put("amount", object.getAmount());
        } else {
            values.putNull("amount");
            values.put("fromTime", TimeToInt(object.getFromTime()));
            values.put("toTime", TimeToInt(object.getToTime()));
        }

        values.put("teaspoons", object.getTeaspoons().toString());

        //todo: handle case of item being added out of order :(
        //probably only if appliesToDate < current date
        //need to open the next item and save it...

        values.put("previousValueId", getPreviousId(object.getDoableItemId(), object.getAppliesToDate()));

        return values;
    }


    public int getPreviousId(int itemId, Date date) {
        Cursor c = db.rawQuery(
                "select max(dateCreated) as maxDate from " + this.tableName
                        + " where itemId = ? "
                        + " and appliesToDate < ?"
                        + " order by dateCreated desc",
                new String[]{"" + itemId, super.DateToTimeStamp(date)});

        if (c.moveToFirst()) {

            String lastDate = c.getString(c.getColumnIndex("maxDate"));

            c.close();

            if (lastDate != null) {
                Cursor c2 = db.rawQuery(
                        "select id from " + this.tableName
                                + " where dateCreated = ? "
                                + " and itemId = ?",
                        new String[]{lastDate, "" + itemId});

                c2.moveToFirst();

                int id = c2.getInt(0);

                c2.close();

                return id;
            }
        }

        return 0;
    }

    public static final String ColId = "id";
    public static final String ColItemId = "items_id";

    public static final String ColItemName = "items_name";
    public static final String ColUnitType = "unitType";
    public static final String ColAmount = "amount";
    public static final String ColTeaspoons = "teaspoons";

    public static final String ColLastTeaspoons = "lastTeaspoons";
    public static final String ColLastAmount = "lastAmount";
    public static final String ColLastAppliesToDate = "lastAppliesToDate";

    public static final String ColDescription = "description";
    public static final String ColPrivate = "private";
    public static final String ColDateCreated = "dateCreated";
    public static final String ColDateModified = "dateModified";

    public static final String ColFromTime = "fromTime";
    public static final String ColLastFromTime = "lastFromTime";

    //returns a cursor of doable items:
    public Cursor getItems(Date date) {

        open();

        String sql ="select "
                        + " vals.id as _id, vals.id, vals.description, "
                        + " vals.fromTime, vals.toTime, vals.amount, "
                        + " vals.teaspoons, "

                        + " vals.dateCreated, vals.dateModified, "

                        + " items.id as items_id, items.name as items_name, items.unitType, items.private, "
                        + " coalesce(lastVal.teaspoons, vals.teaspoons, latestVal.teaspoons) lastTeaspoons, "
                        + " coalesce(lastVal.amount, vals.amount, latestVal.amount) lastAmount, "
                        + " coalesce(lastVal.fromTime, vals.fromTime, latestVal.fromTime) lastFromTime, "
                        + " coalesce(lastVal.toTime, vals.toTime, latestVal.toTime) lastToTime, "
                        + " coalesce(lastVal.appliesToDate, vals.appliesToDate, latestVal.appliesToDate) lastAppliesToDate "

                        + " from " + DoableItemTable.TableName + " as items "
                        + " left outer join " + this.tableName + " as vals "
                        + " on vals.itemId = items.id "
                        + " and vals.appliesToDate = ?"
                        + " left outer join " + this.tableName + " as lastVal "
                        + " on vals.previousValueId = lastVal.id "

                        + " left outer join ViewItemValueMax as valueMaxJunction "
                        + " on items.id = valueMaxJunction.itemId "

                        + " left outer join " + this.tableName + " as latestVal "
                        + " on valueMaxJunction.valueId = latestVal.id "


                        + " order by vals.dateCreated desc";

        Cursor cursor = db.rawQuery(sql, new String[]{super.DateToTimeStamp(date)});

        return cursor;
    }

    @Override
    public DoableValue get(int id) throws ParseException {
        Cursor c = getSingle(id);

        DoableValue val = new DoableValue();

        if (c.moveToFirst()) {

            val = new DoableValue(c.getInt(c.getColumnIndex("id")));

            super.setCommonValues(val, c);


            val.setAmount(c.getFloat(c.getColumnIndex("amount")));
            val.setAppliesToDate(simpleDateFormat.parse(c.getString(c.getColumnIndex("appliesToDate"))));

            val.setFromTime(IntToTime(c.getInt(c.getColumnIndex("fromTime"))));
            val.setToTime(IntToTime(c.getInt(c.getColumnIndex("toTime"))));

            val.setDoableItemId(c.getInt(c.getColumnIndex("itemId")));

            val.setTeaspoons(
                    TeaSpoons.valueOf(
                            c.getString(c.getColumnIndex("teaspoons"))));


        }


        return val;
    }
}


