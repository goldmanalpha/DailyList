package com.com.goldmanalpha.androidutility.ui;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

public class SimpleTaggingCursorAdapter extends SimpleCursorAdapter {
    
    
    public SimpleTaggingCursorAdapter(Context context, int layout, 
                                      Cursor c, String[] from, int[] to) {
        super(context, layout, c, from, to);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v =  super.getView(position, convertView, parent);    //To change body of overridden methods use File | Settings | File Templates.

        v.setTag(position);

        return v;
    }
}
