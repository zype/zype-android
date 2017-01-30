package com.zype.android.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;

import com.zype.android.R;

import java.util.ArrayList;

/**
 * @author vasya
 * @version 1
 *          date 7/13/15
 */
public class CustomAlertDialog extends DialogFragment {

    public static CustomAlertDialog newInstance(int title, int message) {
        CustomAlertDialog frag = new CustomAlertDialog();
        Bundle args = new Bundle();
        args.putInt("title", title);
        args.putInt("message", message);
        frag.setArguments(args);
        return frag;
    }

    public static AlertDialog.Builder getListDialog(String titleName, ArrayList<String> itemList, Context context, DialogInterface.OnClickListener callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme);
        builder.setTitle(titleName);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.item_option_dialog);
        arrayAdapter.addAll(itemList);

        builder.setAdapter(arrayAdapter, callback);
        return builder;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");
        int message = getArguments().getInt("message");

        return new AlertDialog.Builder(getActivity(), R.style.DialogTheme)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dismiss();
                            }
                        }
                )
                .create();
    }
}
