package com.yjl.funk.ui.fragment;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.yjl.funk.player.MusicClient;
import com.yjl.funk.R;
import com.yjl.funk.imageloader.frescohelper.FrescoHelper;
import com.yjl.funk.imageloader.frescoview.FrescoImageView;
import com.yjl.funk.imageloader.processors.BlurPostprocessor;
import com.yjl.funk.model.AlbumInfo;
import com.yjl.funk.model.MusicTrack;
import com.yjl.funk.network.HttpCallback;
import com.yjl.funk.network.HttpClient;
import com.yjl.funk.network.HttpException;
import com.yjl.funk.ui.activity.MainActivity;
import com.yjl.funk.ui.adapter.OnlineMusicListAdapter;
import com.yjl.funk.ui.base.BaseFragment;
import com.yjl.funk.utils.NetworkUtil;
import com.yjl.funk.widget.AlbumIntroDialog;
import com.yjl.funk.widget.DataEmptyView;
import com.yjl.funk.widget.LoadingDialog;
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
 * Created by Administrator on 2017/11/10.
 */

public class AlbumInfoFragment extends BaseFragment{

    private Unbinder unbinder;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_song)
    RecyclerView rvSong;
    @BindView(R.id.ll_play_all)
    LinearLayout llPlayAll;
    @BindView(R.id.tv_multiple_choice)
    TextView tvMultipleChoice;
    @BindView(R.id.tv_list_total)
    TextView tvListTotal;
    @BindView(R.id.iv_bg)
    FrescoImageView ivBg;
    @BindView(R.id.iv_album)
    FrescoImageView ivAlbum;
    @BindView(R.id.tv_author)
    TextView tvAuthor;
    @BindView(R.id.tv_describe)
    TextView tvDescribe;
    @BindView(R.id.tv_info)
    TextView tvInfo;
    @BindView(R.id.ll_base_info)
    LinearLayout llBaseInfo;
    private OnlineMusicListAdapter adapter;
    private AlbumIntroDialog albumIntroDialog;//专辑介绍dialog
    private List<MusicTrack> songList = new ArrayList<>();
    private String album_id = "0";
    private LoadingDialog loadingDialog;

    public static AlbumInfoFragment newInstance(String album_id) {
        AlbumInfoFragment fragment = new AlbumInfoFragment();
        Bundle args = new Bundle();
        args.putString("album_id",album_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            album_id = getArguments().getString("album_id","0");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_info, container, false);
        unbinder = ButterKnife.bind(this, view);
        initViews();
        initListeners();
        return view;
    }

    @Override
    public void initViews() {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.getInstance().onBackPressed();
            }
        });
        netWorkErrorView = new NetWorkErrorView(getActivity());
        emptyView = new DataEmptyView(getActivity());
        loadingDialog = new LoadingDialog(getActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvSong.setLayoutManager(layoutManager);
        adapter = new OnlineMusicListAdapter(getActivity(), R.layout.item_music, songList, true, false);
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
        RxView.clicks(tvInfo).throttleFirst(1, TimeUnit.SECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (albumIntroDialog != null) {
                    albumIntroDialog.show();
                }
            }
        });
        //播放全部
        RxView.clicks(llPlayAll).throttleFirst(1,TimeUnit.SECONDS).subscribe(new Action1<Void>() {
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


    public void getData() {
        loadingDialog.onShow();
        HttpClient.getNewAlbumInfo(album_id, new HttpCallback<AlbumInfo>() {
            @Override
            public void onSuccess(final AlbumInfo albumInfo) {
                loadingDialog.onDismiss();
                if (albumInfo != null && albumInfo.getSonglist() != null) {
                    albumIntroDialog = new AlbumIntroDialog(getActivity(), albumInfo);
                    toolbar.setTitle(albumInfo.getAlbumInfo().getTitle());
                    llBaseInfo.setVisibility(View.VISIBLE);
                    FrescoHelper.loadFrescoImageCircle(ivAlbum, albumInfo.getAlbumInfo().getPic_s500(), 0, false);
                    tvAuthor.setText("歌手: " + albumInfo.getAlbumInfo().getAuthor());
                    tvAuthor.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                           MainActivity.getInstance().switchArtistInfoFragment(albumInfo.getAlbumInfo().getArtist_ting_uid());
                        }
                    });
                    tvDescribe.setText("发布日期: " + albumInfo.getAlbumInfo().getPublishtime());
                    FrescoHelper.loadFrescoImage(ivBg, albumInfo.getAlbumInfo().getPic_s500(), 0, false, new Point(720, 480), new BlurPostprocessor(getActivity(), 15));
                    if (albumInfo.getSonglist().size() != 0) {
                        songList.clear();
                        songList.addAll(albumInfo.getSonglist());
                        adapter.setNewData(albumInfo.getSonglist());
                        llPlayAll.setVisibility(View.VISIBLE);
                        tvListTotal.setText("(共" + albumInfo.getSonglist().size() + "首)");
                        adapter.addFooterView(LayoutInflater.from(getActivity()).inflate(R.layout.rv_footer, null));
                    }else {
                        emptyView.setText("该专辑暂无歌曲");
                        adapter.setEmptyView(emptyView);
                    }
                }
            }

            @Override
            public void onFail(HttpException e) {
                loadingDialog.onDismiss();
                Toasty.error(getActivity(), e.getMessage()).show();
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
}

