package com.euro16;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * Created by Guillaume on 12/03/2016.
 */
public class AlertMsgBox {

    public Activity activity;

    public AlertMsgBox(final Activity activity, String title, String message, String textButton, DialogInterface.OnClickListener listener) {
        this.activity = activity;
        AlertDialog LDialog = new AlertDialog.Builder(this.activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(textButton, listener).create();
        LDialog.show();
    }
}
