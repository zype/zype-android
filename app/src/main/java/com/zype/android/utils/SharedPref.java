package com.zype.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class SharedPref {

  private static SharedPreferences mSharedPref;

  private SharedPref() {
  }

  public static void init(Context context) {
    if (mSharedPref == null)
      mSharedPref = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
  }

  private static void assertNotNull() {
    Objects.requireNonNull(mSharedPref);
  }


  public static void save(String key, String value) {
    assertNotNull();
    SharedPreferences.Editor mEditor = mSharedPref.edit();
    if (!TextUtils.isEmpty(value)) {
      mEditor.putString(key, value);
      mEditor.commit();
    }
  }

  public static void save(String key, boolean value) {
    assertNotNull();
    SharedPreferences.Editor mEditor = mSharedPref.edit();
    mEditor.putBoolean(key, value);
    mEditor.commit();
  }

  public static void save(String key, long value) {
    assertNotNull();
    SharedPreferences.Editor mEditor = mSharedPref.edit();
    mEditor.putLong(key, value);
    mEditor.commit();
  }

  public static void save(String key, int value) {
    assertNotNull();
    SharedPreferences.Editor mEditor = mSharedPref.edit();
    mEditor.putInt(key, value);
    mEditor.commit();
  }

  public static void save(String key, Set<String> set) {
    assertNotNull();
    SharedPreferences.Editor mEditor = mSharedPref.edit();
    mEditor.putStringSet(key, set);
    mEditor.commit();
  }

  public static String getString(String key) {
    assertNotNull();
    return mSharedPref.getString(key, "");
  }

  public static int getInt(String key) {
    assertNotNull();

    return mSharedPref.getInt(key, -1);
  }

  public static long getLong(String key) {
    assertNotNull();
    return mSharedPref.getLong(key, -1);
  }

  public static Set<String> getStringSet(String key) {
    assertNotNull();
    return mSharedPref.getStringSet(key, new HashSet<>());
  }

  public static void remove(String key) {
    assertNotNull();
    mSharedPref.edit().remove(key).commit();
  }

  public static boolean getBoolean(String key) {
    assertNotNull();
    return mSharedPref.getBoolean(key, false);
  }

  public static void clear() {
    assertNotNull();
    SharedPreferences.Editor mEditor = mSharedPref.edit();
    mEditor.clear();
    mEditor.commit();
  }
}