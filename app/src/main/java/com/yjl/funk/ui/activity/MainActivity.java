package com.yjl.funk.ui.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.yjl.funk.R;
import com.yjl.funk.imageloader.frescohelper.FrescoHelper;
import com.yjl.funk.imageloader.frescoview.FrescoImageView;
import com.yjl.funk.model.FragmentName;
import com.yjl.funk.model.MusicEvent;
import com.yjl.funk.model.MusicTrack;
import com.yjl.funk.player.Constants;
import com.yjl.funk.player.MusicClient;
import com.yjl.funk.ui.base.BaseMusicActivity;
import com.yjl.funk.ui.fragment.AlbumInfoFragment;
import com.yjl.funk.ui.fragment.AlbumListFragment;
import com.yjl.funk.ui.fragment.ArtistInfoFragment;
import com.yjl.funk.ui.fragment.BillBoardFragment;
import com.yjl.funk.ui.fragment.PlayingFragment;
import com.yjl.funk.ui.fragment.SearchFragment;
import com.yjl.funk.ui.fragment.TabFragment;
import com.yjl.funk.utils.FileUtils;
import com.yjl.funk.utils.PermissionRequest;
import com.yjl.funk.utils.RxBus;
import com.yjl.funk.utils.ScreenUtils;
import com.yjl.funk.widget.RoundProgressBar;
import com.yjl.funk.widget.Toasty;
import com.yjl.funk.widget.lrc.LRCTextView;
import com.yjl.funk.widget.lrc.LrcEntry;
import com.yjl.funk.widget.lrc.LrcFinder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.functions.Action1;


