package com.zype.android.ui.video_details.fragments.options;

import com.zype.android.R;
import com.zype.android.core.provider.Contract;
import com.zype.android.core.provider.CursorHelper;
import com.zype.android.core.provider.DataHelper;
import com.zype.android.service.DownloadConstants;
import com.zype.android.service.DownloadHelper;
import com.zype.android.service.DownloaderService;
import com.zype.android.ui.base.BaseFragment;
import com.zype.android.ui.base.BaseVideoActivity;
import com.zype.android.ui.dialog.CustomAlertDialog;
import com.zype.android.ui.dialog.LatestMenuDialogFragment;
import com.zype.android.ui.video_details.fragments.OnDetailActivityFragmentListener;
import com.zype.android.ui.main.fragments.videos.VideosMenuItem;
import com.zype.android.ui.player.PlayerFragment;
import com.zype.android.utils.BundleConstants;
import com.zype.android.utils.FileUtils;
import com.zype.android.utils.Logger;
import com.zype.android.utils.UiUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

public class OptionsFragment extends BaseFragment implements OptionsAdapter.OptionClickListener {

    private static final String ARG_VIDEO_ID = "video_id";
    private String videoId;
    private Context mContext;
    private OptionsAdapter mAdapter;
    private android.support.v7.widget.RecyclerView mOptionList;

    private boolean isFavorite;
    private boolean isAudioDownloading;
    private boolean isVideoDownloading;
    private boolean isVideoDownloaded;
    private boolean isAudioDownloaded;

    private boolean playAsVideo = true;

    private OnDetailActivityFragmentListener mListener;

