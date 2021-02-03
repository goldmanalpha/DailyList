package com.goldmanalpha.dailydo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.com.goldmanalpha.dailydo.db.DoableItemValueTableAdapter;
import com.goldmanalpha.dailydo.databinding.EditDescriptionBinding;
import com.goldmanalpha.dailydo.model.DoableValue;

import java.text.ParseException;

import static com.goldmanalpha.androidutility.DateHelper.LongDateString;
import static com.goldmanalpha.androidutility.DateHelper.sameTimeGmt;

/**
 * Created by IntelliJ IDEA.
 * User: Aaron
 * Date: 1/2/12
 * Time: 5:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class EditDescriptionActivity extends ActivityBase {

    DoableItemValueTableAdapter tableAdapter;
    DoableValue value;
    public static final String ExtraValueId = "valueId";
    public static final String ExtraValueOutOfRangeDateOK = "ExtraValueOutOfRangeDateOK";

    boolean outOfRangeDateOK;
    String startingValue;
    private EditDescriptionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        binding = EditDescriptionBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        tableAdapter = new DoableItemValueTableAdapter();

        Intent intent = getIntent();
        int valueId = intent.getIntExtra(ExtraValueId, 0);
        outOfRangeDateOK = intent.getBooleanExtra(ExtraValueOutOfRangeDateOK, false);

        try {
            value = tableAdapter.get(valueId);
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            this.finish();
        }

        this.date = LongDateString(sameTimeGmt(value.getAppliesToDate()));
        setWindowState(value.getAppliesToDate());

        setTitle(this.date + ": " + value.getItem().getName());

        startingValue = value.getDescription();

        if (startingValue != null) {
            binding.editDescriptionEntry.setText(startingValue);
        }

        binding.headerStartText.setText(this.date);
        binding.headerMiddleText.setText(value.getItem().getName());
        binding.headerEndText.setText(value.valueDisplayString());
    }

    String date;

    @Override
    public void onBackPressed() {
        click_ok(null);
    }

    public void click_ok(View v) {

        if (this.getLastWindowState().equals(WindowState.OUT_OF_RANGE) && !outOfRangeDateOK) {
            final DoableValue value2 = value;

            if (getEditor().getText().toString().equals(startingValue)) {
                finish();
            } else {
                SeriousConfirmationDialog dlg = new SeriousConfirmationDialog(this,
                        value.getItem().getName(), "Change value on: " + this.date,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                                if (id == DialogInterface.BUTTON_POSITIVE) {
                                    Save();
                                }
                            }
                        });

                dlg.show();
            }
        } else {
            Save();
        }
    }

    private EditText getEditor() {
        return binding.editDescriptionEntry;
    }

    private void Save() {
        EditText editor = getEditor();

        value.setDescription(editor.getText().toString());
        tableAdapter.save(value);
        finish();
    }

    public void click_cancel(View v) {
        finish();
    }
}
