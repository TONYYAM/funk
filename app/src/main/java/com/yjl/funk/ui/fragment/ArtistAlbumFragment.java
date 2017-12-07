package com.yjl.funk.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yjl.funk.R;
import com.yjl.funk.model.Album;
import com.yjl.funk.model.ArtistAlbumList;
import com.yjl.funk.model.ArtistSongList;
import com.yjl.funk.network.HttpCallback;
import com.yjl.funk.network.HttpClient;
import com.yjl.funk.network.HttpException;
import com.yjl.funk.ui.adapter.NewAlbumListAdapter;
import com.yjl.funk.ui.base.BaseFragment;
import com.yjl.funk.utils.NetworkUtil;
import com.yjl.funk.widget.DataEmptyView;
import com.yjl.funk.widget.LoadingProgressView;
import com.yjl.funk.widget.NetWorkErrorView;
import com.yjl.funk.widget.Toasty;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017/11/9.
 */

public class ArtistAlbumFragment extends BaseFragment {
    @BindView(R.id.load_view)
    LoadingProgressView loadView;
    @BindView(R.id.ll_load)
    LinearLayout llLoad;
    @BindView(R.id.rv_album)
    RecyclerView rvAlbum;
    private Unbinder unbinder;
    private String tinguid = "0";
    private NetWorkErrorView netWorkErrorView;
    private DataEmptyView emptyView;
    private NewAlbumListAdapter adapter;
    private List<Album> albumList = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        tinguid = getArguments().getString("tinguid","0");
        View view = inflater.inflate(R.layout.fragment_artist_album, container, false);
        unbinder = ButterKnife.bind(this, view);
        initViews();
        initListeners();
        return view;
    }

    @Override
    public void initViews() {
        netWorkErrorView = new NetWorkErrorView(getActivity());
        emptyView = new DataEmptyView(getActivity());
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),3);
        rvAlbum.setLayoutManager(layoutManager);
        adapter = new NewAlbumListAdapter(getActivity(),R.layout.item_special_album,albumList,false);
        rvAlbum.setAdapter(adapter);
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
    public void getData(){
        progressShow();
        HttpClient.getArtistAlbum(tinguid, new HttpCallback<ArtistAlbumList>() {
            @Override
            public void onSuccess(ArtistAlbumList artistAlbumList) {
                progressDismiss();
                if(artistAlbumList!=null&&artistAlbumList.getAlbumlist()!=null){
                    if (artistAlbumList.getAlbumlist().size()==0){
                        emptyView.setText("暂无专辑");
                        adapter.setEmptyView(emptyView);
                    }else {
                        adapter.setNewData(artistAlbumList.getAlbumlist());
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