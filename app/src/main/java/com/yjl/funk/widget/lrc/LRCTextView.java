package com.yjl.funk.widget.lrc;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import com.yjl.funk.utils.DensityUtils;

public class LRCTextView extends View {

    private Rect mbounds;
    private int textWidth;
    private int textHeight;
    private int startX;
    private int startY;
    private Paint paint;
    private Paint oriPain;
    private long mills = 0;
    private float percent = 0;
    private long currentMills = 0;
    private long delay = 0;
    private int line = 0;
    private String lrcText = "";
    private Handler handler = new Handler();
    private int gravity = Gravity.LEFT;
    private boolean is_draw_origin = false;
    private Runnable drawRunnable = new Runnable() {
        @Override
        public void run() {
            if (currentMills <= mills) {
                currentMills += delay;
                percent = ((float) (currentMills+500)/ (float) mills);
                invalidate();
                handler.postDelayed(drawRunnable, delay);
            } else {

            }
        }
    };

    public void pause() {
        handler.removeCallbacks(drawRunnable);
    }

    public void start() {
        handler.post(drawRunnable);
    }
    public void setTextStyle(@ColorRes int defaultColor, @ColorRes int progressColor, int textSize,int gravity){
        oriPain.setColor(ContextCompat.getColor(getContext(),defaultColor));
        paint.setColor(ContextCompat.getColor(getContext(),progressColor));
        oriPain.setTextSize(DensityUtils.sp2px(getContext(),textSize));
        paint.setTextSize(DensityUtils.sp2px(getContext(),textSize));
        this.gravity = gravity;
    }
    public LRCTextView(Context context) {
        this(context, null);
    }

    public LRCTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LRCTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureText();
        int width = getWidthMeasure(widthMeasureSpec);
        int height = getHeightMeasure(heightMeasureSpec);
        setMeasuredDimension(width, height);
        switch (gravity){
            case Gravity.LEFT:
                startX = 0;
                break;
            case Gravity.CENTER:
                startX = getMeasuredWidth() / 2 - textWidth / 2;
                break;
            default:
                startX = 0;
                break;
        }
        startY = getMeasuredHeight() / 2 + textHeight / 2;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void measureText() {
        textWidth = (int) oriPain.measureText(lrcText);
        FontMetrics fm = oriPain.getFontMetrics();
        textHeight = (int) (fm.descent + fm.ascent);
        oriPain.getTextBounds(lrcText, 0, lrcText.length(), mbounds);

    }

    private int getHeightMeasure(int heights) {
        int mode = MeasureSpec.getMode(heights);
        int val = MeasureSpec.getSize(heights);
        int result = 0;
        switch (mode) {
            case MeasureSpec.EXACTLY:
                result = val;
                break;
            case MeasureSpec.AT_MOST:
                // break;//不要break 就可以 把上面的 result = textWidth; 注掉；
            case MeasureSpec.UNSPECIFIED:
                result = textHeight + getPaddingTop() + getPaddingBottom();
                break;
        }
        // EXACTLY的值 是精确的 最大也是屏幕大小 AT_MOST warp_content 动态大小 不会超过屏幕
        result = mode == MeasureSpec.AT_MOST ? Math.min(result, val) : result;
        return result;
    }

    private int getWidthMeasure(int widths) {
        int mode = MeasureSpec.getMode(widths);
        int width = MeasureSpec.getSize(widths);
        int result = 0;
        if (mode == MeasureSpec.EXACTLY) {
            // 精确值
            result = width;
        } else if (mode == MeasureSpec.UNSPECIFIED) {

            result = textWidth;
        }
        if (mode == MeasureSpec.AT_MOST) {
            result = textWidth + getPaddingLeft() + getPaddingRight();
        }
        // EXACTLY的值 是精确的 最大也是屏幕大小 AT_MOST warp_content 动态大小 不会超过屏幕
        result = mode == MeasureSpec.AT_MOST ? Math.min(result, width) : result;
        // 如果这个模式 就会取到 result = textWidth;不会取到超过 math_content
        return result;
    }

    public void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        oriPain = new Paint(Paint.ANTI_ALIAS_FLAG);
        oriPain.setDither(true);
        paint.setStyle(Style.FILL);
        // oriPain.setStyle(Style.STROKE);//空心字
        oriPain.setStyle(Style.FILL);
        mbounds = new Rect();
    }

    /**
     * 设置歌词
     *
     * @param lrc
     */
    public void setLrc(String lrc, long mills,int line) {
        if (mills <= 0 || TextUtils.isEmpty(lrc) || (lrcText.equals(lrc)&&this.line==line))
            return;
        this.lrcText = lrc;
        this.percent = 0;
        this.line = line;
        this.delay = (mills-300)/ (4 * lrc.length());
        this.mills = mills-300;
        this.currentMills = 0;
        this.is_draw_origin = false;
        requestLayout();
        handler.post(drawRunnable);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!is_draw_origin) {
            int l = (int) (startX + percent * mbounds.width());
            canvasDrawLeft(canvas, l);
        }else {
            canvasDrawOriginal(canvas);
        }
    }

    private void canvasDrawOriginal(Canvas canvas) {
        //textHeight是负值，getMeasuredHeight() / 2字体的底部在控件中间，要加上字体的一半
        canvas.drawText(lrcText, startX, getMeasuredHeight() / 2 - (textHeight) / 2, oriPain);
        // 高度一半 减去字体descent()+ ascent()=字体高度 的一半 让字体一半在上面 一半在下面
    }

    private void canvasDrawLeft(Canvas canvas, int l) {
        canvas.save();
        canvasDrawOriginal(canvas);
        canvas.clipRect(startX, 0, l, getMeasuredHeight());
        canvas.drawText(
                lrcText,
                startX,
                getMeasuredHeight() / 2- (textHeight) / 2
                        , paint);
        canvas.restore();
    }
    public void setText(String text){
        this.lrcText = text;
        requestLayout();
        this.is_draw_origin = true;
        invalidate();

    }

}