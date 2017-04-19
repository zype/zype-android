package com.zype.android.ui.main.fragments.settings;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

/**
 * @author vasya
 * @version 1
 *          date 7/3/15
 */
public class SettingsItem {
    String title;
    int iconId;
    int iconColorRes;
    int id;

    public SettingsItem(int id, @DrawableRes int drawable, @ColorRes int colorRes, String title) {
        this.id = id;
        this.iconId = drawable;
        this.iconColorRes = colorRes;
        this.title = title;
    }

    public SettingsItem(Context context, int id, @StringRes int titleId) {
        this.id = id;
        this.iconId = -1;
        this.iconColorRes = -1;
        this.title = context.getString(titleId);
    }
    public SettingsItem(Context context, int id, String title) {
        this.id = id;
        this.iconId = -1;
        this.iconColorRes = -1;
        this.title = title;
    }

    public SettingsItem(Context context, int id, @DrawableRes int drawable, @ColorRes int colorRes, @StringRes int titleId) {
        this.id = id;
        this.iconId = drawable;
        this.iconColorRes = colorRes;
        this.title = context.getString(titleId);
    }
    public SettingsItem(Context context, int id, @DrawableRes int drawable, @ColorRes int colorRes, String title) {
        this.id = id;
        this.iconId = drawable;
        this.iconColorRes = colorRes;
        this.title = title;
    }

}
