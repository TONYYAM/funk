// IFunkService.aidl
package com.yjl.funk;

import com.yjl.funk.model.MusicTrack;

interface IFunkService {
    void play(int position);
    void setPlayListAndPlay(in List<MusicTrack> track,int position);
    MusicTrack getCurrentTrack();
    List<MusicTrack> getPlayList();
    void setPlayMode(int mode);
    int getPlayMode();
    int getNowPlayingPosition();
    boolean isPlaying();
    boolean isPreparing();
    boolean isPausing();
    boolean isIdle();
    int duration();
    int position();
    int secondPosition();
    void playOrPause();
    void next();
    void prev();
    void seekTo(int progress);

}
