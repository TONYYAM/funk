package com.yjl.funk.ui.fragment;

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
import com.yjl.funk.model.OnlineSongList;
import com.yjl.funk.model.MusicTrack;
import com.yjl.funk.network.HttpCallback;
import com.yjl.funk.network.HttpClient;
import com.yjl.funk.network.HttpException;
import com.yjl.funk.ui.activity.MainActivity;
import com.yjl.funk.ui.adapter.OnlineMusicListAdapter;
import com.yjl.funk.ui.base.BaseFragment;
import com.yjl.funk.utils.NetworkUtil;
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

public class BillBoardFragment extends BaseFragment{
    private Unbinder unbinder;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_bill)
    RecyclerView rvBill;
    @BindView(R.id.ll_play_all)
    LinearLayout llPlayAll;
    @BindView(R.id.tv_multiple_choice)
    TextView tvMultipleChoice;
    @BindView(R.id.tv_list_total)
    TextView tvListTotal;
    private String type = "1";//1新歌榜 2 热歌榜 3,新歌速递
    private LoadingDialog loadingDialog;
    private OnlineMusicListAdapter adapter;
    private List<MusicTrack> songList = new ArrayList<>();
    private static final int MUSIC_LIST_SIZE = 100;
    private int mOffset = 0;
    public static BillBoardFragment newInstance(String type) {
        BillBoardFragment fragment = new BillBoardFragment();
        Bundle args = new Bundle();
        args.putCharSequence("type",type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        type = getArguments().getString("type","1");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_billboard, container, false);
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
        rvBill.setLayoutManager(layoutManager);

            adapter = new OnlineMusicListAdapter(getActivity(), R.layout.item_music, songList, true,true);

        rvBill.setAdapter(adapter);
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
        HttpClient.getSongListInfo(type, MUSIC_LIST_SIZE, mOffset, new HttpCallback<OnlineSongList>() {
                @Override
                public void onSuccess(OnlineSongList onlineSongList) {
                    if (onlineSongList != null && onlineSongList.getSong_list() != null) {
                        toolbar.setTitle(onlineSongList.getBillboard().getName());
                        if (onlineSongList.getSong_list().size()!= 0) {
                            songList.clear();
                            songList.addAll(onlineSongList.getSong_list());
                            adapter.setNewData(onlineSongList.getSong_list());
                            llPlayAll.setVisibility(View.VISIBLE);
                            tvListTotal.setText("(共"+onlineSongList.getSong_list().size()+"首)");
                            adapter.addFooterView(LayoutInflater.from(getActivity()).inflate(R.layout.rv_footer, null));
                        }else {
                            emptyView.setText("暂无歌曲");
                            adapter.setEmptyView(emptyView);
                        }
                    }
                    loadingDialog.onDismiss();
                }

                @Override
                public void onFail(HttpException e) {
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
                    loadingDialog.onDismiss();
                }
            });
        }
    }


