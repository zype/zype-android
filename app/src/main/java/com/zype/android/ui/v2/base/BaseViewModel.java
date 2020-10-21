package com.zype.android.ui.v2.base;

import android.app.Application;

import com.zype.android.DataRepository;
import com.zype.android.zypeapi.ZypeApi;

import androidx.lifecycle.AndroidViewModel;

/**
 * Created by Evgeny Cherkasov on 21.05.2019.
 */
public class BaseViewModel extends AndroidViewModel {
    protected DataRepository repo;
    protected ZypeApi api;

    public BaseViewModel(Application application) {
        super(application);
        repo = DataRepository.getInstance(application);
        api = ZypeApi.getInstance();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }


}
