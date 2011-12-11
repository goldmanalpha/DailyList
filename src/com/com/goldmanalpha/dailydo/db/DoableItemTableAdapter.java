package com.com.goldmanalpha.dailydo.db;

import android.content.ContentValues;
import android.content.Context;
import com.goldmanalpha.dailydo.model.DoableItem;

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
