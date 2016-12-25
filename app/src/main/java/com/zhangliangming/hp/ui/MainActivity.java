package com.zhangliangming.hp.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhangliangming.hp.ui.adapter.LocalSongAdapter;
import com.zhangliangming.hp.ui.common.Constants;
import com.zhangliangming.hp.ui.manage.MediaManage;
import com.zhangliangming.hp.ui.model.Category;
import com.zhangliangming.hp.ui.model.SongInfo;
import com.zhangliangming.hp.ui.model.SongMessage;
import com.zhangliangming.hp.ui.observable.ObserverManage;
import com.zhangliangming.hp.ui.service.MediaPlayerService;
import com.zhangliangming.hp.ui.util.ActivityManager;
import com.zhangliangming.hp.ui.util.DataUtil;
import com.zhangliangming.hp.ui.widget.BaseSeekBar;
import com.zhangliangming.hp.ui.widget.LinearLayoutRecyclerView;
import com.zhangliangming.hp.ui.widget.PressedRelativeLayout;
import com.zhangliangming.hp.ui.widget.SlideBar;

import java.util.List;
import java.util.Observable;
import java.util.Observer;


/**
 * 主界面
 */
public class MainActivity extends BaseActivity implements Observer {
    private long mExitTime;
    /**
     * 扫描按钮
     */
    private PressedRelativeLayout scanButton;
    /**
     * 本地歌曲列表视图
     */
    private LinearLayoutRecyclerView localPlayListview;

    /**
     * 本地的歌曲列表
     */
    private List<Category> localPlayListSongCategorys;
    /**
     * 本地歌曲适配器
     */
    private LocalSongAdapter localSongAdapter;
    private SlideBar localSlideBar;
    /**
     * 显示字母的TextView
     */
    private TextView localDialog;

    private LinearLayoutManager localLayoutManager;

    private RelativeLayout playBarRelativeLayout;
    /**
     * 进度条
     */
    private BaseSeekBar seekBar;
    /**
     * 歌曲名称
     */
    private TextView songName;
    /**
     * 歌手名称
     */
    private TextView artistName;
    /**
     * 暂停按钮
     */
    private PressedRelativeLayout pauseBarPlayParent;
    /**
     * 播放按钮
     */
    private PressedRelativeLayout playBarPlayParent;
    /**
     * 下一首按钮
     */
    private PressedRelativeLayout playBarNextParent;
    /**
     * 上一首按钮
     */
    private PressedRelativeLayout playBarPreParent;


