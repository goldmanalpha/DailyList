package com.goldmanalpha.dailydo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.com.goldmanalpha.androidutility.ui.SimpleTaggingCursorAdapter;
import com.com.goldmanalpha.dailydo.db.DoableItemTableAdapter;
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
        Cursor cursor = doableItemValueTableAdapter.getItems(date);
        adapter.changeCursor(cursor);
    }

    private void SetupList(Date date) {

        if (setupDate) {
            SetupList2(date);
            return;
        }

        doableItemValueTableAdapter = new DoableItemValueTableAdapter(this);
        Cursor cursor = doableItemValueTableAdapter.getItems(date);

        startManagingCursor(cursor);

        String[] from = new String[]{DoableItemValueTableAdapter.ColItemName,
                DoableItemValueTableAdapter.ColUnitType};

        int[] to = new int[]{R.id.list_name, R.id.list_unit_type};

        ListView myList = (ListView) findViewById(R.id.main_list);

        final int nameColIndex = cursor.getColumnIndex(DoableItemValueTableAdapter.ColItemName);

        adapter = new SimpleCursorAdapter(myList.getContext(),
                R.layout.main_list_item, cursor, from, to);



        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {




            public boolean setViewValue(View aView, Cursor aCursor, int aColumnIndex) {

                if (aColumnIndex == nameColIndex) {
                    //attach the keys to the parent
                    ValueIdentfier ids = new ValueIdentfier();

                    ids.ValueId = aCursor.getInt(
                            aCursor.getColumnIndex(DoableItemValueTableAdapter.ColId));

                    ids.ItemId = aCursor.getInt(
                            aCursor.getColumnIndex(DoableItemValueTableAdapter.ColItemId));

                    View parentRow = ListRow(aView);


                    parentRow.setTag(ids);

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

    class ValueIdentfier {
        public int ValueId;
        public int ItemId;
    }

    public void nameClick(View view) {
        int id = DoableItemId(view);

        Toast.makeText(getApplicationContext(),
                "" + id + ((TextView) view).getText(),
                Toast.LENGTH_SHORT).show();

    }

    int DoableItemId(View view) {
        View v = ListRow(view);

        Object tag = v.getTag();

        return Integer.parseInt(tag.toString());
    }

    View ListRow(View view) {
        View v = view;

        while (((View) v.getParent()).getId() != R.id.main_list) {
            v = (View) v.getParent();
        }

        return v;
    }


    public void unit_type_click(View v) {
        int id = DoableItemId(v);

        Toast.makeText(getApplicationContext(),
                "" + id + " " + ((TextView) v).getText(),
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
