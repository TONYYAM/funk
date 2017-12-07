package com.yjl.funk.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yjl.funk.R;

/**
 * Created by Administrator on 2017/11/9.
 */

public class DataEmptyView extends FrameLayout {
    private TextView empty;
    public DataEmptyView(Context context) {
        this(context,null);
    }

    public DataEmptyView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DataEmptyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    private void init(Context context){
        View.inflate(context, R.layout.view_status_empty,this);
        empty = (TextView) findViewById(R.id.tv_status_empty);
    }
    public void setText(String text){
        empty.setText(text);
    }
}
