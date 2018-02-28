package com.zype.android.ui.settings;

import com.zype.android.BuildConfig;
import com.zype.android.R;
import com.zype.android.ZypeSettings;
import com.zype.android.core.settings.SettingsProvider;
import com.zype.android.utils.FileUtils;
import com.zype.android.utils.StorageUtils;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.view.MenuItem;

public class SettingsActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new MainPreferenceFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return MainPreferenceFragment.class.getName().equals(fragmentName)
                || DownloadPreferenceFragment.class.getName().equals(fragmentName);
    }

    public static class MainPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main_settings);

//            Preference downloadPreference = findPreference("download_preferences");
//            downloadPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//                @Override
//                public boolean onPreferenceClick(Preference preference) {
//                    getFragmentManager().beginTransaction()
//                            .addToBackStack("download_preferences")
//                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                            .replace(android.R.id.content, new DownloadPreferenceFragment()).commit();
//                    return true;
//                }
//            });
//
//            SwitchPreference downloadAutoPreference = (SwitchPreference) findPreference("DOWNLOAD_AUTO");
//            boolean isDownloadAuto = SettingsProvider.getInstance().isDownloadAuto();
//            downloadAutoPreference.setChecked(isDownloadAuto);
//
//            downloadAutoPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//                @Override
//                public boolean onPreferenceChange(Preference preference, Object newValue) {
//                    SettingsProvider.getInstance().setUserPreferenceDownloadAuto((boolean) newValue);
//                    if (SettingsProvider.getInstance().isDownloadAuto()) {
//                        SettingsProvider.getInstance().setDownloadLatestOne(true);
//                    } else {
//                        SettingsProvider.getInstance().setDownloadLatestOne(false);
//                    }
//                    return true;
//                }
//            });
            Preference termsPreference = findPreference("terms");
            termsPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(getActivity().getApplicationContext(), TermsActivity.class));
                    return true;
                }
            });

            try {
                PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                String version = pInfo.versionName;
                Preference versionPreference = findPreference("version");
                versionPreference.setSummary(version);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            Preference prefZypeTemplateVersion = findPreference("ZypeTemplateVersion");
            prefZypeTemplateVersion.setSummary(BuildConfig.ZYPE_TEMPLATE_VERSION);
        }
    }

    public static class DownloadPreferenceFragment extends PreferenceFragment {

        private ListPreferenceCompat storagePreference;
        private SwitchPreference wifiOnlyPreferences;
        private ListPreferenceCompat downloadType;
        private ListPreferenceCompat maxSize;
        private EditTextPreference currentDownloadsSize;
        private SwitchPreference autoRemoveWatchedContent;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_download);

            storagePreference = (ListPreferenceCompat) findPreference("PREF_STORAGE");
            wifiOnlyPreferences = (SwitchPreference) findPreference("DOWNLOAD_WIFI");
            downloadType = (ListPreferenceCompat) findPreference("DOWNLOAD_TYPE");
            maxSize = (ListPreferenceCompat) findPreference("PREF_MAX_DOWNLOAD_TOTAL_SIZE");
            currentDownloadsSize = (EditTextPreference) findPreference("PREF_CURRENT_DOWNLOADS_SIZE");
            autoRemoveWatchedContent = (SwitchPreference) findPreference("PREF_AUTO_REMOVE_WATCHED_CONTENT");

            //check if SD available
            checkAndSetupStoragePreferences(storagePreference);

            loadUserSpecificPreferences();

            //setup saving per user logic
            storagePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    SettingsProvider.getInstance().setUserPreferenceStorage((String) newValue);
                    return true;
                }
            });
            wifiOnlyPreferences.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    SettingsProvider.getInstance().setUserPreferenceLoadWiFiOnly((boolean) newValue);
                    return true;
                }
            });
            downloadType.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    SettingsProvider.getInstance().setUserPreferenceDownloadType((String) newValue);
                    return true;
                }
            });

            maxSize.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    SettingsProvider.getInstance().setUserPreferenceMaxSize((String) newValue);
                    return true;
                }
            });

            autoRemoveWatchedContent.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    SettingsProvider.getInstance().setUserPreferenceAutoRemoveWatchedContent((boolean) newValue);
                    return true;
                }
            });
        }

        private void checkAndSetupStoragePreferences(ListPreferenceCompat storagePreference) {
            CharSequence[] values = storagePreference.getEntries();
            String freeSpaceInternal = FileUtils.formatFileSize(StorageUtils.getFreeSpaceInternal(getActivity()));
            values[0] = values[0] + " " + freeSpaceInternal;
            if (!StorageUtils.isSdCardAvailableToUse(getActivity())) {
                storagePreference.setEnabled(false);
            } else {
                String freeSpaceExternal = FileUtils.formatFileSize(StorageUtils.getFreeSpaceExternal(getActivity()));
                values[1] = values[1] + " " + freeSpaceExternal;
            }
        }

        private void loadUserSpecificPreferences() {

            //wifi only downloads
            boolean wifiOnly = SettingsProvider.getInstance().isUserPreferenceLoadWifiOnlySet();
            wifiOnlyPreferences.setChecked(wifiOnly);

            // downloads size
            long allLoadsSize = StorageUtils.getSizeOfDownloadsFolder(getActivity());
            currentDownloadsSize.setSummary(FileUtils.formatFileSize(SettingsProvider.getInstance().getReserved()));

            //auto-remove watched content
            boolean autoRemoveValue = SettingsProvider.getInstance().isUserPreferenceAutoRemoveWatchedContentSet();
            autoRemoveWatchedContent.setChecked(autoRemoveValue);

            //preferable storage
            int savedValue = SettingsProvider.getInstance().getUserPreferenceStorage();
            storagePreference.setValue(String.valueOf(savedValue));

            //limit of downloads
            String maxValue = SettingsProvider.getInstance().getUserPreferenceMaxSize();
            maxSize.setValue(String.valueOf(maxValue));

            //type of loaded content
            String type = SettingsProvider.getInstance().getUserPreferenceDownloadType();
            downloadType.setValue(String.valueOf(type));
        }
    }
}
