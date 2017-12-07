package com.yjl.funk.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.yjl.funk.R;

/**
 * Created by Administrator on 2017/11/9.
 */

public class NetWorkErrorView extends FrameLayout {

    public NetWorkErrorView(Context context) {
        this(context,null);
    }

    public NetWorkErrorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public NetWorkErrorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    private void init(Context context){
        View.inflate(context, R.layout.view_status_no_netwotk,this);
    }
}