    /**
     * 歌曲处理
     */
    private Handler songInfoHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            SongMessage songMessageTemp = (SongMessage) msg.obj;
            SongInfo songInfo = songMessageTemp.getSongInfo();
            if (songInfo == null
                    || songMessageTemp.getType() == SongMessage.ERRORMUSIC
                    || songMessageTemp.getType() == SongMessage.SERVICEERRORMUSIC) {

                seekBar.setProgress(0);
                seekBar.setSecondaryProgress(0);
                seekBar.setMax(0);

                songName.setText(getString(R.string.def_songName));
                artistName.setText(getString(R.string.def_artist));

                playBarPlayParent.setVisibility(View.VISIBLE);
                pauseBarPlayParent.setVisibility(View.INVISIBLE);
                ;


                return;
            }
            if (songMessageTemp.getType() == SongMessage.INITMUSIC) {

                seekBar.setProgress(0);
                seekBar.setSecondaryProgress(0);
                seekBar.setMax((int) songInfo.getDuration());

                songName.setText(songInfo.getTitle());
                artistName.setText(songInfo.getSinger());

                playBarPlayParent.setVisibility(View.VISIBLE);
                pauseBarPlayParent.setVisibility(View.INVISIBLE);


            } else if (songMessageTemp.getType() == SongMessage.SERVICEPLAYMUSIC) {
                playBarPlayParent.setVisibility(View.INVISIBLE);
                pauseBarPlayParent.setVisibility(View.VISIBLE);


            } else if (songMessageTemp.getType() == SongMessage.SERVICEPLAYINGMUSIC) {

                seekBar.setProgress((int) songInfo.getPlayProgress());

            } else if (songMessageTemp.getType() == SongMessage.SERVICEPAUSEEDMUSIC) {
                playBarPlayParent.setVisibility(View.VISIBLE);
                pauseBarPlayParent.setVisibility(View.INVISIBLE);

                seekBar.setProgress((int) songInfo.getPlayProgress());


            } else if (songMessageTemp.getType() == SongMessage.SERVICEPLAYWAITING) {

            } else if (songMessageTemp.getType() == SongMessage.SERVICEPLAYWAITINGEND) {

            } else if (songMessageTemp.getType() == SongMessage.SERVICEDOWNLOADFINISHED) {
                seekBar.setSecondaryProgress(0);
            }
        }

    };


    @Override
    public int setContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    public void onCreate() {
        //设置状态栏
        LinearLayout statusBarView = (LinearLayout) findViewById(R.id.statusBarView);
        setStatusBarView(statusBarView);
        init();
        //
        ObserverManage.getObserver().addObserver(this);
        ActivityManager.getInstance().addActivity(this);
    }

    /**
     *
     */
    private void init() {
        //扫描按钮
        scanButton = (PressedRelativeLayout) findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.in_from_left);
            }
        });

        //
        playBarRelativeLayout = (RelativeLayout) findViewById(R.id.playBar);

        playBarRelativeLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LrcActivity.class));
                overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_to_left);
            }
        });

        seekBar = (BaseSeekBar) findViewById(R.id.seekBar);
        songName = (TextView) findViewById(R.id.songName);
        artistName = (TextView) findViewById(R.id.artistName);

        pauseBarPlayParent = (PressedRelativeLayout) findViewById(R.id.pauseParent);
        pauseBarPlayParent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SongMessage songMessage = new SongMessage();
                songMessage.setType(SongMessage.PAUSEMUSIC);
                // 通知
                ObserverManage.getObserver().setMessage(songMessage);
            }
        });

        playBarPlayParent = (PressedRelativeLayout) findViewById(R.id.playParent);
        playBarPlayParent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                SongMessage songMessage = new SongMessage();
                songMessage.setType(SongMessage.PLAYMUSIC);
                // 通知
                ObserverManage.getObserver().setMessage(songMessage);
            }
        });

        playBarNextParent = (PressedRelativeLayout) findViewById(R.id.nextParent);
        playBarNextParent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SongMessage songMessage = new SongMessage();
                songMessage.setType(SongMessage.NEXTMUSIC);
                // 通知
                ObserverManage.getObserver().setMessage(songMessage);
            }
        });


        playBarPreParent = (PressedRelativeLayout) findViewById(R.id.preParent);
        playBarPreParent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SongMessage songMessage = new SongMessage();
                songMessage.setType(SongMessage.PREMUSIC);
                // 通知
                ObserverManage.getObserver().setMessage(songMessage);
            }
        });


        localPlayListview = (LinearLayoutRecyclerView) findViewById(R.id.localPlayListview);

        // 如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        localPlayListview.setHasFixedSize(true);

        localLayoutManager = new LinearLayoutManager(this);
        localPlayListview.setLinearLayoutManager(localLayoutManager);

        localSlideBar = (SlideBar) findViewById(R.id.localSlideBar);

        localDialog = (TextView) findViewById(R.id.localDialog);

        localSlideBar.setTextView(localDialog);
