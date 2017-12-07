package com.yjl.funk.ui.base;


import android.content.ComponentName;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import com.yjl.funk.IFunkService;
import com.yjl.funk.player.MusicClient;
import com.yjl.funk.utils.ScreenUtils;

import butterknife.ButterKnife;


/**
 * Created by Administrator on 2017/3/2.
 */

public abstract class BaseMusicActivity extends AppCompatActivity  implements ServiceConnection {
    private MusicClient.ServiceToken mToken;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(OnLayoutInit());
        ButterKnife.bind(this);
        initToolBar();
        initViews();
        initListeners();
        ScreenUtils.setSystemBarTransparent(this);

        mToken = MusicClient.bindToService(this, this);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }


    public abstract int OnLayoutInit();
    /**
     * 初始化控件
     *
     */
    public abstract void initViews();

    /**
     * 监听设置
     */
    public abstract void initListeners();
    /**
     * 初始化工具栏*/
    public abstract void initToolBar();
    /**
     * app字体不随系统字体的大小改变而改变
     */

    @Override
    public void onServiceConnected(final ComponentName name, final IBinder service) {
        MusicClient.mService = IFunkService.Stub.asInterface(service);

    }


    @Override
    public void onServiceDisconnected(final ComponentName name) {
       MusicClient.mService = null;
    }
    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();

    }
    @Override
    protected void onStart(){
        super.onStart();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解注eventbus
        if (mToken != null) {
            MusicClient.unbindFromService(mToken);
            mToken = null;
        }
    }

}
