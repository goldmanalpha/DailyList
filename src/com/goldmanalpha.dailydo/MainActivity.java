package com.goldmanalpha.dailydo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.com.goldmanalpha.androidutility.ui.SimpleTaggingCursorAdapter;
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

        SimpleTaggingCursorAdapter adapter = new SimpleTaggingCursorAdapter(myList.getContext(),
                R.layout.main_list_item, cursor, from, to);

        myList.setAdapter(adapter);

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                
                
                // When clicked, show a toast with the TextView text
                Toast.makeText(getApplicationContext(), 
                        ((TextView) view).getText(),
                        Toast.LENGTH_SHORT).show();
            }
        });


    }


   public void nameClick(View view)
   {

       View v = view;

       while (((View) v.getParent()).getId() != R.id.main_list)
       {
           v = (View) v.getParent();
       }


       Object tag = v.getTag();

       
       Toast.makeText(getApplicationContext(),
               tag.toString() + ((TextView) view).getText(),
               Toast.LENGTH_SHORT).show();
       
   }
    
   public void unit_type_click(View v)
   {
       Toast.makeText(getApplicationContext(), ((TextView) v).getText(),
               Toast.LENGTH_SHORT).show();

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
