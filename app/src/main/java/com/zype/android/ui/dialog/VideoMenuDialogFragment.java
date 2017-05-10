package com.zype.android.ui.dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.zype.android.R;
import com.zype.android.ui.main.fragments.videos.VideosMenuAdapter;
import com.zype.android.ui.main.fragments.videos.VideosMenuItem;

import java.util.ArrayList;

/**
 * @author vasya
 * @version 1
 *          date 10/13/15
 */

public class VideoMenuDialogFragment extends DialogFragment {

    ListView optionList;
    private ArrayList<VideosMenuItem> list;
    private OnItemClickListener listener;

    public static VideoMenuDialogFragment newInstance(ArrayList<VideosMenuItem> items) {
        VideoMenuDialogFragment frag = new VideoMenuDialogFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("list", items);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            list = getArguments().getParcelableArrayList("list");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_fragment, container, false);
        optionList = (ListView) view.findViewById(R.id.list);
        if (listener != null) {
            optionList.setOnItemClickListener(listener);
            listener = null;
        }

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        VideosMenuAdapter adapter = new VideosMenuAdapter(getActivity(),
                android.R.layout.simple_list_item_1, list);

        optionList.setAdapter(adapter);


    }

    public void setOnItemClickListener(@Nullable OnItemClickListener listener) {
        if (optionList != null) {
            optionList.setOnItemClickListener(listener);
        } else {
            this.listener = listener;
        }
    }

    public ArrayList<VideosMenuItem> getList() {
        return list;
    }
}
