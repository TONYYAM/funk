package com.yjl.funk.utils;

import android.content.Context;
import android.os.Environment;


import com.yjl.funk.network.HttpCallback;
import com.yjl.funk.network.HttpClient;
import com.yjl.funk.network.HttpException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {
    public static final String MP3 = ".mp3";
    public static final String LRC = ".lrc";
    public static final String COVER = "cover_";
    public static String getAppDir() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/FunkMusic";
    }

    public static String getMusicDir() {
        String dir = getAppDir() + "/music/";
        return mkdirs(dir);
    }
    public static File getCacheDirFile() {
        String dir = getAppDir() + "/cache/";
        return new File(dir);
    }
    public static String getLrcDir() {
        String dir = getAppDir() + "/lyric/";
        return mkdirs(dir);
    }

    public static String getAlbumDir() {
        String dir = getAppDir() + "/album/";
        return mkdirs(dir);
    }


    private static String mkdirs(String dir) {
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        return dir;
    }
    public static boolean exists(String path) {
        File file = new File(path);
        return file.exists();
    }
    //下载专辑封面
    public static void downloadAlbumCover(String picUrl, String fileName) {
        HttpClient.downloadFile(picUrl, FileUtils.getAlbumDir(), fileName, new HttpCallback<File>() {
            @Override
            public void onSuccess(File file) {
            }

            @Override
            public void onFail(HttpException e) {
            }

            @Override
            public void onFinish() {

            }
        });
    }
    public static void saveLrcFile(String path, String content) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(path));
            bw.write(content);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}