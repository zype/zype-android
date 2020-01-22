package com.zype.android.ui.main.fragments.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.zype.android.Auth.AuthHelper;
import com.zype.android.R;
import com.zype.android.ZypeConfiguration;
import com.zype.android.ZypeSettings;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.ui.OnLoginAction;
import com.zype.android.ui.OnMainActivityFragmentListener;
import com.zype.android.ui.settings.SettingsActivity;
import com.zype.android.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment implements ListView.OnItemClickListener {

    private static final int ITEM_SETTINGS = 0;
    private static final int ITEM_WEB = 1;
    private static final int ITEM_FACEBOOK = 2;
    private static final int ITEM_TWITTER = 3;
    private static final int ITEM_INSTAGRAM = 4;

    private ListAdapter mAdapter;
    private Button mSigninButton;
//    private View mSigninButtonFake;

    private OnMainActivityFragmentListener mListener;
    private OnLoginAction mOnLoginListener;

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<SettingsItem> settingsItems = new ArrayList<>();
        settingsItems.add(new SettingsItem(getActivity(), ITEM_SETTINGS, R.drawable.icn_settings, R.color.black_38, R.string.settings));
        settingsItems.add(new SettingsItem(getActivity(), ITEM_WEB, String.format(getString(R.string.settings_web), getString(R.string.app_name))));
        settingsItems.add(new SettingsItem(getActivity(), ITEM_FACEBOOK, R.drawable.icn_facebook, R.color.facebook_bg_color, String.format(getString(R.string.settings_facebook), getString(R.string.app_name))));
        settingsItems.add(new SettingsItem(getActivity(), ITEM_TWITTER, R.drawable.icn_twitter, R.color.twitter_bg_color, String.format(getString(R.string.settings_twitter), getString(R.string.app_name))));
        settingsItems.add(new SettingsItem(getActivity(), ITEM_INSTAGRAM, R.drawable.instagram, R.color.instagram, String.format(getString(R.string.settings_instagram), getString(R.string.app_name))));
        mAdapter = new SettingsListAdapter(getActivity(), R.layout.list_item_settings, settingsItems);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ListView listView = (ListView) view.findViewById(R.id.list_settings);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);
        mSigninButton = (Button) view.findViewById(R.id.sign_in_button);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initSignInButton();
    }

    private void initSignInButton() {
        if (SettingsProvider.getInstance().isLoggedIn()) {
            mSigninButton.setText(R.string.action_sign_out);
            mSigninButton.setVisibility(View.VISIBLE);
            mSigninButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnLoginListener.onLogout();
                    AuthHelper.onLoginStateChanged();
                    initSignInButton();
                }
            });
        }
        else {
            mSigninButton.setText(R.string.action_sign_in);
            if (ZypeConfiguration.isUniversalSubscriptionEnabled(getActivity())
                    || ZypeConfiguration.isNativeToUniversalSubscriptionEnabled(getActivity())
                    || ZypeConfiguration.isUniversalTVODEnabled(getActivity())) {
                mSigninButton.setVisibility(View.VISIBLE);
                mSigninButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnLoginListener.onRequestLogin();
                    }
                });
            }
            else {
                mSigninButton.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case ITEM_SETTINGS:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                break;
            case ITEM_FACEBOOK:
                if (!TextUtils.isEmpty(ZypeSettings.FACEBOOK_ID)) {
                    String facebookUrl = String.format("https://m.facebook.com/%1$s", ZypeSettings.FACEBOOK_ID);
                    AppUtils.openFacebook(getContext(), facebookUrl);
                }
                break;
            case ITEM_TWITTER:
                if (!TextUtils.isEmpty(ZypeSettings.TWITTER_ID)) {
                    AppUtils.openTwitter(getContext(), ZypeSettings.TWITTER_ID, String.format("https://twitter.com/%1$s", ZypeSettings.TWITTER_ID));
                }
                break;
            case ITEM_WEB:
                if (!TextUtils.isEmpty(ZypeSettings.WEB_URL)) {
                    AppUtils.openWeb(getContext(), ZypeSettings.WEB_URL);
                }
                break;
            case ITEM_INSTAGRAM:
                if (!TextUtils.isEmpty(ZypeSettings.INSTAGRAM_ID)) {
                    AppUtils.openInstagram(getContext(), ZypeSettings.INSTAGRAM_ID, String.format("https://www.instagram.com/%1$s", ZypeSettings.INSTAGRAM_ID));
                }
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
