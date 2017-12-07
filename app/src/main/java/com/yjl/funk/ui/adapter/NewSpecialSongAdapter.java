package com.yjl.funk.ui.adapter;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.LayoutRes;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jakewharton.rxbinding.view.RxView;
import com.yjl.funk.player.MusicClient;
import com.yjl.funk.R;
import com.yjl.funk.imageloader.frescohelper.FrescoHelper;
import com.yjl.funk.imageloader.frescoview.FrescoImageView;
import com.yjl.funk.model.MusicTrack;
import com.yjl.funk.utils.AppUtil;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

public class NewSpecialSongAdapter extends BaseQuickAdapter<MusicTrack, BaseViewHolder> {
    private Context context;
    private List<MusicTrack> tracks;

    public NewSpecialSongAdapter(Context context, @LayoutRes int resId, List<MusicTrack> list) {
        super(resId, list);
        this.context = context;
        this.tracks = list;
    }


    @Override
    protected void convert(final BaseViewHolder helper, final MusicTrack item) {
        FrescoHelper.loadFrescoImage((FrescoImageView) helper.getView(R.id.iv_pic),item.getPic_big(),R.drawable.default_load_cover,false,new Point(300,250));
        helper.setText(R.id.tv_name,item.getTitle());
        helper.setText(R.id.tv_describe,item.getAuthor());
        helper.setText(R.id.tv_listen_count, AppUtil.numToStr(String.valueOf(new Random().nextInt(3000000)%2990001+10000)));
        RxView.clicks(helper.itemView).throttleFirst(1, TimeUnit.SECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                MusicClient.setPlayListAndPlayAt(tracks,helper.getLayoutPosition());
            }
        });
    }
}