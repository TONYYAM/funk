package com.yjl.funk.ui.fragment;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yjl.funk.R;
import com.yjl.funk.imageloader.frescohelper.FrescoHelper;
import com.yjl.funk.imageloader.frescoview.FrescoImageView;
import com.yjl.funk.imageloader.processors.ColorFilterPostprocessor;
import com.yjl.funk.model.ArtistInfo;
import com.yjl.funk.network.HttpCallback;
import com.yjl.funk.network.HttpClient;
import com.yjl.funk.network.HttpException;
import com.yjl.funk.ui.activity.MainActivity;
import com.yjl.funk.ui.base.BaseFragment;
import com.yjl.funk.widget.LoadingDialog;
import com.yjl.funk.widget.Toasty;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017/11/10.
 */

public class ArtistInfoFragment extends BaseFragment {

    @BindView(R.id.iv_bg)
    FrescoImageView ivBg;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindArray(R.array.artist_info_tag)
    String[] titles;
    private Unbinder unbinder;
    private String tinguid = "0";
    private LoadingDialog loadingDialog;
    private PagerAdapter adapter;
    private List<Fragment> list = new ArrayList<>();
    private ArtistHotSongFragment hotSongFragment;
    private ArtistAlbumFragment albumFragment;
    private ArtistIntroFragment introFragment;
    public static ArtistInfoFragment newInstance(String tinguid) {
        ArtistInfoFragment fragment = new ArtistInfoFragment();
        Bundle args = new Bundle();
        args.putString("tinguid", tinguid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tinguid = getArguments().getString("tinguid", "0");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist_info, container, false);
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
        loadingDialog = new LoadingDialog(getActivity());
        Bundle bundle = new Bundle();
        bundle.putCharSequence("tinguid",tinguid);
        hotSongFragment = new ArtistHotSongFragment();
        hotSongFragment.setArguments(bundle);
        list.add(0,hotSongFragment);
        albumFragment = new ArtistAlbumFragment();
        albumFragment.setArguments(bundle);
        list.add(1,albumFragment);
        introFragment = new ArtistIntroFragment();
        list.add(2,introFragment);
        //ViewPager的适配器
        adapter = new PagerAdapter(getChildFragmentManager());
        viewpager.setAdapter(adapter);
        //绑定
        tabLayout.setupWithViewPager(viewpager);
        tabLayout.getTabAt(0).setText(titles[0]);
        tabLayout.getTabAt(1).setText(titles[1]);
        tabLayout.getTabAt(2).setText(titles[2]);
        viewpager.setOffscreenPageLimit(2);
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



    class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }

    }
    public void getData(){
        loadingDialog.onShow();
        HttpClient.getArtistInfo(tinguid, new HttpCallback<ArtistInfo>() {
            @Override
            public void onSuccess(ArtistInfo artistInfo) {
                loadingDialog.onDismiss();
                if(artistInfo!=null){
                    introFragment.setInfo(artistInfo.getIntro());
                    toolbar.setTitle(artistInfo.getName());
                    FrescoHelper.loadFrescoImage(ivBg, artistInfo.getAvatar_s500(), 0, false, new Point(720, 480),new ColorFilterPostprocessor(Color.parseColor("#70000000")));
                }
            }

            @Override
            public void onFail(HttpException e) {
                loadingDialog.onDismiss();
                Toasty.error(getActivity(), e.getMessage()).show();
            }
        });
    }
}