package com.yjl.funk.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.jakewharton.rxbinding.view.RxView;
import com.yjl.funk.R;
import com.yjl.funk.ui.activity.MainActivity;
import com.yjl.funk.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.functions.Action1;

/**
 * Created by Administrator on 2017/11/10.
 */

public class TabFragment extends BaseFragment{
    @BindView(R.id.iv_menu)
    ImageView ivMenu;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.iv_search)
    ImageView ivSearch;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    private Unbinder unbinder;
    @BindArray(R.array.main_tab)
    String[] titles;
    private List<Fragment> list = new ArrayList<>();
    private MainMusicFragment mainMusicFragment;
    private MyMusicFragment myMusicFragment;
    private PagerAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab, container, false);
        unbinder = ButterKnife.bind(this, view);
        initViews();
        initListeners();
        return view;
    }

    @Override
    public void initViews() {
        mainMusicFragment = new MainMusicFragment();
        myMusicFragment = new MyMusicFragment();
        list.add(mainMusicFragment);
        list.add(myMusicFragment);
        //ViewPager的适配器
        adapter = new PagerAdapter(getChildFragmentManager());
        viewpager.setAdapter(adapter);
        //绑定
        tabLayout.setupWithViewPager(viewpager);
        tabLayout.getTabAt(0).setText(titles[0]);
        tabLayout.getTabAt(1).setText(titles[1]);
        //设置当前页面
        viewpager.setCurrentItem(0);
        viewpager.setOffscreenPageLimit(2);
    }

    @Override
    public void initListeners() {
        RxView.clicks(ivMenu).throttleFirst(1, TimeUnit.SECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                MainActivity.getInstance().openDrawLayout();
            }
        });
        RxView.clicks(ivSearch).throttleFirst(1, TimeUnit.SECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                MainActivity.getInstance().switchSearchFragment();
            }
        });
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
}
