package com.yjl.funk.player;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.yjl.funk.model.MusicTrack;
import com.yjl.funk.service.FunkMusicService;
import com.yjl.funk.utils.FileUtils;


public class MediaSessionManager {
    private static final String TAG = "MediaSessionManager";
    private static final long MEDIA_SESSION_ACTIONS = PlaybackStateCompat.ACTION_PLAY
            | PlaybackStateCompat.ACTION_PAUSE
            | PlaybackStateCompat.ACTION_PLAY_PAUSE
            | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
            | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
            | PlaybackStateCompat.ACTION_STOP
            | PlaybackStateCompat.ACTION_SEEK_TO;

    private FunkMusicService mPlayService;
    private MediaSessionCompat mMediaSession;

    public MediaSessionManager(FunkMusicService playService) {
        mPlayService = playService;
        setupMediaSession();
    }

    private void setupMediaSession() {
        mMediaSession = new MediaSessionCompat(mPlayService, TAG);
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS | MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS);
        mMediaSession.setCallback(callback);
        mMediaSession.setActive(true);
    }

    public void updatePlaybackState() {
        int state = (mPlayService.isPlaying() || mPlayService.isPreparing()) ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED;
        mMediaSession.setPlaybackState(
                new PlaybackStateCompat.Builder()
                        .setActions(MEDIA_SESSION_ACTIONS)
                        .setState(state, mPlayService.getCurrentPlayListPosition(), 1)
                        .build());
    }

    public void updateMetaData(final MusicTrack track) {
        if (track == null) {
            mMediaSession.setMetadata(null);
            return;
        }

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Bitmap cover = BitmapFactory.decodeFile(FileUtils.getAlbumDir()+FileUtils.COVER+track.getAlbum_id());
                MediaMetadataCompat.Builder metaData = new MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, track.getTitle())
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, track.getAuthor())
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, track.getAlbum_title())
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, track.getAuthor())
                        .putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER,mPlayService.getCurrentPlayListPosition()+1)
                        .putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS,mPlayService.getPlayListSize())
                        .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, cover)
                        ;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    metaData.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS,mPlayService.getPlayListSize());
                }

                mMediaSession.setMetadata(metaData.build());
            }
        });

    }

    public void release() {
        mMediaSession.setCallback(null);
        mMediaSession.setActive(false);
        mMediaSession.release();
    }


      MediaSessionCompat.Callback callback = new MediaSessionCompat.Callback() {
            @Override
            public void onPause() {
                mPlayService.pause();

            }

            @Override
            public void onPlay() {
                mPlayService.start();
            }

            @Override
            public void onSeekTo(long pos) {
                mPlayService.seek((int) pos);
            }

            @Override
            public void onSkipToNext() {
                mPlayService.goToNext();
            }

            @Override
            public void onSkipToPrevious() {
                mPlayService.prev();
            }

            @Override
            public void onStop() {
                mPlayService.pause();
                mPlayService.seek(0);

            }
        };

    }

