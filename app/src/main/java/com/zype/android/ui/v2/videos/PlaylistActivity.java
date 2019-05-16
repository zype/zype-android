package com.zype.android.ui.v2.videos;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.zype.android.DataRepository;
import com.zype.android.Db.Entity.Playlist;
import com.zype.android.R;
import com.zype.android.utils.BundleConstants;

/**
 * Created by Evgeny Cherkasov on 12.02.2019
 */
public class PlaylistActivity extends AppCompatActivity {
    private static final String TAG = PlaylistActivity.class.getSimpleName();

    private String playlistId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_v2);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent() != null) {
            playlistId = getIntent().getStringExtra(BundleConstants.PLAYLIST_ID);
        }
        else {
            throw new IllegalStateException("Playlist Id can not be empty");
        }

        Fragment fragment = VideosFragment.newInstance(playlistId);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameContent, fragment).commit();

        updateTitle();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateTitle() {
        Playlist playlist = DataRepository.getInstance(getApplication()).getPlaylistSync(playlistId);
        if (playlist != null) {
            getSupportActionBar().setTitle(playlist.title);
        }
    }

}
