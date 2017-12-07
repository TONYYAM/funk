package com.yjl.funk.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yjl.funk.R;
import com.yjl.funk.model.SearchResult;
import com.yjl.funk.model.MusicTrack;
import com.yjl.funk.network.HttpCallback;
import com.yjl.funk.network.HttpClient;
import com.yjl.funk.network.HttpException;
import com.yjl.funk.ui.activity.MainActivity;
import com.yjl.funk.ui.adapter.OnlineMusicListAdapter;
import com.yjl.funk.ui.base.BaseFragment;
import com.yjl.funk.widget.LoadingDialog;
import com.yjl.funk.widget.Toasty;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017/11/10.
 */

public class SearchFragment extends BaseFragment {
    private Unbinder unbinder;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.ll_result)
    LinearLayout llResult;
    @BindView(R.id.search_view)
    SearchView searchView;
    @BindView(R.id.rv_result)
    RecyclerView rvResult;
    private OnlineMusicListAdapter musicListAdapter;
    private List<MusicTrack> songList = new ArrayList<>();
    private LoadingDialog loadingDialog;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        unbinder = ButterKnife.bind(this, view);
        initViews();
        initListeners();
        return view;
    }

    @Override
    public void initViews() {
        loadingDialog = new LoadingDialog(getActivity());
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.clearFocus();
               MainActivity.getInstance().onBackPressed();
            }
        });
           setSearchView();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvResult.setLayoutManager(layoutManager);
        musicListAdapter = new OnlineMusicListAdapter(getActivity(),R.layout.item_music,songList,false,true);
        rvResult.setAdapter(musicListAdapter);
    }

    @Override
    public void initListeners() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    private void setSearchView() {

        //设置展开后图标的样式,false时ICON在搜索框外,true为在搜索框内，无法修改
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("搜索音乐");
        searchView.setSubmitButtonEnabled(false);

        //设置最右侧的提交按钮
        ImageView mCollapsedIcon = (ImageView) searchView.findViewById(R.id.search_mag_icon);
        mCollapsedIcon.setImageDrawable(ActivityCompat.getDrawable(getActivity(),R.drawable.ic_search));
        TextView textView = (TextView) searchView
                .findViewById(android.support.v7.appcompat.R.id.search_src_text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        textView.setHintTextColor(ActivityCompat.getColor(getActivity(), R.color.text_hint)); //hint文字颜色
        textView.setTextColor(Color.WHITE);  //输入的文字颜色
        ImageView closeButton = (ImageView) searchView
                .findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        closeButton.setImageDrawable(ActivityCompat.getDrawable(getActivity(),R.drawable.ic_close_search));
        //光标颜色
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(textView, R.color.white);
        } catch (Exception e) {

        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getData(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
    }

    public void getData(String keyWord) {

        if (!TextUtils.isEmpty(keyWord)) {
            loadingDialog.onShow();
            HttpClient.searchMusic(keyWord, new HttpCallback<SearchResult>() {
                @Override
                public void onSuccess(SearchResult searchResult) {
                    llResult.setVisibility(View.VISIBLE);
                    loadingDialog.onDismiss();
                    if (searchResult!=null&&searchResult.getResult()!=null&&searchResult.getResult().getSong_info()!=null) {
                        if(searchResult.getResult().getSong_info().getSong_list().size()!=0) {
                            songList.clear();
                            songList.addAll(searchResult.getResult().getSong_info().getSong_list());
                            musicListAdapter.setNewData(searchResult.getResult().getSong_info().getSong_list());
                        }
                        else {
                            musicListAdapter.setEmptyView(R.layout.view_status_empty);
                        }
                    }
                }

                @Override
                public void onFail(HttpException e) {
                    loadingDialog.onDismiss();
                    Toasty.error(getActivity(), e.getMessage()).show();
                }
            });
        }
        else {
            Toasty.error(getActivity(), "请输入您要搜索的内容").show();
        }
    }
}