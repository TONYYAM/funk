package com.yjl.funk.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.yjl.funk.R;
import com.yjl.funk.imageloader.frescohelper.FrescoHelper;
import com.yjl.funk.imageloader.frescoview.FrescoImageView;
import com.yjl.funk.imageloader.processors.BlurPostprocessor;
import com.yjl.funk.model.MusicEvent;
import com.yjl.funk.model.MusicTrack;
import com.yjl.funk.player.Constants;
import com.yjl.funk.player.MusicClient;
import com.yjl.funk.ui.activity.MainActivity;
import com.yjl.funk.ui.adapter.PlayPagerAdapter;
import com.yjl.funk.ui.base.BaseFragment;
import com.yjl.funk.utils.RxBus;
import com.yjl.funk.widget.AlbumCircleView;
import com.yjl.funk.widget.DotView;
import com.yjl.funk.widget.Toasty;
import com.yjl.funk.widget.lrc.LRCTextView;
import com.yjl.funk.widget.lrc.LrcUtils;
import com.yjl.funk.widget.lrc.LrcView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by Administrator on 2017/11/17.
 */

public class PlayingFragment extends BaseFragment {
    LRCTextView tvLrcSingle;
    AlbumCircleView ivAlbum;
    LrcView lyricView;
    @BindView(R.id.iv_play_mode)
    ImageView ivPlayMode;
    @BindView(R.id.iv_prev)
    ImageView ivPrev;
    @BindView(R.id.iv_play)
    ImageView ivPlay;
    @BindView(R.id.iv_next)
    ImageView ivNext;
    @BindView(R.id.iv_play_list)
    ImageView ivPlayList;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_progress)
    TextView tvProgress;
    @BindView(R.id.tv_duration)
    TextView tvDuration;
    @BindView(R.id.seek_progress)
    SeekBar seekProgress;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.dot_view)
    DotView dotView;
    @BindView(R.id.iv_detail)
    ImageView ivDetail;
    private Unbinder unbinder;
    @BindView(R.id.iv_bg)
    FrescoImageView ivBg;
    Subscription busSubscription;
    MusicTrack track;
    private List<View> views = new ArrayList<>();
    private String coverPath = "";
    private AnimatorSet animationSet;
    private boolean IS_DRAGGING = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playing, container, false);
        unbinder = ButterKnife.bind(this, view);
        initViews();
        initListeners();
        return view;
    }

    @Override
    public void initViews() {
        registerRxBus();
        View albumView = LayoutInflater.from(getActivity()).inflate(R.layout.view_play_album, null);
        View lrcView = LayoutInflater.from(getActivity()).inflate(R.layout.view_play_lrc, null);
        views.add(albumView);
        views.add(lrcView);
        dotView.setNum(2);
        dotView.setSelected(0);
        tvLrcSingle = ButterKnife.findById(albumView, R.id.tv_lrc_single);
        ivAlbum = ButterKnife.findById(albumView, R.id.iv_album);
        lyricView = ButterKnife.findById(lrcView, R.id.lrc_view);
        viewpager.setAdapter(new PlayPagerAdapter(views));
        tvLrcSingle.setTextStyle(R.color.white, R.color.colorAccent, 13, Gravity.CENTER);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                track = MusicClient.getCurrentTrack();
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.getInstance().onBackPressed();
                    }
                });
                toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
                if (track != null) {
                    toolbar.setTitle(track.getTitle());
                    toolbar.setSubtitle(track.getAuthor());
                }
                switch (MusicClient.getPlayMode()) {
                    case Constants.REPEAT_ALL:
                        ivPlayMode.setImageResource(R.drawable.ic_cycle);
                        break;
                    case Constants.REPEAT_CURRENT:
                        ivPlayMode.setImageResource(R.drawable.ic_repeat);
                        break;
                    case Constants.SHUFFLE:
                        ivPlayMode.setImageResource(R.drawable.ic_shuffle);
                        break;
                }
                loadAlbum(track);
                initStateChange();
                seekProgress.setMax(MusicClient.getDuration());
                seekProgress.setProgress(MusicClient.getPosition());
                tvDuration.setText(LrcUtils.formatTime(MusicClient.getDuration()));
                tvProgress.setText(LrcUtils.formatTime(MusicClient.getPosition()));
                if (MusicClient.isPlaying() || !MainActivity.getInstance().getLrcEntryList().isEmpty())
                    lyricView.setLrcEntryList(MainActivity.getInstance().getLrcEntryList());

            }
        },200);
    }

    @Override
    public void initListeners() {
        //播放模式
        RxView.clicks(ivPlayMode).throttleFirst(1, TimeUnit.SECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                int mode = MusicClient.getPlayMode();
                switch (mode) {
                    case Constants.REPEAT_ALL:
                        ivPlayMode.setImageResource(R.drawable.ic_shuffle);
                        MusicClient.setPlayMode(Constants.SHUFFLE);
                        Toasty.info(getActivity(), "随机播放", 100).show();
                        break;
                    case Constants.REPEAT_CURRENT:
                        ivPlayMode.setImageResource(R.drawable.ic_cycle);
                        MusicClient.setPlayMode(Constants.REPEAT_ALL);
                        Toasty.info(getActivity(), "列表循环", 100).show();
                        break;
                    case Constants.SHUFFLE:
                        ivPlayMode.setImageResource(R.drawable.ic_repeat);
                        MusicClient.setPlayMode(Constants.REPEAT_CURRENT);
                        Toasty.info(getActivity(), "单曲循环", 100).show();
                        break;
                }
            }
        });
        //上一首
        RxView.clicks(ivPrev).throttleFirst(1, TimeUnit.SECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                MusicClient.prev();
            }
        });
        //暂停或播放
        RxView.clicks(ivPlay).throttleFirst(100, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                MusicClient.playOrPause();
            }
        });
        //下一首
        RxView.clicks(ivNext).throttleFirst(1, TimeUnit.SECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                MusicClient.next();
            }
        });
        //播放列表
        RxView.clicks(ivPlayList).throttleFirst(300, TimeUnit.MICROSECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {

            }
        });
        //歌曲详情
        RxView.clicks(ivDetail).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                showDetailDialog(MusicClient.getCurrentTrack());
            }
        });
        //seekBar
        seekProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvProgress.setText(LrcUtils.formatTime(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                IS_DRAGGING = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                IS_DRAGGING = false;
                tvProgress.setText(LrcUtils.formatTime(seekBar.getProgress()));
                tvLrcSingle.setText("");
                MusicClient.seekTo(seekBar.getProgress());
            }
        });
        //歌词拖动事件
        lyricView.setOnPlayClickListener(new LrcView.OnPlayClickListener() {
            @Override
            public boolean onPlayClick(long time) {
                MusicClient.seekTo((int) time);
                return true;
            }
        });
        //viewpager
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                dotView.setSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void registerRxBus() {

        //注册播放器变化观察者
        busSubscription = RxBus.getDefault().toObservable(MusicEvent.class).subscribe(new Action1<MusicEvent>() {
            @Override
            public void call(MusicEvent musicEvent) {
                switch (musicEvent.getMsg()) {
                    case Constants.MUSIC_CHANGED:
                        track = MusicClient.getCurrentTrack();
                        if (animationSet != null && animationSet.isRunning())
                            animationSet.cancel();
                        musicChangeAnim(track);
                        if (track != null) {
                            toolbar.setTitle(track.getTitle());
                            toolbar.setSubtitle(track.getAuthor());
                        }
                        seekProgress.setProgress(0);
                        lyricView.setLabel("搜索歌词中...");
                        lyricView.reset();
                        tvLrcSingle.setText("");
                        break;
                    case Constants.PLAYSTATE_CHANGED:
                        initStateChange();
                        break;
                    case Constants.LRC_DOWNLOADED:
                        lyricView.setLrcEntryList(MainActivity.getInstance().getLrcEntryList());
                        break;
                    case Constants.LRC_ERROR:
                        tvLrcSingle.setText("");
                        lyricView.setLabel("暂无歌词");
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (busSubscription != null && !busSubscription.isUnsubscribed()) {
            busSubscription.unsubscribe();
        }
    }

    public void loadAlbum(MusicTrack track) {
        String cover;
        if (track != null) {
            if (!TextUtils.isEmpty(track.getPic_big())) {
                cover = track.getPic_big().split("\\@")[0] + "@s_1,w_1000,h_1000";
            } else {
                cover = track.getPic_small().split("\\@")[0] + "@s_1,w_1000,h_1000";
            }
            //判断与上次封面是否一样避免重新加载
            if (!TextUtils.isEmpty(cover) && !coverPath.equals(cover)) {
                coverPath = cover;
                FrescoHelper.loadFrescoImage(ivBg, coverPath, 0, false, new BlurPostprocessor(getActivity(), 25));
                FrescoHelper.loadFrescoImage(ivAlbum, coverPath, R.drawable.default_load_cover_circle, false);
            }
        }
    }

    public void updateLrc(String lrc, long mills, int line) {
        tvLrcSingle.setLrc(lrc, mills, line);
    }

    public void updateProgress(int progress,int secondProgress,int duration) {
        if (!IS_DRAGGING) {
            seekProgress.setProgress(progress);
            tvProgress.setText(LrcUtils.formatTime(progress));
        }
        seekProgress.setSecondaryProgress(secondProgress);
        lyricView.updateTime(progress);
        tvDuration.setText(LrcUtils.formatTime(duration));
    }

    public void initStateChange() {
        //初始化数据
        if (MusicClient.isPlaying() || MusicClient.isPreparing()) {
            ivAlbum.startRotation();
            tvLrcSingle.start();
            seekProgress.setMax(MusicClient.getDuration());
            ivPlay.setImageResource(R.drawable.ic_pause_large);
            if (MainActivity.getInstance().getLrcEntryList().isEmpty()) {
                tvLrcSingle.setText("");
            }
        } else {

            tvLrcSingle.pause();
            ivPlay.setImageResource(R.drawable.ic_play_large);
            ivAlbum.pauseRotation();
        }
    }

    //音乐切换动画
    public void musicChangeAnim(final MusicTrack track) {
        long duration = 250;
        animationSet = new AnimatorSet();
        ObjectAnimator scaleSmallX = ObjectAnimator.ofFloat(ivAlbum, "scaleX", 1f, 0.5f);
        scaleSmallX.setDuration(duration);
        ObjectAnimator scaleSmallY = ObjectAnimator.ofFloat(ivAlbum, "scaleY", 1f, 0.5f);
        scaleSmallY.setDuration(duration);
        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(ivAlbum, "scaleX", 0.5f, 1f);
        scaleUpX.setDuration(duration);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(ivAlbum, "scaleY", 0.5f, 1f);
        scaleUpY.setDuration(duration);
        animationSet.play(scaleSmallX).with(scaleSmallY);
        animationSet.play(scaleUpX).with(scaleUpY).after(scaleSmallX);
        scaleSmallY.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                loadAlbum(track);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animationSet.start();
    }

    public void showDetailDialog(final MusicTrack track) {
        final BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.view_music_detail, null);
        TextView tvTitle = ButterKnife.findById(dialogView,R.id.tv_title);
        TextView tvAuthor = ButterKnife.findById(dialogView,R.id.tv_author);
        TextView tvAlbum = ButterKnife.findById(dialogView,R.id.tv_album);
        if(track!=null) {
            tvTitle.setText("歌曲：" + track.getTitle());
            tvAuthor.setText("歌手：" + track.getAuthor());
            tvAlbum.setText("专辑："+track.getAlbum_title());
            RxView.clicks(tvAuthor).throttleFirst(1,TimeUnit.SECONDS).subscribe(new Action1<Void>() {
                @Override
                public void call(Void aVoid) {
                    dialog.dismiss();
                    MainActivity.getInstance().hidePlayingFragment();
                  MainActivity.getInstance().switchArtistInfoFragment(track.getTing_uid());
                }
            });
            RxView.clicks(tvAlbum).throttleFirst(1,TimeUnit.SECONDS).subscribe(new Action1<Void>() {
                @Override
                public void call(Void aVoid) {
                    dialog.dismiss();
                    MainActivity.getInstance().hidePlayingFragment();
                    MainActivity.getInstance().switchAlbumInfoFragment(track.getAlbum_id());
                }
            });
        }
        dialog.setContentView(dialogView);
        dialog.setCancelable(true);
        dialog.show();
    }
}
