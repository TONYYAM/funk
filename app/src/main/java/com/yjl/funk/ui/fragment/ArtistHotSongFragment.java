package com.yjl.funk.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.yjl.funk.player.MusicClient;
import com.yjl.funk.R;
import com.yjl.funk.model.ArtistSongList;
import com.yjl.funk.model.MusicTrack;
import com.yjl.funk.network.HttpCallback;
import com.yjl.funk.network.HttpClient;
import com.yjl.funk.network.HttpException;
import com.yjl.funk.ui.adapter.OnlineMusicListAdapter;
import com.yjl.funk.ui.base.BaseFragment;
import com.yjl.funk.utils.DensityUtils;
import com.yjl.funk.utils.NetworkUtil;
import com.yjl.funk.widget.DataEmptyView;
import com.yjl.funk.widget.LoadingProgressView;
import com.yjl.funk.widget.NetWorkErrorView;
import com.yjl.funk.widget.Toasty;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.functions.Action1;

/**
 * Created by Administrator on 2017/11/9.
 */

public class ArtistHotSongFragment extends BaseFragment {
    @BindView(R.id.load_view)
    LoadingProgressView loadView;
    @BindView(R.id.ll_load)
    LinearLayout llLoad;
    @BindView(R.id.tv_list_total)
    TextView tvListTotal;
    @BindView(R.id.tv_multiple_choice)
    TextView tvMultipleChoice;
    @BindView(R.id.ll_play_all)
    LinearLayout llPlayAll;
    @BindView(R.id.rv_song)
    RecyclerView rvSong;
    private Unbinder unbinder;
    private String tinguid = "0";
    private OnlineMusicListAdapter adapter;
    private List<MusicTrack> songList = new ArrayList<>();
    private NetWorkErrorView netWorkErrorView;
    private DataEmptyView emptyView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        tinguid = getArguments().getString("tinguid","0");
        View view = inflater.inflate(R.layout.fragment_artist_hot_song, container, false);
        unbinder = ButterKnife.bind(this, view);
        initViews();
        initListeners();
        return view;
    }

    @Override
    public void initViews() {
        netWorkErrorView = new NetWorkErrorView(getActivity());
        emptyView = new DataEmptyView(getActivity());
        loadView.setStrokeWidth(DensityUtils.dip2px(getActivity(),1));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvSong.setLayoutManager(layoutManager);
        adapter = new OnlineMusicListAdapter(getActivity(),R.layout.item_music,songList,true,true);
        rvSong.setAdapter(adapter);

    }

    @Override
    public void initListeners() {
        //转场动画加载完成后请求数据
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        },200);
        //播放全部
        RxView.clicks(llPlayAll).throttleFirst(1, TimeUnit.SECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                MusicClient.setPlayListAndPlayAt(songList,0);
            }
        });
        RxView.clicks(tvMultipleChoice).throttleFirst(1,TimeUnit.SECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
    public void getData(){
        progressShow();
        HttpClient.getArtistSong(tinguid, new HttpCallback<ArtistSongList>() {
            @Override
            public void onSuccess(ArtistSongList artistSongList) {
                progressDismiss();
                if(artistSongList!=null&&artistSongList.getSongList()!=null){
                    if (artistSongList.getSongList().size()!=0){
                        songList.clear();
                        songList.addAll(artistSongList.getSongList());
                        adapter.setNewData(artistSongList.getSongList());
                        llPlayAll.setVisibility(View.VISIBLE);
                        tvListTotal.setText("(共"+artistSongList.getSongList().size()+"首)");
                        adapter.addFooterView(LayoutInflater.from(getActivity()).inflate(R.layout.rv_footer, null));
                    }else {
                        emptyView.setText("暂无热门歌曲");
                        adapter.setEmptyView(emptyView);
                    }
                }
            }
            @Override
            public void onFail(HttpException e) {
                progressDismiss();
                Toasty.error(getActivity(),e.getMessage()).show();
                if(!NetworkUtil.isNetConnect(getActivity())){
                    adapter.setEmptyView(netWorkErrorView);
                    netWorkErrorView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getData();
                        }
                    });
                }
            }
        });
    }
    public void progressShow(){
        llLoad.setVisibility(View.VISIBLE);
        loadView.startAnim();
    }
    public void progressDismiss(){
        loadView.stopAnim();
        llLoad.setVisibility(View.GONE);
    }
}
