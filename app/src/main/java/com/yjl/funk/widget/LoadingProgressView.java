package com.yjl.funk.widget;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AndroidException;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.yjl.funk.R;
import com.yjl.funk.utils.DensityUtils;


/**
 * Created by Administrator on 2017/3/18.
 * loadingview 加载动画view
 */

public class LoadingProgressView extends View {
    private Context context;
    private float mWidth = 0f;
    private int strokeWidth = DensityUtils.dip2px(getContext(),3);
    private Paint mPaint;
    private float mPadding = 0f;
    private int color = Color.argb(100, 255, 255, 255);
    private float mAnimPercent;
    public LoadingProgressView (Context context) {
        this(context,null);
    }
    public LoadingProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context =context;
    }





    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (getMeasuredWidth() > getHeight())
            mWidth = getMeasuredHeight();
        else
            mWidth = getMeasuredWidth();
        mPadding = 5;


    }
    public void setStrokeWidth(int strokeWidth){
        this.strokeWidth = strokeWidth;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        RectF rectF = new RectF(mPadding+0.24f*mWidth, mPadding+0.24f*mWidth, mWidth - mPadding-0.24f*mWidth, mWidth - mPadding-0.24f*mWidth);;
        final int s = (int) mAnimPercent;
        final float mp;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(strokeWidth);
        switch (s) {
            case 0:
                mp = mAnimPercent;
                mPaint.setColor(Color.BLUE);
                canvas.drawArc(rectF, 90 + (360 * mp), 30 + 240 * (1 - mp), false, mPaint);
                break;
            case 1:
                mp = mAnimPercent - 1;
                mPaint.setColor(getResources().getColor(R.color.colorPrimary));
                canvas.drawArc(rectF, 90 + (180 * mp), 30 + 240 * mp, false, mPaint);
                break;
            case 2:
                mp = mAnimPercent - 2;
                mPaint.setColor(getResources().getColor(R.color.colorPrimaryDark));
                canvas.drawArc(rectF, -90 + (360 * mp), 30 + 240 * (1 - mp), false, mPaint);
                break;
            case 3:
                mp = mAnimPercent - 3;
                mPaint.setColor(getResources().getColor(R.color.colorAccent));
                canvas.drawArc(rectF, -90 + (180 * mp), 30 + 240 * mp, false, mPaint);
                break;
        }
    }



    public void startAnim() {
        stopAnim();
      startViewAnim();
    }

    public void stopAnim() {
        if (valueAnimator != null) {
            clearAnimation();
            valueAnimator.setRepeatCount(1);
            valueAnimator.cancel();
            valueAnimator.end();
        }
    }

    ValueAnimator valueAnimator;

    private ValueAnimator startViewAnim() {
        valueAnimator = ValueAnimator.ofFloat(0f,1f,2f,3f,4f);

        valueAnimator.setDuration(2000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);//无限循环
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);//

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {


                mAnimPercent = (Float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        if(!valueAnimator.isRunning())
            valueAnimator.start();
        return valueAnimator;
    }



    }
