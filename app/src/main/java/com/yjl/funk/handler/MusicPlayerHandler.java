package com.yjl.funk.handler;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.yjl.funk.service.FunkMusicService;

import java.lang.ref.WeakReference;

import static com.yjl.funk.player.Constants.FADEDOWN;
import static com.yjl.funk.player.Constants.FADEUP;
import static com.yjl.funk.player.Constants.FOCUSCHANGE;

/**
 * MediaPLayer 控制Handler
 **/
public class MusicPlayerHandler extends Handler {
    private final WeakReference<FunkMusicService> mService;
    private float mCurrentVolume = 1.0f;
    private MediaPlayer mPlayer;

    public MusicPlayerHandler(final FunkMusicService service, MediaPlayer player, final Looper looper) {
        super(looper);
        mService = new WeakReference<FunkMusicService>(service);
        this.mPlayer = player;
    }


    @Override
    public void handleMessage(final Message msg) {
        final FunkMusicService service = mService.get();
        if (service == null) {
            return;
        }
        synchronized (service) {
            switch (msg.what) {
                case FADEDOWN:
                    mCurrentVolume -= .05f;
                    if (mCurrentVolume > .2f) {
                        sendEmptyMessageDelayed(FADEDOWN, 10);
                    } else {
                        mCurrentVolume = .2f;
                    }
                    mPlayer.setVolume(mCurrentVolume,mCurrentVolume);
                    break;
                case FADEUP:
                    mCurrentVolume += .01f;
                    if (mCurrentVolume < 1.0f) {
                        sendEmptyMessageDelayed(FADEUP, 10);
                    } else {
                        mCurrentVolume = 1.0f;
                    }
                    mPlayer.setVolume(mCurrentVolume,mCurrentVolume);
                    break;
                case FOCUSCHANGE:
                    switch (msg.arg1) {
                        case AudioManager.AUDIOFOCUS_LOSS:
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                            service.pause();
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                            removeMessages(FADEUP);
                            sendEmptyMessage(FADEDOWN);
                            break;
                        case AudioManager.AUDIOFOCUS_GAIN:
                            if (!service.isPlaying()) {
                                mCurrentVolume = 0f;
                                service.mPlayer.setVolume(mCurrentVolume,mCurrentVolume);
                                service.start();
                            } else {
                                removeMessages(FADEDOWN);
                                sendEmptyMessage(FADEUP);
                            }
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    }
}