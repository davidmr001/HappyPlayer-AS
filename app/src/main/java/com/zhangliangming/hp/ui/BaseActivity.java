package com.zhangliangming.hp.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;


/**
 * Created by zhangliangming on 2016/12/17.
 */

public abstract class BaseActivity extends Activity {

    /**
     * 是否设置状态栏
     */
    private boolean hasSBV = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //获取界面
        ViewGroup contentView = (ViewGroup) LayoutInflater.from(getApplicationContext()).inflate(setContentViewId(), null);

        //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
        //设置界面内容
        setContentView(contentView);
        onCreate();
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    private static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier(
                "status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    /**
     * 设置主界面的布局文件
     *
     * @return
     */
    public abstract int setContentViewId();

    /**
     *
     */
    public abstract void onCreate();

    /**
     * 设置状态栏view
     * @param titleView
     */
    public void setStatusBarView(View titleView) {
        ViewGroup titleViewGroup = (ViewGroup) titleView;

        //添加状态栏
        int statusBarHeight = getStatusBarHeight(this);
        View statusBarView = new View(this);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                statusBarHeight);
        statusBarView.setBackgroundColor(getResources().getColor(R.color.titleBG));
        titleViewGroup.addView(statusBarView, 0, lp);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.activity_ani_exist);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.activity_ani_exist);
    }
}
