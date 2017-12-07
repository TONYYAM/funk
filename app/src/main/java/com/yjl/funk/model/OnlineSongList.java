package com.yjl.funk.model;

import java.util.List;

/**
 * Created by Administrator on 2017/11/6.
 */

public class OnlineSongList {
    private List<MusicTrack> song_list;
    private Billboard billboard;

    public List<MusicTrack> getSong_list() {
        return song_list;
    }

    public void setSong_list(List<MusicTrack> song_list) {
        this.song_list = song_list;
    }

    public Billboard getBillboard() {
        return billboard;
    }
    public void setBillboard(Billboard billboard) {
        this.billboard = billboard;
    }
}
