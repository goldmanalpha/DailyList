package com.goldmanalpha.dailydo;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.android.internal.util.Predicate;
import com.com.goldmanalpha.dailydo.db.DoableItemTableAdapter;
import com.com.goldmanalpha.dailydo.db.LookupTableAdapter;
import com.goldmanalpha.androidutility.ArrayHelper;
import com.goldmanalpha.androidutility.EnumHelper;
import com.goldmanalpha.dailydo.model.DoableItem;
import com.goldmanalpha.dailydo.model.SimpleLookup;
import com.goldmanalpha.dailydo.model.UnitType;

import java.util.List;

public class AddItemActivity extends Activity {

    DoableItem doableItem;
    DoableItemTableAdapter doableItemTableAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        doableItemTableAdapter = new DoableItemTableAdapter();
        doableItem = new DoableItem();

        setContentView(R.layout.add_item);
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

        categoryTableAdapter = LookupTableAdapter.getItemCategoryTableAdapter();

        final List<SimpleLookup> categories = categoryTableAdapter.list();

        SimpleLookup addItem = new SimpleLookup(-2);
        addItem.setName("Select Category");
        categories.add(0, addItem);

        addItem = new SimpleLookup(-1);
        addItem.setName("Add Category");
        categories.add(addItem);

        ArrayAdapter<SimpleLookup> adapter = new ArrayAdapter<SimpleLookup>(
                this, android.R.layout.simple_spinner_item,
                categories);

        adapter.setDropDownViewResource(R.layout.short_spinner_dropdown_item);

        categoryField.setAdapter(adapter);


        SimpleLookup [] lookupArray = new SimpleLookup[categories.size()];

        int selectedPosition = ArrayHelper.IndexOfP(
            categories.toArray(lookupArray), new Predicate<SimpleLookup>() {
            public boolean apply(SimpleLookup simpleLookup) {
                if (doableItem == null)
                    return false;

                return simpleLookup.getId() == doableItem.getCategoryId();  //To change body of implemented methods use File | Settings | File Templates.
            }
        });

        categoryField.setSelection(selectedPosition);

        categoryField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here

                if (((SimpleLookup) ((Spinner) parentView).getSelectedItem()).getId() == -1) {
                    initiatePopupWindow();
                }


                SimpleLookup lookup = categories.get(position);

                if (lookup.getId() > 0) {
                    doableItem.setCategoryId(lookup.getId());
                }
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
        alwaysShowAppliesToTimeCheckbox.setChecked(doableItem.getAlwaysShowAppliesToTime());
    }

    EditText nameField;
    EditText descriptionField;
    Spinner unitTypeField;
    Spinner categoryField;
    CheckBox isPrivateCheckbox;
    Checkable alwaysShowAppliesToTimeCheckbox;


    void findFieldsInUi() {
        nameField = (EditText) findViewById(R.id.name);
        descriptionField = (EditText) findViewById(R.id.description);
        unitTypeField = (Spinner) findViewById(R.id.UnitTypeSpinner);
        categoryField = (Spinner) findViewById(R.id.categorySpinner);
        isPrivateCheckbox = (CheckBox) findViewById(R.id.isPrivateCheckbox);
        alwaysShowAppliesToTimeCheckbox = (CheckBox) findViewById(R.id.addItemAlwaysShowAppliesToCheckbox);

        Button okButton = (Button) findViewById(R.id.okButton);
        Button cancelButton = (Button) findViewById(R.id.cancelButton);

        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AddItemActivity.this.okClick(view);
            }
        }
        );

        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AddItemActivity.this.finish();
            }
        }
        );

    }


    private PopupWindow pw;

    private void initiatePopupWindow() {
        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //Inflate the view from a predefined XML layout
            final View layout = inflater.inflate(R.layout.edit_lookup,
                    (ViewGroup) findViewById(R.id.edit_lookup_root));
            // create a 300px width and 470px height PopupWindow
            pw = new PopupWindow(layout, 300, 470, true);
            // display the popup in the center
            pw.showAtLocation(layout, Gravity.CENTER, 0, 0);


            Button okButton = (Button) layout.findViewById(R.id.okButton);
            Button cancelButton = (Button) layout.findViewById(R.id.cancelButton);

            okButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    //save:
                    String name =
                            ((EditText) layout.findViewById(R.id.name)).getText().toString();

                    String description =
                            ((EditText) layout.findViewById(R.id.description))
                                    .getText().toString();


                    if (("" + name).trim().length() > 0) {
                        SimpleLookup lookup = new SimpleLookup();

                        lookup.setName(name);
                        lookup.setDescription(description);

                        categoryTableAdapter.save(lookup);

                        setupCategories();

                        pw.dismiss();
                    } else {
                        Toast.makeText(AddItemActivity.this, "name is required", Toast.LENGTH_SHORT).show();
                    }

                }
            }
            );

            cancelButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    pw.dismiss();
                }
            }
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void okClick(View view) {


        try {
            DoableItem item = doableItem;


            item.setName(nameField.getText().toString());

            item.setDescription(descriptionField.getText().toString());

            item.setUnitType(UnitType.valueOf(unitTypeField.getSelectedItem().toString()));

            item.setPrivate(isPrivateCheckbox.isChecked());

            item.setAlwaysShowAppliesToTime(alwaysShowAppliesToTimeCheckbox.isChecked());

            if (item.getName().trim().length() > 0 && item.getUnitType() != UnitType.unset) {

                doableItemTableAdapter = new DoableItemTableAdapter();
                doableItemTableAdapter.save(item);
                //doableItemTableAdapter.close();


                Toast toast = Toast.makeText(this, item.getName() + " saved.", Toast.LENGTH_SHORT);
                toast.show();

                finish();
            } else {

                Toast toast = Toast.makeText(this, "Fill in name and unit type to save.", Toast.LENGTH_SHORT);
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