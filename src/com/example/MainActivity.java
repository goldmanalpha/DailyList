package com.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends Activity
{

    private TextView mDateDisplay;
    Date mDisplayingDate;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mDateDisplay = (TextView) findViewById(R.id.dateDisplay);

        updateDisplayDate(new Date());
        final Button prevButton = (Button) findViewById(R.id.prevDay);

        prevButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                updateDisplayDate(addDays(mDisplayingDate, -1));
            }
        });

        final Button nextButton = (Button) findViewById(R.id.nextDay);

        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                updateDisplayDate(addDays(mDisplayingDate, 1));
            }
        });



    }

    private void updateDisplayDate(Date date) {

        mDisplayingDate = date;
        SimpleDateFormat format = new SimpleDateFormat("EEE, MMM d, yyyy");
        mDateDisplay.setText(format.format(date));
    }

    Date addDays(Date date, int days)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);  // number of days to add
        return c.getTime();  // dt is now the new date
    }

}
