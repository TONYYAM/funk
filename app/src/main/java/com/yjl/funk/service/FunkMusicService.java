package com.yjl.funk.service;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.AudioEffect;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.yjl.funk.MyApplication;
import com.yjl.funk.audiocache.CacheListener;
import com.yjl.funk.audiocache.HttpProxyCacheServer;
import com.yjl.funk.network.HttpCallback;
import com.yjl.funk.network.HttpClient;
import com.yjl.funk.network.HttpException;
import com.yjl.funk.player.Constants;
import com.yjl.funk.player.FunkNotificationManager;
import com.yjl.funk.player.MediaSessionManager;
import com.yjl.funk.handler.MusicPlayerHandler;
import com.yjl.funk.model.DownloadInfo;
import com.yjl.funk.model.MusicTrack;
import com.yjl.funk.network.JsonCallback;
import com.yjl.funk.receiver.MediaButtonIntentReceiver;
import com.yjl.funk.utils.FileUtils;
import com.yjl.funk.widget.Toasty;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.request.RequestCall;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Call;

import static com.yjl.funk.MyApplication.getProxy;
import static com.yjl.funk.player.Constants.CMDNAME;
import static com.yjl.funk.player.Constants.CMDNEXT;
import static com.yjl.funk.player.Constants.CMDPAUSE;
import static com.yjl.funk.player.Constants.CMDPLAY;
import static com.yjl.funk.player.Constants.CMDPREVIOUS;
import static com.yjl.funk.player.Constants.CMDSTOP;
import static com.yjl.funk.player.Constants.CMDTOGGLEPAUSE;
import static com.yjl.funk.player.Constants.FADEDOWN;
import static com.yjl.funk.player.Constants.FADEUP;
import static com.yjl.funk.player.Constants.FOCUSCHANGE;
import static com.yjl.funk.player.Constants.IDLE_DELAY;
import static com.yjl.funk.player.Constants.LRC_DOWNLOADED;
import static com.yjl.funk.player.Constants.LRC_ERROR;
import static com.yjl.funk.player.Constants.MUSIC_CHANGED;
import static com.yjl.funk.player.Constants.NEXT_ACTION;
import static com.yjl.funk.player.Constants.PAUSE_ACTION;
import static com.yjl.funk.player.Constants.PLAYSTATE_CHANGED;
import static com.yjl.funk.player.Constants.PREVIOUS_ACTION;
import static com.yjl.funk.player.Constants.REPEAT_ACTION;
import static com.yjl.funk.player.Constants.REPEAT_ALL;
import static com.yjl.funk.player.Constants.REPEAT_CURRENT;
import static com.yjl.funk.player.Constants.SERVICECMD;
import static com.yjl.funk.player.Constants.SHUFFLE;
import static com.yjl.funk.player.Constants.SHUFFLE_ACTION;
import static com.yjl.funk.player.Constants.SHUTDOWN;
import static com.yjl.funk.player.Constants.STOP_ACTION;
import static com.yjl.funk.player.Constants.TOGGLEPAUSE_ACTION;
import static com.yjl.funk.player.Constants.TRACK_ERROR;
import static com.yjl.funk.player.Constants.TRACK_ERROR_INFO;
import static com.yjl.funk.network.HttpClient.BASE_URL;
import static com.yjl.funk.network.HttpClient.METHOD_MUSIC_LINK;
import static com.yjl.funk.network.HttpClient.PARAM_METHOD;
import static com.yjl.funk.network.HttpClient.PARAM_SONG_ID;

/**
 * Created by tonyy on 2017/11/12.
 */

public class FunkMusicService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener,CacheListener {

