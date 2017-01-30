package com.zype.android.ui.main.fragments.videos;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author vasya
 * @version 1
 *          date 10/13/15
 */
public class VideosMenuItem implements Parcelable {
    int id;
    int stringId;

    private VideosMenuItem() {

    }

    public VideosMenuItem(int id, int stringId) {
        this.id = id;
        this.stringId = stringId;
    }

    public int getId(){
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.stringId);
    }

    protected VideosMenuItem(Parcel in) {
        this.id = in.readInt();
        this.stringId = in.readInt();
    }

    public static final Creator<VideosMenuItem> CREATOR = new Creator<VideosMenuItem>() {
        public VideosMenuItem createFromParcel(Parcel source) {
            return new VideosMenuItem(source);
        }

        public VideosMenuItem[] newArray(int size) {
            return new VideosMenuItem[size];
        }
    };
}
