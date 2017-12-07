package com.yjl.funk.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.yjl.funk.R;
import com.yjl.funk.imageloader.frescohelper.FrescoHelper;
import com.yjl.funk.imageloader.frescoview.FrescoImageView;
import com.yjl.funk.imageloader.processors.BlurPostprocessor;
import com.yjl.funk.model.AlbumInfo;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import rx.functions.Action1;


/**
 * Created by Administrator on 2017/11/8.
 */

public class AlbumIntroDialog extends Dialog {
    private Context context;
    private AlbumInfo info;
    private FrescoImageView ivBg,ivAlbum;
    private ImageView ivCLose;
    private TextView tv_name,tv_describe;
    public AlbumIntroDialog(Context context, AlbumInfo info) {
        this(context, R.style.Dialog_Fullscreen);
        this.context = context;
        this.info = info;
        initView();
    }

    public AlbumIntroDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.dismiss();
    }
    public void initView(){
        View view = View.inflate(context,R.layout.dialog_album_info,null);
        ivAlbum = ButterKnife.findById(view,R.id.iv_album_img);
        ivBg = ButterKnife.findById(view,R.id.iv_album_bg);
        tv_name = ButterKnife.findById(view,R.id.tv_album_name);
        tv_describe = ButterKnife.findById(view,R.id.tv_album_describe);
        ivCLose = ButterKnife.findById(view,R.id.iv_close);
        if(info!=null) {
            FrescoHelper.loadFrescoImageCircle(ivAlbum, info.getAlbumInfo().getPic_s500(), 0, false);
            FrescoHelper.loadFrescoImage(ivBg, info.getAlbumInfo().getPic_s500(), 0, false, new Point(720, 480), new BlurPostprocessor(context, 15));
            tv_name.setText(info.getAlbumInfo().getTitle());
            if(TextUtils.isEmpty(info.getAlbumInfo().getInfo()))
                tv_describe.setText("暂无简介");
            else
            tv_describe.setText(info.getAlbumInfo().getInfo());
        }
        RxView.clicks(ivCLose).throttleFirst(1, TimeUnit.SECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                AlbumIntroDialog.this.dismiss();
            }
        });
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        this.addContentView(view,lp);
    }


}
