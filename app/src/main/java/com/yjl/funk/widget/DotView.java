package com.yjl.funk.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import com.yjl.funk.utils.DensityUtils;


public class DotView extends LinearLayout implements PagerIndicator {

    public interface OnDotClickHandler {
        public void onDotClick(int index);
    }

    private int mLittleDotSize = -2;
    private int mDotSpan = 36;
    private float mDotRadius = 6f;

    private int mCurrent =-1;
    private int mTotal = 0;

    private int mSelectedColor = Color.parseColor("#FFFFFF");
    private int mUnSelectedColor = Color.parseColor("#40FFFFFF");
    private OnDotClickHandler mOnDotClickHandler;

    public DotView(Context context) {
        super(context);
    }

    public DotView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setGravity(Gravity.CENTER_HORIZONTAL);

                mDotRadius = DensityUtils.dip2px(getContext(),3);

                mDotSpan = DensityUtils.dip2px(getContext(),15);

        mLittleDotSize = (int) (mDotSpan / 2 + mDotRadius * 2);
    }

    @Override
    public final void setNum(int num) {
        if (num < 0)
            return;

        mTotal = num;

        removeAllViews();
        setOrientation(HORIZONTAL);
        for (int i = 0; i < num; i++) {
            LittleDot dot = new LittleDot(getContext(), i);
            dot.setColor(mUnSelectedColor);
            dot.setLayoutParams(new LayoutParams((int) mLittleDotSize, (int) mDotRadius * 2, 1));
            dot.setClickable(true);
            dot.setOnClickListener(mDotClickHandler);
            addView(dot);
        }
    }

    @Override
    public int getTotal() {
        return mTotal;
    }

    @Override
    public int getCurrentIndex() {
        return mCurrent;
    }

    public void setOnDotClickHandler(OnDotClickHandler handler) {
        mOnDotClickHandler = handler;
    }

    private OnClickListener mDotClickHandler = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v instanceof LittleDot && null != mOnDotClickHandler) {
                mOnDotClickHandler.onDotClick(((LittleDot) v).getIndex());
            }
        }
    };

    public final void setSelected(int index) {
        if (index >= getChildCount() || index < 0 || mCurrent == index) {
            return;
        }
        if (mCurrent < getChildCount() && mCurrent >=0) {
            ((LittleDot) getChildAt(mCurrent)).setColor(mUnSelectedColor);
        }
        ((LittleDot) getChildAt(index)).setColor(mSelectedColor);
        mCurrent = index;
    }

    private class LittleDot extends View {

        private int mColor;
        private Paint mPaint;
        private int mIndex;

        public LittleDot(Context context, int index) {
            super(context);
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mIndex = index;
        }

        public int getIndex() {
            return mIndex;
        }

        public void setColor(int color) {
            if (color == mColor)
                return;
            mColor = color;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            mPaint.setColor(mColor);
            canvas.drawCircle(mLittleDotSize / 2, mDotRadius, mDotRadius, mPaint);
        }
    }

    public void setSelectedColor(int color) {
        if (mSelectedColor != color) {
            mSelectedColor = color;
            invalidate();
        }
    }

    public void setColor(int selectedColor, int unSelectedColor) {
        if (mSelectedColor != selectedColor || mUnSelectedColor != unSelectedColor) {
            mSelectedColor = selectedColor;
            mUnSelectedColor = unSelectedColor;
            invalidate();
        }
    }

    public void setUnSelectedColor(int color) {
        if (mUnSelectedColor != color) {
            mSelectedColor = color;
            invalidate();
        }
    }
}
