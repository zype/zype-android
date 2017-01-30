package com.zype.android.utils;

import android.support.annotation.Nullable;

import com.zype.android.webapi.model.player.File;

import java.util.List;

/**
 * @author vasya
 * @version 1
 *          date 10/19/15
 */
public class ListUtils {
    @Nullable
    public static File getStringWith(List<File> files, String text) {
        for (File f : files) {
            if (f.getUrl().endsWith(text)) {
                return f;
            }
        }
        return null;
    }
}
