package com.yjl.funk.player;

/**
 * Created by tonyy on 2017/11/12.
 */

public class Constants {
    public static final String PLAYSTATE_CHANGED = "com.yjl.funk.playstatechanged";
    public static final String MUSIC_CHANGED = "com.yjl.funk.musicchanged";
    public static final String PLAYLIST_CHANGED = "com.yjl.funk.playlistchanged";
    public static final String LRC_ERROR = "com.yjl.funk.lrcerror";
    public static final String LRC_DOWNLOADED = "com.yjl.funk.lrcdownloaded";
    public static final String TRACK_ERROR = "com.yjl.funk.trackerror";
    public static final String SERVICECMD = "com.yjl.funk.musicservicecommand";
    public static final String TOGGLEPAUSE_ACTION = "com.yjl.funk.togglepause";
    public static final String PAUSE_ACTION = "com.yjl.funk.pause";
    public static final String STOP_ACTION = "com.yjl.funk.stop";
    public static final String PREVIOUS_ACTION = "com.yjl.funk.previous";
    public static final String NEXT_ACTION = "com.yjl.funk.next";
    public static final String REPEAT_ACTION = "com.yjl.funk.repeat";
    public static final String SHUFFLE_ACTION = "com.yjl.funk.shuffle";
    public static final String CMDNAME = "command";
    public static final String CMDTOGGLEPAUSE = "togglepause";
    public static final String CMDSTOP = "stop";
    public static final String CMDPAUSE = "pause";
    public static final String CMDPLAY = "play";
    public static final String CMDPREVIOUS = "previous";
    public static final String CMDNEXT = "next";
    public static final String EXTRA_NOTIFICATION = "FROM_FUNK_MUSIC_NOTIFICATION";
    //随机
    public static final int SHUFFLE= 201;
    //单曲循环
    public static final int REPEAT_CURRENT = 202;
    //列表循环
    public static final int REPEAT_ALL = 203;
    public static final String TAG = "MusicPlaybackService";
    //关闭当前服务
    public static final String SHUTDOWN = "com.yjl.funk.shutdown";
    //播放器焦点变化
    public static final int FOCUSCHANGE = 5;
    //音量递减
    public static final int FADEDOWN = 6;
    //音量递增
    public static final int FADEUP = 7;
    //关闭当前服务延时
    public static final int IDLE_DELAY = 5 * 60 * 1000;
    //歌曲名称
    public static final String TRACK_ERROR_INFO = "trackerrorinfo";
}
