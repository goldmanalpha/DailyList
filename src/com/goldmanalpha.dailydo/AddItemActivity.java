package com.goldmanalpha.dailydo;

import android.app.Activity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.com.goldmanalpha.dailydo.db.DoableItemTableAdapter;
import com.com.goldmanalpha.dailydo.db.LookupTableAdapter;
import com.goldmanalpha.androidutility.ArrayHelper;
import com.goldmanalpha.androidutility.EnumHelper;
import com.goldmanalpha.dailydo.model.DoableItem;
import com.goldmanalpha.dailydo.model.SimpleLookup;
import com.goldmanalpha.dailydo.model.UnitType;

import java.util.ArrayList;
import java.util.List;

public class AddItemActivity extends Activity {

    DoableItem doableItem;
    DoableItemTableAdapter doableItemTableAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        doableItemTableAdapter = new DoableItemTableAdapter(this);
        doableItem = new DoableItem();

        setContentView(R.layout.additem);
        findFieldsInUi();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,
                EnumHelper.EnumNameToStringArray(UnitType.values()));

        adapter.setDropDownViewResource(R.layout.short_spinner_dropdown_item);

        unitTypeField.setAdapter(adapter);

        if (getIntent().hasExtra("itemId")) {
            loadItem(getIntent().getIntExtra("itemId", 0));
        }

        setupCategories();
    }

    LookupTableAdapter categoryTableAdapter;
    Cursor categoriesCursor;

    private void setupCategories() {
        //To change body of created methods use File | Settings | File Templates.

        categoryTableAdapter = LookupTableAdapter.getItemCategoryTableAdapter(this);

        List<SimpleLookup> categories = categoryTableAdapter.list();

        SimpleLookup addItem = new SimpleLookup(-2);
        addItem.setName("Select Category");
        categories.add(addItem);

        addItem = new SimpleLookup(-1);
                addItem.setName("Add Category");
                categories.add(addItem);



        ArrayAdapter<SimpleLookup> adapter = new ArrayAdapter<SimpleLookup>(
                this, android.R.layout.simple_spinner_item,
                categories);

        adapter.setDropDownViewResource(R.layout.short_spinner_dropdown_item);

        categoryField.setAdapter(adapter);


        categoryField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here

                Toast.makeText(AddItemActivity.this,"Selected",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    @Override
    public void onBackPressed() {
        okClick(null);
    }

    static final String[] unitTypes = EnumHelper.EnumNameToStringArray(UnitType.values());


    void loadItem(int itemId) {
        doableItem = doableItemTableAdapter.get(itemId);

        nameField.setText(doableItem.getName());
        descriptionField.setText(doableItem.getDescription());

        int index = ArrayHelper.IndexOf(unitTypes, doableItem.getUnitType().toString());

        if (index > -1) {
            unitTypeField.setSelection(index);
        }

        isPrivateCheckbox.setChecked(doableItem.getPrivate());
    }

    EditText nameField;
    EditText descriptionField;
    Spinner unitTypeField;
    Spinner categoryField;
    CheckBox isPrivateCheckbox;


    void findFieldsInUi() {
        nameField = (EditText) findViewById(R.id.name);
        descriptionField = (EditText) findViewById(R.id.description);
        unitTypeField = (Spinner) findViewById(R.id.UnitTypeSpinner);
        categoryField = (Spinner) findViewById(R.id.categorySpinner);
        isPrivateCheckbox = (CheckBox) findViewById(R.id.isPrivateCheckbox);


    }

    public void okClick(View view) {


        try {
            DoableItem item = doableItem;


            item.setName(nameField.getText().toString());

            item.setDescription(descriptionField.getText().toString());

            item.setUnitType(UnitType.valueOf(unitTypeField.getSelectedItem().toString()));

            item.setPrivate(isPrivateCheckbox.isChecked());


            if (item.getName().trim().length() > 0 && item.getUnitType() != UnitType.unset) {

                doableItemTableAdapter = new DoableItemTableAdapter(this);
                doableItemTableAdapter.save(item);
                //doableItemTableAdapter.close();


                Toast toast = Toast.makeText(this, item.getName() + " saved.", Toast.LENGTH_LONG);
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