public class MainActivity extends BaseMusicActivity {
    private static MainActivity mInstance;
    @BindView(R.id.status_bar)
    View statusBar;
    @BindView(R.id.navigation_view)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.content_container)
    FrameLayout contentContainer;
    @BindView(R.id.iv_icon)
    FrescoImageView ivIcon;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_subtitle)
    LRCTextView tvSubtitle;
    @BindView(R.id.quick_control_container)
    FrameLayout quickControlContainer;
    @BindView(R.id.progressBar)
    RoundProgressBar progressBar;
    @BindView(R.id.iv_play_pause)
    ImageView ivPlayPause;
    @BindView(R.id.iv_play_list)
    ImageView ivPlayList;
    @BindView(R.id.playing_container)
    FrameLayout playingContainer;
    private List<LrcEntry> lrcEntryList = new ArrayList<>();
    //播放界面是否显示
    private boolean isShow = false;
    PlayingFragment playingFragment;
    private TabFragment tabFragment;
    Handler controlHandler = new Handler();
    private static int DELAY = 500;
    //接收后台音乐状态变化广播接收器
    private PlayerStatusReceiver receiver = new PlayerStatusReceiver();
    private Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            if (MusicClient.isPlaying()) {
                int progress = MusicClient.getPosition()+DELAY;
                progressBar.setProgress(progress);
                if(playingFragment!=null&&playingFragment.isAdded()){
                    playingFragment.updateProgress(progress,MusicClient.getSecondPosition(),MusicClient.getDuration());
                }
                if (!lrcEntryList.isEmpty()) {
                    int line = LrcFinder.findShowLine(lrcEntryList,progress);
                    if(lrcEntryList.get(line)!=null) {
                        String lrcText = lrcEntryList.get(line).getText();
                        long time = 0;
                        if(line<lrcEntryList.size()-1) {
                             time = lrcEntryList.get(line + 1).getTime() - lrcEntryList.get(line).getTime();
                        }
                        tvSubtitle.setLrc(lrcText, time,line);
                        if(playingFragment!=null&&playingFragment.isAdded()){
                            playingFragment.updateLrc(lrcText,time,line);
                        }
                    }
                }
                progressBar.postDelayed(progressRunnable, DELAY);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionsRequest();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.MUSIC_CHANGED);
        filter.addAction(Constants.PLAYSTATE_CHANGED);
        filter.addAction(Constants.PLAYLIST_CHANGED);
        filter.addAction(Constants.TRACK_ERROR);
        filter.addAction(Constants.LRC_DOWNLOADED);
        filter.addAction(Constants.LRC_ERROR);
        registerReceiver(receiver, filter);
        if (savedInstanceState != null) {
            tabFragment = (TabFragment) getSupportFragmentManager().getFragment(savedInstanceState, FragmentName.TAB);
            playingFragment = (PlayingFragment) getSupportFragmentManager().getFragment(savedInstanceState, FragmentName.PLAYING);
        }
    }

    @Override
    public int OnLayoutInit() {
        mInstance = this;
        return R.layout.activity_main;
    }

    @Override
    public void initViews() {
        AppBarLayout.LayoutParams layoutParams = new AppBarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.getStatusBarHeight(this));
        statusBar.setLayoutParams(layoutParams);
        switchTabFragment();
        tvSubtitle.setTextStyle(R.color.text_gray,R.color.colorAccent,12,Gravity.LEFT);
    }

    @Override
    public void initListeners() {
        //播放暂停按钮
        RxView.clicks(ivPlayPause).throttleFirst(300, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                MusicClient.playOrPause();
            }
        });
        //点击底部快速控制栏;
        RxView.clicks(quickControlContainer).throttleFirst(300, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                showPlayingFragment();
            }
        });
    }

    @Override
    public void initToolBar() {

    }

    public static MainActivity getInstance() {
        return mInstance;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    public void permissionsRequest() {
        PermissionRequest.with(this)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .result(new PermissionRequest.Result() {
                    @Override
                    public void onGranted() {

                    }

                    @Override
                    public void onDenied() {

                    }
                })
                .request();
    }

    @Override
    public void onBackPressed() {
        if (isShow) {
            //确保回退栈没有fragment时开启侧滑
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }
            hidePlayingFragment();
            return;
        }
        //fragment回退栈中没有fragment时
        if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
            //如果回到主界面 开启侧滑
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }
        } else {
            if (drawerLayout.isDrawerOpen(Gravity.START)) {
                drawerLayout.closeDrawers();
            } else {
                moveTaskToBack(true);
            }
            return;
        }
        super.onBackPressed();
    }

    //打开抽屉
    public void openDrawLayout() {
        controlHandler.post(new Runnable() {
            @Override
            public void run() {
                drawerLayout.openDrawer(Gravity.START);
            }
        });
    }

    //主界面
    public void switchTabFragment() {
        controlHandler.post(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if (tabFragment == null)
                    tabFragment = new TabFragment();
                else {
                    tabFragment = (TabFragment) getSupportFragmentManager().findFragmentByTag(FragmentName.TAB);
                }
                transaction.add(R.id.content_container, tabFragment, FragmentName.TAB).commitAllowingStateLoss();
            }
        });
    }

    //榜单界面
    public void switchBillBoardFragment(final String type) {

        controlHandler.post(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in, 0, 0, R.anim.slide_out);
                BillBoardFragment billBoardFragment = BillBoardFragment.newInstance(type);
                transaction.add(R.id.content_container, billBoardFragment, FragmentName.BILLBOARD).addToBackStack(FragmentName.BILLBOARD).commitAllowingStateLoss();
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }
        });
    }

    //新专上架界面
    public void switchAlbumListFragment() {

        controlHandler.post(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in, 0, 0, R.anim.slide_out);
                AlbumListFragment albumListFragment = new AlbumListFragment();
                transaction.add(R.id.content_container, albumListFragment, FragmentName.NEW_ALBUM).addToBackStack(FragmentName.NEW_ALBUM).commitAllowingStateLoss();
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }
        });
    }

    //专辑详情界面
    public void switchAlbumInfoFragment(final String album_id) {

        controlHandler.post(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in, 0, 0, R.anim.slide_out);
                AlbumInfoFragment albumInfoFragment = AlbumInfoFragment.newInstance(album_id);
                transaction.add(R.id.content_container, albumInfoFragment, FragmentName.ALBUM_INFO).addToBackStack(FragmentName.ALBUM_INFO).commitAllowingStateLoss();
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }
        });
    }

    //歌手详情
    public void switchArtistInfoFragment(final String tinguid) {

        controlHandler.post(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in, 0, 0, R.anim.slide_out);
                ArtistInfoFragment artistInfoFragment = ArtistInfoFragment.newInstance(tinguid);
                transaction.add(R.id.content_container, artistInfoFragment, FragmentName.ARTIST_INFO).addToBackStack(FragmentName.ARTIST_INFO).commitAllowingStateLoss();
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }
        });
    }

    //搜索界面
    public void switchSearchFragment() {

        controlHandler.post(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in, 0, 0, R.anim.slide_out);
                SearchFragment searchFragment = new SearchFragment();
                transaction.add(R.id.content_container, searchFragment, FragmentName.SEARCH).addToBackStack(FragmentName.SEARCH).commitAllowingStateLoss();
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }
        });
    }

    //显示播放界面
    private void showPlayingFragment() {
        if (isShow) {
            return;
        }
        controlHandler.post(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in, 0);
                if (playingFragment == null) {
                    playingFragment = new PlayingFragment();
                    transaction.add(android.R.id.content, playingFragment, FragmentName.PLAYING);
                } else if (playingFragment.isAdded()) {
                    transaction.show(playingFragment);
                }
                transaction.commitAllowingStateLoss();
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                isShow = true;
            }
        });
    }

    //关闭播放界面
    public void hidePlayingFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(0, R.anim.slide_out);
        transaction.hide(playingFragment);
        transaction.commitAllowingStateLoss();
        isShow = false;
    }

    class PlayerStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                //切换歌曲
                case Constants.MUSIC_CHANGED:
                    updateQuickControl(Constants.MUSIC_CHANGED);
                    RxBus.getDefault().post(new MusicEvent(action));
                    break;
                //播放列表变化
                case Constants.PLAYLIST_CHANGED:
                    break;
                //播放状态监听
                case Constants.PLAYSTATE_CHANGED:
                    updateQuickControl(Constants.PLAYSTATE_CHANGED);
                    RxBus.getDefault().post(new MusicEvent(action));
                    break;
                //歌词下载成功
                case Constants.LRC_DOWNLOADED:
                    updateQuickControl(Constants.LRC_DOWNLOADED);
                    break;
                //获取歌词失败
                case Constants.LRC_ERROR:
                    updateQuickControl(Constants.LRC_ERROR);
                    RxBus.getDefault().post(new MusicEvent(action));
                    break;
                //播放异常
                case Constants.TRACK_ERROR:
                    String errInfo = intent.getStringExtra(Constants.TRACK_ERROR_INFO);
                    Toasty.error(MainActivity.this, errInfo).show();
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    public void updateQuickControl(String action) {
        MusicTrack track = MusicClient.getCurrentTrack();
        switch (action) {
            case Constants.MUSIC_CHANGED:
                lrcEntryList.clear();
                if (track != null) {
                    String cover = "";
                    if (TextUtils.isEmpty(track.getPic_big())) {
                        if (!TextUtils.isEmpty(track.getPic_small()))
                            cover = track.getPic_small().split("\\@")[0] + "@s_1,w_500,h_500";

                    } else {
                        cover = track.getPic_big();
                    }
                    FrescoHelper.loadFrescoImage(ivIcon, cover, R.drawable.default_load_cover_small, false);
                    if (!MusicClient.isPlaying()) {
                        tvSubtitle.setText(track.getAuthor());
                    }
                    tvTitle.setText(track.getTitle());
                    progressBar.setProgress(1000);
                }
                break;
            case Constants.PLAYSTATE_CHANGED:
                if (MusicClient.isPlaying() || MusicClient.isPreparing()) {
                    if(!lrcEntryList.isEmpty())
                    tvSubtitle.start();
                    ivPlayPause.setImageResource(R.drawable.ic_pause_black);
                } else {
                    if(!lrcEntryList.isEmpty())
                    tvSubtitle.pause();
                    ivPlayPause.setImageResource(R.drawable.ic_play_pink);
                }
                progressBar.setMax(MusicClient.getDuration());
                progressBar.postDelayed(progressRunnable, 10);
                break;
            case Constants.LRC_ERROR:
                if (track != null)
                    tvSubtitle.setText(track.getAuthor());
                break;
            case Constants.LRC_DOWNLOADED:
                if(track!=null) {
                    String lrc = FileUtils.getLrcDir()+track.getSong_id() + FileUtils.LRC;
                    File lrcFile = new File(lrc);
                    if (lrcFile.exists())
                        loadLrc(lrcFile);
                }
                break;
        }
    }
    public List<LrcEntry> getLrcEntryList(){
        return lrcEntryList;
    }

    //加载歌词
    public void loadLrc(final File lrcFile) {


        new AsyncTask<File, Integer, List<LrcEntry>>() {
            @Override
            protected List<LrcEntry> doInBackground(File... params) {
                return LrcEntry.parseLrc(params[0]);
            }

            @Override
            protected void onPostExecute(List<LrcEntry> lrcEntries) {
                lrcEntryList.addAll(lrcEntries);
                if(!lrcEntryList.isEmpty())
                    RxBus.getDefault().post(new MusicEvent(Constants.LRC_DOWNLOADED));
            }
        }.execute(lrcFile);
    }

}
