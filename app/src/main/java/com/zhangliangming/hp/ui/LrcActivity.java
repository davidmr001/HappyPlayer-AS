package com.zhangliangming.hp.ui;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.happy.lyrics.model.LyricsInfo;
import com.happy.lyrics.model.LyricsLineInfo;
import com.happy.lyrics.model.LyricsTag;
import com.happy.lyrics.system.LyricsInfoIO;
import com.zhangliangming.hp.ui.common.Constants;
import com.zhangliangming.hp.ui.manage.LyricsManage;
import com.zhangliangming.hp.ui.manage.MediaManage;
import com.zhangliangming.hp.ui.model.SongInfo;
import com.zhangliangming.hp.ui.model.SongMessage;
import com.zhangliangming.hp.ui.observable.ObserverManage;
import com.zhangliangming.hp.ui.util.ActivityManager;
import com.zhangliangming.hp.ui.util.ColorUtil;
import com.zhangliangming.hp.ui.util.DataUtil;
import com.zhangliangming.hp.ui.util.LyricsParserUtil;
import com.zhangliangming.hp.ui.util.LyricsUtil;
import com.zhangliangming.hp.ui.util.MediaUtils;
import com.zhangliangming.hp.ui.util.ToastUtil;
import com.zhangliangming.hp.ui.widget.BaseImageButton;
import com.zhangliangming.hp.ui.widget.ColorRelativeLayout;
import com.zhangliangming.hp.ui.widget.LrcSeekBar;
import com.zhangliangming.hp.ui.widget.ManyLineLyricsView;
import com.zhangliangming.hp.ui.widget.PressedRelativeLayout;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;

/**
 * 歌曲界面窗口
 */
public class LrcActivity extends BaseActivity implements Observer {
    /**
     * 返回按钮
     */
    private PressedRelativeLayout backButton;
    /**
     * 标题
     */
    private TextView titleTextView;

    /**
     * 随机播放
     */
    private BaseImageButton mode_random_button;
    /**
     * 顺序播放
     */
    private BaseImageButton mode_all1_button;
    /**
     * 单曲循环
     */
    private BaseImageButton mode_single_button;

    /**
     * 循环播放
     */
    private BaseImageButton mode_all_button;
    /**
     * 上一首
     */
    private BaseImageButton preImageView;
    /**
     * 播放按钮
     */
    private BaseImageButton playImageView;
    /**
     * 暂停按钮
     */
    private BaseImageButton pauseImageView;
    /**
     * 下一首
     */
    private BaseImageButton nextImageView;
    /**
     *
     */
    private BaseImageButton menuButton;

    /**
     * 播放模式
     */
    private int playModel = Constants.playModel;
    /**
     * 当前播放歌曲
     */
    private SongInfo mSongInfo;
    /**
     * 播放进度条
     */
    private LrcSeekBar playerSeekBar;

    /**
     * 判断其是否是正在拖动
     */
    private boolean isStartTrackingTouch = false;
    /**
     * 播放进度
     */
    private TextView songProgressTextView;
    /**
     * 歌曲进度
     */
    private TextView songSizeTextView;
    /**
     * 歌词解析
     */
    private LyricsParserUtil lyricsParser;

    /**
     * 歌词列表
     */
    private TreeMap<Integer, LyricsLineInfo> lyricsLineTreeMap;


    /**
     * 多行歌词
     */
    private ManyLineLyricsView manyLineLyricsView;

    /**
     * 弹出menu菜单
     */
    private RelativeLayout popMenuParent;
    /**
     *
     */
    private LinearLayout popLayout;

    /**
     * 弹出菜单的取消按钮
     */
    private PressedRelativeLayout cancelMenuButtonParent;

