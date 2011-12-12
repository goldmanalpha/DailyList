package com.goldmanalpha.dailydo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.com.goldmanalpha.dailydo.db.Converter;
import com.com.goldmanalpha.dailydo.db.DoableItemTableAdapter;
import com.goldmanalpha.dailydo.model.DoableItem;
import com.goldmanalpha.dailydo.model.UnitType;

public class AddItemActivity extends Activity {

    DoableItem doableItem;
    DoableItemTableAdapter doableItemTableAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        doableItemTableAdapter = new DoableItemTableAdapter(this);

        doableItem = new DoableItem();

        setContentView(R.layout.additem);

        Spinner s = (Spinner) findViewById(R.id.UnitTypeSpinner);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(
                this, R.array.unittypelist, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(R.layout.short_spinner_dropdown_item);

        s.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        doableItemTableAdapter.close();
        super.onDestroy();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void okClick(View view) {


        try {
            DoableItem item = new DoableItem();

            final EditText nameField = (EditText) findViewById(R.id.name);
            item.setName(nameField.getText().toString());

            final EditText descriptionField = (EditText) findViewById(R.id.description);
            item.setDescription(descriptionField.getText().toString());

            final Spinner unitTypeField = (Spinner) findViewById(R.id.UnitTypeSpinner);
            item.setUnitType(Converter.stringToUnitType(this, unitTypeField.getSelectedItem().toString()));

            final CheckBox isPrivateCheckbox = (CheckBox) findViewById(R.id.isPrivateCheckbox);
            item.setPrivate(isPrivateCheckbox.isChecked());


            if (item.getName().trim().length() > 0 && item.getUnitType() != UnitType.unset) {

                doableItemTableAdapter  = new DoableItemTableAdapter(this);
                doableItemTableAdapter.save(item);
                doableItemTableAdapter.close();


                Toast toast = Toast.makeText(this, item.getName() +  " saved.", Toast.LENGTH_LONG);
                toast.show();

                finish();
            } else {

                Toast toast = Toast.makeText(this, "Fill in name and unit type to save.", Toast.LENGTH_LONG);
                toast.show();
            }


        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

            Toast toast = Toast.makeText(this, "Save Error " + e.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void cancelClick(View view) {
        finish();
    }
}