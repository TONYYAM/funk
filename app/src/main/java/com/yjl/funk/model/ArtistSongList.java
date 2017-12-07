package com.yjl.funk.model;

import java.util.List;

/**
 * Created by Administrator on 2017/11/9.
 */

public class ArtistSongList {
    List<MusicTrack> songList;

    public List<MusicTrack> getSongList() {
        return songList;
    }

    public void setSongList(List<MusicTrack> songList) {
        this.songList = songList;
    }
}