    private int mPlayMode = REPEAT_ALL;
    private int currentPlayingPosition = -1;
    //播放状态
    private static final int STATE_IDLE = 100;
    private static final int STATE_PREPARING = 101;
    private static final int STATE_PLAYING = 102;
    private static final int STATE_PAUSE = 103;
    //已缓存百分比
    private int cachedPercent = 0;
    //当前播放状态
    private int mCurrentPlayState = STATE_IDLE;
    //当前服务start_id;
    public int mServiceStartId = -1;
    // IPC远程通信binder
    private IBinder mBinder = new ServiceStub(this);
    //播放器
    public MediaPlayer mPlayer = new MediaPlayer();
    //播放列表
    List<MusicTrack> playList = new ArrayList<>();
    //音频管理
    public AudioManager mAudioManager;
    //闹钟管理
    private AlarmManager mAlarmManager;
    //关闭当前ui服务;
    private PendingIntent mShutdownIntent;
    //是否关闭当前服务
    private boolean mShutdownScheduled;
    //mediabutton接受器
    private ComponentName mMediaButtonReceiverComponent;
    //接受指令广播
    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            handleCommandIntent(intent);
        }
    };
    //mediaPlayer消息处理（音频焦点）
    private MusicPlayerHandler mPlayerHandler;
    private MediaSessionManager mMediaSessionManager;
    //歌曲链接请求
    RequestCall linkCall;
    private final AudioManager.OnAudioFocusChangeListener mAudioFocusListener = new AudioManager.OnAudioFocusChangeListener() {

        @Override
        public void onAudioFocusChange(final int focusChange) {
            mPlayerHandler.obtainMessage(FOCUSCHANGE, focusChange, 0).sendToTarget();
        }
    };
    private HandlerThread mHandlerThread;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
