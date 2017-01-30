package com.zype.android.ui.main.fragments.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.zype.android.R;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.ui.OnLoginAction;
import com.zype.android.ui.OnMainActivityFragmentListener;
import com.zype.android.ui.settings.SettingsActivity;
import com.zype.android.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment implements ListView.OnItemClickListener {

    private ListAdapter mAdapter;
    private Button mSigninButton;
    private View mSigninButtonFake;

    private OnMainActivityFragmentListener mListener;
    private OnLoginAction mOnLoginListener;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<SettingsItem> settingsItems = new ArrayList<>();
        settingsItems.add(new SettingsItem(getActivity(), 0, R.drawable.icn_settings, R.color.black_38, R.string.settings));
        settingsItems.add(new SettingsItem(getActivity(), 1, R.drawable.icn_facebook, R.color.facebook_bg_color, R.string.settings_facebook));
        settingsItems.add(new SettingsItem(getActivity(), 2, R.drawable.icn_twitter, R.color.twitter_bg_color, R.string.settings_twitter));
        settingsItems.add(new SettingsItem(getActivity(), 3, R.string.settings_web));
        settingsItems.add(new SettingsItem(getActivity(), 4, R.string.settings_instagram));
        mAdapter = new SettingsListAdapter(getActivity(), R.layout.list_item_settings, settingsItems);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ListView listView = (ListView) view.findViewById(R.id.list_settings);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);
        mSigninButton = (Button) view.findViewById(R.id.sign_in_button);
        mSigninButtonFake = view.findViewById(R.id.fake_sign_in_button);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initSignInButton();
    }

    private void initSignInButton() {
        if (SettingsProvider.getInstance().isLogined()) {
            mSigninButtonFake.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnLoginListener.onLogout();
                    initSignInButton();
                }
            });
            mSigninButton.setText(R.string.action_sign_out);
        } else {
            mSigninButtonFake.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnLoginListener.onRequestLogin();
                }
            });
            mSigninButton.setText(R.string.action_sign_in);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                break;
            case 1:
                // TODO: Add valid Facebook url
//                String facebookUrl = "https://www.facebook.com/[app_name]";
//                AppUtils.openFacebook(getContext(), facebookUrl);
                break;
            case 2:
                // TODO: Add valid Twitter id and url
//                AppUtils.openTwitter(getContext(), "[app_name]", "https://twitter.com/[app_name]");
                break;
            case 3:
                // TODO: Add valid web url
//                AppUtils.openWeb(getContext(), "http://www.[app_name]");
                break;
            case 4:
                // TODO: Add valid Instagram id and url
//                AppUtils.openInstagram(getContext(), "[app_name]", "http://www.instagram.com/[app_name]");
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnMainActivityFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        try {
            mOnLoginListener = (OnLoginAction) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnLoginAction");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mOnLoginListener = null;
    }
}
