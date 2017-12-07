package com.yjl.funk.ui.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jakewharton.rxbinding.view.RxView;
import com.yjl.funk.player.MusicClient;
import com.yjl.funk.R;
import com.yjl.funk.imageloader.frescohelper.FrescoHelper;
import com.yjl.funk.imageloader.frescoview.FrescoImageView;
import com.yjl.funk.model.MusicTrack;
import com.yjl.funk.utils.DensityUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

/**
 * Created by Administrator on 2017/11/7.
 */

public class OnlineMusicListAdapter extends BaseQuickAdapter<MusicTrack, BaseViewHolder> {
    private Context context;
    private boolean is_rank, is_img;//是否显示排名 是否显示图片
    private List<MusicTrack> list;

    public OnlineMusicListAdapter(Context context, @LayoutRes int resId, List<MusicTrack> list, boolean is_rank, boolean is_img) {
        super(resId, list);
        this.context = context;
        this.is_rank = is_rank;
        this.is_img = is_img;
        this.list = list;
    }

    @Override
    protected void convert(final BaseViewHolder helper, MusicTrack item) {
        TextView rank = helper.getView(R.id.tv_rank);
        if (is_rank) {
            if (helper.getLayoutPosition() < 3) {
                rank.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            } else {
                rank.setTextColor(ContextCompat.getColor(context, R.color.text_gray));
            }
            rank.setText(String.valueOf(helper.getLayoutPosition() + 1));
        } else {
            rank.setVisibility(View.GONE);
        }
        helper.setText(R.id.tv_name, item.getTitle());
        helper.setText(R.id.tv_describe, item.getAuthor() + " · " + item.getAlbum_title());
        if (is_img) {
            String cover = "";
            if(TextUtils.isEmpty(item.getPic_big())){
                if(!TextUtils.isEmpty(item.getPic_small()))
                    cover = item.getPic_small().split("\\@")[0]+"@s_1,w_500,h_500";

            }else {
                cover = item.getPic_big();
            }
            FrescoHelper.loadFrescoImage((FrescoImageView) helper.getView(R.id.iv_song),cover, R.drawable.default_load_cover_small, DensityUtils.dip2px(context, 2), false);
        }else
            helper.setVisible(R.id.iv_song, false);

        RxView.clicks(helper.itemView).throttleFirst(1, TimeUnit.SECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                MusicClient.setPlayListAndPlayAt(list, helper.getLayoutPosition());
            }
        });
    }

}
