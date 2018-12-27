package com.zype.android.utils;

import com.zype.android.R;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.ui.NavigationHelper;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

public class DialogHelper {

    public static void showAlert(final Context context, String title, String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(context.getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    public static void showSubscriptionAlertIssue(final Context context) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(context.getString(R.string.dialog_subscribe_title));
        alertDialog.setMessage(context.getString(R.string.dialog_subscribe_message));
        alertDialog.setPositiveButton(context.getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String url = SettingsProvider.getInstance().getSubscribeUrl();
                if (!TextUtils.isEmpty(url)) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    context.startActivity(i);
                }
                dialog.cancel();
            }
        });
        alertDialog.setNegativeButton(context.getString(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    public static void showEntitlementAlert(final Context context, String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(context.getString(R.string.entitlement_dialog_error_title));
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(context.getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    public static void showLoginAlert(final Activity activity) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle(activity.getString(R.string.dialog_login_title));
        alertDialog.setMessage(activity.getString(R.string.dialog_login_message));
        alertDialog.setPositiveButton(activity.getString(R.string.dialog_login_button_login), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                NavigationHelper.getInstance(activity).switchToLoginScreen(activity);
                dialog.dismiss();
            }
        });
        alertDialog.setNegativeButton(activity.getString(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    public static void showErrorAlert(final Activity activity, String message) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setMessage(message);
        alertDialog.setNegativeButton(activity.getString(R.string.dialog_button_close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = alertDialog.create();
        if (!activity.isFinishing()) {
            alert.show();
        }
    }

}
