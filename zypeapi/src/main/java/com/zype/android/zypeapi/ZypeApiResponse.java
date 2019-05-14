package com.zype.android.zypeapi;

/**
 * Created by Evgeny Cherkasov on 11.02.2019.
 */
public class ZypeApiResponse<T> {
    public T data;
    public boolean isSuccessful;

    public ZypeApiResponse(T data, boolean isSuccessful) {
        this.data = data;
        this.isSuccessful = isSuccessful;
    }
}
