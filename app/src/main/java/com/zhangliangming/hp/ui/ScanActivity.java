package com.zhangliangming.hp.ui;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import com.zhangliangming.hp.ui.util.ActivityManager;
import com.zhangliangming.hp.ui.widget.PressedRelativeLayout;

/**
 * 扫描歌曲界面
 */
public class ScanActivity extends BaseActivity {
    /**
     * 返回按钮
     */
    private PressedRelativeLayout backButton;
    /**
     * 扫描按钮
     */
    private PressedRelativeLayout scanButton;

    @Override
    public int setContentViewId() {
        return R.layout.activity_scan;
    }

    @Override
    public void onCreate() {
        //设置状态栏
        LinearLayout statusBarView = (LinearLayout) findViewById(R.id.statusBarView);
        setStatusBarView(statusBarView);
        init();
        //
        ActivityManager.getInstance().addActivity(this);
    }

    private void init() {
        //返回按钮
        backButton = (PressedRelativeLayout) findViewById(R.id.backParent);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //扫描按钮
        scanButton = (PressedRelativeLayout) findViewById(R.id.scanParent);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScanActivity.this, ScaningActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }
}
