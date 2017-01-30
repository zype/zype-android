package com.zype.android.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;

import com.zype.android.R;

/**
 * Created by Evgeny Cherkasov on 04.10.2016.
 */

public class ErrorDialogFragment extends DialogFragment {
    public final static String TAG = ErrorDialogFragment.class.getSimpleName();

    private final static String PARAMETERS_MESSAGE = "Message";
    private final static String PARAMETERS_TITLE = "Title";
    private final static String PARAMETERS_BUTTON = "Button";

    private final static String BUTTON_OK = "Ok";

    private String title;
    private String message;
    private String button;

    public ErrorDialogFragment() {
    }

    public static ErrorDialogFragment newInstance(String message, String title, String button) {
        ErrorDialogFragment fragment = new ErrorDialogFragment();
        Bundle args = new Bundle();
        args.putString(PARAMETERS_BUTTON, button);
        args.putString(PARAMETERS_MESSAGE, message);
        args.putString(PARAMETERS_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        initParameters(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        builder.setMessage(message);
        builder.setPositiveButton(button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        return builder.create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PARAMETERS_BUTTON, button);
        outState.putString(PARAMETERS_MESSAGE, message);
        outState.putString(PARAMETERS_TITLE, title);
    }

    private void initParameters(Bundle savedInstanceState) {
        Bundle args;
        if (savedInstanceState != null) {
            args = savedInstanceState;
        }
        else {
            args = getArguments();
        }
        if (args != null) {
            title = args.getString(PARAMETERS_TITLE);
            message = args.getString(PARAMETERS_MESSAGE);
            button = args.getString(PARAMETERS_BUTTON, BUTTON_OK);
        }
        else {
            button = BUTTON_OK;
        }
    }
}
