package com.goldmanalpha.dailydo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.com.goldmanalpha.dailydo.db.DoableItemValueTableAdapter;
import com.goldmanalpha.androidutility.DayOnlyDate;
import com.goldmanalpha.dailydo.model.DoableBase;
import com.goldmanalpha.dailydo.model.DoableValue;

import java.text.ParseException;
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

        DoableBase.setContext(getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mDateDisplay = (TextView) findViewById(R.id.dateDisplay);

        updateDisplayDate(new DayOnlyDate());

    }

    boolean setupDate = false;
    SimpleCursorAdapter listCursorAdapter;

    private void SetupList2(Date date) {
        cursor = doableItemValueTableAdapter.getItems(date);
        startManagingCursor(cursor);

        listCursorAdapter.changeCursor(cursor);
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

        setupDate = true;

        doableItemValueTableAdapter = new DoableItemValueTableAdapter(this);
        cursor = doableItemValueTableAdapter.getItems(date);

        valueIdColumnIndex =cursor.getColumnIndex(DoableItemValueTableAdapter.ColId);
        itemIdColumnIndex = cursor.getColumnIndex(DoableItemValueTableAdapter.ColItemId);
        
        startManagingCursor(cursor);

        String[] from = new String[]{DoableItemValueTableAdapter.ColItemName,
                DoableItemValueTableAdapter.ColUnitType,
                DoableItemValueTableAdapter.ColAmount
        };

        int[] to = new int[]{R.id.list_name, R.id.list_unit_type,
                R.id.amount
                };

        myList = (ListView) findViewById(R.id.main_list);

        final int nameColIndex = cursor.getColumnIndex(DoableItemValueTableAdapter.ColItemName);

        listCursorAdapter = new SimpleCursorAdapter(myList.getContext(),
                R.layout.main_list_item, cursor, from, to);

        listCursorAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

            public boolean setViewValue(View aView, Cursor aCursor, int aColumnIndex) {

                if (aColumnIndex == nameColIndex) {


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

        myList.setAdapter(listCursorAdapter);

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

    public void add_click(View v) throws ParseException {
        TextView tv = (TextView) v;

        int addAmount = 0;
        
        switch (tv.getId()) {
            case R.id.big_minus:
                addAmount = -5;
                break;
            case R.id.big_plus:
                addAmount = +5;
                break;
            case R.id.plus:
                addAmount = 1;
                break;
            case R.id.minus:
                addAmount = -1;
                break;
            default:
                Toast.makeText(getApplicationContext(),
                        "Unexpected source for add_click", Toast.LENGTH_LONG)
                        .show();
        }
        
        if (addAmount != 0)
        {
            ValueIdentifier ids = GetValueIds(v);

            DoableValue value =  doableItemValueTableAdapter
                    .get(ids.ValueId);

            if (value.getDoableItemId() == 0)
            {
                value.setDoableItemId(ids.ItemId);
            }

            //todo use tsp of main item and set to 64ths?!
            //todo: check unit type to see if its a time, etc.

            value.setAmount(value.getAmount() + addAmount);
            value.setAppliesToDate(this.mDisplayingDate);


            doableItemValueTableAdapter.save(value);

            cursor.requery();
        }



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

        SetupList(new DayOnlyDate(date));
    }

    Date addDays(Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);  // number of days to add
        return c.getTime();  // dt is now the new date
    }


}
