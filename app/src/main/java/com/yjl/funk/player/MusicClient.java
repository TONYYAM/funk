package com.yjl.funk.player;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.yjl.funk.IFunkService;
import com.yjl.funk.model.MusicTrack;
import com.yjl.funk.service.FunkMusicService;

import java.util.List;
import java.util.WeakHashMap;

/**
 * Created by Administrator on 2017/11/14.
 */

public class MusicClient {
    private static final WeakHashMap<Context, ServiceBinder> mConnectionMap;
    private static final long[] sEmptyList;
    public static IFunkService mService = null;
    private static ContentValues[] mContentValuesCache = null;

    static {
        mConnectionMap = new WeakHashMap<Context, ServiceBinder>();
        sEmptyList = new long[0];
    }

    public static final ServiceToken bindToService(final Context context,
                                                   final ServiceConnection callback) {

        Activity realActivity = ((Activity) context).getParent();
        if (realActivity == null) {
            realActivity = (Activity) context;
        }
        final ContextWrapper contextWrapper = new ContextWrapper(realActivity);
        contextWrapper.startService(new Intent(contextWrapper, FunkMusicService.class));
        final ServiceBinder binder = new ServiceBinder(callback,
                contextWrapper.getApplicationContext());
        if (contextWrapper.bindService(
                new Intent().setClass(contextWrapper, FunkMusicService.class), binder, 0)) {
            mConnectionMap.put(contextWrapper, binder);
            return new ServiceToken(contextWrapper);
        }
        return null;
    }

    public static void unbindFromService(final ServiceToken token) {
        if (token == null) {
            return;
        }
        final ContextWrapper mContextWrapper = token.mWrappedContext;
        final ServiceBinder mBinder = mConnectionMap.remove(mContextWrapper);
        if (mBinder == null) {
            return;
        }
        mContextWrapper.unbindService(mBinder);
        if (mConnectionMap.isEmpty()) {
            mService = null;
        }
    }
    public static void setPlayListAndPlayAt(List<MusicTrack> tracks,int position){
        if(mService!=null){
            try {
                mService.setPlayListAndPlay(tracks,position);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    public static void play(int position){
        if (mService!=null){
            try {
                mService.play(position);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    /**获取当前正在播放的音乐*/
    public static MusicTrack getCurrentTrack(){
        MusicTrack track = null;
        if (mService!=null){
            try {
                track = mService.getCurrentTrack();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return track;
    }
    /**
     * 是否正在播放*/
    public static boolean isPlaying(){
        boolean isPlaying = false;
        if(mService!=null) {
            try {
                isPlaying = mService.isPlaying();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return isPlaying;
    }
    /**
     * 是否正在准备*/
    public static boolean isPreparing(){
        boolean isPreparing = false;
        if(mService!=null) {
            try {
                isPreparing = mService.isPreparing();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return isPreparing;
    }
    /**
     * 是否正在暂停*/
    public static boolean isPausing(){
        boolean isPausing = false;
        if(mService!=null) {
            try {
                isPausing = mService.isPausing();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return isPausing;
    }
    /**
     * 是否闲置*/
    public static boolean isIdle(){
        boolean isIdle = false;
        if(mService!=null) {
            try {
                isIdle = mService.isIdle();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return isIdle;
    }
    /**
     * 文件时长**/
    public static int getDuration(){
        int duration = 0;
        if(mService!=null) {
            try {
                duration = mService.duration();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return duration;
    }
    /**当前播放进度**/
    public static int getPosition(){
        int position = 0;
        if(mService!=null) {
            try {
                position = mService.position();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return position;
    }
    /**缓存进度**/
    public static int getSecondPosition(){
        int position = 0;
        if(mService!=null) {
            try {
                position = mService.secondPosition();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return position;
    }
    /**暂停或播放**/
    public static void playOrPause(){
        if (mService!=null){
            try {
                mService.playOrPause();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 下一首**/
    public static void next() {
        if(mService!=null){
            try {
                mService.next();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    /**上一首**/
    public static void prev(){
        if(mService!=null){
            try {
                mService.prev();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 当前播放列表**/
    public static List<MusicTrack> getPlayList(){
        if(mService!=null){
            try {
               return mService.getPlayList();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    /***
     * 设置播放模式***/
    public static void setPlayMode(int mode){
        if(mService!=null){
            try {
                mService.setPlayMode(mode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    /***获取当前播放模式**/
    public static int getPlayMode(){
        if(mService!=null){
            try {
                return mService.getPlayMode();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return Constants.REPEAT_ALL;
    }
    /***
     * 当前播放位置**/
    public static int getNowPlayingPosition(){
        if (mService!=null){
            try {
                return mService.getNowPlayingPosition();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
    /**滑动到指定位置**/
    public static void seekTo(int progress){
        if (mService!=null){
            try {
                mService.seekTo(progress);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    public static final class ServiceToken {
        public ContextWrapper mWrappedContext;

        public ServiceToken(final ContextWrapper context) {
            mWrappedContext = context;
        }
    }
    public static final class ServiceBinder implements ServiceConnection {
        private final ServiceConnection mCallback;
        private final Context mContext;


        public ServiceBinder(final ServiceConnection callback, final Context context) {
            mCallback = callback;
            mContext = context;
        }

        @Override
        public void onServiceConnected(final ComponentName className, final IBinder service) {
            mService = IFunkService.Stub.asInterface(service);
            if (mCallback != null) {
                mCallback.onServiceConnected(className, service);
            }
        }

        @Override
        public void onServiceDisconnected(final ComponentName className) {
            if (mCallback != null) {
                mCallback.onServiceDisconnected(className);
            }
            mService = null;
        }
    }
}
