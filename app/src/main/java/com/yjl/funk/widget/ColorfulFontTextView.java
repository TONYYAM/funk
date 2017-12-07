package com.yjl.funk.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;

import com.yjl.funk.R;

public class ColorfulFontTextView extends AppCompatTextView {

    int TextViewWidth;                      //TextView的宽度
    private LinearGradient mLinearGradient;     //渲染器
    private Paint paint;
    private Rect mTextBound = new Rect();
    private boolean is_show = false;

    public ColorfulFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onDraw(Canvas canvas) {
      super.onDraw(canvas);
        if (is_show) {
            TextViewWidth = getMeasuredWidth();
            paint = getPaint();
            String mTipText = getText().toString();
            paint.getTextBounds(mTipText, 0, mTipText.length(), mTextBound);
            //初始化线性渲染器
            mLinearGradient = new LinearGradient(0, 0, TextViewWidth, 0,
                    new int[]{ContextCompat.getColor(getContext(), R.color.colorAccent), ContextCompat.getColor(getContext(), R.color.colorPrimary)}, new float[]
                    {0.3f, 0.6f}, Shader.TileMode.REPEAT);
            paint.setShader(mLinearGradient);
            canvas.drawText(mTipText,0, getMeasuredHeight() / 2 + mTextBound.height() / 2, paint);
        }
    }

    public void setIsShow(boolean is_show) {
        this.is_show = is_show;
    }
}