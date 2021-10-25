package com.zype.android.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.media.session.MediaButtonReceiver;

import com.zype.android.Db.Entity.Video;
import com.zype.android.R;
import com.zype.android.ZypeApp;
import com.zype.android.ui.main.MainActivity;
import com.zype.android.ui.video_details.v2.VideoDetailActivity;
import com.zype.android.utils.BundleConstants;
import com.zype.android.utils.Logger;

public class PlayerService extends Service {
    private static final String TAG_FOREGROUND_SERVICE = "FOREGROUND_SERVICE";

    public static final String ACTION_START_FOREGROUND_SERVICE_PLAY = "ACTION_START_FOREGROUND_SERVICE_PLAY";
    public static final String ACTION_START_FOREGROUND_SERVICE_PAUSE = "ACTION_START_FOREGROUND_SERVICE_PAUSE";

    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";

    public static final String VIDEO_TITLE_EXTRA = "VIDEO_TITLE_EXTRA";
    public static final String VIDEO_ID_EXTRA = "VIDEO_ID_EXTRA";
    public static final String MEDIA_SESSION_TOKEN_EXTRA = "MEDIA_SESSION_TOKEN_EXTRA";

    public static final String PLAYER_SERVICE_CHANNEL_ID = "PLAYER_SERVICE_CHANNEL_100";
    public static final String PLAYER_SERVICE_CHANNEL_NAME = "Audio Background Service";



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG_FOREGROUND_SERVICE, "My foreground service onCreate().");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if(action!=null)

                switch (action) {
                    case ACTION_START_FOREGROUND_SERVICE_PLAY: case  ACTION_START_FOREGROUND_SERVICE_PAUSE:
                        startForegroundService(action == ACTION_START_FOREGROUND_SERVICE_PLAY, intent);
                        //Toast.makeText(getApplicationContext(), "Foreground service is started.", Toast.LENGTH_LONG).show();
                        break;
                    case ACTION_STOP_FOREGROUND_SERVICE:
                        stopForegroundService();
                        //Toast.makeText(getApplicationContext(), "Foreground service is stopped.", Toast.LENGTH_LONG).show();
                        break;
                }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /* Used to build and start foreground service. */
    private void startForegroundService(boolean isPlay, Intent serviceIntent){

        String title = serviceIntent.getStringExtra(VIDEO_TITLE_EXTRA);
        String videoId = serviceIntent.getStringExtra(VIDEO_ID_EXTRA);
        MediaSessionCompat.Token mediaSessionToken = serviceIntent.getParcelableExtra(MEDIA_SESSION_TOKEN_EXTRA);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(PLAYER_SERVICE_CHANNEL_ID, PLAYER_SERVICE_CHANNEL_NAME, title, videoId, mediaSessionToken, isPlay );
        } else {

            Intent notificationIntent;
            Bundle bundle = new Bundle();
            bundle.putString(BundleConstants.VIDEO_ID, videoId);
            notificationIntent = new Intent(this, VideoDetailActivity.class);
            notificationIntent.putExtras(bundle);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);

            PendingIntent intent = PendingIntent.getActivity(this, 0,
                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
                    ZypeApp.NOTIFICATION_CHANNEL_ID);
            builder.setContentIntent(intent)
                    .setContentTitle(this.getString(R.string.app_name))
                    .setContentText(title)
                    .setSmallIcon(R.drawable.ic_background_playback)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setAutoCancel(true)
                    .setOngoing(true)
                    .setWhen(0);

            if (isPlay) {
                builder.addAction(new NotificationCompat.Action(R.drawable.ic_pause_black_24dp, "Pause",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                                PlaybackStateCompat.ACTION_PLAY_PAUSE)));
            } else {
                builder.addAction(new NotificationCompat.Action(R.drawable.ic_play_arrow_black_24dp, "Play",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                                PlaybackStateCompat.ACTION_PLAY_PAUSE)));
            }

            builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSessionToken)
                    .setShowCancelButton(true)
                    .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                            PlaybackStateCompat.ACTION_STOP)));


            // Build the notification.
            Notification notification = builder.build();

            // Start foreground service.
            startForeground(ZypeApp.NOTIFICATION_ID, notification);
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel( String channelId, String channelName, String videoTitle, String videoId, MediaSessionCompat.Token mediaSessionToken, Boolean isPlay) {
        Intent resultIntent = new Intent(this, VideoDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(BundleConstants.VIDEO_ID, videoId);
        resultIntent.putExtras(bundle);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
// Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationChannel chan = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);

        chan.enableVibration(false);
        chan.setSound(null, null);

        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
        notificationBuilder
                .setContentIntent(resultPendingIntent)
                .setContentTitle(this.getString(R.string.app_name))
                .setContentText(videoTitle)
                .setSmallIcon(R.drawable.ic_background_playback)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setAutoCancel(true)
                .setOngoing(true)
                .setWhen(0);

        if (isPlay) {
            notificationBuilder.addAction(new NotificationCompat.Action(R.drawable.ic_pause_black_24dp, "Pause",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                            PlaybackStateCompat.ACTION_PLAY_PAUSE)));
        } else {
            notificationBuilder.addAction(new NotificationCompat.Action(R.drawable.ic_play_arrow_black_24dp, "Play",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                            PlaybackStateCompat.ACTION_PLAY_PAUSE)));
        }

        notificationBuilder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(mediaSessionToken)
                .setShowCancelButton(true)
                .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                        PlaybackStateCompat.ACTION_STOP)));

        Notification notification = notificationBuilder.build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(ZypeApp.NOTIFICATION_ID, notificationBuilder.build());


        startForeground(ZypeApp.NOTIFICATION_ID, notification);

    }

    private void stopForegroundService() {
        Log.d(TAG_FOREGROUND_SERVICE, "Stop foreground service.");

        // Stop foreground service and remove the notification.
        stopForeground(true);

        // Stop the foreground service.
        stopSelf();
    }
}
