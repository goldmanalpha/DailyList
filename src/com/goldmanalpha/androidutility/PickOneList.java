package com.goldmanalpha.androidutility;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PickOneList extends ListActivity {

    public static final String Title = "title_bar";
    public static final String Choices = "choices";
    public static final String SelectedItem = "selItem";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String[] choices = getIntent().getStringArrayExtra(PickOneList.Choices);
        final String title = getIntent().getStringExtra(PickOneList.Title);
        final String selectedItem = getIntent().getStringExtra(PickOneList.SelectedItem);


        super.setTitle(title);

        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice, choices));

        final ListView listView = getListView();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemSelected(AdapterView parentView, View childView, int position, long id) {

            }

            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //To change body of implemented methods use File | Settings | File Templates.

                Intent intent = new Intent();

                intent.putExtra(PickOneList.SelectedItem, choices[position]);

                setResult(RESULT_OK, intent);

                finish();
            }
        });


        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        for (int i = 0; i < choices.length; i++) {
            if (choices[i].equals(selectedItem)) {
                listView.setSelection(i);
                break;
            }
        }

    }

}
