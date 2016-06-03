package com.euro16.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * Created by Guillaume on 12/03/2016.
 */
public class AlertMsgBox {

    public Activity activity;

    public AlertMsgBox(Activity activity, String title, String message, String textButton, DialogInterface.OnClickListener listener) {
        this.activity = activity;
        AlertDialog LDialog = new AlertDialog.Builder(this.activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(textButton, listener).create();
        LDialog.show();
        LDialog.setCanceledOnTouchOutside(false);
    }

    public AlertMsgBox(Activity activity, String title, String message, String textBtnPos, String textBtnNeg, DialogInterface.OnClickListener listenerBtnPos, DialogInterface.OnClickListener listenerBtnNeg) {
        this.activity = activity;
        AlertDialog LDialog = new AlertDialog.Builder(this.activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(textBtnPos, listenerBtnPos)
                .setNegativeButton(textBtnNeg, listenerBtnNeg)
                .create();
        LDialog.show();
        LDialog.setCanceledOnTouchOutside(false);
    }
}
