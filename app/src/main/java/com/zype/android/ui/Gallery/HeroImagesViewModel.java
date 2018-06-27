package com.zype.android.ui.Gallery;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.zype.android.ui.Gallery.Model.HeroImageLiveData;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.builder.ZObjectParamsBuilder;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Evgeny Cherkasov on 18.06.2018
 */
public class HeroImagesViewModel extends ViewModel {
    private HeroImageLiveData data;
    private MutableLiveData<Integer> currentPage;
    private Timer timer;
    private TimerTask timerTask;
    private long TIMER_PERIOD = 7000;

    public HeroImageLiveData getHeroImages() {
        if (data == null) {
            data = new HeroImageLiveData();
            loadHeroImages();
        }
        return data;
    }

    public LiveData<Integer> startTimer(int startPage) {
        currentPage = new MutableLiveData<>();
        currentPage.setValue(startPage);

        if (timer == null) {
            timer = new Timer();
        }
        else {
            timer.cancel();
            timer.purge();
        }
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (currentPage.getValue() == data.getValue().size() - 1) {
                    currentPage.postValue(0);
                }
                else {
                    currentPage.postValue(currentPage.getValue() + 1);
                }
            }
        };
        timer.schedule(timerTask, 0, TIMER_PERIOD);

        return currentPage;
    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void loadHeroImages() {
        ZObjectParamsBuilder builder = new ZObjectParamsBuilder()
                .addType(ZObjectParamsBuilder.TYPE_TOP_PLAYLISTS);
        WebApiManager.getInstance().executeRequest(WebApiManager.Request.Z_OBJECT, builder.build());
    }
}
