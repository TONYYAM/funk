package com.yjl.funk;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.yjl.funk.audiocache.HttpProxyCacheServer;
import com.yjl.funk.imageloader.frescohelper.FrescoHelper;
import com.yjl.funk.imageloader.frescohelper.MyBitmapMemoryCacheParamsSupplier;
import com.yjl.funk.network.HttpInterceptor;
import com.yjl.funk.utils.FileUtils;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;


/**
 * project:KDB—seller
 * Created  on 2017/5/2 0002.
 * author :yanjinlong
 */

public class MyApplication extends Application {

    private static MyApplication instance;
    private HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy(Context context) {
      MyApplication app = (MyApplication) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                .maxCacheFilesCount(10)
                .cacheDirectory(FileUtils.getCacheDirFile())
                .build();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (instance == null) {
            synchronized (MyApplication.class) {
                if (instance == null) {
                    instance= this;
                }
            }
        }
        initOkHttpUtils();
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setDownsampleEnabled(true)
                .setBitmapMemoryCacheParamsSupplier(new MyBitmapMemoryCacheParamsSupplier((ActivityManager)getSystemService(Context.ACTIVITY_SERVICE)))
                .build();
        Fresco.initialize(this,config);

    }
    public static MyApplication getInstance() {
        return instance;
    }
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        FrescoHelper.trimMemory(level);
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        FrescoHelper.clearMemory();
    }
    private void initOkHttpUtils() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(new HttpInterceptor())
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }
}
