package com.goldmanalpha.dailydo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.com.goldmanalpha.dailydo.db.DailyDoDatabaseHelper;
import com.com.goldmanalpha.dailydo.db.DatabaseRoot;
import com.com.goldmanalpha.dailydo.db.DoableItemValueTableAdapter;
import com.com.goldmanalpha.dailydo.db.DoableValueCursorHelper;
import com.com.goldmanalpha.dailydo.db.LookupTableAdapter;
import com.goldmanalpha.androidutility.ArrayHelper;
import com.goldmanalpha.androidutility.BackupHelper;
import com.goldmanalpha.androidutility.DateHelper;
import com.goldmanalpha.androidutility.DayOnlyDate;
import com.goldmanalpha.androidutility.EnumHelper;
import com.goldmanalpha.androidutility.FileHelper;
import com.goldmanalpha.androidutility.PickOneList;
import com.goldmanalpha.dailydo.Main.SearchSupport;
import com.goldmanalpha.dailydo.databinding.MainBinding;
import com.goldmanalpha.dailydo.model.DoableValue;
import com.goldmanalpha.dailydo.model.SimpleLookup;
import com.goldmanalpha.dailydo.model.TeaSpoons;
import com.goldmanalpha.dailydo.model.TeaspoonHelper;
import com.goldmanalpha.dailydo.model.UnitType;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.nononsenseapps.filepicker.Utils;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

import filteredcursor.android.FilteredCursorFactory;

import static com.goldmanalpha.androidutility.DateHelper.gmtToLocalTime;
import static com.goldmanalpha.androidutility.DateHelper.short24TimeFormat;
import static com.goldmanalpha.androidutility.DateHelper.shortMonthDateFormat;
import static com.goldmanalpha.androidutility.DateHelper.simpleDateFormatGmt;

public class MainActivity extends ActivityBase {

    DoableValueCursorHelper cursorHelper;
    private TextView mDateDisplay;
    Date mDisplayingDate;
    DoableItemValueTableAdapter doableItemValueTableAdapter;

    HashMap<Integer, AltFocus> usesAltFocusMap = new HashMap<Integer, AltFocus>();
    public static String ExtraValueDateGetTimeLong = "dateToShow";
    public static String ExtraValueCategoryId = "categoryId";

    public static String SelectedCategoryIdPrefKey = "selectedCategoryId";

    private boolean isFirstInstance;
    private static boolean instanceCreated;
    private boolean outOfRangeDateOK;

    public MainActivity() {
        this.isFirstInstance = !instanceCreated;
        instanceCreated = true;
    }

    static int instanceCount;

    @Override
    protected String RightTitle() {

        String suffix = Integer.toString(instanceCount);
        instanceCount++;
        return suffix;
    }

    private MainBinding binding;
    private boolean showOldItemsWithoutValues = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = MainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        mainList = binding.mainList;
        registerForContextMenu(mainList);

        mDateDisplay = findViewById(R.id.dateDisplay);

        Intent intent = getIntent();
        Long dateLong = intent.getLongExtra(ExtraValueDateGetTimeLong, new DayOnlyDate().getTime());
        updateDisplayDate(new Date(dateLong));

        selectedCategoryId = Preferences().getInt(SelectedCategoryIdPrefKey, SimpleLookup.ALL_ID);
        selectedCategoryId = intent.getIntExtra(ExtraValueCategoryId, selectedCategoryId);

        setupCategories();

