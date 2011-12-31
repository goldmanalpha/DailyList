package com.goldmanalpha.androidutility;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PickOneList extends ListActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] choices = getIntent().getStringArrayExtra("goldmanalpha.pickone.choices");
        String title = getIntent().getStringExtra("goldmanalpha.pickone.title");
        String selectedItem = getIntent().getStringExtra("goldmanalpha.pickone.selectedItem");


        super.setTitle(title);

        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice, choices));

        final ListView listView = getListView();

        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        for(int i = 0; i < choices.length ; i ++)
        {
            if (choices[i].equals(selectedItem))
            {
                listView.setSelection(i);
                break;
            }
        }

    }


    private static final String[] GENRES = new String[] {
        "Action", "Adventure", "Animation", "Children", "Comedy", "Documentary", "Drama",
        "Foreign", "History", "Independent", "Romance", "Sci-Fi", "Television", "Thriller"
    };
}
