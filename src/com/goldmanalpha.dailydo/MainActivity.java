package com.goldmanalpha.dailydo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.com.goldmanalpha.dailydo.db.DoableItemTableAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends Activity {

    private TextView mDateDisplay;
    Date mDisplayingDate;
    DoableItemTableAdapter doableItemTableAdapter;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        SetupList();

        mDateDisplay = (TextView) findViewById(R.id.dateDisplay);

        updateDisplayDate(new Date());

    }

    private void SetupList() {
        doableItemTableAdapter = new DoableItemTableAdapter(this);
        Cursor cursor = doableItemTableAdapter.getItems();

        startManagingCursor(cursor);

        String[] from = new String[]{DoableItemTableAdapter.ColName, DoableItemTableAdapter.ColUnitType};
        int [] to = new int[]{R.id.list_name, R.id.list_unit_type};

        ListView myList=(ListView)findViewById(R.id.main_list);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(myList.getContext(),
                R.layout.main_list_item, cursor, from, to);

        myList.setAdapter(adapter);
    }

    public void nextDayClick(View v) {
        updateDisplayDate(addDays(mDisplayingDate, 1));

    }


    public void prevDayClick(View v) {
        updateDisplayDate(addDays(mDisplayingDate, -1));

    }

    public void addItemClick(View v) {
        startActivity(new Intent(this, AddItemActivity.class));
    }

    private void updateDisplayDate(Date date) {

        mDisplayingDate = date;
        SimpleDateFormat format = new SimpleDateFormat("EEE, MMM d, yyyy");
        mDateDisplay.setText(format.format(date));
    }

    Date addDays(Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);  // number of days to add
        return c.getTime();  // dt is now the new date
    }

}
