package com.goldmanalpha.dailydo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.com.goldmanalpha.dailydo.db.DoableItemTableAdapter;
import com.com.goldmanalpha.dailydo.db.DoableItemValueTableAdapter;
import com.com.goldmanalpha.dailydo.db.DoableValueCursorHelper;
import com.com.goldmanalpha.dailydo.db.ItemSortingTableAdapter;
import com.goldmanalpha.androidutility.DateHelper;
import com.goldmanalpha.dailydo.model.DoableItem;
import com.goldmanalpha.dailydo.model.SimpleLookup;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemHistoryActivity extends ActivityBase {

    ListView mainList;
    DoableItemValueTableAdapter doableItemValueTableAdapter;
    Cursor cachedCursor;
    int itemId;
    SharedPreferences preferences;
    Boolean multiMode;
    int limitToCategoryId;
    List<Integer> highlightItemIds;
    int sortingItemId;

    DoableValueCursorHelper cursorHelper;
    public static String ExtraValueItemId = "itemId";
    public static String ExtraValueItemName = "itemName";
    public static String ExtraValueIsMultiMode = "Mode";
    public static String ExtraValueLimitToCategoryId = "LimitToCategory";
    public static String ExtraHighlightItemId = "ExtraHighlightItemId";

    private static final SimpleDateFormat short24TimeFormat = new SimpleDateFormat("HH:mm");
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE. M/d/yy");
    private static final String ShowLongDescriptionKey = "SingleItemHistoryShowLongDescription";

    private static ItemSortingTableAdapter sortingTableAdapter = new ItemSortingTableAdapter();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sortingTableAdapter.clear(this.thisInstanceNumber);

        preferences = getSharedPreferences(getApplication().getPackageName(), MODE_PRIVATE);
        this.showLongDescription = preferences.getBoolean(ShowLongDescriptionKey, true);

        Intent intent = getIntent();

        multiMode = intent.getBooleanExtra(ExtraValueIsMultiMode, false);
        limitToCategoryId = intent.getIntExtra(ExtraValueLimitToCategoryId, SimpleLookup.UNSET_ID);

        highlightItemIds = new ArrayList<Integer>();
        int highlightItemId = intent.getIntExtra(ExtraHighlightItemId, 0);
        highlightItemIds.add(highlightItemId);

        itemId = intent.getIntExtra(ExtraValueItemId, 0);
        String itemName = intent.getStringExtra(ExtraValueItemName);

        sortingItemId = itemId;
        if (itemId == 0) {
            sortingItemId = highlightItemId;
        }

        setContentView(R.layout.single_history);

        TextView nameView = findViewById(R.id.single_history_name);
        nameView.setText(this.multiMode ? "History" : itemName);

        FrameLayout multiNav = findViewById(R.id.history_multi_navigation_ui);

        if (!multiMode) {
            multiNav.setVisibility(View.GONE);
        } else {
            multiNav.setVisibility(View.VISIBLE);
        }

        mainList = findViewById(R.id.single_history_list);
        SetupList(itemId, limitToCategoryId);

        mainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //To change body of implemented methods use File | Settings | File Templates.

                Intent intent = new Intent(ItemHistoryActivity.this, MainActivity.class);

                SetCursor(view, cachedCursor);

                try {
                    Date appliesToDate = doableItemValueTableAdapter.getAppliesToDate(cachedCursor);
                    intent.putExtra(MainActivity.ExtraValueDateGetTimeLong, appliesToDate.getTime());

                    int itemIdColIdx = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColItemId);

                    DoableItem item = new DoableItemTableAdapter().get(cachedCursor.getInt(itemIdColIdx));

                    intent.putExtra(MainActivity.ExtraValueCategoryId, item.getCategoryId());

                    startActivity(intent);
                } catch (ParseException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    Toast.makeText(ItemHistoryActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    static int instanceCount;
    private int thisInstanceNumber;
    Boolean incrementedInstanceCount = false;

    @Override
    protected String RightTitle() {

        String suffix = Integer.toString(instanceCount);
        if (!incrementedInstanceCount) {
            instanceCount++;
            thisInstanceNumber = instanceCount;
        }

        return suffix;
    }

    EditText highlightText;

    String highlightText() {
        highlightText = highlightText == null ?
                (EditText) findViewById(R.id.highlight_text) : highlightText;

        String text = highlightText.getText().toString();
        text = text == null ? "" : text;
        return text;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        android.view.MenuItem item = menu.add(0, MenuItems.ToggleLongDescription, 0, (showLongDescription ? "Short" : "Long") + " Description");
        item.setCheckable(true);
        item.setChecked(showLongDescription);

        if (sortingItemId != 0) {
            menu.add(0, MenuItems.SortValueAscending, 0, "Sort Val Asc");
            menu.add(0, MenuItems.SortValueDescending, 0, "Sort Val Desc");
            //menu.add(0, MenuItems.SortDateDescending, 0, "Sort Date Desc");
            //menu.add(0, MenuItems.SortDateAscending, 0, "Sort Date Asc");
        }

        return true;
    }

    static final class MenuItems {
        public static final int ToggleLongDescription = 0;
        public static final int SortValueAscending = 1;
        public static final int SortValueDescending = 2;
        public static final int SortDateAscending = 3;
        public static final int SortDateDescending = 4;
    }

    boolean showLongDescription;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MenuItems.ToggleLongDescription:
                showLongDescription = !item.isChecked();

                item.setTitle((showLongDescription ? "Short" : "Long") + " Description");
                item.setChecked(showLongDescription);
                preferences.edit().putBoolean(ShowLongDescriptionKey, showLongDescription).commit();

                SetupList(this.itemId, limitToCategoryId);

                break;
            case MenuItems.SortDateAscending:
                break;
            case MenuItems.SortDateDescending:
                break;
            case MenuItems.SortValueAscending:
                sortingTableAdapter.setupValueSort(sortingItemId, this.thisInstanceNumber, true);
                SetupList(this.itemId, limitToCategoryId);
                break;
            case MenuItems.SortValueDescending:
                sortingTableAdapter.setupValueSort(sortingItemId, this.thisInstanceNumber, false);
                SetupList(this.itemId, limitToCategoryId);
                break;
        }

        return true;
    }

    Date lastDate;
    SimpleCursorAdapter listCursorAdapter;

    private void SetupList(Integer itemId, int limitToCategoryId) {

        doableItemValueTableAdapter = new DoableItemValueTableAdapter();
        cachedCursor = doableItemValueTableAdapter.getItems(thisInstanceNumber, itemId, limitToCategoryId);
        cursorHelper = new DoableValueCursorHelper(cachedCursor);

//        startManagingCursor(cachedCursor);

        String[] from = new String[]{
                DoableItemValueTableAdapter.ColAmount,
                DoableItemValueTableAdapter.ColTeaspoons,
                DoableItemValueTableAdapter.ColPotency,
                DoableItemValueTableAdapter.ColDescription,
                DoableItemValueTableAdapter.ColAppliesToDate,
                DoableItemValueTableAdapter.ColAppliesToDate,
                DoableItemValueTableAdapter.ColAppliesToTime,
                DoableItemValueTableAdapter.ColFromTime,
                DoableItemValueTableAdapter.ColItemName
        };

        int[] to = new int[]{
                R.id.single_history_item_value,
                R.id.single_history_item_tsp,
                R.id.single_history_item_potency,
                R.id.single_history_item_description,
                R.id.single_history_item_date,
                R.id.item_group_header,
                R.id.single_history_item_applies_to_time,
                R.id.single_history_item_time_value,
                R.id.single_history_item_name
        };

        final int teaspoonColIdx = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColTeaspoons);
        final int potencyColIdx = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColPotency);

        final int appliesToDateColIdx = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColAppliesToDate);
        final int appliesToTimeColIdx = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColAppliesToTime);
        final int showAppliesToTimeCountColIdx = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColShowAppliesToTimeCount);

        final int fromTimeColumnIndex = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColFromTime);
        final int toTimeColumnIndex = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColToTime);

        final int valueIdColumnIndex = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColId);
        final int itemIdColumnIndex = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColItemId);
        final int descriptionColumnIndex = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColDescription);
        final int createdDateColIdx = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColDateCreated);
        final int itemNameColIdx = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColItemName);

        final Map<String, Integer> valueIdForDateDisplay = new HashMap<>();

        SimpleCursorAdapter listCursorAdapter = new SimpleCursorAdapter(mainList.getContext(),
                R.layout.single_history_item, cachedCursor, from, to);

        mainList.setAdapter(listCursorAdapter);

        listCursorAdapter.setViewBinder(
                new SimpleCursorAdapter.ViewBinder() {
                    @Override
                    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

                        if (columnIndex == itemNameColIdx) {
                            TextView tv = (TextView) view;

                            if (multiMode) {
                                tv.setText(cursor.getString(columnIndex));

                                if (highlightItemIds.contains(cursor.getInt(itemIdColumnIndex))) {
                                    // tv.setTextColor(Color.WHITE);
                                    tv.setBackgroundColor(Color.GREEN);
                                } else {
                                    //todo: specify global default for this to use in code and xml
                                    // tv.setTextColor(Color.GRAY);

                                    tv.setBackgroundColor(ContextCompat.getColor(mainList.getContext(), R.color.transparent));
                                }
                            } else {
                                tv.setVisibility(View.GONE);
                            }

                            return true;
                        }

                        if (columnIndex == descriptionColumnIndex) {
                            TextView tv = (TextView) view;

                            String description = cursor.getString(columnIndex);

                            if (description == null || description.trim() == "") {
                                tv.setVisibility(View.GONE);
                            } else {

                                String highlightText = highlightText();
                                description =
                                        description.replaceAll("(?i)" + highlightText, "<font color=\"red\">" + highlightText() + "</font>");
                                tv.setSingleLine(!showLongDescription);
                                tv.setText(Html.fromHtml(description));
                            }

                            return true;
                        }

                        if (columnIndex == appliesToDateColIdx) {
                            TextView tv = (TextView) view;

                            String appliesToDate = cursor.getString(appliesToDateColIdx);

                            try {
                                Date d = DateHelper.TimeStampToDate(appliesToDate, DateHelper.simpleDateFormatLocal);
                                String formattedDate = dateFormat.format(d);

                                tv.setText(tv.getText() + " " + formattedDate);

                                if (multiMode) {
                                    if (tv.getId() == R.id.single_history_item_date) {
                                        tv.setVisibility(View.GONE);
                                    }

                                    if (tv.getId() == R.id.item_group_header) {

                                        int currentValueId = cursor.getInt(valueIdColumnIndex);

                                        boolean applyDateHeaderHere = false;

                                        if (valueIdForDateDisplay.containsKey(formattedDate)) {
                                            //if we know where to put the date, we'll use that to reapply:
                                            applyDateHeaderHere = currentValueId == valueIdForDateDisplay.get(formattedDate);
                                        } else {
                                            applyDateHeaderHere = !d.equals(lastDate);
                                        }

                                        final String SET_HEADER = "SET";
                                        if (applyDateHeaderHere) {
                                            valueIdForDateDisplay.put(formattedDate, currentValueId);
                                            tv.setTag(SET_HEADER);
                                        } else {
                                            if (!SET_HEADER.equals(tv.getTag())) {  // nasty fix for inexplicable problem where item is repeated with top item's textview so it deletes
//                                                tv.setVisibility(View.GONE);
                                            }
                                        }

                                        lastDate = d;
                                    }
                                } else {
                                    if (tv.getId() == R.id.item_group_header) {
                                        tv.setVisibility(View.GONE);
                                    }
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                tv.setText("ERR: " + e.getMessage());
                            }

                            return true;
                        }

                        if (appliesToTimeColIdx == columnIndex) {

                            TextView tv = (TextView) view;

                            //todo:  too much leaking logic???
                            //converting to objects would be less (or too in)efficient?
                            boolean showAppliesToTime = cursor.getInt(showAppliesToTimeCountColIdx) > 0
                                    && cursorHelper.timesToShowDate(cursor) < 1;

                            if (showAppliesToTime) {
                                Time t = new Time(0, 0, 0);
                                if (cursor.isNull(appliesToTimeColIdx)) {

                                    try {
                                        Date crDate =
                                                DateHelper.TimeStampToDate(cursor.getString(createdDateColIdx));

                                        t = DateHelper.getLocalTime(DateHelper.sameTimeGmt(crDate));
                                    } catch (ParseException e) {
                                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                    }
                                } else {
                                    t = DateHelper.IntToTime(cursor.getInt(appliesToTimeColIdx));
                                }

                                tv.setVisibility(View.VISIBLE);
                                tv.setText(short24TimeFormat.format(t));
                            } else {
                                tv.setVisibility(View.GONE);
                            }

                            return true;
                        }

                        if (columnIndex == fromTimeColumnIndex) {

                            TextView tv = (TextView) view;

                            int timesToShowDate = cursorHelper.timesToShowDate(cursor);

                            if (timesToShowDate > 0) {

                                boolean toShows = timesToShowDate > 1;

                                int startTimeAsInt = cursor.getInt(columnIndex);

                                Time t = DateHelper
                                        .IntToTime(startTimeAsInt);

                                String timeToShow = short24TimeFormat.format(t);

                                if (toShows) {

                                    int endTimeAsInt = cursor.getInt(toTimeColumnIndex);
                                    t = DateHelper
                                            .IntToTime(endTimeAsInt);

                                    timeToShow = timeToShow
                                            + " - "
                                            + short24TimeFormat.format(t)
                                            + " ("
                                            + DateHelper.totalHours(doableItemValueTableAdapter, startTimeAsInt, endTimeAsInt)
                                            + ")";
                                }

                                tv.setText(timeToShow);
                            } else {
                                //stupid android seems to hold old values and apply them automatically when handled = true
                                tv.setText("");
                            }
                            return true;
                        }

                        if (columnIndex == teaspoonColIdx) {
                            TextView tv = ((TextView) view);

                            if (!cursorHelper.isTeaspoons(cursor)) {
                                tv.setVisibility(View.GONE);
                            } else {
                                tv.setText(cursorHelper.getTeaspoons(cursor));
                                tv.setVisibility(View.VISIBLE);
                            }

                            return true;
                        }

                        if (columnIndex == potencyColIdx) {
                            TextView tv = ((TextView) view);

                            if (!cursorHelper.isDrops(cursor)) {
                                tv.setText("");
                            } else {
                                tv.setText("p" + cursor.getInt(potencyColIdx));
                            }

                            return true;
                        }

                        return false;
                    }
                }
        );
    }

    public void item_click(View view) {
        Intent intent = new Intent(this, AddItemActivity.class);
        intent.putExtra("itemId", itemId);
        startActivity(intent);
    }

    public void previous_click(View view) {
        MoveToMatch(false);
    }

    public void next_click(View view) {

        MoveToMatch(true);
    }

    private void MoveToMatch(Boolean forward) {
        boolean found = false;

        String highlightText = highlightText().toLowerCase();
        final int descriptionColumnIndex = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColDescription);
        final int itemIdColumnIndex = cachedCursor.getColumnIndex(DoableItemValueTableAdapter.ColItemId);

        int currentPosition = mainList.getFirstVisiblePosition();
        cachedCursor.moveToPosition(currentPosition);

        while (!found && (forward ? cachedCursor.moveToNext() : cachedCursor.moveToPrevious())) {
            String description = cachedCursor.getString(descriptionColumnIndex);
            Integer itemId = cachedCursor.getInt(itemIdColumnIndex);

            Boolean descriptionMatch = description != null && highlightText != null
                    && highlightText.trim().length() > 0
                    && description.toLowerCase().contains(highlightText);

            Boolean itemMatch = highlightItemIds.contains(itemId);
            found = descriptionMatch || itemMatch;
        }

        if (!found) {

            String msg = "No " + (forward ? "next" : "previous") + " occurrence of '" + highlightText + "' found";
            Toast.makeText(ItemHistoryActivity.this, msg, Toast.LENGTH_LONG)
                    .show();
        } else {
            mainList.setSelectionFromTop(cachedCursor.getPosition(), 0);
        }
    }

    private void SetCursor(View view, Cursor c) {
        c.moveToPosition(mainList.getPositionForView(view));
    }

    public void single_history_item_description_click(View v) {
        Intent intent = new Intent(this, EditDescriptionActivity.class);

        SetCursor(v, cachedCursor);
        intent.putExtra(EditDescriptionActivity.ExtraValueId,
                cursorHelper.getValueId(cachedCursor));
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();    //To change body of overridden methods use File | Settings | File Templates.

        instanceCount--;
    }
}
