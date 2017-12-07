package com.yjl.funk.player;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.yjl.funk.R;
import com.yjl.funk.model.MusicTrack;
import com.yjl.funk.service.FunkMusicService;
import com.yjl.funk.ui.activity.MainActivity;
import com.yjl.funk.utils.FileUtils;


public class FunkNotificationManager {
    private static final int NOTIFICATION_ID = 0X123;
    private static FunkMusicService FunkMusicService;
    private static NotificationManager notificationManager;
    private static final int MODE_FOREGROUND = 0x11;
    private static final int MODE_NONE = 0x12;
    private static int mNotifyMode = -1;
    public static void getInstance(FunkMusicService FunkMusicService) {
        FunkNotificationManager.FunkMusicService = FunkMusicService;
        notificationManager = (NotificationManager) FunkMusicService.getSystemService(Context.NOTIFICATION_SERVICE);
    }
    public static void updateNotification(MusicTrack track) {
        final int newNotifyMode;
        if (FunkMusicService.isPlaying()||FunkMusicService.isPreparing()) {
            newNotifyMode = MODE_FOREGROUND;
        }else {
            newNotifyMode = MODE_NONE;
        }

        if (mNotifyMode != newNotifyMode) {
            if (mNotifyMode == MODE_FOREGROUND) {
                FunkMusicService.stopForeground(newNotifyMode == MODE_NONE);
            } else if (newNotifyMode == MODE_NONE) {
                notificationManager.cancel(NOTIFICATION_ID);
            }
        }

        if (newNotifyMode == MODE_FOREGROUND) {
           FunkMusicService.startForeground(NOTIFICATION_ID, buildNotification(FunkMusicService,track,FunkMusicService.isPlaying()));
        }
        mNotifyMode = newNotifyMode;
    }

    public static void cancelNotification() {
        notificationManager.cancelAll();
    }

    private static Notification buildNotification(Context context,MusicTrack music, boolean isPlaying) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constants.EXTRA_NOTIFICATION, true);
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_notification)
                .setCustomContentView(getRemoteViews(context, music, isPlaying));
        return builder.build();
    }

    private static RemoteViews getRemoteViews(Context context, MusicTrack music, boolean isPlaying) {
        String title = music.getTitle();
        String subtitle = music.getAuthor()+" - "+music.getAlbum_title();
        Bitmap cover = BitmapFactory.decodeFile(FileUtils.getAlbumDir()+FileUtils.COVER+music.getAlbum_id());

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_notification);
        if (cover != null) {
            remoteViews.setImageViewBitmap(R.id.iv_icon, cover);
        } else {
            remoteViews.setImageViewResource(R.id.iv_icon, R.drawable.default_load_cover);
        }
        remoteViews.setTextViewText(R.id.tv_title, title);
        remoteViews.setTextViewText(R.id.tv_subtitle, subtitle);
        remoteViews.setImageViewResource(R.id.iv_play_pause,isPlaying?R.drawable.ic_pause :R.drawable.ic_play);
        remoteViews.setOnClickPendingIntent(R.id.iv_play_pause, addPlaybackAction(context,Constants.TOGGLEPAUSE_ACTION));
        remoteViews.setImageViewResource(R.id.iv_next, R.drawable.ic_skip_next);
        remoteViews.setOnClickPendingIntent(R.id.iv_next,addPlaybackAction(context,Constants.NEXT_ACTION));
        remoteViews.setImageViewResource(R.id.iv_prev, R.drawable.ic_skip_prev);
        remoteViews.setOnClickPendingIntent(R.id.iv_prev,addPlaybackAction(context,Constants.PREVIOUS_ACTION));
        return remoteViews;
    }



    public static PendingIntent addPlaybackAction(Context context,final String action) {
         ComponentName serviceName = new ComponentName(context,FunkMusicService.class);
        Intent intent = new Intent(action);
        intent.setComponent(serviceName);
        return PendingIntent.getService(context, 0, intent, 0);
    }



}
