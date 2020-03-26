package com.zype.android.ui.Gallery;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.zype.android.DataRepository;
import com.zype.android.Db.Entity.Playlist;
import com.zype.android.R;
import com.zype.android.ui.NavigationHelper;
import com.zype.android.ui.base.BaseActivity;
import com.zype.android.utils.BundleConstants;

import static com.zype.android.utils.BundleConstants.REQUEST_LOGIN;
import static com.zype.android.utils.BundleConstants.REQUEST_USER;

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

    // Actions

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_USER:
            case REQUEST_LOGIN:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            String videoId = extras.getString(BundleConstants.VIDEO_ID);
                            String playlistId = extras.getString(BundleConstants.PLAYLIST_ID);
                            NavigationHelper.getInstance(this)
                                    .switchToVideoDetailsScreen(this, videoId, playlistId, false);
                        }
                    }
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
