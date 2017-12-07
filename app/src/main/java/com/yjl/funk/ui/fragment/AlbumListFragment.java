package com.yjl.funk.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yjl.funk.R;
import com.yjl.funk.model.Album;
import com.yjl.funk.model.NewAlbumListData;
import com.yjl.funk.network.HttpCallback;
import com.yjl.funk.network.HttpClient;
import com.yjl.funk.network.HttpException;
import com.yjl.funk.ui.activity.MainActivity;
import com.yjl.funk.ui.adapter.NewAlbumListAdapter;
import com.yjl.funk.ui.base.BaseFragment;
import com.yjl.funk.utils.NetworkUtil;
import com.yjl.funk.widget.DataEmptyView;
import com.yjl.funk.widget.LoadingDialog;
import com.yjl.funk.widget.NetWorkErrorView;
import com.yjl.funk.widget.Toasty;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017/11/10.
 */

public class AlbumListFragment extends BaseFragment{

    private Unbinder unbinder;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_album)
    RecyclerView rvAlbum;
    private NewAlbumListAdapter adapter;
    private List<Album> albumList = new ArrayList<>();
    private LoadingDialog loadingDialog;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        initViews();
        initListeners();
        return view;
    }

    @Override
    public void initViews() {
        toolbar.setTitle("新专上架");
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
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        rvAlbum.setLayoutManager(layoutManager);
        adapter = new NewAlbumListAdapter(getActivity(), R.layout.item_special_album, albumList,false);
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

    public void getData() {
        loadingDialog.onShow();
        HttpClient.getNewAlbumList(42, new HttpCallback<NewAlbumListData>() {
            @Override
            public void onSuccess(NewAlbumListData newAlbumListData) {
                if (newAlbumListData!=null&&newAlbumListData.getPlaze_album_list().getRM().getAlbum_list().getList().size() != 0)
                    adapter.setNewData(newAlbumListData.getPlaze_album_list().getRM().getAlbum_list().getList());
                else {
                    emptyView.setText("暂无专辑");
                    adapter.setEmptyView(emptyView);
                }
                loadingDialog.onDismiss();
            }

            @Override
            public void onFail(HttpException e) {
                Toasty.error(getActivity(), e.getMessage()).show();
                loadingDialog.onDismiss();
                if (!NetworkUtil.isNetConnect(getActivity())) {
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
