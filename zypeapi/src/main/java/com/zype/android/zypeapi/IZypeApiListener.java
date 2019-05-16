package com.zype.android.zypeapi;

/**
 * Created by Evgeny Cherkasov on 11.02.2019.
 */
public interface IZypeApiListener<T> {
    void onCompleted(ZypeApiResponse<T> response);
}
