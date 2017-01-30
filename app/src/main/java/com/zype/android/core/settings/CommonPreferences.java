package com.zype.android.core.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import java.util.HashSet;

abstract class CommonPreferences {

    private final SharedPreferences sp;

    CommonPreferences(Context context) {
        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    String get(String key, String defValue) {
        return sp.getString(key, defValue);
    }

    boolean get(String key, boolean defValue) {
        return sp.getBoolean(key, defValue);
    }

    long get(String key, long defValue) {
        return sp.getLong(key, defValue);
    }

    protected int get(String key, int defValue) {
        return sp.getInt(key, defValue);
    }

    protected float get(String key, float defValue) {
        return sp.getFloat(key, defValue);
    }

    protected HashSet<String> get(String key, HashSet<String> defValue) {
        return (HashSet)sp.getStringSet(key, defValue);
    }

    void set(String key, String value) {
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(key, value);
        ed.apply();
    }

    void set(String key, boolean value) {
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(key, value);
        ed.apply();
    }

    void set(String key, long value) {
        SharedPreferences.Editor ed = sp.edit();
        ed.putLong(key, value);
        ed.apply();
    }

    protected void set(String key, float value) {
        SharedPreferences.Editor ed = sp.edit();
        ed.putFloat(key, value);
        ed.apply();
    }

    protected void set(String key, int value) {
        SharedPreferences.Editor ed = getEditor();
        ed.putInt(key, value);
        commit(ed);
    }

    protected void set(String key, HashSet<String> value) {
        SharedPreferences.Editor ed = getEditor();
        ed.putStringSet(key, value);
        commit(ed);
    }

    private SharedPreferences.Editor getEditor() {
        return sp.edit();
    }

    private void commit(SharedPreferences.Editor ed) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            // async call
            ed.apply();
        } else {
            // old api, writes disk on UI thread
            ed.commit();
        }
    }

    public int getInt(String key, int defValue) {
        return sp.getInt(key, defValue);
    }

    public String getString(String key, String defValue) {
        return sp.getString(key, defValue);
    }


//    private void set(String prefUserCountry, Country country) {
//
//    }
}