// 设置右侧触摸监听
        localSlideBar
                .setOnTouchingLetterChangedListener(new SlideBar.OnTouchingLetterChangedListener() {

                    public void onTouchingLetterChanged(String s) {
                        if (localSongAdapter != null) {
                            // 该字母首次出现的位置
                            int position = localSongAdapter
                                    .getPositionForSection(s.charAt(0));
                            if (position != -1) {
                                localPlayListview
                                        .move(position,
                                                LinearLayoutRecyclerView.smoothScroll);
                            }
                        }

                    }
                });
        localPlayListview
                .OnLinearLayoutRecyclerViewScrollListener(new LinearLayoutRecyclerView.OnLinearLayoutRecyclerViewScrollListener() {

                    @Override
                    public void onScrollEnd(int firstIndex) {
                        if (localSongAdapter != null) {
                            char choose = localSongAdapter
                                    .getPositionForIndex(firstIndex);
                            if (choose != -1) {
                                localSlideBar.setChoose(choose);
                            }
                        }
                    }
                });

        loadData();
    }

    private void loadData() {

        new AsyncTask<String, Integer, Void>() {

            @Override
            protected Void doInBackground(String... arg0) {

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                localPlayListSongCategorys = MediaManage.getMediaManage(MainActivity.this).getAllLocalSongData();
                //初始化当前播放的歌曲数据
                int playListType = Constants.playListType;
                MediaManage.getMediaManage(MainActivity.this).initSongInfoData(playListType);

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (localPlayListSongCategorys != null
                        && localPlayListSongCategorys.size() != 0) {
                    localSongAdapter = new LocalSongAdapter(getApplicationContext(),
                            localPlayListSongCategorys);
                    localPlayListview.setAdapter(localSongAdapter);

                    if (localSongAdapter != null) {
                        char choose = localSongAdapter
                                .getPositionForIndex(0);
                        if (choose != -1) {
                            localSlideBar.setChoose(choose);
                        }
                    }
                }
            }

        }.execute("");

    }

    /**
     * 关闭
     */
    private void close() {

        Constants.APPCLOSE = true;

        // 如果服务正在运行，则是正在播放
        if (MediaPlayerService.isServiceRunning) {
            stopService(new Intent(MainActivity.this, MediaPlayerService.class));
        }

        ActivityManager.getInstance().exit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, R.string.exit_tip, Toast.LENGTH_SHORT)
                        .show();
                mExitTime = System.currentTimeMillis();
            } else {
                close();
            }
        }
        return false;
    }

    @Override
    public void update(Observable arg0, Object data) {
        if (data instanceof SongMessage) {
            SongMessage songMessageTemp = (SongMessage) data;
            if (songMessageTemp.getType() == SongMessage.INITMUSIC
                    || songMessageTemp.getType() == SongMessage.SERVICEPLAYINGMUSIC
                    || songMessageTemp.getType() == SongMessage.SERVICEPAUSEEDMUSIC
                    || songMessageTemp.getType() == SongMessage.ERRORMUSIC
                    || songMessageTemp.getType() == SongMessage.SERVICEPLAYMUSIC
                    || songMessageTemp.getType() == SongMessage.SERVICEERRORMUSIC) {
                Message msg = new Message();
                msg.obj = songMessageTemp;
                songInfoHandler.sendMessage(msg);

            } else if (songMessageTemp.getType() == SongMessage.UPDATEMUSIC
                    || songMessageTemp.getType() == SongMessage.SERVICEPLAYWAITING
                    || songMessageTemp.getType() == SongMessage.SERVICEPLAYWAITINGEND
                    || songMessageTemp.getType() == SongMessage.SERVICEDOWNLOADFINISHED) {
                Message msg = new Message();
                msg.obj = songMessageTemp;
                songInfoHandler.sendMessage(msg);
            } else if (songMessageTemp.getType() == SongMessage.DESLRCLOCKORUNLOCK) {
                if (Constants.desktopLyricsIsMove) {
                    // 解锁
                    Constants.desktopLyricsIsMove = false;
                } else {
                    Constants.desktopLyricsIsMove = true;
                }

                DataUtil.saveValue(MainActivity.this,
                        Constants.desktopLyricsIsMove_KEY,
                        Constants.desktopLyricsIsMove);

                SongMessage songMessageT = new SongMessage();
                songMessageT.setType(SongMessage.DESLRCLOCKORUNLOCKED);
                ObserverManage.getObserver().setMessage(songMessageT);

            }
        }
    }
}
