package com.zype.android.ui.base;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import com.zype.android.R;
import com.zype.android.ZypeApp;
import com.zype.android.service.DownloadConstants;
import com.zype.android.ui.dialog.CustomAlertDialog;
import com.zype.android.ui.main.fragments.videos.VideosCursorAdapter;
import com.zype.android.utils.BundleConstants;
import com.zype.android.utils.UiUtils;
import com.zype.android.webapi.WebApiManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment {

    protected boolean onSaveInstanceState;
    private WebApiManager mApi;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApi = WebApiManager.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mApi.subscribe(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public WebApiManager getApi() {
        return mApi;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mApi.unsubscribe(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            final Tracker tracker = ZypeApp.getTracker();
            if (tracker != null) {
                if (getActivity() != null) {
                    tracker.setScreenName(((BaseActivity) getActivity()).getActivityName() + " | " + getFragmentName());
                    tracker.send(new HitBuilders.ScreenViewBuilder().build());
                }
            }
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        onSaveInstanceState = true;
    }

    public void onRestoreInstanceState(Bundle inState) {
        onSaveInstanceState = false;
    }

    protected abstract String getFragmentName();

    protected static void updateListItem(Activity activity, View view, Intent intent, int action, String fileId, VideosCursorAdapter.VideosViewHolder viewHolder) {
        if (TextUtils.equals(fileId, viewHolder.videoId)) {
            String errorMessage;
            int progress;
            switch (action) {
                case DownloadConstants.PROGRESS_CANCELED_AUDIO:
                    viewHolder.isAudioDownloaded = false;
                    UiUtils.showWarningSnackbar(view, "Audio download has canceled");
                    hideProgress(viewHolder);
                    break;
                case DownloadConstants.PROGRESS_CANCELED_VIDEO:
                    viewHolder.isVideoDownloaded = false;
                    UiUtils.showWarningSnackbar(view, "Video Download has canceled");
                    hideProgress(viewHolder);
                    break;
                case DownloadConstants.PROGRESS_END_AUDIO:
                    viewHolder.isAudioDownloaded = true;
                    UiUtils.showPositiveSnackbar(view, "Audio was downloaded");
                    hideProgress(viewHolder);
                    break;
                case DownloadConstants.PROGRESS_END_VIDEO:
                    viewHolder.isVideoDownloaded = true;
                    UiUtils.showPositiveSnackbar(view, "Video was downloaded");
                    hideProgress(viewHolder);
                    break;
                case DownloadConstants.PROGRESS_FAIL_AUDIO:
                    viewHolder.isAudioDownloaded = false;
                    errorMessage = intent.getStringExtra(BundleConstants.PROGRESS_ERROR_MESSAGE);
                    UiUtils.showErrorSnackbar(view, errorMessage);
                    hideProgress(viewHolder);
                    break;
                case DownloadConstants.PROGRESS_FAIL_VIDEO:
                    viewHolder.isVideoDownloaded = false;
                    errorMessage = intent.getStringExtra(BundleConstants.PROGRESS_ERROR_MESSAGE);
                    UiUtils.showErrorSnackbar(view, errorMessage);
                    hideProgress(viewHolder);
                    break;
                case DownloadConstants.PROGRESS_START_AUDIO:
                    viewHolder.isAudioDownloaded = false;
                    UiUtils.showPositiveSnackbar(view, "Audio downloading has started");
                    updateListView(viewHolder, 0);
                    break;
                case DownloadConstants.PROGRESS_START_VIDEO:
                    viewHolder.isVideoDownloaded = false;
                    UiUtils.showPositiveSnackbar(view, "Video downloading was started");
                    updateListView(viewHolder, 0);
                    break;
                case DownloadConstants.PROGRESS_UPDATE_AUDIO:
                    viewHolder.isAudioDownloaded = false;
                    progress = intent.getIntExtra(BundleConstants.PROGRESS, 0);
                    updateListView(viewHolder, progress);
                    break;
                case DownloadConstants.PROGRESS_UPDATE_VIDEO:
                    viewHolder.isVideoDownloaded = false;
                    progress = intent.getIntExtra(BundleConstants.PROGRESS, 0);
                    updateListView(viewHolder, progress);
                    break;
                case DownloadConstants.PROGRESS_FREE_SPACE:
                    viewHolder.isVideoDownloaded = false;
                    viewHolder.isAudioDownloaded = false;
                    hideProgress(viewHolder);
                    if (activity instanceof BaseActivity) {
                        int error = intent.getIntExtra(BundleConstants.PROGRESS_ERROR_MESSAGE, -1);
                        if (error == -1) {
                            error = R.string.alert_dialog_message_free_space;
                        }
                        DialogFragment newFragment = CustomAlertDialog.newInstance(
                                R.string.alert_dialog_title_free_space, error);
                        newFragment.show(((BaseActivity) activity).getSupportFragmentManager(), "dialog_free_space");
                    } else {
                        UiUtils.showWarningSnackbar(view, activity.getString(R.string.alert_dialog_message_free_space));
                    }

                    break;
                default:
                    throw new IllegalStateException("unknown action=" + action);
            }
        }
    }

    private static void updateListView(VideosCursorAdapter.VideosViewHolder viewHolder, int newProgress) {
        viewHolder.progressBar.setVisibility(View.VISIBLE);
        viewHolder.progressBar.setProgress(newProgress);
    }

    private static void hideProgress(VideosCursorAdapter.VideosViewHolder viewHolder) {
        viewHolder.progressBar.setVisibility(View.GONE);
        viewHolder.progressBar.setProgress(0);
    }
}
