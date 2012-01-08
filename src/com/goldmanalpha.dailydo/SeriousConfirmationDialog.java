package com.goldmanalpha.dailydo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class SeriousConfirmationDialog extends AlertDialog.Builder{

    public SeriousConfirmationDialog (Context context, String title, String message,
                                     DialogInterface.OnClickListener onClickListener) {
        super(context);
        setIcon(android.R.drawable.ic_dialog_alert);
        setTitle(title);
        setMessage(message);
        setPositiveButton(R.string.ok, onClickListener);
        setNegativeButton(R.string.cancel, null);
    }
}
