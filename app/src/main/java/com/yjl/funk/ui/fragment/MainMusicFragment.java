package com.yjl.funk.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.yjl.funk.MyApplication;
import com.yjl.funk.R;
import com.yjl.funk.model.Album;
import com.yjl.funk.model.NewAlbumListData;
import com.yjl.funk.model.OnlineSongList;
import com.yjl.funk.model.MusicTrack;
import com.yjl.funk.network.HttpCallback;
import com.yjl.funk.network.HttpClient;
import com.yjl.funk.network.HttpException;
import com.yjl.funk.ui.activity.MainActivity;
import com.yjl.funk.ui.adapter.NewAlbumListAdapter;
import com.yjl.funk.ui.adapter.NewSpecialSongAdapter;
import com.yjl.funk.ui.base.BaseFragment;
import com.yjl.funk.widget.Toasty;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.functions.Action1;

/**
 * Created by Administrator on 2017/11/1.
 */

public class MainMusicFragment extends BaseFragment{
    @BindView(R.id.rv_new_song)
    RecyclerView rvNewSong;
    @BindView(R.id.rv_new_album)
    RecyclerView rvNewAlbum;
    @BindView(R.id.tv_new_bill_rank1)
    TextView tvNewBillRank1;
    @BindView(R.id.tv_new_bill_rank2)
    TextView tvNewBillRank2;
    @BindView(R.id.tv_new_bill_rank3)
    TextView tvNewBillRank3;
    @BindView(R.id.tv_hot_bill_rank1)
    TextView tvHotBillRank1;
    @BindView(R.id.tv_hot_bill_rank2)
    TextView tvHotBillRank2;
    @BindView(R.id.tv_hot_bill_rank3)
    TextView tvHotBillRank3;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.card_new_bill)
    CardView cardNewBill;
    @BindView(R.id.card_hot_bill)
    CardView cardHotBill;
    @BindView(R.id.iv_new_song_more)
    ImageView ivNewSongMore;
    @BindView(R.id.iv_new_album_more)
    ImageView ivNewAlbumMore;
    private Unbinder unbinder;
    private NewSpecialSongAdapter newSpecialSongAdapter;//新歌速递适配器
    private NewAlbumListAdapter newAlbumListAdapter;//新专上架；
    private List<MusicTrack> newSongs = new ArrayList<>();
    private List<Album> newAlbums = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_music, container, false);
        unbinder = ButterKnife.bind(this, view);
        initViews();
        initListeners();
        return view;
    }

    @Override
    public void initViews() {
        refreshLayout.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorAccent, R.color.colorPrimary);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        rvNewSong.setLayoutManager(layoutManager);
        newSpecialSongAdapter = new NewSpecialSongAdapter(getActivity(), R.layout.item_special_music, newSongs);
        rvNewSong.setAdapter(newSpecialSongAdapter);
        GridLayoutManager layoutManager1 = new GridLayoutManager(getActivity(), 2);
        rvNewAlbum.setLayoutManager(layoutManager1);
        newAlbumListAdapter = new NewAlbumListAdapter(getActivity(), R.layout.item_special_album, newAlbums,true);
        rvNewAlbum.setAdapter(newAlbumListAdapter);
        rvNewSong.setNestedScrollingEnabled(false);
        rvNewAlbum.setNestedScrollingEnabled(false);
        refreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
                getData();
            }
        },300);
    }

    @Override
    public void initListeners() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
        RxView.clicks(cardHotBill).throttleFirst(1, TimeUnit.SECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                MainActivity.getInstance().switchBillBoardFragment("2");
            }
        });
        RxView.clicks(cardNewBill).throttleFirst(1,TimeUnit.SECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                MainActivity.getInstance().switchBillBoardFragment("1");
            }
        });
        RxView.clicks(ivNewSongMore).throttleFirst(1,TimeUnit.SECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                MainActivity.getInstance().switchBillBoardFragment("8");
            }
        });
        RxView.clicks(ivNewAlbumMore).throttleFirst(1,TimeUnit.SECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                MainActivity.getInstance().switchAlbumListFragment();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void getData() {
        getBillBroadMusic();
        getNewAlbum();
        getNewBill();
        getHotBill();
    }

    //billbroad
    public void getBillBroadMusic() {
        HttpClient.getSongListInfo("8", 6, 0, new HttpCallback<OnlineSongList>() {
            @Override
            public void onSuccess(OnlineSongList onlineSongList) {
                newSongs.clear();
                newSongs.addAll(onlineSongList.getSong_list());
                newSpecialSongAdapter.setNewData(onlineSongList.getSong_list());
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFail(HttpException e) {
                Toasty.error(getActivity(), e.getMessage()).show();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    //新专上架
    public void getNewAlbum() {
        HttpClient.getNewAlbumList(4, new HttpCallback<NewAlbumListData>() {
            @Override
            public void onSuccess(NewAlbumListData newAlbumListData) {
                if(newAlbumListData!=null)
                newAlbumListAdapter.setNewData(newAlbumListData.getPlaze_album_list().getRM().getAlbum_list().getList());
            }

            @Override
            public void onFail(HttpException e) {
                Toasty.error(getActivity(), e.getMessage()).show();
            }
        });
    }

    //新歌榜
    public void getNewBill() {
        HttpClient.getSongListInfo("1", 3, 0, new HttpCallback<OnlineSongList>() {
            @Override
            public void onSuccess(OnlineSongList onlineSongList) {
                if (onlineSongList!=null&&onlineSongList.getSong_list()!=null&&onlineSongList.getSong_list().size() > 2) {
                    tvNewBillRank1.setText("1. " + onlineSongList.getSong_list().get(0).getTitle() + " - " + onlineSongList.getSong_list().get(0).getAuthor());
                    tvNewBillRank2.setText("2. " + onlineSongList.getSong_list().get(1).getTitle() + " - " + onlineSongList.getSong_list().get(1).getAuthor());
                    tvNewBillRank3.setText("3. " + onlineSongList.getSong_list().get(2).getTitle() + " - " + onlineSongList.getSong_list().get(2).getAuthor());
                }
            }

            @Override
            public void onFail(HttpException e) {
                Toasty.error(getActivity(), e.getMessage()).show();
            }
        });
    }

    //热歌榜
    public void getHotBill() {
        HttpClient.getSongListInfo("2", 3, 0, new HttpCallback<OnlineSongList>() {
            @Override
            public void onSuccess(OnlineSongList onlineSongList) {
                if (onlineSongList!=null&&onlineSongList.getSong_list().size() > 2) {
                    tvHotBillRank1.setText("1. " + onlineSongList.getSong_list().get(0).getTitle() + " - " + onlineSongList.getSong_list().get(0).getAuthor());
                    tvHotBillRank2.setText("2. " + onlineSongList.getSong_list().get(1).getTitle() + " - " + onlineSongList.getSong_list().get(1).getAuthor());
                    tvHotBillRank3.setText("3. " + onlineSongList.getSong_list().get(2).getTitle() + " - " + onlineSongList.getSong_list().get(2).getAuthor());
                }
            }

            @Override
            public void onFail(HttpException e) {
                Toasty.error(getActivity(), e.getMessage()).show();
            }
        });
    }
}
