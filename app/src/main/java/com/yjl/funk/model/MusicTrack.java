package com.yjl.funk.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * Created by Administrator on 2017/11/6.
 */
public class MusicTrack implements Parcelable {
    private String song_id;
    private String title;
    private String ting_uid;
    private String author;
    private String album_id;
    private String album_title;
    private String pic_big;
    private String pic_small;
    private String lrclink;
    private String path;

    public MusicTrack(String song_id, String title, String ting_uid, String author, String album_id, String album_title, String pic_big, String pic_small, String lrclink, String path) {
        this.song_id = song_id;
        this.title = title;
        this.ting_uid = ting_uid;
        this.author = author;
        this.album_id = album_id;
        this.album_title = album_title;
        this.pic_big = pic_big;
        this.pic_small = pic_small;
        this.lrclink = lrclink;
        this.path = path;
    }

    public MusicTrack(){}
    public MusicTrack(Parcel in) {
        song_id = in.readString();
        title = in.readString();
        ting_uid = in.readString();
        author = in.readString();
        album_id = in.readString();
        album_title = in.readString();
        pic_big = in.readString();
        pic_small = in.readString();
        lrclink = in.readString();
        path = in.readString();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(song_id);
        dest.writeString(title);
        dest.writeString(ting_uid);
        dest.writeString(author);
        dest.writeString(album_id);
        dest.writeString(album_title);
        dest.writeString(pic_big);
        dest.writeString(pic_small);
        dest.writeString(lrclink);
        dest.writeString(path);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public String getSong_id() {
        return song_id;
    }

    public void setSong_id(String song_id) {
        this.song_id = song_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTing_uid() {
        return ting_uid;
    }

    public void setTing_uid(String ting_uid) {
        this.ting_uid = ting_uid;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(String album_id) {
        this.album_id = album_id;
    }

    public String getAlbum_title() {
        return album_title;
    }

    public void setAlbum_title(String album_title) {
        this.album_title = album_title;
    }

    public String getPic_big() {
        return pic_big;
    }

    public void setPic_big(String pic_big) {
        this.pic_big = pic_big;
    }

    public String getPic_small() {
        return pic_small;
    }

    public void setPic_small(String pic_small) {
        this.pic_small = pic_small;
    }

    public String getLrclink() {
        return lrclink;
    }

    public void setLrclink(String lrclink) {
        this.lrclink = lrclink;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public static final Parcelable.Creator<MusicTrack> CREATOR = new Parcelable.Creator<MusicTrack>() {
        @Override
        public MusicTrack createFromParcel(Parcel source) {
            return new MusicTrack(source);
        }

        @Override
        public MusicTrack[] newArray(int size) {
            return new MusicTrack[size];
        }
    };

}
