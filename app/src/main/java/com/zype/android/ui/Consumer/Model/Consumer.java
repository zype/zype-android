package com.zype.android.ui.Consumer.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Evgeny Cherkasov on 28.06.2017.
 */

public class Consumer implements Parcelable{
    public String email;
    public String password;

    public Consumer() {}

    //
    // 'Parcelable' implementation
    //
    private Consumer(Parcel in) {
        email = in.readString();
        password = in.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(email);
        parcel.writeString(password);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Consumer> CREATOR = new Parcelable.Creator<Consumer>() {
        @Override
        public Consumer createFromParcel(Parcel in) {
            return new Consumer(in);
        }

        @Override
        public Consumer[] newArray(int size) {
            return new Consumer[size];
        }
    };

}