    /**
     * 颜色面板
     */
    private ColorRelativeLayout[] colorRelativeLayout;
    /**
     * 字体大小
     */
    private LrcSeekBar fontSizeSeekBar;
    /**
     * 歌词后退
     */
    private PressedRelativeLayout lrcDecreaseIconParent;
    /**
     * 重置
     */
    private PressedRelativeLayout resetIconParent;
    /***
     * 歌词增加
     */
    private PressedRelativeLayout lrcIncreaseIconParent;
    /**
     * 设置显示播放模式的按钮
     */
    private Handler playModelHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // 0是 顺序播放 1是随机播放 2是循环播放 3是单曲播放
            switch (playModel) {
                case 0:
                    mode_random_button.setVisibility(View.INVISIBLE);
                    mode_all1_button.setVisibility(View.VISIBLE);
                    mode_single_button.setVisibility(View.INVISIBLE);
                    mode_all_button.setVisibility(View.INVISIBLE);

                    break;
                case 1:
                    mode_random_button.setVisibility(View.VISIBLE);
                    mode_all1_button.setVisibility(View.INVISIBLE);
                    mode_single_button.setVisibility(View.INVISIBLE);
                    mode_all_button.setVisibility(View.INVISIBLE);

                    break;
                case 2:
                    mode_random_button.setVisibility(View.INVISIBLE);
                    mode_all1_button.setVisibility(View.INVISIBLE);
                    mode_single_button.setVisibility(View.INVISIBLE);
                    mode_all_button.setVisibility(View.VISIBLE);

                    break;
                case 3:

                    mode_random_button.setVisibility(View.INVISIBLE);
                    mode_all1_button.setVisibility(View.INVISIBLE);
                    mode_single_button.setVisibility(View.VISIBLE);
                    mode_all_button.setVisibility(View.INVISIBLE);

                    break;
            }
        }
    };
    private Handler songHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            SongMessage songMessageTemp = (SongMessage) msg.obj;
            SongInfo songInfo = songMessageTemp.getSongInfo();
            if (songInfo == null
                    || songMessageTemp.getType() == SongMessage.ERRORMUSIC
                    || songMessageTemp.getType() == SongMessage.SERVICEERRORMUSIC) {


                playerSeekBar.setEnabled(false);
                playerSeekBar.setProgress(0);
                playerSeekBar.setSecondaryProgress(0);
                playerSeekBar.setMax(0);
                playerSeekBar.popupWindowDismiss();


                titleTextView.setText(getString(R.string.def_songName) + "\n" + getString(R.string.def_artist));
                songProgressTextView.setText("00:00");
                songSizeTextView.setText("00:00");

                playImageView.setVisibility(View.VISIBLE);
                pauseImageView.setVisibility(View.INVISIBLE);

                manyLineLyricsView.setHasLrc(false);

                return;
            } else {
                if (songMessageTemp.getType() == SongMessage.INITMUSIC) {

                    mSongInfo = songInfo;

                    playerSeekBar.setEnabled(true);
                    playerSeekBar.setMax((int) songInfo.getDuration());
                    playerSeekBar.setSecondaryProgress(0);
                    playerSeekBar.setProgress((int) songInfo.getPlayProgress());

                    playerSeekBar.popupWindowDismiss();

                    titleTextView.setText(songInfo.getTitle() + "\n" + songInfo.getSinger());
                    songProgressTextView.setText(MediaUtils.formatTime((int) songInfo
                            .getPlayProgress()));
                    songSizeTextView.setText(MediaUtils.formatTime((int) songInfo
                            .getDuration()));

                    if (songInfo.getDownloadStatus() == SongInfo.DOWNLOADED) {

                    } else {

                    }

                    if (MediaManage.PLAYING == MediaManage.getMediaManage(
                            LrcActivity.this).getPlayStatus()) {
                        playImageView.setVisibility(View.INVISIBLE);
                        pauseImageView.setVisibility(View.VISIBLE);
                    } else {
                        playImageView.setVisibility(View.VISIBLE);
                        pauseImageView.setVisibility(View.INVISIBLE);
                    }


                    LyricsUtil.loadLyrics(songInfo.getSid(),
                            songInfo.getTitle(), songInfo.getSinger(),
                            songInfo.getDisplayName(), songInfo.getKscUrl(),
                            SongMessage.LRCTYPELRC);

                    manyLineLyricsView.setHasLrc(false);
                } else if (songMessageTemp.getType() == SongMessage.SERVICEPLAYMUSIC) {

                    playImageView.setVisibility(View.INVISIBLE);
                    pauseImageView.setVisibility(View.VISIBLE);

                } else if (songMessageTemp.getType() == SongMessage.SERVICEPLAYINGMUSIC) {

                    songProgressTextView.setText(MediaUtils.formatTime((int) songInfo
                            .getPlayProgress()));
                    if (!isStartTrackingTouch) {
                        playerSeekBar.setProgress((int) songInfo
                                .getPlayProgress());

                    }

                    if (manyLineLyricsView.getHasLrc()) {

                        manyLineLyricsView.showLrc((int) songInfo
                                .getPlayProgress());
                    }

                } else if (songMessageTemp.getType() == SongMessage.SERVICEPAUSEEDMUSIC) {
                    playImageView.setVisibility(View.VISIBLE);
                    pauseImageView.setVisibility(View.INVISIBLE);

                    playerSeekBar.setProgress((int) songInfo.getPlayProgress());
                    songProgressTextView.setText(MediaUtils.formatTime((int) songInfo
                            .getPlayProgress()));

                    if (manyLineLyricsView.getHasLrc()) {

                        manyLineLyricsView.showLrc((int) songInfo
                                .getPlayProgress());
                    }

                } else if (songMessageTemp.getType() == SongMessage.ERRORMUSIC) {

                } else if (songMessageTemp.getType() == SongMessage.SERVICEERRORMUSIC) {

                } else if (songMessageTemp.getType() == SongMessage.UPDATEMUSIC) {
                    long max = songInfo.getDuration();
                    float downloadProgress = songInfo.getDownloadProgress();
                    long fileSize = songInfo.getSize();
                    if (fileSize <= downloadProgress) {
                        playerSeekBar.setSecondaryProgress(0);
                    } else
                        playerSeekBar.setSecondaryProgress((int) (downloadProgress
                                / fileSize * max));
                } else if (songMessageTemp.getType() == SongMessage.SERVICEDOWNLOADFINISHED) {
                    playerSeekBar.setSecondaryProgress(0);
                }
            }
        }

    };


    @Override
    public int setContentViewId() {
        return R.layout.activity_lrc;
    }

    @Override
    public void onCreate() {
        //设置状态栏
        LinearLayout statusBarView = (LinearLayout) findViewById(R.id.statusBarView);
        setStatusBarView(statusBarView);
        init();
        initData();
        //
        ObserverManage.getObserver().addObserver(this);
        ActivityManager.getInstance().addActivity(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        SongInfo songInfo = MediaManage.getMediaManage(this).getSongInfo();
        SongMessage songMessage = new SongMessage();
        songMessage.setSongInfo(songInfo);
        songMessage.setType(SongMessage.INITMUSIC);
        Message msg = new Message();
        msg.obj = songMessage;
        songHandler.sendMessage(msg);
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
        titleTextView = (TextView) findViewById(R.id.title);

        //
        manyLineLyricsView = (ManyLineLyricsView) findViewById(R.id.manyLineLyricsView);

        //随机播放按钮
        mode_random_button = (BaseImageButton) findViewById(R.id.mode_random_button);
        mode_random_button.setIcon(getString(R.string.mode_random_def_icon), getString(R.string.mode_random_pressed_icon));
        mode_random_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modelOnClick(view);
            }
        });

        mode_all1_button = (BaseImageButton) findViewById(R.id.mode_all1_button);
        mode_all1_button.setIcon(getString(R.string.mode_all1_def_icon), getString(R.string.mode_all1_pressed_icon));
        mode_all1_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modelOnClick(view);
            }
        });

        mode_single_button = (BaseImageButton) findViewById(R.id.mode_single_button);
        mode_single_button.setIcon(getString(R.string.mode_single_def_icon), getString(R.string.mode_single_pressed_icon));
        mode_single_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modelOnClick(view);
            }
        });

        mode_all_button = (BaseImageButton) findViewById(R.id.mode_all_button);
        mode_all_button.setIcon(getString(R.string.mode_all_def_icon), getString(R.string.mode_all_pressed_icon));
        mode_all_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modelOnClick(view);
            }
        });

        playModelHandler.sendEmptyMessage(0);

        //播放
        playImageView = (BaseImageButton) findViewById(R.id.playing_button);
        playImageView.setIcon(getString(R.string.play_def_icon), getString(R.string.play_pressed_icon));
        playImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                SongMessage songMessage = new SongMessage();
                songMessage.setType(SongMessage.PLAYMUSIC);
                // 通知
                ObserverManage.getObserver().setMessage(songMessage);
            }
        });
        //暂停
        pauseImageView = (BaseImageButton) findViewById(R.id.pause_button);
        pauseImageView.setIcon(getString(R.string.pause_def_icon), getString(R.string.pause_pressed_icon));
        pauseImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                SongMessage songMessage = new SongMessage();
                songMessage.setType(SongMessage.PAUSEMUSIC);
                // 通知
                ObserverManage.getObserver().setMessage(songMessage);
            }
        });
