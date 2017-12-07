package com.yjl.funk.ui.base;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import com.yjl.funk.widget.DataEmptyView;
import com.yjl.funk.widget.NetWorkErrorView;


public abstract class BaseFragment extends Fragment {
    protected Activity mActivity;
    public NetWorkErrorView netWorkErrorView;
    public DataEmptyView emptyView;

    public abstract void initViews();

    /**
     * 监听设置
     */
    public abstract void initListeners();

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (Activity)context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}