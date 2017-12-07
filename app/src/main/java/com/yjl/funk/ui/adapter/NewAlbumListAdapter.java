package com.yjl.funk.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.annotation.LayoutRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jakewharton.rxbinding.view.RxView;
import com.yjl.funk.R;
import com.yjl.funk.imageloader.frescohelper.FrescoHelper;
import com.yjl.funk.imageloader.frescoview.FrescoImageView;
import com.yjl.funk.model.Album;
import com.yjl.funk.ui.activity.MainActivity;
import com.yjl.funk.utils.NetworkUtil;
import com.yjl.funk.widget.Toasty;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

/**
 * Created by Administrator on 2017/11/6.
 */

public class NewAlbumListAdapter extends BaseQuickAdapter<Album, BaseViewHolder> {
    private Context context;
    private boolean is_time;
    public NewAlbumListAdapter(Context context, @LayoutRes int resId, List<Album> list,boolean is_time) {
        super(resId, list);
        this.context = context;
        this.is_time = is_time;
    }


    @Override
    protected void convert(BaseViewHolder helper, final Album item) {
        if (item != null) {
            FrescoHelper.loadFrescoImage((FrescoImageView) helper.getView(R.id.iv_pic), item.getPic_big() + "@s_1,w_450,h_450", R.drawable.default_load_cover, false, new Point(400, 500));
            helper.setText(R.id.tv_name, item.getTitle());
            helper.setText(R.id.tv_describe, item.getAuthor());
            TextView publish = helper.getView(R.id.tv_publish_time);
            if(is_time)
            publish.setText("发布时间 " + item.getPublishtime());
            else publish.setVisibility(View.GONE);
            RxView.clicks(helper.itemView).throttleFirst(1, TimeUnit.SECONDS).subscribe(new Action1<Void>() {
                @Override
                public void call(Void aVoid) {
                    if (NetworkUtil.isNetConnect(context)) {
                        MainActivity.getInstance().switchAlbumInfoFragment(item.getAlbum_id());
                    } else {
                        Toasty.error(context, "网络无连接，请检查您的网络设置").show();
                    }
                }
            });

        }
    }
}