    private BroadcastReceiver downloaderReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int action = intent.getIntExtra(DownloadConstants.ACTION_TYPE, 0);
            String fileId = intent.getStringExtra(DownloadConstants.EXTRA_FILE_ID);
            if (TextUtils.equals(videoId, fileId)) {
                String errorMessage;
                int progress;
                switch (action) {
                    case DownloadConstants.PROGRESS_CANCELED_AUDIO:
                        isAudioDownloaded = false;
                        isAudioDownloading = false;
                        UiUtils.showWarningSnackbar(getView(), "Audio download has canceled");
                        hideProgress();
                        break;
                    case DownloadConstants.PROGRESS_CANCELED_VIDEO:
                        isVideoDownloaded = false;
                        isVideoDownloading = false;
                        UiUtils.showWarningSnackbar(getView(), "Video Download has canceled");
                        hideProgress();
                        break;
                    case DownloadConstants.PROGRESS_END_AUDIO:
                        isAudioDownloaded = true;
                        isAudioDownloading = false;
                        UiUtils.showPositiveSnackbar(getView(), "Audio was downloaded");
                        hideProgress();
                        break;
                    case DownloadConstants.PROGRESS_END_VIDEO:
                        isVideoDownloaded = true;
                        isVideoDownloading = false;
                        UiUtils.showPositiveSnackbar(getView(), "Video was downloaded");
                        hideProgress();
                        break;
                    case DownloadConstants.PROGRESS_FAIL_AUDIO:
                        isAudioDownloaded = false;
                        isAudioDownloading = false;
                        errorMessage = intent.getStringExtra(BundleConstants.PROGRESS_ERROR_MESSAGE);
                        UiUtils.showErrorSnackbar(getView(), errorMessage);
                        hideProgress();
                        break;
                    case DownloadConstants.PROGRESS_FAIL_VIDEO:
                        isVideoDownloaded = false;
                        isVideoDownloading = false;
                        errorMessage = intent.getStringExtra(BundleConstants.PROGRESS_ERROR_MESSAGE);
                        UiUtils.showErrorSnackbar(getView(), errorMessage);
                        hideProgress();
                        break;
                    case DownloadConstants.PROGRESS_START_AUDIO:
                        isAudioDownloaded = false;
                        isAudioDownloading = true;
                        UiUtils.showPositiveSnackbar(getView(), "Audio downloading has started");
                        updateListView(0);
                        break;
                    case DownloadConstants.PROGRESS_START_VIDEO:
                        isVideoDownloaded = false;
                        isVideoDownloading = true;
                        UiUtils.showPositiveSnackbar(getView(), "Video downloading was started");
                        updateListView(0);
                        break;
                    case DownloadConstants.PROGRESS_UPDATE_AUDIO:
                        isAudioDownloaded = false;
                        isAudioDownloading = true;
                        progress = intent.getIntExtra(BundleConstants.PROGRESS, 0);
                        updateListView(progress);
                        break;
                    case DownloadConstants.PROGRESS_UPDATE_VIDEO:
                        isVideoDownloaded = false;
                        isVideoDownloading = true;
                        progress = intent.getIntExtra(BundleConstants.PROGRESS, 0);
                        updateListView(progress);
                        break;
                    case DownloadConstants.PROGRESS_FREE_SPACE:
                        int error = intent.getIntExtra(BundleConstants.PROGRESS_ERROR_MESSAGE, -1);
                        if (error == -1) {
                            error = R.string.alert_dialog_message_free_space;
                        }
                        isVideoDownloaded = false;
                        isAudioDownloaded = false;
                        isVideoDownloading = false;
                        isAudioDownloading = false;
                        DialogFragment newFragment = CustomAlertDialog.newInstance(
                                R.string.alert_dialog_title_free_space, error);
                        newFragment.show(getActivity().getSupportFragmentManager(), "dialog_free_space");
                        hideProgress();
                        break;
                    default:
                        throw new IllegalStateException("unknown action=" + action);
                }
            }
        }
    };

    public OptionsFragment() {
    }

    public static OptionsFragment newInstance(String videoId) {
        OptionsFragment fragment = new OptionsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_VIDEO_ID, videoId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        if (getArguments() != null && getArguments().containsKey(ARG_VIDEO_ID)) {
            videoId = getArguments().getString(ARG_VIDEO_ID);
            Cursor cursor = CursorHelper.getVideoCursor(getActivity().getContentResolver(), videoId);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    if (cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_IS_DOWNLOADED_VIDEO)) == 1) {
                    } else if (cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_IS_DOWNLOADED_AUDIO)) == 1) {
                        playAsVideo = false;
                    } else {
                        playAsVideo = true;
                    }
                } else {
                    throw new IllegalStateException("DB does not contains video with VideoId=" + videoId);
                }
                cursor.close();
            } else {
                throw new IllegalStateException("DB does not contains video with VideoId=" + videoId);
            }
        } else {
            throw new IllegalStateException("VideoId can not be empty");
        }
    }

    private void updateOptionList() {
        Cursor cursor = CursorHelper.getVideoCursor(getActivity().getContentResolver(), videoId);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                isFavorite = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_IS_FAVORITE)) == 1;
                isAudioDownloaded = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_IS_DOWNLOADED_AUDIO)) == 1;
                isVideoDownloaded = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Video.COLUMN_IS_DOWNLOADED_VIDEO)) == 1;
            } else {
                throw new IllegalStateException("DB does not contains video with VideoId=" + videoId);
            }
            cursor.close();
        } else {
            throw new IllegalStateException("DB does not contains video with VideoId=" + videoId);
        }
        setPlayAsVariable();
        mAdapter = new OptionsAdapter(getOptionsList(), videoId, this);
        mOptionList.setAdapter(mAdapter);
        mOptionList.setLayoutManager(new LinearLayoutManager(getContext()));
        mOptionList.post(new Runnable() {
            @Override
            public void run() {
                int currentProgress = DownloaderService.currentProgress(videoId);
                if (currentProgress > -1) {
                    updateListView(currentProgress);
                    isVideoDownloading = true;
                }
            }
        });

    }

    private void setPlayAsVariable() {
        switch (mListener.getCurrentFragment()) {
            case PlayerFragment.TYPE_VIDEO_LOCAL:
            case PlayerFragment.TYPE_VIDEO_WEB:
                playAsVideo = true;
                break;
            case PlayerFragment.TYPE_AUDIO_WEB:
            case PlayerFragment.TYPE_AUDIO_LOCAL:
                playAsVideo = false;
                break;
            case BaseVideoActivity.TYPE_YOUTUBE:
            case BaseVideoActivity.TYPE_WEB:
                playAsVideo = true;
                break;
            default:
                throw new IllegalStateException("unknown fragment type " + mListener.getCurrentFragment());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_options, container, false);
        mOptionList = (RecyclerView) view.findViewById(R.id.list_options);
        return view;
    }

    private List<Options> getOptionsList() {
        List<Options> list = new ArrayList<>(2);
        /*if (mListener.getCurrentFragment() != BaseVideoActivity.TYPE_YOUTUBE) {
            list.add(new Options(0, getString(R.string.option_play_as), playAsVideo));
            list.add(new Options(1, getString(R.string.option_download), R.drawable.icn_downloads));
        }*/
        list.add(new Options(2, getFavoriteTitle(isFavorite), getFavoriteIcon(isFavorite)));
        list.add(new Options(3, getString(R.string.option_share), R.drawable.icn_share));
        if (mListener.getCurrentFragment() != BaseVideoActivity.TYPE_YOUTUBE) {
            list.add(new Options(1, getString(R.string.option_download), R.drawable.icn_downloads));
        }
        return list;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateOptionList();
        IntentFilter filter = new IntentFilter(DownloadConstants.ACTION);
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(downloaderReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).unregisterReceiver(downloaderReceiver);
    }

    @DrawableRes
    private int getFavoriteIcon(boolean isFavorite) {
        if (isFavorite) {
            return R.drawable.icn_favorites;
        } else {
            return R.drawable.icn_addtofavorites;
        }
    }

    @NonNull
    private String getFavoriteTitle(boolean isFavorite) {
        if (isFavorite) {
            return getString(R.string.menu_unfavorite);
        } else {
            return getString(R.string.menu_favorite);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnDetailActivityFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnDetailActivityFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(OptionsAdapter.ViewHolder holder) {
        ArrayList<String> items = new ArrayList<>();
        switch (holder.id) {
            /*case 0:
                items.add(getString(R.string.option_play_as_video));
                items.add(getString(R.string.option_play_as_audio));
                if (((BaseVideoActivity) getActivity()).isChromecastConntected()) {
                    DialogFragment newFragment = CustomAlertDialog.newInstance(
                            R.string.alert_dialog_title_transcoded, R.string.alert_dialog_message_transcoded);
                    newFragment.show(getActivity().getSupportFragmentManager(), "dialog_transcoded");
                } else {
                    AlertDialog.Builder builder = CustomAlertDialog.getListDialog(getString(R.string.option_play_as), items, mContext, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            switch (which) {
                                case 0:
                                    playAsVideo = true;
                                    mListener.onShowVideo();
                                    break;
                                case 1:
                                    playAsVideo = false;
                                    mListener.onShowAudio();
                                    break;
                            }
                            mAdapter.changeList(getOptionsList());
                        }
                    });
                    android.support.v7.app.AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                }
                break;*/
            case 1:
                ArrayList<VideosMenuItem> downloadItems = new ArrayList<>();
                if (isAudioDownloading || isVideoDownloading) {
                    downloadItems.add(new VideosMenuItem(0, R.string.menu_cancel_download));
                } else {
                    if (isAudioDownloaded) {
                        downloadItems.add(new VideosMenuItem(1, R.string.menu_delete_download_audio));
                    } else {
                        String audioUrl = DataHelper.getAudioUrl(getActivity().getContentResolver(), videoId);
                        if (!TextUtils.isEmpty(audioUrl)) {
                            downloadItems.add(new VideosMenuItem(2, R.string.option_download_audio));
                        }
                    }
                    if (isVideoDownloaded) {
                        downloadItems.add(new VideosMenuItem(3, R.string.menu_delete_download_video));
                    } else {
                        String videoUrl = DataHelper.getVideoUrl(getActivity().getContentResolver(), videoId);
                        if (!TextUtils.isEmpty(videoUrl)) {
                            downloadItems.add(new VideosMenuItem(4, R.string.option_download_video));
                        }
                    }
                }
                final LatestMenuDialogFragment fragment = LatestMenuDialogFragment.newInstance(downloadItems);
                fragment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        switch (fragment.getList().get(position).getId()) {
                            case 0:
                                DownloadHelper.stopDownload(mContext.getContentResolver(), videoId);
                                break;
                            case 1:
                                FileUtils.deleteAudioFile(videoId, mContext);
                                DataHelper.setAudioDeleted(mContext.getContentResolver(), videoId);
                                isAudioDownloaded = false;
                                break;
                            case 2:
                                mListener.onDownloadAudio(videoId);
                                break;
                            case 3:
                                FileUtils.deleteVideoFile(videoId, mContext);
                                DataHelper.setVideoDeleted(mContext.getContentResolver(), videoId);
                                isVideoDownloaded = false;
                                break;
                            case 4:
                                mListener.onDownloadVideo(videoId);
                                break;
                        }
                        fragment.dismiss();
                    }
                });
                if (!downloadItems.isEmpty()) {
                    fragment.show(getActivity().getFragmentManager(), "menu");
                } else {
                    Logger.v("Still don't have url to load");
                }
                break;
            case 2:
                isFavorite = !isFavorite;
                if (isFavorite) {
                    mListener.onFavorite(videoId);
                } else {
                    mListener.onUnFavorite(videoId);
                }
                mAdapter.changeList(getOptionsList());
                break;
            case 3:
                mListener.onShareVideo(videoId);
                break;
        }
    }


    public void updateListView(int newProgress) {
        View convertView = mOptionList.getChildAt(2);
        if (convertView != null) {
            ProgressBar bar = (ProgressBar) convertView.findViewById(R.id.progressBar);
            bar.setVisibility(View.VISIBLE);
            bar.setProgress(newProgress);
        }
    }

    private void hideProgress() {
        View convertView = mOptionList.getChildAt(2);
        if (convertView != null) {
            ProgressBar bar = (ProgressBar) convertView.findViewById(R.id.progressBar);
            bar.setVisibility(View.GONE);
        }
    }

    @Override
    protected String getFragmentName() {
        return getString(R.string.fragment_name_options);
    }


}
