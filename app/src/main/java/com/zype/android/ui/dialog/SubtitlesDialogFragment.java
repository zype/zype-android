package com.zype.android.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.zype.android.R;

/**
 * Created by Evgeny Cherkasov on 27.02.2018.
 */

public class SubtitlesDialogFragment extends DialogFragment {

    /**
     * Fragment TAG.
     */
    private static final String TAG = SubtitlesDialogFragment.class.getSimpleName();

    /**
     * Subtitles dialog listener.
     */
    public interface ISubtitlesDialogListener {
        /**
         * On item selectedmethod.
         *
         * @param dialog The alert dialog fragment to listen on.
         */
        void onItemSelected(SubtitlesDialogFragment dialog, int selectedItem);
    }

    /**
     * Dialog title.
     */
    private String mTitle;

    /**
     * List of subtitles tracks
     */
    private CharSequence[] items;

    /**
     * Selected subtitle track
     */
    private int selectedItem = 0;

    /**
     * Dialog listener reference.
     */
    private ISubtitlesDialogListener mDialogListener;

    private static final String EXTRA_TITLE = "ExtraTitle";
    private static final String EXTRA_ITEMS = "ExtraItems";
    private static final String EXTRA_SELECTED_ITEM = "ExtraSelectedItem";

    /**
     * Create and show alert dialog fragment.
     *
     * @param activity             Activity.
     * @param title                Dialog title.
     * @param items                List of tracks to select.
     * @param selectedItem         Selected track
     * @param listener             Dialog listener reference.
     */
    public static void createAndShowSubtitlesDialogFragment(Activity activity,
                                                            String title,
                                                            CharSequence[] items,
                                                            int selectedItem,
                                                            ISubtitlesDialogListener listener) {

        SubtitlesDialogFragment dialog = SubtitlesDialogFragment.newInstance(title, items, selectedItem);
        dialog.mTitle = dialog.getArguments().getString(EXTRA_TITLE);
        dialog.items = dialog.getArguments().getCharSequenceArray(EXTRA_ITEMS);
        dialog.selectedItem = dialog.getArguments().getInt(EXTRA_SELECTED_ITEM);
        dialog.mDialogListener = listener;
        FragmentManager fragmentManager = activity.getFragmentManager();
        dialog.setCancelable(true);
        dialog.show(fragmentManager, TAG);
    }

    /**
     * Default constructor.
     */
    public SubtitlesDialogFragment() {
    }

    /**
     * Creates new dialog instance
     *
     * @param title                Dialog title.
     * @param items                List of tracks to select.
     * @param selectedItem         Selected track
     */
    public static SubtitlesDialogFragment newInstance(String title, CharSequence[] items, int selectedItem) {
        SubtitlesDialogFragment fragment = new SubtitlesDialogFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_TITLE, title);
        args.putCharSequenceArray(EXTRA_ITEMS, items);
        args.putInt(EXTRA_SELECTED_ITEM, selectedItem);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (items.length > 0) {
            ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_multiple_choice, items);
            builder.setTitle(mTitle);
            builder.setSingleChoiceItems(adapter, selectedItem, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mDialogListener.onItemSelected(SubtitlesDialogFragment.this, which);
                }
            });

        }
        else {
            builder.setMessage(R.string.subtitles_dialog_empty);
            builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dismiss();
                }
            });
        }
        Dialog result = builder.create();
        return result;
    }

    /**
     * Set listener
     *
     * @param listener             Dialog listener reference.
     */
    public void setListener(ISubtitlesDialogListener listener) {
        this.mDialogListener = listener;
    }
}
