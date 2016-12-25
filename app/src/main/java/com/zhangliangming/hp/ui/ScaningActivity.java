package com.zhangliangming.hp.ui;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.zhangliangming.hp.ui.db.SongDB;
import com.zhangliangming.hp.ui.model.SongInfo;
import com.zhangliangming.hp.ui.model.SongMessage;
import com.zhangliangming.hp.ui.model.StorageInfo;
import com.zhangliangming.hp.ui.observable.ObserverManage;
import com.zhangliangming.hp.ui.util.ActivityManager;
import com.zhangliangming.hp.ui.util.AudioFilter;
import com.zhangliangming.hp.ui.util.MediaUtils;
import com.zhangliangming.hp.ui.util.StorageListUtil;
import com.zhangliangming.hp.ui.widget.BaseColorImageButton;

import java.io.File;
import java.util.List;

/**
 * 扫描中
 */
public class ScaningActivity extends BaseActivity {
    /**
     * 是否完成
     */
    private boolean isFinish = false;

    /**
     * 扫描按钮
     */
    private BaseColorImageButton scanFinishButton;
    /**
     * 扫描结果
     */
    private TextView scaningTipTextView;
    /**
     * 扫描路径
     */
    private TextView pathTextView;

    /**
     * 歌曲首数
     */
    private int songSize = 0;

    private Handler scanHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    // 开始扫描
                    scanFinishButton.setVisibility(View.INVISIBLE);
                    pathTextView.setVisibility(View.VISIBLE);

                    break;
                case 1:
                    // 扫描完成
                    scanFinishButton.setVisibility(View.VISIBLE);
                    pathTextView.setVisibility(View.INVISIBLE);
                    break;
                case 2:
                    // 扫描中
                    String path = (String) msg.obj;
                    pathTextView.setText(path);
                    break;
                default:
                    break;
            }
            scaningTipTextView.setText("已添加歌曲" + songSize + "首");
        }

    };


    @Override
    public int setContentViewId() {
        return R.layout.activity_scaning;
    }

    @Override
    public void onCreate() {
        init();
        loadData();
    }

    private void init() {
        ActivityManager.getInstance().addActivity(this);

        scaningTipTextView = (TextView) findViewById(R.id.scaningTip);
        pathTextView = (TextView) findViewById(R.id.scaningPathTip);

        //扫描完成
        scanFinishButton = (BaseColorImageButton) findViewById(R.id.scanFinishButton);
        scanFinishButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }

    private void loadData() {
        new AsyncTask<String, Integer, String>() {

            protected String doInBackground(String... arg0) {
                scanStart();
                scaning();
                return null;
            }

            protected void onPostExecute(String result) {
                scaned();
            }

        }.execute("");
    }

    /**
     * 扫描开始
     */
    private void scanStart() {
        scanHandler.sendEmptyMessage(0);
        isFinish = false;
    }

    /**
     * 扫描中
     */
    private void scaning() {
        scannerMusic();
    }

    /**
     * 扫描歌曲，从手机文件夹里面进行递归扫描
     */
    private void scannerMusic() {
        songSize = 0;
        List<StorageInfo> list = StorageListUtil
                .listAvaliableStorage(getApplicationContext());
        for (int i = 0; i < list.size(); i++) {
            StorageInfo storageInfo = list.get(i);
            scannerLocalAudioFile(storageInfo.path, true);
        }
    }

    /**
     * @param Path        搜索目录
     * @param IsIterative 是否进入子文件夹
     */
    public void scannerLocalAudioFile(String Path, boolean IsIterative) {
        File[] files = new File(Path).listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File f = files[i];

                Message msg = new Message();
                msg.what = 2;
                msg.obj = f.getPath();
                scanHandler.sendMessage(msg);

                if (f.isFile() && AudioFilter.acceptFilter(f)) {

                    String Extension = MediaUtils.getFileExt(f.getPath());
                    if (f.getPath().endsWith(Extension)) // 判断扩展名
                    {
                        if (!f.exists()) {
                            continue;
                        }
                        // 文件名
                        String displayName = f.getName();
                        if (displayName.endsWith(Extension)) {
                            displayName = MediaUtils.removeExt(displayName);
                        }

                        boolean isExists = SongDB.getSongInfoDB(this)
                                .songIsExists(displayName);
                        if (isExists) {
                            continue;
                        }
                        // 将扫描到的数据保存到播放列表
                        SongInfo songInfo = MediaUtils.getSongInfoByFile(f
                                .getPath());
                        if (songInfo != null) {
                            SongDB.getSongInfoDB(this).add(songInfo);
                            songSize++;
                        } else {
                            continue;
                        }

                    }
                    if (!IsIterative)
                        break;
                } else if (f.isDirectory() && f.getPath().indexOf("/.") == -1) // 忽略点文件（隐藏文件/文件夹）
                {
                    scannerLocalAudioFile(f.getPath(), IsIterative);
                }
            }
        }
    }

    /**
     * 扫描完成
     */
    private void scaned() {
        scanHandler.sendEmptyMessage(1);
        isFinish = true;

        SongMessage songMessage = new SongMessage();
        songMessage.setType(SongMessage.SCANEDMUSIC);
        // 通知
        ObserverManage.getObserver().setMessage(songMessage);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (isFinish) {
                finish();
                overridePendingTransition(0, 0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
