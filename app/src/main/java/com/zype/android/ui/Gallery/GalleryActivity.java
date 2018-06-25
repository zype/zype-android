package com.zype.android.ui.Gallery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.zype.android.DataRepository;
import com.zype.android.Db.Entity.Playlist;
import com.zype.android.R;
import com.zype.android.ui.base.BaseActivity;
import com.zype.android.utils.BundleConstants;

public class GalleryActivity extends BaseActivity {
    private static final String TAG = GalleryActivity.class.getSimpleName();

    private String parentPlaylistId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent() != null) {
            parentPlaylistId = getIntent().getStringExtra(BundleConstants.PLAYLIST_ID);
        } else {
            throw new IllegalStateException("Playlist Id can not be empty");
        }

        Fragment fragment = GalleryFragment.newInstance(parentPlaylistId);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameContent, fragment).commit();

        updateTitle();
    }

    protected String getActivityName() {
        return TAG;
    }

    private void updateTitle() {
        Playlist playlist = DataRepository.getInstance(getApplication()).getPlaylistSync(parentPlaylistId);
        if (playlist != null) {
            getSupportActionBar().setTitle(playlist.title);
        }
    }


}
