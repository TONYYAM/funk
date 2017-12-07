package com.yjl.funk.model;

import java.util.List;

/**
 * Created by Administrator on 2017/11/8.
 */

public class AlbumInfo {
    private Album albumInfo;
    private List<MusicTrack> songlist;

    public Album getAlbumInfo() {
        return albumInfo;
    }

    public void setAlbumInfo(Album albumInfo) {
        this.albumInfo = albumInfo;
    }

    public List<MusicTrack> getSonglist() {
        return songlist;
    }

    public void setSonglist(List<MusicTrack> songlist) {
        this.songlist = songlist;
    }
}