//        cancelShutdown();
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        //保存播放历史
        //saveQueue(true);
        stopSelf(mServiceStartId);
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
//        cancelShutdown();

    }

    @Override
    public void onCreate() {
        super.onCreate();

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMediaButtonReceiverComponent = new ComponentName(getPackageName(),
                MediaButtonIntentReceiver.class.getName());
        mAudioManager.registerMediaButtonEventReceiver(mMediaButtonReceiverComponent);
        mMediaSessionManager = new MediaSessionManager(this);
        FunkNotificationManager.getInstance(this);
        registerBroadCast();
        intHandlerAndPlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 关闭任和音频效果
        final Intent audioEffectsIntent = new Intent(AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION);
        audioEffectsIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, getAudioSessionId());
        audioEffectsIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getPackageName());
        sendBroadcast(audioEffectsIntent);
        //关闭音频焦点
        abandonAudioFocus();
        mMediaSessionManager.release();
        FunkNotificationManager.cancelNotification();
        mPlayerHandler.removeCallbacksAndMessages(null);
        unregisterReceiver(mIntentReceiver);
        mHandlerThread.quit();
        MyApplication.getProxy(this).unregisterCacheListener(this);
        mPlayer.release();
        mPlayer = null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mServiceStartId = startId;
        if (intent != null) {
            final String action = intent.getAction();

            if (SHUTDOWN.equals(action)) {
                mShutdownScheduled = false;
                releaseAndStopService();
                return START_NOT_STICKY;
            }

            handleCommandIntent(intent);
        }

        return START_NOT_STICKY;
    }

    /**
     * 获取音频焦点
     **/
    public boolean requestAudioFocus() {
        return mAudioManager.requestAudioFocus(mAudioFocusListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    /**
     * 释放音频焦点
     **/
    public void abandonAudioFocus() {
        mAudioManager.abandonAudioFocus(mAudioFocusListener);
    }

    /**
     * 初始化闹钟管理
     **/
    public void initAlarmManager() {
        final Intent shutdownIntent = new Intent(this, FunkMusicService.class);
        shutdownIntent.setAction(SHUTDOWN);
        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mShutdownIntent = PendingIntent.getService(this, 0, shutdownIntent, 0);
//        scheduleDelayedShutdown();
    }

    /**
     * 延时关闭当前服务
     **/
    private void scheduleDelayedShutdown() {
        mAlarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + IDLE_DELAY, mShutdownIntent);
        mShutdownScheduled = true;
    }

    /**
     * 取消关闭当前服务
     **/
    private void cancelShutdown() {
        if (mShutdownScheduled) {
            mAlarmManager.cancel(mShutdownIntent);
            mShutdownScheduled = false;
        }
    }


    /**
     * 初始化播放器和播放状态消息接收器
     **/
    public void intHandlerAndPlayer() {
        mHandlerThread = new HandlerThread("MusicPlayerHandler",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();
        mPlayerHandler = new MusicPlayerHandler(this, mPlayer, mHandlerThread.getLooper());

    }

    /**
     * 注册接受指令广播
     **/
    public void registerBroadCast() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(SERVICECMD);
        filter.addAction(PAUSE_ACTION);
        filter.addAction(STOP_ACTION);
        filter.addAction(NEXT_ACTION);
        filter.addAction(PREVIOUS_ACTION);
        filter.addAction(REPEAT_ACTION);
        filter.addAction(SHUFFLE_ACTION);
        registerReceiver(mIntentReceiver, filter);
    }


    /**
     * 处理指令
     **/
    private void handleCommandIntent(Intent intent) {
        final String action = intent.getAction();
        final String command = SERVICECMD.equals(action) ? intent.getStringExtra(CMDNAME) : null;
        if (CMDNEXT.equals(command) || NEXT_ACTION.equals(action)) {
            next();
        } else if (CMDPREVIOUS.equals(command) || PREVIOUS_ACTION.equals(action)) {
            prev();
        } else if (CMDTOGGLEPAUSE.equals(command) || TOGGLEPAUSE_ACTION.equals(action)) {
            if (isPlaying()) {
                pause();

            } else {
                start();
            }
        } else if (CMDPAUSE.equals(command) || PAUSE_ACTION.equals(action)) {
            pause();
        } else if (CMDPLAY.equals(command)) {
            start();
        } else if (CMDSTOP.equals(command) || STOP_ACTION.equals(action)) {
            pause();
            seek(0);
            releaseAndStopService();
        } else if (REPEAT_ACTION.equals(action)) {
        } else if (SHUFFLE_ACTION.equals(action)) {
        }
    }

    /**
     * 释放并停止服务
     **/
    public void releaseAndStopService() {


        mAudioManager.abandonAudioFocus(mAudioFocusListener);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)

            stopSelf(mServiceStartId);
    }

    /**
     * 播放器状态
     **/
    //正在播放
    public boolean isPlaying() {
        return mCurrentPlayState == STATE_PLAYING;
    }

    //暂停
    public boolean isPausing() {
        return mCurrentPlayState == STATE_PAUSE;
    }

    //准备
    public boolean isPreparing() {
        return mCurrentPlayState == STATE_PREPARING;
    }

    //闲置
    public boolean isIDLE() {
        return mCurrentPlayState == STATE_IDLE;
    }

    /**
     * 发送歌曲错误消息
     **/
    public void sendErrorMessage(final String errorInfo) {
        final Intent i = new Intent(TRACK_ERROR);
        i.putExtra(TRACK_ERROR_INFO, errorInfo);
        sendBroadcast(i);
    }


    /**
     * 设置播放列表
     **/
    public void setPlayListAndPlayAt(List<MusicTrack> list, int position) {
        synchronized (this) {
            if (list == null || list.isEmpty())
                return;
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
            }
            playList.clear();
            playList.addAll(list);
            play(position);
        }

    }

    /**
     * 播放指定位置歌曲
     **/
    public void play(int position) {
        cachedPercent = 0;
        MyApplication.getProxy(this).unregisterCacheListener(this);
        synchronized (this) {
            mPlayer.stop();
            mPlayer.setOnCompletionListener(null);
            mPlayer.reset();
            currentPlayingPosition = position;
            //获取播放链接信息并播放
            MusicTrack track = getCurrentMusicTrack();
            //如果之前已获取直接播放
            if (!TextUtils.isEmpty(track.getPath())) {
                play(track.getPath());
                downLoadLrc(track);
            } else {
                loadDataAndStart();
            }
            notifyChange(MUSIC_CHANGED);
        }
    }

    /**
     * 播放音乐链接
     */
    public void play(String path) {
        HttpProxyCacheServer proxy = getProxy(this);
        String proxyUrl = proxy.getProxyUrl(path,getCurrentMusicTrack());
        proxy.registerCacheListener(this,path);
        synchronized (this) {
            try {

                mPlayer.setDataSource(proxyUrl);
                mPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            mPlayer.setOnPreparedListener(mPreparedListener);
            mCurrentPlayState = STATE_PREPARING;
            mMediaSessionManager.updateMetaData(getCurrentMusicTrack());
            mMediaSessionManager.updatePlaybackState();
            mPlayer.setOnCompletionListener(this);
        }
    }

    private MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            if (isPreparing()) {
                start();
            }
        }
    };
    /**播放歌曲*/
    /**
     * 开始播放
     **/
    public void start() {
        synchronized (this) {
            if (!isPreparing() && !isPausing()) {
                return;
            }
            if (requestAudioFocus()) {
                //打开音效
                final Intent intent = new Intent(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION);
                intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, getAudioSessionId());
                intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getPackageName());
                sendBroadcast(intent);
                //音频管理注册线控接收器
                mAudioManager.registerMediaButtonEventReceiver(new ComponentName(getPackageName(),
                        MediaButtonIntentReceiver.class.getName()));

                //如果播放器已初始化
                mPlayer.start();
                mCurrentPlayState = STATE_PLAYING;
                //移除音量衰减 发送音量递增消息
                mPlayerHandler.removeMessages(FADEDOWN);
                mPlayerHandler.sendEmptyMessage(FADEUP);
                notifyChange(PLAYSTATE_CHANGED);
                mMediaSessionManager.updatePlaybackState();
                FunkNotificationManager.updateNotification(getCurrentMusicTrack());
            }
        }
    }

    /**
     * 暂停
     **/
    public void pause() {
        synchronized (this) {
            if (!isPlaying() || isPreparing())
                return;
            //移除音量递增消息
            mPlayerHandler.removeMessages(FADEUP);
            //关闭player一切音效
            final Intent intent = new Intent(
                    AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION);
            intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, getAudioSessionId());
            intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getPackageName());
            sendBroadcast(intent);
            mPlayer.pause();
            mCurrentPlayState = STATE_PAUSE;
            notifyChange(PLAYSTATE_CHANGED);
            mMediaSessionManager.updatePlaybackState();
            FunkNotificationManager.updateNotification(getCurrentMusicTrack());
        }
    }

    /**
     * 操作下一首
     */
    public void next() {
        synchronized (this) {
            mPlayer.stop();
            if (playList.isEmpty() && currentPlayingPosition < 0)
                return;
            switch (mPlayMode) {
                case SHUFFLE:
                    currentPlayingPosition = new Random().nextInt(playList.size());
                    play(currentPlayingPosition);
                    break;
                default:
                    if (currentPlayingPosition >= playList.size() - 1)
                        currentPlayingPosition = 0;
                    else
                        currentPlayingPosition++;
                    play(currentPlayingPosition);
                    break;
            }
        }
    }

    /**
     * 无操作下一首
     **/
    public void goToNext() {
        synchronized (this) {
            mPlayer.stop();
            if (playList.isEmpty() && currentPlayingPosition < 0)
                return;
            switch (mPlayMode) {
                case SHUFFLE:

                    currentPlayingPosition = new Random().nextInt(playList.size());
                    play(currentPlayingPosition);
                    break;
                case REPEAT_CURRENT:
                    play(currentPlayingPosition);
                    break;
                case REPEAT_ALL:
                    if (currentPlayingPosition >= playList.size() - 1 || currentPlayingPosition < 0)
                        currentPlayingPosition = 0;
                    else
                        currentPlayingPosition++;
                    play(currentPlayingPosition);
                    break;
            }
        }
    }
    /**
     * 获取下一首的位置**/

    /**
     * 前一首
     **/
    public void prev() {
        synchronized (this) {
            mPlayer.stop();
            if (playList.isEmpty() && currentPlayingPosition < 0)
                return;
            if (currentPlayingPosition == 0)
                currentPlayingPosition = playList.size() - 1;
            else
                currentPlayingPosition = currentPlayingPosition - 1;
            play(currentPlayingPosition);
        }
    }

    /**
     * playerSessionId
     **/
    public int getAudioSessionId() {
        synchronized (this) {

            return mPlayer.getAudioSessionId();
        }
    }

    /**
     * 当前播放的歌曲
     **/
    public MusicTrack getCurrentMusicTrack() {
        synchronized (this) {
            return playList.get(currentPlayingPosition);
        }
    }

    //移除某首歌曲
    public void removeMusicTrack(MusicTrack track) {

    }

    /**
     * 暂停或播放
     **/
    public void playOrPause() {
        synchronized (this) {
            if (isPlaying()) {
                pause();
            } else if (isPausing()) {
                start();
            } else if (isPreparing()) {
                stop();
            } else if (isIDLE()) {
                play(currentPlayingPosition);
            }
        }
    }

    /**
     * 获得播放模式
     **/
    public int getPlayMode() {

        synchronized (this) {return mPlayMode;}
    }

    /**
     * 停止
     */
    public void stop() {
        synchronized (this) {
            if (isIDLE()) {
                return;
            }
            pause();
            mCurrentPlayState = STATE_IDLE;
        }
    }

    /**
     * 指定播放位置
     **/
    public void seek(int position) {
            if (position < 0) {
                position = 0;
            } else if (position > mPlayer.getDuration()) {
                position = mPlayer.getDuration();
            }
            mPlayer.seekTo(position);
    }
    /**当前播放时长*/
    public int duration() {

        synchronized (this) {
            return mPlayer.getDuration();
        }
    }

    /**
    * 当前播放列表**/
    public List<MusicTrack> getPlayList(){
        synchronized (this) {
            return playList;
        }
    }
    /**
     * 当前播放位置
     */
    public int getCurrentPlayListPosition() {
        synchronized (this) {
            if (currentPlayingPosition < 0) {
                return 0;
            }
            return currentPlayingPosition;
        }
    }

    /**
     * 当前播放进度
     */
    public int position() {
        synchronized (this) {
            if (isPlaying() || isPausing()) {
                return mPlayer.getCurrentPosition();
            } else {
                return 0;
            }
        }
    }
    /**缓存进度***/
    public int secondPosition(){
        synchronized (this) {
            if(MyApplication.getProxy(this).isCached(getCurrentMusicTrack())){
                return duration();
            }else {
                return cachedPercent*duration();
            }
        }
    }

    public int getPlayListSize() {

        return playList.size();
    }
    public void setPlayMode(int mode){
        this.mPlayMode = mode;
    }

    /**
     * 状态处理
     */
    public void notifyChange(String what) {
        Intent intent = new Intent();
        intent.setAction(what);
        sendStickyBroadcast(intent);
    }

    //获取歌曲链接
    public void loadDataAndStart() {
        final MusicTrack track = getCurrentMusicTrack();
        //防止过快点击 以免消耗资源 取消上次请求
        if (linkCall != null)
            linkCall.cancel();
        linkCall = OkHttpUtils.get().url(BASE_URL)
                .addParams(PARAM_METHOD, METHOD_MUSIC_LINK)
                .addParams(PARAM_SONG_ID, track.getSong_id())
                .build();
        // 如果未下载封面 下载专辑封面
        if ((!TextUtils.isEmpty(track.getPic_big())||!TextUtils.isEmpty(track.getPic_small()))&& !FileUtils.exists(FileUtils.getAlbumDir()+FileUtils.COVER + track.getAlbum_id())) {
            if(TextUtils.isEmpty(track.getPic_big())){
                FileUtils.downloadAlbumCover(track.getPic_small(), FileUtils.COVER + track.getAlbum_id());
            }else {
                FileUtils.downloadAlbumCover(track.getPic_big(), FileUtils.COVER + track.getAlbum_id());
            }
        }

        linkCall.execute(new JsonCallback<DownloadInfo>(DownloadInfo.class) {
            @Override
            public void onResponse(DownloadInfo response, int id) {
                if (response != null && response.getBitrate() != null) {
                    play(response.getBitrate().getFile_link());
                    downLoadLrc(track);
                }
                else {
                    sendErrorMessage("此歌曲无法收听");
                    pause();
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                if (!call.isCanceled()) {
                    sendErrorMessage("无法获取歌曲信息,为您切换下一首");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            goToNext();
                        }
                    },2000);
                }
            }

            @Override
            public void onAfter(int id) {

            }
        });

    }
    public void downLoadLrc(MusicTrack track){
        //下载歌词
        if(!TextUtils.isEmpty(track.getLrclink())) {
            String lrc = track.getSong_id()+FileUtils.LRC;
            if(!FileUtils.exists(lrc))
                HttpClient.downloadFile(track.getLrclink(),FileUtils.getLrcDir(),lrc, new HttpCallback<File>() {
                    @Override
                    public void onSuccess(File file) {
                        notifyChange(LRC_DOWNLOADED);
                    }

                    @Override
                    public void onFail(HttpException e) {
                        notifyChange(LRC_ERROR);
                    }

                    @Override
                    public void onFinish() {

                    }
                });
            else
                notifyChange(LRC_DOWNLOADED);
        }else {
            notifyChange(LRC_ERROR);
        }
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
        if(mp!=null) {
            seek(0);
            goToNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                mPlayer.release();
                mPlayer = new MediaPlayer();
                sendErrorMessage("播放失败,为您切换下一首");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        goToNext();
                    }
                }, 2000);
                break;
            default:
                break;
        }
        return false;
    }
    //歌曲缓存进度监听
    @Override
    public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
        cachedPercent = percentsAvailable;
    }
}