        binding.showAllItems.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showOldItemsWithoutValues = isChecked;
                SetupList2(mDisplayingDate);
            }
        });

        setupSearchListener();
    }

    private void setupSearchListener() {
        binding.searchItemEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchSupport.setSearchString(String.valueOf(s));
                MainActivity.this.SetupList2(mDisplayingDate);
            }
        });
    }

    SharedPreferences Preferences() {
        return getSharedPreferences(getApplication().getPackageName(), MODE_PRIVATE);
    }

    boolean showPrivate = true;

    static final class MenuItems {
        public static final int AddItem = 0;
        public static final int Backup = 1;
        public static final int Quit = 2;

        public static final int DeleteDb = 4;

        public static final int PublicPrivateSwitch = 5;

        public static final int AddSeparateValue = 6;

        public static final int BackupFolder = 7;

        public static final int DeleteItem = 8;

        public static final int SingleItemHistory = 9;
        public static final int AllItemHistory = 10;
        public static final int ThisCategoryItemHistory = 11;
        public static final int AllItemHistoryHighlightItem = 12;
        public static final int ThisCategoryItemHistoryHighlightItem = 13;

        public static final int RestoreFromBackup = 999912;
    }

    public void onShowOptionsMenu(View v) {
        this.openOptionsMenu();
    }

    MenuItem PublicPrivateMenuItem;

    private SpannableString asSS(String s) {
        SpannableString ss = new SpannableString(s);
        ss.setSpan(new ForegroundColorSpan(Color.YELLOW), 0, s.length(), 0);
        return ss;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Preferences

        //group, item, order, title_bar

        PublicPrivateMenuItem =
                menu.add(0, MenuItems.PublicPrivateSwitch, 0, asSS("Pub Only"));

        menu.add(0, MenuItems.Quit, 0, asSS("Quit"));

        menu.add(1, MenuItems.AllItemHistory, 0, asSS("Hstry"));

        menu.add(1, MenuItems.ThisCategoryItemHistory, 0, asSS("Cat Hstry"));

        menu.add(1, MenuItems.Backup, 0, asSS("Backup"));

        menu.add(0, MenuItems.AddItem, 0, asSS("Add Item"));

        menu.add(1, MenuItems.BackupFolder, 0, asSS("Backup Folder"));

        menu.add(1, MenuItems.RestoreFromBackup, 0, asSS("Restore DB"));

        //menu.add(1, MenuItems.DeleteDb, 0, "DELETE DB");

        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId() == binding.mainList.getId()) {

            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

            cachedCursor.moveToPosition(info.position);

            final String name = cachedCursor.getString(
                    cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColItemName)
            );

            menu.setHeaderTitle(name);

            menu.add(Menu.NONE, MenuItems.SingleItemHistory, 0, "Item History");

            menu.add(Menu.NONE, MenuItems.ThisCategoryItemHistoryHighlightItem, 0, "Cat History");
            menu.add(Menu.NONE, MenuItems.AllItemHistoryHighlightItem, 0, "All History");

            menu.add(Menu.NONE, MenuItems.AddSeparateValue, 0, "Add Separate Value");
            menu.add(Menu.NONE, MenuItems.DeleteItem, 0, "Delete Value");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        final ValueIdentifier ids = getValueIdsForCurrentCursorPosition();
        final String name = cachedCursor.getString(
                cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColItemName)
        );
        boolean handled = false;
        SeriousConfirmationDialog dlg = null;

        switch (item.getItemId()) {
            case MenuItems.AddSeparateValue:

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

                if (description != null && (description).trim().length() != 0) {
                    Toast.makeText(this, "Can't delete value with description -- delete description first.",
                            Toast.LENGTH_LONG).show();
                } else {
                    dlg = new SeriousConfirmationDialog(this,
                            name, "Delete item?",
                            new DialogInterface.OnClickListener() {
                                @Override
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

            case MenuItems.SingleItemHistory:

                Intent intent = new Intent(this, ItemHistoryActivity.class);

                intent.putExtra(ItemHistoryActivity.ExtraValueItemName, name);
                intent.putExtra(ItemHistoryActivity.ExtraValueItemId, ids.ItemId);
                intent.putExtra(ItemHistoryActivity.ExtraValueIsMultiMode, false);

                startActivity(intent);

                break;

            case MenuItems.AllItemHistoryHighlightItem:
                intent = new Intent(this, ItemHistoryActivity.class);
                intent.putExtra(ItemHistoryActivity.ExtraValueIsMultiMode, true);

                intent.putExtra(ItemHistoryActivity.ExtraHighlightItemId, ids.ItemId);

                startActivity(intent);
                break;

            case MenuItems.ThisCategoryItemHistoryHighlightItem:
                intent = new Intent(this, ItemHistoryActivity.class);
                intent.putExtra(ItemHistoryActivity.ExtraValueIsMultiMode, true);
                intent.putExtra(ItemHistoryActivity.ExtraValueLimitToCategoryId, selectedCategoryId);

                intent.putExtra(ItemHistoryActivity.ExtraHighlightItemId, ids.ItemId);

                startActivity(intent);
                break;
        }

        return handled;
    }

    public void main_date_click(View v) {
        updateDisplayDate(new DayOnlyDate());
    }

    public void list_description_click(View v) {
        ValueIdentifier ids = this.GetValueIds(v);

        if (ids.ValueId == 0) {
            Toast.makeText(this, "Need to set value before description", Toast.LENGTH_SHORT)
                    .show();
        } else {
            Intent intent = new Intent(this, EditDescriptionActivity.class);

            intent.putExtra(EditDescriptionActivity.ExtraValueId, ids.ValueId);
            intent.putExtra(EditDescriptionActivity.ExtraValueOutOfRangeDateOK, outOfRangeDateOK);

            startActivity(intent);
        }
    }

    void shareFile(String filePath) {

        Uri uri = FileProvider.getUriForFile(this,
                this.getApplicationContext().getPackageName() + ".provider2",
                new File(filePath));

        Intent intent = ShareCompat.IntentBuilder.from(this)
                .getIntent()
                .setAction(Intent.ACTION_SEND) //Change if needed
                .putExtra(Intent.EXTRA_STREAM, uri)
                .setDataAndType(uri, "application/octet-stream")
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .putExtra(Intent.EXTRA_SUBJECT, "Upload File")
                .putExtra(Intent.EXTRA_TEXT, "Upload File");

        startActivity(Intent.createChooser(intent, "Upload File"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String path = "data/data/" + this.getPackageName() + "/databases/";

        Intent intent;

        switch (item.getItemId()) {
            case MenuItems.PublicPrivateSwitch:
                this.showPrivate = !this.showPrivate;
                PublicPrivateMenuItem.setTitle(this.showPrivate ? "Pub Only" : "Show Private");
                this.SetupList2(mDisplayingDate);
                break;
            case MenuItems.DeleteDb:
                DeleteConfirmationDialog dlg = new DeleteConfirmationDialog(this,
                        new DialogInterface.OnClickListener() {
                            @Override
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

            case MenuItems.AllItemHistory:
                intent = new Intent(this, ItemHistoryActivity.class);
                intent.putExtra(ItemHistoryActivity.ExtraValueIsMultiMode, true);
                startActivity(intent);
                break;

            case MenuItems.ThisCategoryItemHistory:
                intent = new Intent(this, ItemHistoryActivity.class);
                intent.putExtra(ItemHistoryActivity.ExtraValueIsMultiMode, true);
                intent.putExtra(ItemHistoryActivity.ExtraValueLimitToCategoryId, selectedCategoryId);
                startActivity(intent);
                break;

            case (MenuItems.RestoreFromBackup):

                BackupHelper helper = new BackupHelper();

                SharedPreferences preferences1 =
                        getSharedPreferences(getApplication().getPackageName(), MODE_PRIVATE);

                String targetPath1 = preferences1.getString("BackupFolder", path);

                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    //ask for permission
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                    }
                }

                String[] files = helper.BackupFiles(targetPath1);

                PickRestoreDB(files);

                break;
            case (MenuItems.Backup):
                Toast.makeText(this, "Backing up", Toast.LENGTH_LONG).show();

                BackupService backupService = new BackupService();

                SharedPreferences preferences =
                        getSharedPreferences(getApplication().getPackageName(), MODE_PRIVATE);

                String targetPath = preferences.getString("BackupFolder", path);

                String backupFileName = backupService.doBackup("", getPackageName(), targetPath);

                Toast.makeText(this, "DailyDo DB Backed Up. Recommended to share it with DroopBox", Toast.LENGTH_SHORT).show();

                this.shareFile(backupFileName);

                break;

            case (MenuItems.BackupFolder):

                try {

                    Intent selectDirectoyIntent = new Intent(this, FilePickerActivity.class);
                    selectDirectoyIntent.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                    selectDirectoyIntent.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);
                    startActivityForResult(selectDirectoyIntent, IntentRequestCodes.BackupFolder);
                } catch (Exception e) {
                    Log.e(this.LogTag, "exception", e);
                    e.printStackTrace();

                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                }
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

    private void setupCategories() {
        Spinner categorySelector = findViewById(R.id.categorySpinner);

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
                this, R.layout.spinner_row,
                categories);
        adapter.setDropDownViewResource(R.layout.short_spinner_dropdown_item);

        categorySelector.setAdapter(adapter);

        SimpleLookup[] lookupArray = new SimpleLookup[categories.size()];

        int selectedPosition = ArrayHelper.IndexOfP(
                categories.toArray(lookupArray), new Predicate<SimpleLookup>() {
                    @Override
                    public boolean test(SimpleLookup simpleLookup) {
                        return simpleLookup.getId() == MainActivity.this.selectedCategoryId;  //To change body of implemented methods use File | Settings | File Templates.
                    }
                });

        //todo: this reset to previous state doesn't exactly work
        //the display text in the spinner is wrong.
        categorySelector.setSelection(selectedPosition, true);

        categorySelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                selectedCategoryId =
                        ((SimpleLookup) parentView.getSelectedItem()).getId();

                searchSupport.setCategory(selectedCategoryId);
                binding.allCategoryOptions.setVisibility(searchSupport.isSearchMode() ? View.VISIBLE : View.GONE);

                Preferences().edit().putInt(SelectedCategoryIdPrefKey, selectedCategoryId).commit();

                MainActivity.this.SetupList2(mDisplayingDate);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }

    SearchSupport searchSupport = new SearchSupport();

    @Override
    protected void onPause() {
        DatabaseRoot.close();
        super.onPause();
    }

    @Override
    protected void onResume() {

        setupCategories();

        SetupList2(mDisplayingDate);

        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
    }

    private Cursor filterCursor(Cursor cursorToFilter) {
        return FilteredCursorFactory.createUsingSelector(cursorToFilter, new FilteredCursorFactory.Selector() {

            @Override
            public boolean select(Cursor cursor) {

                String itemName = cursor.getString(nameColumnIndex);
                if (!searchSupport.isMatch(itemName)) {
                    return false;
                }

                if (!showOldItemsWithoutValues
                        && isOldDate()
                ) {

                    int[] fieldIndices =
                            {
                                    amountColumnIndex, fromTimeColumnIndex, toTimeColumnIndex
                            };

                    for (int idx : fieldIndices) {
                        if (cursor.getInt(idx) != 0) {
                            return true;
                        }
                    }

                    return false;
                }

                return true;
            }
        });
    }

    boolean setupDate = false;
    SimpleCursorAdapter listCursorAdapter;

    Date lastSetupList2Date;

    private boolean isOldDate() {
        boolean isOutOfRange = MainActivity.this.getLastWindowState().equals(WindowState.OUT_OF_RANGE);
        boolean isHistory = new Date().after(mDisplayingDate);
        return isHistory && isOutOfRange;
    }

    private void setDateOptions(Date date) {
        setWindowState(date);

        int visibility = isOldDate() ?
                LinearLayout.VISIBLE : LinearLayout.GONE;
        binding.oldDateOptions.setVisibility(visibility);
    }

    public void SetupList2(Date date) {

        this.outOfRangeDateOK = this.outOfRangeDateOK && date.equals(lastSetupList2Date);
        lastSetupList2Date = date;

        setDateOptions(date);

        cachedCursor.close();
        cachedCursor = filterCursor(doableItemValueTableAdapter.getItems(gmtToLocalTime(date), showPrivate, selectedCategoryId));
        listCursorAdapter.changeCursor(cachedCursor);
    }

    ListView mainList;
    Cursor cachedCursor;
    int nameColumnIndex;
    int valueIdColumnIndex;
    int itemIdColumnIndex;

    int fromTimeColumnIndex;
    int toTimeColumnIndex;
    int lastFromTimedColumnIndex;
    int lastToTimeColumnIndex;
    int descriptionColumnIndex;
    int amountColumnIndex;

    int teaspoonColIdx;
    int lastTeaspoonColIdx;

    int potencyColIdx;
    int lastPotencyColIdx;

    private void SetupList(Date date) {

        if (setupDate) {
            SetupList2(date);
            return;
        }

        setDateOptions(date);
        setupDate = true;

        doableItemValueTableAdapter = new DoableItemValueTableAdapter();
        cachedCursor = filterCursor(doableItemValueTableAdapter.getItems(gmtToLocalTime(date), showPrivate, SimpleLookup.ALL_ID));

        nameColumnIndex = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColItemName);
        valueIdColumnIndex = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColId);
        itemIdColumnIndex = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColItemId);
        descriptionColumnIndex = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColDescription);
        final int nowColumnIndex = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColPlaceHolder1);
        final int unitTypeColumnIndex = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColUnitType);

