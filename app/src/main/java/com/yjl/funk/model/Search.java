package com.yjl.funk.model;

/**
 * Created by Administrator on 2017/11/10.
 */

public class Search {
    private OnlineSongList song_info;
    private AlbumList album_info;
    private ArtistList artist_info;

    public OnlineSongList getSong_info() {
        return song_info;
    }

    public void setSong_info(OnlineSongList song_info) {
        this.song_info = song_info;
    }

    public AlbumList getAlbum_info() {
        return album_info;
    }

    public void setAlbum_info(AlbumList album_info) {
        this.album_info = album_info;
    }

    public ArtistList getArtist_info() {
        return artist_info;
    }

    public void setArtist_info(ArtistList artist_info) {
        this.artist_info = artist_info;
    }
}
