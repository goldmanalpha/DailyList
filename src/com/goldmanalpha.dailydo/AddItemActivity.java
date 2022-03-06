package com.goldmanalpha.dailydo;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import com.com.goldmanalpha.dailydo.db.DoableItemTableAdapter;
import com.com.goldmanalpha.dailydo.db.LookupTableAdapter;
import com.goldmanalpha.androidutility.ArrayHelper;
import com.goldmanalpha.androidutility.EnumHelper;
import com.goldmanalpha.dailydo.model.DoableItem;
import com.goldmanalpha.dailydo.model.SimpleLookup;
import com.goldmanalpha.dailydo.model.UnitType;

import java.util.List;
import java.util.function.Predicate;

public class AddItemActivity extends ActivityBase {

    DoableItem doableItem;
    DoableItemTableAdapter doableItemTableAdapter;

    @Override
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

        isPrivateCheckbox.setChecked(true);

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
                this, R.layout.spinner_row,
                categories);

        adapter.setDropDownViewResource(R.layout.short_spinner_dropdown_item);

        categoryField.setAdapter(adapter);

        SimpleLookup[] lookupArray = new SimpleLookup[categories.size()];

        int selectedPosition = ArrayHelper.IndexOfP(
                categories.toArray(lookupArray), new Predicate<SimpleLookup>() {
                    public boolean test(SimpleLookup simpleLookup) {
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

                if (((SimpleLookup) parentView.getSelectedItem()).getId() == -1) {
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

        isPrivateCheckbox.setChecked(doableItem.isPrivate());
        alwaysShowAppliesToTimeCheckbox.setChecked(doableItem.isAlwaysShowAppliesToTime());
    }

    EditText nameField;
    EditText descriptionField;
    Spinner unitTypeField;
    Spinner categoryField;
    CheckBox isPrivateCheckbox;
    Checkable alwaysShowAppliesToTimeCheckbox;

    void findFieldsInUi() {
        nameField = findViewById(R.id.name);
        descriptionField = findViewById(R.id.description);
        unitTypeField = findViewById(R.id.UnitTypeSpinner);
        categoryField = findViewById(R.id.categorySpinner);
        isPrivateCheckbox = findViewById(R.id.isPrivateCheckbox);
        alwaysShowAppliesToTimeCheckbox = (CheckBox) findViewById(R.id.addItemAlwaysShowAppliesToCheckbox);

        Button okButton = findViewById(R.id.okButton);
        Button cancelButton = findViewById(R.id.cancelButton);

        okButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            AddItemActivity.this.okClick(view);
                                        }
                                    }
        );

        cancelButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
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
            // this broke on new phones in 2019
            // probably because it became too small for higher resolutions
            // TODO: make as percent of width & height
            pw = new PopupWindow(layout, 900, 1200, true);
            // display the popup in the center
            pw.showAtLocation(layout, Gravity.CENTER, 0, 0);

            Button okButton = layout.findViewById(R.id.okButton);
            Button cancelButton = layout.findViewById(R.id.cancelButton);

            okButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
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
                                                @Override
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