//        startManagingCursor(cachedCursor);
        cursorHelper = new DoableValueCursorHelper(cachedCursor);

        String[] from = new String[]{
                DoableItemValueTableAdapter.ColItemName,
                DoableItemValueTableAdapter.ColUnitType,
                DoableItemValueTableAdapter.ColAmount,
                DoableItemValueTableAdapter.ColTeaspoons,
                DoableItemValueTableAdapter.ColPotency,
                DoableItemValueTableAdapter.ColLastAppliesToDate,
                DoableItemValueTableAdapter.ColLastAmount,
                DoableItemValueTableAdapter.ColLastTeaspoons,
                DoableItemValueTableAdapter.ColLastPotency,
                DoableItemValueTableAdapter.ColFromTime,
                DoableItemValueTableAdapter.ColLastFromTime,
                DoableItemValueTableAdapter.ColToTime,
                DoableItemValueTableAdapter.ColLastToTime,
                DoableItemValueTableAdapter.ColDescription,
                DoableItemValueTableAdapter.ColPlaceHolder1, //set to now
                DoableItemValueTableAdapter.ColAppliesToTime
        };

        int[] to = new int[]{R.id.list_name, R.id.list_unit_type,
                R.id.amount, R.id.list_teaspoons, R.id.list_potency,
                R.id.list_lastDate, R.id.list_lastAmount,
                R.id.list_lastTeaspoons, R.id.list_lastPotency, R.id.list_time1_value,
                R.id.list_lastTime1, R.id.list_time2_value, R.id.list_lastTime2,
                R.id.list_description, R.id.list_set_now,
                R.id.list_applies_to_time
        };

        teaspoonColIdx = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColTeaspoons);
        lastTeaspoonColIdx = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColLastTeaspoons);

        potencyColIdx = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColPotency);
        lastPotencyColIdx = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColLastPotency);

        final int lastAppliesToDateColIdx = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColLastAppliesToDate);
        final int lastTeaspoonsColIdx = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColLastTeaspoons);
        final int appliesToTimeColIdx = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColAppliesToTime);
        final int showAppliesToTimeCountColIdx = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColShowAppliesToTimeCount);
        final int createdDateColIdx = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColDateCreated);
        final String sTimeSpan = UnitType.timeSpan.toString();

        lastFromTimedColumnIndex = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColLastFromTime);
        fromTimeColumnIndex = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColFromTime);
        toTimeColumnIndex = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColToTime);
        lastToTimeColumnIndex = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColLastToTime);
        amountColumnIndex = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColAmount);

        listCursorAdapter = new SimpleCursorAdapter(mainList.getContext(),
                R.layout.main_list_item, cachedCursor, from, to);

        mainList.setAdapter(listCursorAdapter);

        listCursorAdapter.setViewBinder(
                new SimpleCursorAdapter.ViewBinder() {
                    @Override
                    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

                        boolean returnValue = false;

                        if (columnIndex == unitTypeColumnIndex) {
                            TextView tv = (TextView) view;

                            String value = cursor.getString(columnIndex);

                            if (value.equals(sTimeSpan)) {
                                value = "ts";
                            }

                            tv.setText(value);

                            return true;
                        }

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
                            }

                            if (showAppliesToTime && cursor.getInt(valueIdColumnIndex) != 0) {
                                Time t = new Time(0, 0, 0);
                                if (cursor.isNull(appliesToTimeColIdx)) {

                                    if (!isActive)
                                        tv.setShadowLayer(6, 0, 0, Color.YELLOW);

                                    try {
                                        Date createdDate = DateHelper.TimeStampToDate(cursor.getString(createdDateColIdx), simpleDateFormatGmt);
                                        t = DateHelper.getLocalTime(createdDate);
                                    } catch (ParseException e) {
                                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                    }
                                } else {
                                    t = DateHelper.IntToTime(cursor.getInt(appliesToTimeColIdx));
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

                                TextView dashView = ((ViewGroup) tv.getParent()).findViewById(dashId);

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
                                Time t = DateHelper
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
                                            + DateHelper.totalHours(doableItemValueTableAdapter, startTimeAsInt, timeAsInt)
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

                            if (cursorHelper.isTeaspoons(cursor)) {
                                tv.setText(TeaspoonHelper.shortName(getTeaspoonsForCursorPosition(cursor)));
                            } else {
                                tv.setText("");
                            }
                            returnValue = true;
                        }

                        if (columnIndex == lastTeaspoonsColIdx) {
                            TextView tv = ((TextView) view);

                            if (!cursorHelper.isTeaspoons(cursor)) {
                                tv.setText("");
                                returnValue = true;
                            }
                        }

                        if (columnIndex == potencyColIdx || columnIndex == lastPotencyColIdx) {
                            TextView tv = ((TextView) view);

                            if (!cursorHelper.isDrops(cursor)) {
                                tv.setText("");
                                returnValue = true;
                            } else {
                                if (columnIndex == potencyColIdx && cursor.getInt(valueIdColumnIndex) == 0) {
                                    tv.setText("p" + cursor.getInt(lastPotencyColIdx));
                                } else {
                                    tv.setText("p" + cursor.getInt(columnIndex));
                                }

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
            @Override
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
            Date d = DateHelper.simpleDateFormatLocal.parse(lastAppliesToDate);

            TextView tv = view;

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

        if (ids.ValueId == 0) {
            //if there's no value, just set to now -- no conf needed:
            doableItemValueTableAdapter.save(value);
            SetupList2(mDisplayingDate);
        } else {

            SeriousConfirmationDialog dlg = new SeriousConfirmationDialog(this,
                    value.getItem().getName(), "Set " + whichTimeToSet + " to current time?",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                            if (id == DialogInterface.BUTTON_POSITIVE) {

                                doableItemValueTableAdapter.save(value);
                                SetupList2(mDisplayingDate);
                            }
                        }
                    });

            dlg.show();
        }
    }

    public void unit_type_click(View v) {
        ValueIdentifier ids = GetValueIds(v);

        Toast.makeText(getApplicationContext(),
                ids.toString() + " " + ((TextView) v).getText(),
                Toast.LENGTH_SHORT).show();
    }

    DoableValue potencyClickValue;

    public void potency_click(View v) {
        //setup the value, because the intent will close the cursor
        if ((potencyClickValue = GetValue(v)) == null) return;

        if (potencyClickValue.getId() == 0) {
            Toast.makeText(this, "Set a value before changing potency", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(this, PickOneList.class);

        intent.putExtra(PickOneList.Title, "Pick Exp Value Vs. Std Potency");

        intent.putExtra(PickOneList.SelectedItem, this.potencyClickValue.getPotency().toString());

        //values from -1 to neg 30 should be sufficient:

        final int start = 3;
        final int end = -30;

        String[] potencies = new String[Math.abs(end) + start + 1];

        for (int i = start; i >= end; i--) {
            potencies[Math.abs(i - start)] = String.valueOf(i);
        }

        intent.putExtra(PickOneList.Choices, potencies);

        startActivityForResult(intent, IntentRequestCodes.PotencySelection);
    }

    DoableValue teaspoonsClickValue;

    public void teaspoons_click(View v) {

        //setup the value, because the intent will close the cursor
        if ((teaspoonsClickValue = GetValue(v)) == null) return;

//in case it was an unset/new value:
        SetDefaultsForNewValue(teaspoonsClickValue);

        Intent intent = new Intent(this, PickOneList.class);

        intent.putExtra(PickOneList.Title, "Pick Unit Teaspoon Size");

        intent.putExtra(PickOneList.SelectedItem, ((TextView) v).getText());

        intent.putExtra(PickOneList.Choices,
                EnumHelper.EnumNameToStringArray(TeaSpoons.values(), 1));

        startActivityForResult(intent, IntentRequestCodes.TeaspoonSelection);
    }

    private DoableValue GetValue(View v) {
        DoableValue retVal = null;
        try {
            retVal = doableItemValueTableAdapter
                    .get(this.GetValueIds(v).ValueId);
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

            Toast.makeText(this, "Error loading value: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return retVal;
    }

    private void PickRestoreDB(String[] choices) {

        Intent intent = new Intent(this, PickOneList.class);

        intent.putExtra(PickOneList.Title, "Pick DB to Restore");

        intent.putExtra(PickOneList.Choices, choices);

        startActivityForResult(intent, IntentRequestCodes.RestoreDBSelection);
    }

    class IntentRequestCodes {
        public static final int TeaspoonSelection = 1;

        public static final int BackupFolder = 2;

        public static final int NoOp = 0;

        public static final int RestoreDBSelection = 3;

        public static final int PotencySelection = 4;
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

                    List<Uri> files = Utils.getSelectedFilesFromResult(data);
                    for (Uri uri : files) {
                        File file = Utils.getFileForUri(uri);
                        // Do something with the result...
                        String path = file.getAbsolutePath();

                        Preferences().edit().putString("BackupFolder", path).commit();

                        Toast.makeText(this, "Saved folder: " + path, Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case IntentRequestCodes.RestoreDBSelection:

                if (resultCode == RESULT_OK) {

                    final String restoreFile = data.getStringExtra(PickOneList.SelectedItem);

                    SeriousConfirmationDialog dlg = new SeriousConfirmationDialog(this,
                            "Restore DB?!", "Restore db from: " + restoreFile,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {

                                    if (id == DialogInterface.BUTTON_POSITIVE) {
                                        //backup?!

                                        String localPath = "data/data/" + getPackageName() + "/databases/";

                                        //delete
                                        File f = new File(
                                                "data/data/" + getPackageName() + "/databases/"
                                                        + DailyDoDatabaseHelper.DATABASE_NAME);

                                        String dbFileFullPath = null;
                                        boolean canContinue = false;

                                        try {
                                            dbFileFullPath = f.getCanonicalPath();
                                            canContinue = true;
                                        } catch (IOException e) {
                                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

                                            Toast.makeText(MainActivity.this, "getCanonicalPath Err: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }

                                        if (canContinue) {

                                            DatabaseRoot.close();

                                            f.delete();

                                            String backupDir = Preferences().getString("BackupFolder", localPath);

                                            FileHelper helper = new FileHelper();

                                            //todo: show backup issues in the app
                                            try {
                                                helper.CopyFile(FileHelper.EndSlash(backupDir) + restoreFile, dbFileFullPath);

                                                Toast.makeText(getApplication(), "Success / Restarting", Toast.LENGTH_LONG);
                                            } catch (IOException e) {
                                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

                                                Toast.makeText(MainActivity.this, "Restore Copy Fail: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                        finish();
                                    }
                                }
                            });

                    dlg.show();
                }
                break;

            case IntentRequestCodes.PotencySelection:
                if (resultCode == RESULT_OK) {

                    String potencyString = data.getStringExtra(PickOneList.SelectedItem);

                    if (!potencyClickValue.getPotency().toString().equals(potencyString)) {

                        potencyClickValue.setPotency(Integer.parseInt(potencyString));
                        doableItemValueTableAdapter.save(potencyClickValue);
                    }

                    SetupList(new DayOnlyDate(this.mDisplayingDate));
                }
                break;
        }
    }

    //for a new doable value, this will set defaults enough to save
    //need to have called GetIds prior to this call
    void SetDefaultsForNewValue(DoableValue value) {

        if (value.getId() != 0)
            return;

        value.setAppliesToDate(gmtToLocalTime(mDisplayingDate));

        value.setDoableItemId(GetValueIds(null).ItemId);

        int timesToShow = cursorHelper.timesToShowDate(cachedCursor);

        if (value.getId() == 0) {
            if (timesToShow > 0) {

                int sqlFromTime = cachedCursor.getInt(lastFromTimedColumnIndex);
                value.setFromTime(DateHelper.IntToTime(sqlFromTime));

                if (timesToShow > 1) {
                    int sqlToTime = cachedCursor.getInt(lastToTimeColumnIndex);
                    value.setToTime(DateHelper.IntToTime(sqlToTime));
                }
            }

            value.setPotency(cachedCursor.getInt(lastPotencyColIdx));
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
            TextView otherTv = ((ViewGroup) v.getParent()).findViewById(R.id.list_time2_value);
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
        TextView otherTv = ((ViewGroup) v.getParent().getParent()).findViewById(R.id.list_applies_to_time);
        otherTv.setShadowLayer(0, 0, 0, Color.RED);

        TextView tv = (TextView) v;
        tv.setShadowLayer(3, 3, 3, Color.GREEN);
    }

    public void time2_click(View v) {

        TextView otherTv = ((ViewGroup) v.getParent()).findViewById(R.id.list_time1_value);
        otherTv.setShadowLayer(0, 0, 0, Color.RED);

        TextView tv = (TextView) v;
        tv.setShadowLayer(3, 3, 3, Color.GREEN);

        usesAltFocusMap.put(GetValueIds(v).ItemId, AltFocus.Time2);
    }

    public void clearSearchClick(View v) {
        binding.searchItemEditor.setText(SearchSupport.BLANK);
    }

    public void add_click(View v) throws ParseException {

        Toast.makeText(getApplicationContext(),
                "ValueSet", Toast.LENGTH_LONG)
                .show();

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
                    value.setFromTime(DateHelper.IntToTime(sqlFromTime));

                    if (timesToShow > 1) {
                        int sqlToTime = cachedCursor.getInt(lastToTimeColumnIndex);
                        value.setToTime(DateHelper.IntToTime(sqlToTime));
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
                            t = DateHelper.getLocalTime(value.getDateCreated());
                        }

                        Date updatedTime = DateHelper.addMinutes(t, addAmount);

                        value.setAppliesToTime(new Time(updatedTime.getTime()));
                    }
                }
            }

            if (this.getLastWindowState().equals(WindowState.OUT_OF_RANGE) && !outOfRangeDateOK) {
                final DoableValue value2 = value;
                SeriousConfirmationDialog dlg = new SeriousConfirmationDialog(this,
                        value.getItem().getName(), "Change value on date: " + mDateDisplay.getText(),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                                if (id == DialogInterface.BUTTON_POSITIVE) {
                                    doableItemValueTableAdapter.save(value2);
                                    outOfRangeDateOK = true;
                                    SetupList2(mDisplayingDate);
                                }
                            }
                        });

                dlg.show();
            } else {
                doableItemValueTableAdapter.save(value);
                SetupList2(mDisplayingDate);
            }
        }
    }

    private DoableValue getCurrentValue(ValueIdentifier ids) throws ParseException {
        DoableValue value = doableItemValueTableAdapter
                .get(ids.ValueId);

        value.setAppliesToDate(DateHelper.gmtToLocalTime(this.mDisplayingDate));

        SetDefaultsForNewValue(value);

        return value;
    }

    public void nextDayClick
            (View
                     v) {

        doableItemValueTableAdapter.recalcDisplayOrder();
        updateDisplayDate(DateHelper.addDays(mDisplayingDate, 1));
    }

    public void prevDayClick
            (View
                     v) {
        doableItemValueTableAdapter.recalcDisplayOrder();
        updateDisplayDate(DateHelper.addDays(mDisplayingDate, -1));
    }

    private void updateDisplayDate(Date date) {

        if (mDisplayingDate != null && !mDisplayingDate.equals(date)) {
            usesAltFocusMap.clear();
        }

        mDisplayingDate = date;
        mDateDisplay.setText(DateHelper.LongDateString(date));

        SetupList(new DayOnlyDate(date));
    }

    @Override
    public void onBackPressed() {
        if (this.isFirstInstance) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Closing App")
                    .setMessage("Exit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        instanceCount--;

        binding = null;

        if (this.isFirstInstance)
            DatabaseRoot.close();
    }
}
