package com.yjl.funk.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.yjl.funk.imageloader.frescoview.FrescoImageView;

/**
 * Created by tonyy on 2017/11/18.
 */

public class AlbumCircleView extends FrescoImageView {
    private boolean is_playing = false;
    private boolean is_Start = false;
    private ObjectAnimator animator =  ObjectAnimator.ofFloat(this,"rotation",0f,360f);

    public AlbumCircleView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
    }

    public AlbumCircleView(Context context) {
        super(context);
    }

    public AlbumCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AlbumCircleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void startRotation(){
        if(is_playing)
            return;
        animator.setDuration(60*1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        if(is_Start){
            animator.resume();
        }else {
            animator.start();
            is_Start = true;
        }
        is_playing = true;
    }
    public void pauseRotation(){
        if(is_playing)
            animator.pause();
        is_playing = false;
    }


}