//上一首
        preImageView = (BaseImageButton) findViewById(R.id.pre_button);
        preImageView.setIcon(getString(R.string.pre_def_icon), getString(R.string.pre_pressed_icon));
        preImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                SongMessage songMessage = new SongMessage();
                songMessage.setType(SongMessage.PREMUSIC);
                // 通知
                ObserverManage.getObserver().setMessage(songMessage);
            }
        });
//下一首
        nextImageView = (BaseImageButton) findViewById(R.id.next_button);
        nextImageView.setIcon(getString(R.string.next_def_icon), getString(R.string.next_pressed_icon));

        nextImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                SongMessage songMessage = new SongMessage();
                songMessage.setType(SongMessage.NEXTMUSIC);
                // 通知
                ObserverManage.getObserver().setMessage(songMessage);
            }
        });

        //
        songProgressTextView = (TextView) findViewById(R.id.songProgress);
        songSizeTextView = (TextView) findViewById(R.id.songSize);
        playerSeekBar = (LrcSeekBar) findViewById(R.id.playerSeekBar);
        playerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                // // 拖动条进度改变的时候调用
                if (isStartTrackingTouch) {
                    int progress = playerSeekBar.getProgress();
                    // 往弹出窗口传输相关的进度
                    playerSeekBar.popupWindowShow(progress, playerSeekBar,
                            manyLineLyricsView.getLineLrc(progress));

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
                isStartTrackingTouch = true;
                int progress = playerSeekBar.getProgress();
                // 往弹出窗口传输相关的进度
                playerSeekBar.popupWindowShow(progress, playerSeekBar,
                        manyLineLyricsView.getLineLrc(progress));

            }

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                // 拖动条停止拖动的时候调用
                playerSeekBar.popupWindowDismiss();

                SongMessage songMessage = new SongMessage();
                songMessage.setType(SongMessage.SEEKTOMUSIC);
                songMessage.setProgress(playerSeekBar.getProgress());
                ObserverManage.getObserver().setMessage(songMessage);

                new Thread() {

                    @Override
                    public void run() {
                        try {
                            // 延迟100ms才更新进度，防止歌曲正在播放会出现进度条闪屏
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        isStartTrackingTouch = false;
                    }

                }.start();
            }
        });
        //歌词快进事件
        manyLineLyricsView.setOnLrcClickListener(new ManyLineLyricsView.OnLrcClickListener() {
            @Override
            public void onLrcPlayClicked(int progress) {
                if (mSongInfo != null) {

                    int seekProgress = 0;
                    if (progress >= 0 && progress <= mSongInfo.getDuration()) {
                        seekProgress = progress;
                    } else {
                        seekProgress = (int) mSongInfo.getDuration();
                    }

                    SongMessage songMessage = new SongMessage();

                    int playStatus = MediaManage.getMediaManage(getApplicationContext()).getPlayStatus();
                    if (playStatus == MediaManage.PAUSE) {
                        //开始播放
                        mSongInfo.setPlayProgress(seekProgress);
                        songMessage.setSongInfo(mSongInfo);
                        songMessage.setType(SongMessage.PLAYMUSIC);

                    } else {
                        //快进
                        songMessage.setType(SongMessage.SEEKTOMUSIC);
                        songMessage.setProgress(seekProgress);
                    }

                    ObserverManage.getObserver().setMessage(songMessage);

                }


            }
        });

        popLayout = (LinearLayout) findViewById(R.id.pop_layout);

        popMenuParent = (RelativeLayout) findViewById(R.id.popMenuParent);
        popMenuParent.setBackgroundColor(ColorUtil.parserColor("#000000", 50));
        popMenuParent.setVisibility(View.INVISIBLE);
        popMenuParent.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int topHeight = popLayout.getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {

                    if (topHeight > y) {
                        popMenuParent.setVisibility(View.INVISIBLE);
                    }
                }
                return true;
            }
        });

        //弹出菜单
        menuButton = (BaseImageButton) findViewById(R.id.menu_button);
        menuButton.setIcon(getString(R.string.menu_button_icon), getString(R.string.menu_button_icon));
        menuButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                popMenuParent.setVisibility(View.VISIBLE);
            }
        });
        //取消按钮
        cancelMenuButtonParent = (PressedRelativeLayout) findViewById(R.id.cancelMenuButtonParent);
        cancelMenuButtonParent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                popMenuParent.setVisibility(View.INVISIBLE);
            }
        });
        //
        initPopMenu();
    }

    /**
     * 初始化弹出菜单
     */
    private void initPopMenu() {
        //后退0.5秒
        lrcDecreaseIconParent = (PressedRelativeLayout) findViewById(R.id.lrcDecreaseIconParent);
        lrcDecreaseIconParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lyricsParser != null) {
                    lyricsParser.setOffset(lyricsParser.getOffset() + (-500));
                    //
                    if (mSongInfo != null && manyLineLyricsView.getHasLrc()) {

                        ToastUtil.showCenterTextToast(LrcActivity.this, (float) lyricsParser.getOffset() / 1000 + "秒");

                        manyLineLyricsView.showLrc((int) mSongInfo
                                .getPlayProgress());


                        saveLrcFile();

                    }
                }
            }
        });
        //重置
        resetIconParent = (PressedRelativeLayout) findViewById(R.id.lrcResetIconParent);
        resetIconParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lyricsParser != null) {
                    lyricsParser.setOffset(0);
                    if (mSongInfo != null && manyLineLyricsView.getHasLrc()) {

                        ToastUtil.showCenterTextToast(LrcActivity.this, "还原了");

                        manyLineLyricsView.showLrc((int) mSongInfo
                                .getPlayProgress());

                        saveLrcFile();
                    }

                }
            }
        });

        //添加0.5秒
        lrcIncreaseIconParent = (PressedRelativeLayout) findViewById(R.id.lrcIncreaseIconParent);
        lrcIncreaseIconParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lyricsParser != null) {
                    lyricsParser.setOffset(lyricsParser.getOffset() + 500);
                    if (mSongInfo != null && manyLineLyricsView.getHasLrc()) {

                        ToastUtil.showCenterTextToast(LrcActivity.this, (float) lyricsParser.getOffset() / 1000 + "秒");

                        manyLineLyricsView.showLrc((int) mSongInfo
                                .getPlayProgress());
                        saveLrcFile();

                    }

                }
            }
        });


        //字体颜色
        colorRelativeLayout = new ColorRelativeLayout[Constants.lrcColorStr.length];
        int i = 0;
        colorRelativeLayout[i] = (ColorRelativeLayout) findViewById(R.id.colorPanel0);
        colorRelativeLayout[i].setPanelColor(ColorUtil.parserColor(Constants.lrcColorStr[i]));
        colorRelativeLayout[i++].setOnClickListener(new ColorViewOnClickListener());


        colorRelativeLayout[i] = (ColorRelativeLayout) findViewById(R.id.colorPanel1);
        colorRelativeLayout[i].setPanelColor(ColorUtil.parserColor(Constants.lrcColorStr[i]));
        colorRelativeLayout[i++].setOnClickListener(new ColorViewOnClickListener());


        colorRelativeLayout[i] = (ColorRelativeLayout) findViewById(R.id.colorPanel2);
        colorRelativeLayout[i].setPanelColor(ColorUtil.parserColor(Constants.lrcColorStr[i]));
        colorRelativeLayout[i++].setOnClickListener(new ColorViewOnClickListener());


        colorRelativeLayout[i] = (ColorRelativeLayout) findViewById(R.id.colorPanel3);
        colorRelativeLayout[i].setPanelColor(ColorUtil.parserColor(Constants.lrcColorStr[i]));
        colorRelativeLayout[i++].setOnClickListener(new ColorViewOnClickListener());


        colorRelativeLayout[i] = (ColorRelativeLayout) findViewById(R.id.colorPanel4);
        colorRelativeLayout[i].setPanelColor(ColorUtil.parserColor(Constants.lrcColorStr[i]));
        colorRelativeLayout[i++].setOnClickListener(new ColorViewOnClickListener());


        colorRelativeLayout[i] = (ColorRelativeLayout) findViewById(R.id.colorPanel5);
        colorRelativeLayout[i].setPanelColor(ColorUtil.parserColor(Constants.lrcColorStr[i]));
        colorRelativeLayout[i++].setOnClickListener(new ColorViewOnClickListener());

        colorRelativeLayout[Constants.lrcColorIndex].setSelect(true);

        //字体大小
        fontSizeSeekBar = (LrcSeekBar) findViewById(R.id.fontSizeSeekBar);
        fontSizeSeekBar.setMax(Constants.lrcFontMaxSize
                - Constants.lrcFontMinSize);
        fontSizeSeekBar.setSecondaryProgress(0);
        fontSizeSeekBar.setProgress(Constants.lrcFontSize
                - Constants.lrcFontMinSize);

        fontSizeSeekBar
                .setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar arg0, int arg1,
                                                  boolean arg2) {

                        // 过快刷新，导致页面闪屏
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        // // 拖动条进度改变的时候调用
                        Constants.lrcFontSize = Constants.lrcFontMinSize
                                + fontSizeSeekBar.getProgress();

                        SongInfo songInfo = MediaManage.getMediaManage(getApplicationContext()).getSongInfo();
                        // 通知歌词界面去刷新view
                        if (songInfo != null && manyLineLyricsView.getHasLrc()) {
                            manyLineLyricsView.setFontSize((int)songInfo.getPlayProgress());
                        }else {
                            manyLineLyricsView.setFontSize();
                        }

                        new Thread() {

                            @Override
                            public void run() {
                                DataUtil.saveValue(LrcActivity.this,
                                        Constants.lrcFontSize_KEY,
                                        Constants.lrcFontSize);
                            }

                        }.start();
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar arg0) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar arg0) {
                    }
                });
    }

    /**
     * 保存歌词文件
     */
    private void saveLrcFile() {
        new Thread() {

            @Override
            public void run() {

                LyricsInfo lyricsInfo = lyricsParser.getLyricsIfno();
                String lrcFilePath = Constants.PATH_LYRICS + File.separator + mSongInfo.getDisplayName() + "." + lyricsInfo.getLyricsFileExt();

                Map<String, Object> tags = lyricsInfo.getLyricsTags();
                tags.put(LyricsTag.TAG_OFFSET, lyricsParser.getPlayOffset());
                lyricsInfo.setLyricsTags(tags);


                //保存修改的歌词文件
                try {
                    LyricsInfoIO.getLyricsFileWriter(lrcFilePath).writer(lyricsInfo, lrcFilePath);
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }

        }.start();
    }

    private class ColorViewOnClickListener implements View.OnClickListener {

        public void onClick(View arg0) {

            int index = 0;
            int id = arg0.getId();
            switch (id) {
                case R.id.colorPanel0:
                    index = 0;
                    break;
                case R.id.colorPanel1:
                    index = 1;
                    break;
                case R.id.colorPanel2:
                    index = 2;
                    break;
                case R.id.colorPanel3:
                    index = 3;
                    break;
                case R.id.colorPanel4:
                    index = 4;
                    break;
                case R.id.colorPanel5:
                    index = 5;
                    break;
                default:
                    break;
            }
            Constants.lrcColorIndex = index;
            for (int i = 0; i < colorRelativeLayout.length; i++) {
                if (i == index)
                    colorRelativeLayout[i].setSelect(true);
                else
                    colorRelativeLayout[i].setSelect(false);
            }

            //

            manyLineLyricsView.setFontColor();


            new Thread() {

                @Override
                public void run() {
                    DataUtil.saveValue(LrcActivity.this,
                            Constants.lrcColorIndex_KEY,
                            Constants.lrcColorIndex);
                }

            }.start();
        }
    }


    @Override
    public void onBackPressed() {
        if (popMenuParent.getVisibility() != View.INVISIBLE) {
            popMenuParent.setVisibility(View.INVISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 播放模式点击事件
     *
     * @param v
     */
    public void modelOnClick(View v) {
        switch (playModel) {
            case 0:
                playModel = 1;
                Toast.makeText(this, "随机播放", Toast.LENGTH_SHORT)
                        .show();

                break;
            case 1:
                playModel = 2;
                Toast.makeText(this, "循环播放", Toast.LENGTH_SHORT)
                        .show();
                break;
            case 2:
                playModel = 3;
                Toast.makeText(this, "单曲播放", Toast.LENGTH_SHORT)
                        .show();

                break;
            case 3:
                playModel = 0;
                Toast.makeText(this, "顺序播放", Toast.LENGTH_SHORT)
                        .show();

                break;
        }

        Constants.playModel = playModel;
        DataUtil.saveValue(LrcActivity.this, Constants.playModel_KEY,
                Constants.playModel);

        playModelHandler.sendEmptyMessage(0);

    }

    public void update(Observable arg0, Object data) {
        if (data instanceof SongMessage) {
            SongMessage songMessageTemp = (SongMessage) data;
            if (songMessageTemp.getType() == SongMessage.INITMUSIC
                    || songMessageTemp.getType() == SongMessage.SERVICEPLAYMUSIC
                    || songMessageTemp.getType() == SongMessage.SERVICEPLAYINGMUSIC
                    || songMessageTemp.getType() == SongMessage.SERVICEPAUSEEDMUSIC
                    || songMessageTemp.getType() == SongMessage.ERRORMUSIC
                    || songMessageTemp.getType() == SongMessage.SERVICEERRORMUSIC) {
                Message msg = new Message();
                msg.obj = songMessageTemp;
                songHandler.sendMessage(msg);
            } else if (songMessageTemp.getType() == SongMessage.UPDATEMUSIC || songMessageTemp.getType() == SongMessage.SERVICEDOWNLOADFINISHED) {
                Message msg = new Message();
                msg.obj = songMessageTemp;
                songHandler.sendMessage(msg);
            } else if (songMessageTemp.getType() == SongMessage.SINGERPHOTOLOADED) {

            } else if (songMessageTemp.getType() == SongMessage.LRCLOADED) {
                if (mSongInfo == null)
                    return;
                if (!mSongInfo.getSid().equals(songMessageTemp.getSid())) {
                    return;
                }
                String lrcFilePath = songMessageTemp.getLrcFilePath();
                String sid = songMessageTemp.getSid();

                initLrc(sid, lrcFilePath, true);
            } else if (songMessageTemp.getType() == SongMessage.LRCDOWNLOADED) {
                if (mSongInfo == null)
                    return;
                if (!mSongInfo.getSid().equals(songMessageTemp.getSid())) {
                    return;
                }
                String sid = songMessageTemp.getSid();

                initLrc(sid, null, false);

            }
        }
    }

    /**
     * 初始化歌词
     *
     * @param sid
     * @param lrcFilePath
     * @param isFile
     */
    private void initLrc(final String sid, final String lrcFilePath, final boolean isFile) {
        new AsyncTask<String, Integer, String>() {

            @Override
            protected String doInBackground(String... arg0) {

                if (isFile)
                    lyricsParser = LyricsManage.getLyricsParser(sid,
                            new File(lrcFilePath));
                else

                    lyricsParser = LyricsManage
                            .getLyricsParserByInputStream(sid);
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                lyricsLineTreeMap = lyricsParser.getDefLyricsLineTreeMap();
                if (lyricsLineTreeMap != null && lyricsLineTreeMap.size() != 0) {
                    manyLineLyricsView.init(lyricsParser);
                    manyLineLyricsView.setHasLrc(true);

                    if (mSongInfo != null) {
                        manyLineLyricsView.showLrc((int) mSongInfo
                                .getPlayProgress());
                    }
                }

            }

        }.execute("");
    }
}
