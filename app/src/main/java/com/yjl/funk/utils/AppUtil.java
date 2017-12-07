package com.yjl.funk.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewTreeObserver;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.List;

import okhttp3.ResponseBody;

/**
 * 方法工具类
 * Created by WZG on 2016/10/31.
 */

public class AppUtil {
    public static String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath()+"/kdb/";
    //听取数量转化字符串
    public static String numToStr(String num){
        if(TextUtils.isEmpty(num)){
            return "";
        }else {
            Long numlong = Long.valueOf(num);
            if (numlong > 10000) {
                return (new DecimalFormat("#.0").format(numlong / 10000f)) + "万";
            } else {
                return num;
            }
        }
    }
    public static boolean checkSDCardAvailable(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}
