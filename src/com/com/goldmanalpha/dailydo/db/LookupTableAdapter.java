package com.com.goldmanalpha.dailydo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.goldmanalpha.dailydo.model.DoableItem;
import com.goldmanalpha.dailydo.model.SimpleLookup;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Aaron
 * Date: 1/6/12
 * Time: 6:02 AM
 * To change this template use File | Settings | File Templates.
 */
public class LookupTableAdapter extends TableAdapterBase<SimpleLookup> {

    final String TableName;

    public static LookupTableAdapter getItemCategoryTableAdapter(Context context) {
        return new LookupTableAdapter(context, "ItemCategory");
    }

    private LookupTableAdapter(Context context, String tableName) {
        super(context, tableName);
        TableName = tableName;
    }

    @Override
    protected ContentValues createContentValues(SimpleLookup object) {
        ContentValues vals = super.createContentValues(object);    //To change body of overridden methods use File | Settings | File Templates.

        vals.put("name", object.getName());
        vals.put("description", object.getName());

        return vals;
    }

    @Override
    public void setCommonValues(SimpleLookup val, Cursor c) {
        super.setCommonValues(val, c);    //To change body of overridden methods use File | Settings | File Templates.


        val.setDescription(c.getString(c.getColumnIndex("name")));
        val.setName(c.getString(c.getColumnIndex("description")));

    }

    @Override
    public SimpleLookup get(int id) throws ParseException {

        Cursor c = getSingle(id);

        if (c.moveToFirst()) {
            SimpleLookup item = new SimpleLookup(c.getInt(c.getColumnIndex("id")));

            setCommonValues(item, c);
            return item;  //To change body of implemented methods use File | Settings | File Templates.
        }

        return null;
    }

    public List<SimpleLookup> list() {
        open();

        Cursor c = db.rawQuery("select * from " + tableName, new String[]{});
        List<SimpleLookup> lookups = new ArrayList<SimpleLookup>();

        if (c.moveToFirst()) {
            SimpleLookup item = new SimpleLookup(c.getInt(c.getColumnIndex("id")));
            setCommonValues(item, c);
            lookups.add(item);
        }

        c.close();

        return lookups;
    }
}
