package com.yjl.funk.imageloader.frescohelper.listener;

import android.graphics.Bitmap;

public interface LoadFrescoListener {
    void onSuccess(Bitmap bitmap);

    void onFail();
}