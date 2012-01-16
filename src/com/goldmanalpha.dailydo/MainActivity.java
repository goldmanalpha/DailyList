package com.goldmanalpha.dailydo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.android.internal.util.Predicate;
import com.com.goldmanalpha.dailydo.db.*;
import com.goldmanalpha.androidutility.*;
import com.goldmanalpha.dailydo.model.*;

import java.io.File;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity {

    DoableValueCursorHelper cursorHelper;
    private TextView mDateDisplay;
    Date mDisplayingDate;
    DoableItemValueTableAdapter doableItemValueTableAdapter;

    private static final SimpleDateFormat shortMonthDateFormat = new SimpleDateFormat("MMM-dd");
    private static final SimpleDateFormat short24TimeFormat = new SimpleDateFormat("HH:mm");

    HashMap<Integer, AltFocus> usesAltFocusMap = new HashMap<Integer, AltFocus>();

    public MainActivity() {
    }

    public static String ExtraValueDateLong = "dateToShow";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        mainList = (ListView) findViewById(R.id.main_list);
        registerForContextMenu(mainList);

        mDateDisplay = (TextView) findViewById(R.id.dateDisplay);

        Intent intent = getIntent();
        Long dateLong = intent.getLongExtra(ExtraValueDateLong, new DayOnlyDate().getTime());
        updateDisplayDate(new Date(dateLong));


        if (savedInstanceState != null)
            selectedCategoryId = savedInstanceState.getInt("selectedCategoryId", SimpleLookup.ALL_ID);
        setupCategories();


    }


    boolean showPrivate = true;

    static final class MenuItems {
        public static final int AddItem = 0;
        public static final int Backup = 1;
        public static final int Quit = 2;

        public static final int DeleteDb = 4;

        public static final int PublicPrivateSwitch = 5;

        public static final int DuplicateItem = 6;

        public static final int BackupFolder = 7;

        public static final int DeleteItem = 8;

        public static final int ItemHistory = 9;
    }

    MenuItem PublicPrivateMenuItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Preferences

        //group, item, order, title

        menu.add(0, MenuItems.AddItem, 0, "Add Item");

        PublicPrivateMenuItem =
                menu.add(0, MenuItems.PublicPrivateSwitch, 0, "Pub Only");

        menu.add(0, MenuItems.Quit, 0, "Quit");

        menu.add(1, MenuItems.BackupFolder, 0, "Backup Folder");

        menu.add(1, MenuItems.Backup, 0, "Backup");
        //menu.add(1, MenuItems.DeleteDb, 0, "DELETE DB");

        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.main_list) {


            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

            cachedCursor.moveToPosition(info.position);

            final String name = cachedCursor.getString(
                    cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColItemName)
            );

            menu.setHeaderTitle(name);

            menu.add(Menu.NONE, MenuItems.DuplicateItem, 0, "Duplicate Item");

            menu.add(Menu.NONE, MenuItems.DeleteItem, 0, "Delete Value");

            menu.add(Menu.NONE, MenuItems.ItemHistory, 0, "Item History");
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        final ValueIdentifier ids = getValueIdsForCurrentCursorPosition();
        final String name = cachedCursor.getString(
                cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColItemName)
        );
        ;
        boolean handled = false;
        SeriousConfirmationDialog dlg = null;

        switch (item.getItemId()) {
            case MenuItems.DuplicateItem:

                if (ids == null || ids.ValueId == 0) {
                    Toast.makeText(this, "No value to duplicate", Toast.LENGTH_LONG).show();
                    return true;
                }

                try {
                    MainActivity.this.
                            doableItemValueTableAdapter.createDuplicate(ids.ValueId);

                } catch (ParseException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

                    Toast.makeText(this, "Problem duplicating: " + e.getMessage(), Toast.LENGTH_LONG)
                            .show();
                }

                SetupList2(mDisplayingDate);

                handled = true;


                break;

            case MenuItems.DeleteItem:

                if (ids == null || ids.ValueId == 0) {
                    Toast.makeText(this, "No value to delete", Toast.LENGTH_LONG).show();
                    return true;
                }

                String description = cachedCursor.getString(descriptionColumnIndex);

                if (description != null && (description).trim() != "") {
                    Toast.makeText(this, "Can't delete value with description -- delete description first.",
                            Toast.LENGTH_LONG).show();

                } else {
                    dlg = new SeriousConfirmationDialog(this,
                            name, "Delete item?",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (id == DialogInterface.BUTTON_POSITIVE) {

                                        try {
                                            doableItemValueTableAdapter.delete(ids.ValueId);
                                        } catch (ParseException e) {
                                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

                                            Toast.makeText(MainActivity.this, "Problem deleting: " + e.getMessage(), Toast.LENGTH_LONG)
                                                    .show();
                                        }
                                        SetupList2(mDisplayingDate);
                                    }

                                }
                            });

                    dlg.show();


                }

                handled = true;


                break;

            case MenuItems.ItemHistory:

                Intent intent = new Intent(this, SingleItemHistoryActivity.class);

                intent.putExtra(SingleItemHistoryActivity.ExtraValueItemName, name);
                intent.putExtra(SingleItemHistoryActivity.ExtraValueItemId,  ids.ItemId);

                startActivity(intent);

                break;

        }

        return handled;
    }

    public void list_description_click(View v) {
        ValueIdentifier ids = this.GetValueIds(v);

        if (ids.ValueId == 0) {
            Toast.makeText(this, "Need to set value before description", Toast.LENGTH_SHORT)
                    .show();

        } else {
            Intent intent = new Intent(this, EditDescriptionActivity.class);

            intent.putExtra(EditDescriptionActivity.ExtraValueId, ids.ValueId);

            startActivity(intent);
        }
    }

    void shareFile(String filePath) {

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("application/x-gzip");

        share.putExtra(Intent.EXTRA_STREAM,
                Uri.parse("file://" + filePath));

        share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(share, "Share Backup"));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String path = "data/data/" + this.getPackageName() + "/databases/";

        switch (item.getItemId()) {
            case MenuItems.PublicPrivateSwitch:
                this.showPrivate = !this.showPrivate;
                PublicPrivateMenuItem.setTitle(this.showPrivate ? "Pub Only" : "Show Private");
                this.SetupList2(mDisplayingDate);
                break;
            case MenuItems.DeleteDb:
                DeleteConfirmationDialog dlg = new DeleteConfirmationDialog(this,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                if (id == DialogInterface.BUTTON_POSITIVE) {
                                    //backup?!

                                    String localPath = "data/data/" + getPackageName() + "/databases/";


                                    SharedPreferences preferences =
                                            getSharedPreferences(getApplication().getPackageName(), MODE_PRIVATE);

                                    String targetPath = preferences.getString("BackupFolder", localPath);

                                    BackupHelper helper = new BackupHelper();
                                    helper.backup(localPath, targetPath, DailyDoDatabaseHelper.DATABASE_NAME, "preDelete.");

                                    //delete

                                    File f = new File(
                                            "data/data/" + getPackageName() + "/databases/"
                                                    + DailyDoDatabaseHelper.DATABASE_NAME);

                                    f.delete();

                                    finish();


                                }
                            }
                        });

                dlg.show();

                break;
            case (MenuItems.AddItem):
                cachedCursor.close();
                startActivity(new Intent(this, AddItemActivity.class));
                break;

            case (MenuItems.Backup):
                Toast.makeText(this, "Backing up", Toast.LENGTH_LONG).show();

                BackupService backupService = new BackupService();


                SharedPreferences preferences =
                        getSharedPreferences(getApplication().getPackageName(), MODE_PRIVATE);

                String targetPath = preferences.getString("BackupFolder", "");

                String backupFileName = backupService.doBackup("", getPackageName(), targetPath);

                Toast.makeText(this, "DailyDo DB Backed Up. Recommended to share it with DroopBox", Toast.LENGTH_SHORT).show();

                this.shareFile(backupFileName);

                break;

            case (MenuItems.BackupFolder):

                Intent browser = new Intent(Intent.ACTION_GET_CONTENT);
                browser.setType("file/*");
                startActivityForResult(browser, IntentRequestCodes.BackupFolder);

                break;

            case (MenuItems.Quit):
                Toast.makeText(this, "Bye.", Toast.LENGTH_SHORT).show();

                startService(new Intent(this, BackupService.class));

                finish();
                break;
            default:
                return false;
        }

        return true;
    }


    LookupTableAdapter categoryTableAdapter;
    int selectedCategoryId = SimpleLookup.ALL_ID;
    Spinner categoryField;

    private void setupCategories() {
        categoryField = (Spinner) findViewById(R.id.categorySpinner);

        categoryTableAdapter = LookupTableAdapter.getItemCategoryTableAdapter();

        final List<SimpleLookup> categories = categoryTableAdapter.list();

        SimpleLookup addItem = new SimpleLookup(SimpleLookup.ALL_ID);
        addItem.setName("All Categories");
        categories.add(0, addItem);

        addItem = new SimpleLookup(SimpleLookup.UNSET_ID);
        addItem.setName("No Category");
        categories.add(1, addItem);

        addItem = new SimpleLookup(SimpleLookup.HAS_VALUE_ID);
        addItem.setName("Has Values");
        categories.add(2, addItem);

        ArrayAdapter<SimpleLookup> adapter = new ArrayAdapter<SimpleLookup>(
                this, android.R.layout.simple_spinner_item,
                categories);

        adapter.setDropDownViewResource(R.layout.short_spinner_dropdown_item);

        categoryField.setAdapter(adapter);

        SimpleLookup[] lookupArray = new SimpleLookup[categories.size()];

        //todo: save last category on close
        int selectedPosition = ArrayHelper.IndexOfP(
                categories.toArray(lookupArray), new Predicate<SimpleLookup>() {
            public boolean apply(SimpleLookup simpleLookup) {
                return simpleLookup.getId() == MainActivity.this.selectedCategoryId;  //To change body of implemented methods use File | Settings | File Templates.
            }
        });


        //todo: this reset to previous state doesn't exactly work
        //the display text in the spinner is wrong.
        categoryField.setSelection(selectedPosition, true);

        categoryField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                selectedCategoryId =
                        ((SimpleLookup) ((Spinner) parentView).getSelectedItem()).getId();

                MainActivity.this.SetupList2(mDisplayingDate);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

    }

    @Override
    protected void onResume() {

        setupCategories();

        SetupList2(mDisplayingDate);

        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
    }

    boolean setupDate = false;
    SimpleCursorAdapter listCursorAdapter;

    public void SetupList2(Date date) {

        if (date != mDisplayingDate) {
            usesAltFocusMap.clear();
        }

        cachedCursor.close();
        cachedCursor = doableItemValueTableAdapter.getItems(date, showPrivate, selectedCategoryId);
        startManagingCursor(cachedCursor);

        listCursorAdapter.changeCursor(cachedCursor);
    }

    ListView mainList;
    Cursor cachedCursor;
    int valueIdColumnIndex;
    int itemIdColumnIndex;


    int fromTimeColumnIndex;
    int toTimeColumnIndex;
    int lastFromTimedColumnIndex;
    int lastToTimeColumnIndex;
    int descriptionColumnIndex;

    int teaspoonColIdx;
    int lastTeaspoonColIdx;

    private void SetupList(Date date) {

        if (setupDate) {
            SetupList2(date);
            return;
        }

        setupDate = true;

        doableItemValueTableAdapter = new DoableItemValueTableAdapter();
        cachedCursor = doableItemValueTableAdapter.getItems(date, showPrivate, SimpleLookup.ALL_ID);

        valueIdColumnIndex = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColId);
        itemIdColumnIndex = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColItemId);
        descriptionColumnIndex = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColDescription);
        final int nowColumnIndex = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColPlaceHolder1);

        startManagingCursor(cachedCursor);
        cursorHelper = new DoableValueCursorHelper(cachedCursor);

        String[] from = new String[]{DoableItemValueTableAdapter.ColItemName,
                DoableItemValueTableAdapter.ColUnitType,
                DoableItemValueTableAdapter.ColAmount,
                DoableItemValueTableAdapter.ColTeaspoons,
                DoableItemValueTableAdapter.ColLastAppliesToDate,
                DoableItemValueTableAdapter.ColLastAmount,
                DoableItemValueTableAdapter.ColLastTeaspoons,
                DoableItemValueTableAdapter.ColFromTime,
                DoableItemValueTableAdapter.ColLastFromTime,
                DoableItemValueTableAdapter.ColToTime,
                DoableItemValueTableAdapter.ColLastToTime,
                DoableItemValueTableAdapter.ColDescription,
                DoableItemValueTableAdapter.ColPlaceHolder1, //set to now
                DoableItemValueTableAdapter.ColAppliesToTime
        };

        int[] to = new int[]{R.id.list_name, R.id.list_unit_type,
                R.id.amount, R.id.list_teaspoons,
                R.id.list_lastDate, R.id.list_lastAmount,
                R.id.list_lastTeaspoons, R.id.list_time1_value,
                R.id.list_lastTime1, R.id.list_time2_value, R.id.list_lastTime2,
                R.id.list_description, R.id.list_set_now,
                R.id.list_applies_to_time
        };

        teaspoonColIdx = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColTeaspoons);
        lastTeaspoonColIdx = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColLastTeaspoons);
        final int lastAppliesToDateColIdx = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColLastAppliesToDate);
        final int lastTeaspoonsColIdx = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColLastTeaspoons);
        final int appliesToTimeColIdx = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColAppliesToTime);
        final int showAppliesToTimeCountColIdx = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColShowAppliesToTimeCount);
        final int createdDateColIdx = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColDateCreated);

        lastFromTimedColumnIndex = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColLastFromTime);
        fromTimeColumnIndex = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColFromTime);
        toTimeColumnIndex = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColToTime);
        lastToTimeColumnIndex = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColLastToTime);


        listCursorAdapter = new SimpleCursorAdapter(mainList.getContext(),
                R.layout.main_list_item, cachedCursor, from, to);

        mainList.setAdapter(listCursorAdapter);

        listCursorAdapter.setViewBinder(
                new SimpleCursorAdapter.ViewBinder() {
                    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

                        boolean returnValue = false;

                        if (appliesToTimeColIdx == columnIndex) {


                            int itemId = cursor.getInt(itemIdColumnIndex);
                            TextView tv = (TextView) view;

                            boolean editAppliesToTime = usesAltFocusMap.containsKey(itemId)
                                    && usesAltFocusMap.get(itemId) == AltFocus.AppliesToTime;

                            //todo:  too much leaking logic???
                            //converting to objects would be less (or too in)efficient?
                            boolean showAppliesToTime = cursor.getInt(showAppliesToTimeCountColIdx) > 0
                                    && cursorHelper.timesToShowDate(cursor) < 1;

                            boolean isActive = false;
                            if (editAppliesToTime && showAppliesToTime) {
                                tv.setShadowLayer(3, 3, 3, Color.GREEN);
                                isActive = true;
                            } else {
                                tv.setShadowLayer(0, 0, 0, Color.BLACK);
                                usesAltFocusMap.remove(itemId);
                            }

                            if (showAppliesToTime) {
                                Time t = new Time(0, 0, 0);
                                if (cursor.isNull(appliesToTimeColIdx)) {

                                    if (!isActive)
                                        tv.setShadowLayer(6, 0, 0, Color.YELLOW);

                                    try {
                                        Date crDate =
                                                doableItemValueTableAdapter.TimeStampToDate(cursor.getString(createdDateColIdx));

                                        t = new Time(crDate.getHours(), crDate.getMinutes(), crDate.getSeconds());
                                    } catch (ParseException e) {
                                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                    }
                                } else {
                                    t = doableItemValueTableAdapter.IntToTime(cursor.getInt(appliesToTimeColIdx));
                                }

                                tv.setText(short24TimeFormat.format(t));
                            } else {
                                tv.setText("");
                            }

                            returnValue = true;
                        }

                        if (columnIndex == descriptionColumnIndex) {
                            TextView tv = ((TextView) view);

                            String description = cursor.getString(columnIndex);

                            if (description != null && description.trim().length() > 0) {
                                tv.setShadowLayer(6, 0, 0, Color.MAGENTA);
                                tv.setText("|D|");

                            } else {
                                int id = cursor.getInt(valueIdColumnIndex);

                                if (id == 0) {
                                    ((TextView) view).setText("");
                                } else {
                                    ((TextView) view).setText("|D|");
                                }

                                ((TextView) view).setShadowLayer(0, 0, 0, Color.BLACK);
                            }

                            returnValue = true;
                        }

                        if (columnIndex == fromTimeColumnIndex
                                || columnIndex == lastFromTimedColumnIndex
                                || columnIndex == toTimeColumnIndex
                                || columnIndex == lastToTimeColumnIndex
                                ) {

                            returnValue = true;

                            TextView tv = (TextView) view;

                            int timesToShowDate = cursorHelper.timesToShowDate(cursor);

                            if (timesToShowDate > 1 &&
                                    (columnIndex == fromTimeColumnIndex || columnIndex == toTimeColumnIndex)) {
                                int itemId = cursor.getInt(itemIdColumnIndex);

                                boolean editFirstTime = !usesAltFocusMap.containsKey(itemId)
                                        || usesAltFocusMap.get(itemId) == AltFocus.Time1OrValue;

                                if ((columnIndex == fromTimeColumnIndex && editFirstTime)
                                        || (columnIndex == toTimeColumnIndex && !editFirstTime)
                                        )
                                    tv.setShadowLayer(3, 3, 3, Color.GREEN);
                                else
                                    tv.setShadowLayer(0, 0, 0, Color.BLACK);
                            } else
                                tv.setShadowLayer(0, 0, 0, Color.BLACK);


                            //hide dash if we don't have 2 dates
                            int dashId = 0;

                            if (columnIndex == fromTimeColumnIndex) {
                                dashId = R.id.list_time_separator;
                            }

                            if (columnIndex == lastFromTimedColumnIndex) {
                                dashId = R.id.list_lastTimeSeparator;
                            }

                            if (dashId != 0) {

                                TextView dashView = (TextView)
                                        ((ViewGroup) tv.getParent()).findViewById(dashId);

                                if (timesToShowDate < 2)
                                    dashView.setText("");
                                else
                                    dashView.setText(" - ");
                            }


                            boolean hasValue = hasValue(cursor);
                            boolean fromShows = (columnIndex == lastFromTimedColumnIndex
                                    || (columnIndex == fromTimeColumnIndex && hasValue))
                                    && timesToShowDate > 0;

                            boolean toShows = (columnIndex == lastToTimeColumnIndex
                                    || (columnIndex == toTimeColumnIndex && hasValue))
                                    && timesToShowDate > 1;

                            if (fromShows || toShows) {

                                int timeAsInt = cursor.getInt(columnIndex);
                                Time t = doableItemValueTableAdapter
                                        .IntToTime(timeAsInt);

                                String totalHours = "";
                                if (toShows && timeAsInt > 0) {
                                    int startTimeAsInt = 0;
                                    //calc the total hours
                                    if (columnIndex == lastToTimeColumnIndex) {
                                        startTimeAsInt = cursor.getInt(lastFromTimedColumnIndex);
                                    }

                                    if (columnIndex == toTimeColumnIndex) {
                                        startTimeAsInt = cursor.getInt(fromTimeColumnIndex);
                                    }

                                    totalHours = " ("
                                            + doableItemValueTableAdapter.totalHours(startTimeAsInt, timeAsInt)
                                            + ")";
                                }

                                tv.setText(short24TimeFormat.format(t) + totalHours);


                            } else {
                                //stupid android seems to hold old values and apply them automatically when handled = true
                                tv.setText("");
                            }
                        }

                        if (columnIndex == teaspoonColIdx) {
                            TextView tv = ((TextView) view);

                            if (!cursorHelper.isTeaspoons(cursor)) {
                                tv.setText("");
                                returnValue = true;
                            } else {

                                tv.setText(getTeaspoonsForCursorPosition(cursor));
                                returnValue = true;
                            }
                        }

                        if (columnIndex == lastTeaspoonsColIdx) {
                            TextView tv = ((TextView) view);

                            if (!cursorHelper.isTeaspoons(cursor)) {
                                tv.setText("");
                                returnValue = true;
                            }
                        }

                        if (columnIndex == lastAppliesToDateColIdx) {
                            returnValue = true;

                            ApplyLastAppliesToDateBind((TextView) view, cursor, columnIndex);
                        }

                        if (columnIndex == nowColumnIndex) {

                            int timesToShowDate = cursorHelper.timesToShowDate(cursor);
                            TextView tv = (TextView) view;

                            if (timesToShowDate > 0) {
                                tv.setText("now");
                            } else {
                                tv.setText("");
                            }

                            returnValue = true;
                        }

                        return returnValue;
                    }
                }
        );

        mainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                if (view instanceof TextView) {

                    // When clicked, show a toast with the TextView text
                    Toast.makeText(getApplicationContext(),
                            ((TextView) view).getText(),
                            Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    private void ApplyLastAppliesToDateBind(TextView view, Cursor cursor, int columnIndex) {
        //format the date

        String lastAppliesToDate = cursor.getString(columnIndex);

        if (lastAppliesToDate == null) {
            return;
        }

        try {
            Date d = DoableItemValueTableAdapter.simpleDateFormat.parse(lastAppliesToDate);

            TextView tv = (TextView) view;

            tv.setText(shortMonthDateFormat.format(d));

        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }




    public final static TeaSpoons defaultTeaspoons = TeaSpoons.eighth;

    class ValueIdentifier {
        public int ValueId;
        public int ItemId;

        @Override
        public String toString() {

            return "ItemId: " + ItemId + " ValueId: " + ValueId;
        }
    }

    public boolean hasValue(Cursor c) {
        return cachedCursor.getInt(valueIdColumnIndex) > 0;
    }

    void moveCursorToCurrentRow(View view) {
        GetValueIds(view);
    }

    ValueIdentifier lastValueId;

    //beneficial side effect - sets cachedCursor to proper position
    public ValueIdentifier GetValueIds(View view) {

        if (view == null)
            return lastValueId;

        if (cachedCursor.moveToPosition(mainList.getPositionForView(view))) {

            return getValueIdsForCurrentCursorPosition();
        }

        return (lastValueId = null);
    }

    private ValueIdentifier getValueIdsForCurrentCursorPosition() {
        ValueIdentifier vi = new ValueIdentifier();

        vi.ValueId = cachedCursor.getInt(valueIdColumnIndex);
        vi.ItemId = cachedCursor.getInt(itemIdColumnIndex);

        lastValueId = vi;
        return vi;
    }

    public void nameClick(View view) {
        ValueIdentifier ids = GetValueIds(view);

        Intent intent = new Intent(this, AddItemActivity.class);
        intent.putExtra("itemId", ids.ItemId);

        startActivity(intent);
    }

    public void list_now_click(View v) throws ParseException {
        ValueIdentifier ids = GetValueIds(v);

        final DoableValue value = getCurrentValue(ids);

        Boolean usesTime1 = !usesAltFocusMap.containsKey(ids.ItemId)
                || usesAltFocusMap.get(ids.ItemId) == AltFocus.Time1OrValue;

        Date now = new Date();
        Time nowTime = new Time(now.getHours(), now.getMinutes(), now.getSeconds());

        String whichTimeToSet = "?";
        if (usesTime1) {
            value.setFromTime(nowTime);
            whichTimeToSet = "fromTime";
        } else {
            value.setToTime(nowTime);
            whichTimeToSet = "toTime";
        }


        SeriousConfirmationDialog dlg = new SeriousConfirmationDialog(this,
                value.getItem().getName(), "Set " + whichTimeToSet + " to current time?",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (id == DialogInterface.BUTTON_POSITIVE) {

                            doableItemValueTableAdapter.save(value);
                            SetupList2(mDisplayingDate);

                        }
                    }
                });

        dlg.show();


    }

    public void unit_type_click(View v) {
        ValueIdentifier ids = GetValueIds(v);

        Toast.makeText(getApplicationContext(),
                ids.toString() + " " + ((TextView) v).getText(),
                Toast.LENGTH_SHORT).show();

    }


    DoableValue teaspoonsClickValue;

    public void teaspoons_click(View v) {

        //setup the value, because the intent will close the cursor
        try {
            teaspoonsClickValue = doableItemValueTableAdapter
                    .get(this.GetValueIds(v).ValueId);
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

            Toast.makeText(this, "Error loading value for tsp change: " + e.getMessage(), Toast.LENGTH_LONG).show();

            return;
        }


//in case it was an unset/new value:
        SetDefaultsForNewValue(teaspoonsClickValue);


        Intent intent = new Intent(this, PickOneList.class);

        intent.putExtra(PickOneList.Title, "Pick Unit Teaspoon Size");

        intent.putExtra(PickOneList.SelectedItem, ((TextView) v).getText());

        intent.putExtra(PickOneList.Choices,
                EnumHelper.EnumNameToStringArray(TeaSpoons.values(), 1));


        startActivityForResult(intent, IntentRequestCodes.TeaspoonSelection);
    }

    class IntentRequestCodes {
        public static final int TeaspoonSelection = 1;

        public static final int BackupFolder = 2;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);    //To change body of overridden methods use File | Settings | File Templates.

        switch (requestCode) {
            case IntentRequestCodes.TeaspoonSelection:
                if (resultCode == RESULT_OK) {

                    String setToTeaspoons = data.getStringExtra(PickOneList.SelectedItem);


                    if (!teaspoonsClickValue.getTeaspoons().toString().equals(setToTeaspoons)) {

                        teaspoonsClickValue.setTeaspoons(TeaSpoons.valueOf(setToTeaspoons));

                        doableItemValueTableAdapter.save(teaspoonsClickValue);
                    }

                    SetupList(new DayOnlyDate(this.mDisplayingDate));
                }
                break;
            case IntentRequestCodes.BackupFolder:
                if (resultCode == RESULT_OK) {

                    String FilePath = data.getData().getPath();
                    String FileName = data.getData().getLastPathSegment();
                    int lastPos = FilePath.length() - FileName.length();
                    String Folder = FilePath.substring(0, lastPos);

                    SharedPreferences preferences =
                            getSharedPreferences(getApplication().getPackageName(), MODE_PRIVATE);
                    preferences.edit().putString("BackupFolder", Folder).commit();

                    Toast.makeText(this, "Saved folder: " + Folder, Toast.LENGTH_LONG).show();

                    /*textFile.setText("Full Path: \n" + FilePath + "\n");
              textFolder.setText("Folder: \n" + Folder + "\n");
              textFileName.setText("File Name: \n" + FileName + "\n");*/
                }
                break;

        }


    }


    //for a new doable value, this will set defaults enough to save
    //need to have called GetIds prior to this call
    void SetDefaultsForNewValue(DoableValue value) {

        if (value.getId() != 0)
            return;


        value.setAppliesToDate(mDisplayingDate);

        value.setDoableItemId(GetValueIds(null).ItemId);

        int timesToShow = cursorHelper.timesToShowDate(cachedCursor);

        if (value.getId() == 0) {
            if (timesToShow > 0) {

                int sqlFromTime = cachedCursor.getInt(lastFromTimedColumnIndex);
                value.setFromTime(doableItemValueTableAdapter.IntToTime(sqlFromTime));

                if (timesToShow > 1) {
                    int sqlToTime = cachedCursor.getInt(lastToTimeColumnIndex);
                    value.setToTime(doableItemValueTableAdapter.IntToTime(sqlToTime));
                }

            }

        }

        //if its tsp, make sure there's a tsp type set
        if (value.getTeaspoons() == TeaSpoons.unset && cursorHelper.isTeaspoons(cachedCursor)) {
            value.setTeaspoons(
                    TeaSpoons.valueOf(
                            this.getTeaspoonsForCursorPosition(cachedCursor)));
        }


    }

    String getTeaspoonsForCursorPosition(Cursor c) {


        String setTeaspoons = c.getString(this.teaspoonColIdx);
        String lastTeaspoons = c.getString(this.lastTeaspoonColIdx);

        String unset = TeaSpoons.unset.toString();

        if (setTeaspoons == null || unset.equals(setTeaspoons)) {
            if (lastTeaspoons != null && !unset.equals(lastTeaspoons)) {
                return lastTeaspoons;

            }
        } else {
            return setTeaspoons;
        }


        return defaultTeaspoons.toString();

    }

    public void time1_click(View v) {

        moveCursorToCurrentRow(v);

        if (cursorHelper.timesToShowDate(cachedCursor) > 1) {
            TextView otherTv = (TextView) ((ViewGroup) v.getParent()).findViewById(R.id.list_time2_value);
            otherTv.setShadowLayer(0, 0, 0, Color.RED);

            TextView tv = (TextView) v;
            tv.setShadowLayer(3, 3, 3, Color.GREEN);

            usesAltFocusMap.put(GetValueIds(v).ItemId, AltFocus.Time1OrValue);
        }

    }

    enum AltFocus {
        /**
         * Default
         */
        Time1OrValue,
        Time2,
        AppliesToTime
    }

    public void list_applies_to_time_click(View v) {
        moveCursorToCurrentRow(v);
        usesAltFocusMap.put(GetValueIds(v).ItemId, AltFocus.AppliesToTime);

        TextView tv = (TextView) v;
        tv.setShadowLayer(3, 3, 3, Color.GREEN);
    }

    public void list_amount_click(View v) {
        moveCursorToCurrentRow(v);
        usesAltFocusMap.remove(GetValueIds(v).ItemId);

        //brittle reliance on view composition here:
        TextView otherTv = (TextView) ((ViewGroup) v.getParent().getParent()).findViewById(R.id.list_applies_to_time);
        otherTv.setShadowLayer(0, 0, 0, Color.RED);

        TextView tv = (TextView) v;
        tv.setShadowLayer(3, 3, 3, Color.GREEN);
    }

    public void time2_click(View v) {

        TextView otherTv = (TextView) ((ViewGroup) v.getParent()).findViewById(R.id.list_time1_value);
        otherTv.setShadowLayer(0, 0, 0, Color.RED);


        TextView tv = (TextView) v;
        tv.setShadowLayer(3, 3, 3, Color.GREEN);

        usesAltFocusMap.put(GetValueIds(v).ItemId, AltFocus.Time2);

    }

    public void add_click(View v) throws ParseException {

        ValueIdentifier ids = GetValueIds(v);
        TextView tv = (TextView) v;

        int addAmount = 0;

        int bigAdd = 5;
        int smallAdd = 1;

        Boolean changeAppliesToTime = usesAltFocusMap.containsKey(ids.ItemId)
                && usesAltFocusMap.get(ids.ItemId) == AltFocus.AppliesToTime;

        if (cursorHelper.timesToShowDate(cachedCursor) > 0 || changeAppliesToTime) {
            bigAdd = 60;
            smallAdd = 5;
        }

        switch (tv.getId()) {
            case R.id.big_minus:
                addAmount = -bigAdd;
                break;
            case R.id.big_plus:
                addAmount = +bigAdd;
                break;
            case R.id.plus:
                addAmount = smallAdd;
                break;
            case R.id.minus:
                addAmount = -smallAdd;
                break;
            default:
                Toast.makeText(getApplicationContext(),
                        "Unexpected source for add_click", Toast.LENGTH_LONG)
                        .show();
        }

        if (addAmount != 0) {

            DoableValue value = getCurrentValue(ids);

            int timesToShow = cursorHelper.timesToShowDate(cachedCursor);

            if (value.getId() == 0) {   //a new value
                if (timesToShow > 0) {

                    int sqlFromTime = cachedCursor.getInt(lastFromTimedColumnIndex);
                    value.setFromTime(doableItemValueTableAdapter.IntToTime(sqlFromTime));

                    if (timesToShow > 1) {
                        int sqlToTime = cachedCursor.getInt(lastToTimeColumnIndex);
                        value.setToTime(doableItemValueTableAdapter.IntToTime(sqlToTime));
                    }

                } else {
                    //its a new value, start with last value used
                    value.setAmount(cachedCursor.getFloat(cachedCursor.getColumnIndex(
                            DoableItemValueTableAdapter.ColLastAmount)));
                }


            } else {
                if (timesToShow > 0) {

                    Boolean usesTime1 = !usesAltFocusMap.containsKey(ids.ItemId)
                            || usesAltFocusMap.get(ids.ItemId) == AltFocus.Time1OrValue;


                    Time timeToChange = usesTime1 ? value.getFromTime() : value.getToTime();

                    if (timeToChange == null) {
                        Date now = new Date();
                        timeToChange = new Time(now.getHours(), 0, 0);
                    }

                    Date newTime = DateHelper.addMinutes(timeToChange, addAmount);

                    Time setToTime = new Time(newTime.getHours(), newTime.getMinutes(), newTime.getSeconds());

                    if (usesTime1)
                        value.setFromTime(setToTime);
                    else
                        value.setToTime(setToTime);

                } else {


                    if (!changeAppliesToTime) {
                        value.setAmount(value.getAmount() + addAmount);
                    } else {
                        Time t = value.getAppliesToTime();
                        //only other current else is applies to time:
                        if (t == null) {
                            t = new Time(value.getDateCreated().getTime());
                        }

                        Date updatedTime = DateHelper.addMinutes(t, addAmount);

                        value.setAppliesToTime(new Time(updatedTime.getTime()));

                    }
                }
            }

            doableItemValueTableAdapter.save(value);

            SetupList2(mDisplayingDate);

        }


    }

    private DoableValue getCurrentValue(ValueIdentifier ids) throws ParseException {
        DoableValue value = doableItemValueTableAdapter
                .get(ids.ValueId);

        value.setAppliesToDate(this.mDisplayingDate);

        SetDefaultsForNewValue(value);

        return value;
    }

    public void nextDayClick
            (View
                     v) {

        doableItemValueTableAdapter.recalcDisplayOrder();
        updateDisplayDate(addDays(mDisplayingDate, 1));
    }


    public void prevDayClick
            (View
                     v) {
        doableItemValueTableAdapter.recalcDisplayOrder();
        updateDisplayDate(addDays(mDisplayingDate, -1));
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


    @Override
    protected void onDestroy() {
        super.onDestroy();    //To change body of overridden methods use File | Settings | File Templates.

        //done with cursors -- close db...

        DatabaseRoot.close();
    }
}
