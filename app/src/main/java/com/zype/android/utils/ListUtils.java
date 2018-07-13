package com.zype.android.utils;

import android.support.annotation.Nullable;

import com.zype.android.webapi.model.player.File;

import java.util.List;

/**
 * Created by Evgeny Cherkasov on 12.07.2018
 */


public class ListUtils {

    /**
     * Search file of specified type in the list.
     *
     * @param files List of files
     * @param type File type to search by. Must be in '.<type>' format.
     * @return File of specified type if found, otherwise null
     *
     */
    @Nullable
    public static File getFileByType(List<File> files, String type) {
        for (File f : files) {
            String url = f.getUrl();
            if (url.indexOf("?") > 0) {
                url = url.substring(0, url.indexOf("?"));
            }
            if (url.endsWith(type)) {
                return f;
            }
        }
        return null;
    }
}
