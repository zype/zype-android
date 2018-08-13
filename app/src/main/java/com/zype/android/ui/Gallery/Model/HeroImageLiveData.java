package com.zype.android.ui.Gallery.Model;

import android.arch.lifecycle.LiveData;

import com.squareup.otto.Subscribe;
import com.zype.android.utils.Logger;
import com.zype.android.webapi.WebApiManager;
import com.zype.android.webapi.events.zobject.ZObjectEvent;
import com.zype.android.webapi.model.zobjects.ZobjectData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evgeny Cherkasov on 18.06.2018
 */
public class HeroImageLiveData extends LiveData<List<HeroImage>> {
    @Override
    protected void onActive() {
        WebApiManager.getInstance().subscribe(this);
    }

    @Override
    protected void onInactive() {
        WebApiManager.getInstance().unsubscribe(this);
    }

    @Subscribe
    public void handleZObject(ZObjectEvent event) {
        Logger.d("handleZObject()");
        List<ZobjectData> data = event.getEventData().getModelData().getResponse();
        List<HeroImage> heroImages = new ArrayList<>();
        for (ZobjectData item : data) {
            HeroImage heroImage = new HeroImage();
            heroImage.playlistId = item.playlistId;
            if (item.getPictures() != null && item.getPictures().size() > 0) {
                heroImage.imageUrl = item.getPictures().get(0).getUrl();
            }
            heroImages.add(heroImage);
        }
        setValue(heroImages);
    }

}
