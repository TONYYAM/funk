package com.yjl.funk.service;

import android.os.RemoteException;

import com.yjl.funk.IFunkService;
import com.yjl.funk.model.MusicTrack;

import java.lang.ref.WeakReference;
import java.util.List;

public class ServiceStub extends IFunkService.Stub {

        private final WeakReference<FunkMusicService> mService;

        public ServiceStub(final FunkMusicService service) {
            mService = new WeakReference<FunkMusicService>(service);
        }


    @Override
    public void play(int position) throws RemoteException {
        mService.get().play(position);
    }

    @Override
    public void setPlayListAndPlay(List<MusicTrack> tracks, int position) throws RemoteException {
        mService.get().setPlayListAndPlayAt(tracks,position);
    }

    @Override
    public MusicTrack getCurrentTrack() throws RemoteException {
        return mService.get().getCurrentMusicTrack();
    }

    @Override
    public List<MusicTrack> getPlayList() throws RemoteException {
        return mService.get().getPlayList();
    }

    @Override
    public void setPlayMode(int mode) throws RemoteException {
        mService.get().setPlayMode(mode);
    }

    @Override
    public int getPlayMode() throws RemoteException {
        return mService.get().getPlayMode();
    }

    @Override
    public int getNowPlayingPosition() throws RemoteException {
        return mService.get().getCurrentPlayListPosition();
    }

    @Override
    public boolean isPlaying() throws RemoteException {
        return mService.get().isPlaying();
    }

    @Override
    public boolean isPreparing() throws RemoteException {
        return mService.get().isPreparing();
    }

    @Override
    public boolean isPausing() throws RemoteException {
        return mService.get().isPausing();
    }

    @Override
    public boolean isIdle() throws RemoteException {
        return mService.get().isIDLE();
    }

    @Override
    public int duration() throws RemoteException {
        return mService.get().duration();
    }

    @Override
    public int position() throws RemoteException {
        return mService.get().position();
    }

    @Override
    public int secondPosition() throws RemoteException {
        return mService.get().secondPosition();
    }

    @Override
    public void playOrPause() throws RemoteException {
        mService.get().playOrPause();
    }

    @Override
    public void next() throws RemoteException {
        mService.get().next();
    }

    @Override
    public void prev() throws RemoteException {
        mService.get().prev();
    }

    @Override
    public void seekTo(int progress) throws RemoteException {
        mService.get().seek(progress);
    }

}

 