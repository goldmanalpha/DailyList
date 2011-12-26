package com.goldmanalpha.dailydo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.com.goldmanalpha.dailydo.db.DoableItemValueTableAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends Activity {

    private TextView mDateDisplay;
    Date mDisplayingDate;
    DoableItemValueTableAdapter doableItemValueTableAdapter;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mDateDisplay = (TextView) findViewById(R.id.dateDisplay);

        updateDisplayDate(new Date());

    }

    boolean setupDate = false;
    SimpleCursorAdapter adapter;

    private void SetupList2(Date date) {
        cursor = doableItemValueTableAdapter.getItems(date);
        adapter.changeCursor(cursor);
    }

    ListView myList;
    Cursor cursor;
    int valueIdColumnIndex;
    int itemIdColumnIndex;
    
    private void SetupList(Date date) {

        if (setupDate) {
            SetupList2(date);
            return;
        }

        doableItemValueTableAdapter = new DoableItemValueTableAdapter(this);
        cursor = doableItemValueTableAdapter.getItems(date);

        valueIdColumnIndex =cursor.getColumnIndex(DoableItemValueTableAdapter.ColId);
        itemIdColumnIndex = cursor.getColumnIndex(DoableItemValueTableAdapter.ColItemId);
        
        startManagingCursor(cursor);

        String[] from = new String[]{DoableItemValueTableAdapter.ColItemName,
                DoableItemValueTableAdapter.ColUnitType};

        int[] to = new int[]{R.id.list_name, R.id.list_unit_type};

        myList = (ListView) findViewById(R.id.main_list);

        final int nameColIndex = cursor.getColumnIndex(DoableItemValueTableAdapter.ColItemName);

        adapter = new SimpleCursorAdapter(myList.getContext(),
                R.layout.main_list_item, cursor, from, to);



        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

            public boolean setViewValue(View aView, Cursor aCursor, int aColumnIndex) {

                if (aColumnIndex == nameColIndex) {
                    //attach the keys to the parent
                    ValueIdentifier ids = new ValueIdentifier();

                }
                //todo: use to set formatted time into time field
                /*if (aColumnIndex == 2) {
                    String createDate = aCursor.getString(aColumnIndex);
                    TextView textView = (TextView) aView;
                    textView.setText("Create date: " + MyFormatterHelper.formatDate(getApplicationContext(), createDate));
                    return true;
                }*/

                return false;
            }
        });

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

    class ValueIdentifier {
        public int ValueId;
        public int ItemId;

        @Override
        public String toString()
        {

            return "ItemId: " + ItemId + " ValueId: " + ValueId;
        }
    }
    
    public ValueIdentifier GetValueIds(View view)
    {
        if (cursor.moveToPosition(myList.getPositionForView(view)))
        {
            
            ValueIdentifier vi = new ValueIdentifier();
            
            vi.ValueId = cursor.getInt(valueIdColumnIndex );
            vi.ItemId = cursor.getInt(itemIdColumnIndex);
            
            return vi;
        }

        return null;
    }

    public void nameClick(View view) {
        ValueIdentifier ids =  GetValueIds(view);

        Toast.makeText(getApplicationContext(),
                ids.toString() + " " + ((TextView) view).getText(),
                Toast.LENGTH_SHORT).show();

    }




    public void unit_type_click(View v) {
        ValueIdentifier ids =  GetValueIds(v);

        Toast.makeText(getApplicationContext(),
                ids.toString() + " " + ((TextView) v).getText(),
                Toast.LENGTH_SHORT).show();

    }

    public void add_click(View v) {
        TextView tv = (TextView) v;


        switch (tv.getId()) {
            case R.id.big_minus:
                break;
            case R.id.big_plus:
                break;
            case R.id.plus:
                break;
            case R.id.minus:
                break;
            default:
                Toast.makeText(getApplicationContext(),
                        "Unexpected source for add_click", Toast.LENGTH_LONG)
                        .show();
        }

        Toast.makeText(getApplicationContext(),
                "Add: " + tv.getText(), Toast.LENGTH_LONG)
                .show();


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

        SetupList(new Date(date.getYear(), date.getMonth(), date.getDay()));
    }

    Date addDays(Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);  // number of days to add
        return c.getTime();  // dt is now the new date
    }

}
