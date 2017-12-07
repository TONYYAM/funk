package com.yjl.funk.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.yjl.funk.R;
import com.yjl.funk.utils.DensityUtils;


/**
 * Created by Administrator on 2017/3/18.
 * 加载弹窗
 */

public class LoadingDialog extends Dialog {
    private Context context;
    private LoadingProgressView loadingProgressView;
    public LoadingDialog(Context context) {
        this(context, R.style.LoadingDialogStyle);

    }

    public LoadingDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        initView();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       this.dismiss();
    }
    public void initView(){
        View view = View.inflate(context,R.layout.loading_dialog,null);
        loadingProgressView = (LoadingProgressView) view.findViewById(R.id.load_view);
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        lp.width = DensityUtils.dip2px(context,75); // 宽度
        lp.height = DensityUtils.dip2px(context,75); // 高度
        this.addContentView(view,lp);
        this.setCancelable(false);
    }
    public void onShow(){
        loadingProgressView.post(new Runnable() {
            @Override
            public void run() {
                loadingProgressView.startAnim();
            }
        });
        this.show();
    }
    public void onDismiss() {
        if (loadingProgressView != null)
            loadingProgressView.post(new Runnable() {
                @Override
                public void run() {
                    loadingProgressView.stopAnim();
                }
            });

        if (this.isShowing())
            this.dismiss();
    }

}
