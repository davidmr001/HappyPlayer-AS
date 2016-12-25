package com.zhangliangming.hp.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.view.KeyEvent;

import com.zhangliangming.hp.ui.util.DataUtil;


/**
 * 启动页面
 */
public class SplashActivity extends BaseActivity {

    @Override
    public int setContentViewId() {
        return R.layout.activity_splash;
    }

    @Override
    public void onCreate() {
        loadData();
    }

    /**
     * 加载数据
     */
    private void loadData() {
        new AsyncTask<String, Integer, String>() {

            @Override
            protected String doInBackground(String... strings) {
                try {
                    DataUtil.init(SplashActivity.this);
                    Thread.sleep(1 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                goHome();
            }
        }.execute("");
    }

    /**
     * 跳转到主页面
     */
    private void goHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
        startActivity(intent);
        // 添加界面切换效果，注意只有Android的2.0(SdkVersion版本号为5)以后的版本才支持
        int version = Integer.valueOf(Build.VERSION.SDK_INT);
        if (version >= 5) {
            overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
        }
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
