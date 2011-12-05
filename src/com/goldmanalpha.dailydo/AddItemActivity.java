package com.goldmanalpha.dailydo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

public class AddItemActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.additem);

    }


final String [] items=new String[]{"Item1","Item2","Item3","Item4"};
        ArrayAdapter ad=new ArrayAdapter(this,android.R.layout.simple_spinner_item,items);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spin=(Spinner)findViewById(R.id.Spinner);
        spin.setAdapter(ad);
        spin.setOnItemSelectedListener(new OnItemSelectedListener()
        {

   public void onItemSelected(AdapterView arg0, View arg1,
     int arg2, long arg3) {
    TextView txt=(TextView)findViewById(R.id.txt);
    TextView temp=(TextView)arg1;
    txt.setText(temp.getText());

   }

    public void okClick(View view) {


        final EditText nameField = (EditText) findViewById(R.id.name);
        String name = nameField.getText().toString();

        final EditText emailField = (EditText) findViewById(R.id.description);
        String description = emailField.getText().toString();

        final EditText feedbackField = (EditText) findViewById(R.id.unittype);
        String unittype = feedbackField.getText().toString();

    }

    public void cancelClick(View view) {

    }
}