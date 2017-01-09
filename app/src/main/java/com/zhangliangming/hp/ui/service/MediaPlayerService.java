package com.zhangliangming.hp.ui.service;


import java.util.Observable;
import java.util.Observer;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.tulskiy.musique.model.TrackData;
import com.zhangliangming.hp.ui.common.Constants;
import com.zhangliangming.hp.ui.logger.LoggerManage;
import com.zhangliangming.hp.ui.manage.MediaManage;
import com.zhangliangming.hp.ui.model.SongInfo;
import com.zhangliangming.hp.ui.model.SongMessage;
import com.zhangliangming.hp.ui.observable.ObserverManage;

import com.zlm.audio.player.BasePlayer;
import com.zlm.audio.player.BasePlayer.PlayEvent;


public class MediaPlayerService extends Service implements Observer {
    /**
     * 服务是否在进行
     */
    public static Boolean isServiceRunning = false;
    /**
     * 是否是第一次运行
     */
    private Boolean isFirstStart = true;
    private Context context;
    /**
     * 当前播放歌曲
     */
    private SongInfo songInfo;
    private BasePlayer player;

    private Thread playerThread = null;

    private LoggerManage logger;

    private boolean isError = false;

    private int songDuration = 0;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        context = MediaPlayerService.this.getBaseContext();
        logger = LoggerManage.getZhangLogger(context);
    }

    @Override
    @Deprecated
    public void onStart(Intent intent, int startId) {
        ObserverManage.getObserver().addObserver(this);
        isServiceRunning = true;
        if (!isFirstStart) {
            isFirstStart = false;
            // 播放歌曲
            if (songInfo != null) {
                playMusic(songInfo);
            }
        }
    }

    @Override
    public void onDestroy() {
        isServiceRunning = false;
        // 结束线程
        playerThread = null;
        ObserverManage.getObserver().deleteObserver(this);
        super.onDestroy();
        // // 如果当前的状态不是暂停，如果播放服务被回收了，要重新启动服务
        if (!Constants.APPCLOSE
                && MediaManage.PAUSE != MediaManage.getMediaManage(context)
                .getPlayStatus()) {
            // 在此重新启动,使服务常驻内存
            startService(new Intent(this, MediaPlayerService.class));
        }
    }

    /**
     * 初始化播放器
     */
    private void initMusic() {
        if (player != null) {
            if (player.isPlaying()) {
                player.stop();

                SongMessage msg = new SongMessage();
                msg.setSongInfo(songInfo);
                msg.setType(SongMessage.SERVICEPAUSEEDMUSIC);
                ObserverManage.getObserver().setMessage(msg);

            }
            player.stop();
            player = null;
        }
        if (playerThread != null) {
            playerThread = null;
        }
    }

    /**
     * 初始化播放器
     */
    @SuppressLint("NewApi")
    public void initPlayer() {
        try {
            if (player != null) {
                if (player.isPlaying()) {
                    player.stop();
                }
                player.stop();
                player = null;
            }
            if (playerThread != null) {
                playerThread = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放歌曲
     *
     * @param songInfo
     */
    private void playMusic(SongInfo songInfo) {
        if (songInfo.getType() == SongInfo.LOCALSONG
                || songInfo.getType() == SongInfo.DOWNLOADSONG) {
            playLocalMusic(songInfo);
        }
    }

    /**
     * 播放本地歌曲
     *
     * @param songInfo
     */
    private void playLocalMusic(SongInfo songInfo) {
        this.songInfo = songInfo;
        if (songInfo == null) {

            SongMessage msg = new SongMessage();
            msg.setType(SongMessage.SERVICEERRORMUSIC);
            msg.setErrorMessage(SongMessage.ERRORMESSAGEPLAYSONGNULL);
            ObserverManage.getObserver().setMessage(msg);

            return;
        }

        if (player == null) {
            player = new BasePlayer();

            player.setPlayEvent(new PlayEvent() {

                @Override
                public void stoped() {
                }

                @Override
                public void finished() {
                    // 下一首
                    SongMessage songMessage = new SongMessage();
                    songMessage.setType(SongMessage.NEXTMUSIC);
                    ObserverManage.getObserver().setMessage(songMessage);
                }

                @Override
                public void error() {
                    // 播放出错，1秒过后，播放下一首

                    logger.e("播放歌曲出错,跳转下一首!");

                    // 下一首
                    SongMessage songMessage = new SongMessage();
                    songMessage.setType(SongMessage.NEXTMUSIC);
                    ObserverManage.getObserver().setMessage(songMessage);
                }
            });

        }

        try {

            TrackData trackData = MediaManage.getMediaManage(context).getTrackData(songInfo);

            if (trackData == null) {
                // 播放出错，1秒过后，播放下一首

                logger.e("歌曲格式不支持!");

                MediaManage.getMediaManage(getApplicationContext())
                        .setPlayStatus(MediaManage.PAUSE);
                SongMessage songMessage = new SongMessage();
                songMessage.setType(SongMessage.INITMUSIC);
                songMessage.setSongInfo(songInfo);
                // 通知
                ObserverManage.getObserver().setMessage(songMessage);

                return;
            }

            if (songInfo.getPlayProgress() != 0) {
                trackData.setStartPosition(songInfo.getPlayProgress());
            } else {
                trackData.setStartPosition(0);
            }
            player.open(trackData);

            player.play();

        } catch (Exception e) {
            e.printStackTrace();

            logger.e("播放歌曲出错,跳转下一首!");
            // 下一首
            SongMessage songMessage = new SongMessage();
            songMessage.setType(SongMessage.NEXTMUSIC);
            ObserverManage.getObserver().setMessage(songMessage);

        }
        if (playerThread == null) {
            playerThread = new Thread(new PlayerRunable());
            playerThread.start();
        }
    }

    /**
     * 快进
     *
     * @param progress
     */
    private void seekTo(int progress) {
        songDuration = progress;
        if (songInfo != null) {
            playSeekToMusic(progress);
        }
    }

    /**
     * 播放快进歌曲
     *
     * @param progress
     */
    private void playSeekToMusic(int progress) {
        if (player != null && player.isPlaying()) {
            player.stop();
            player = null;
        }
        songInfo.setPlayProgress(progress);
        playLocalMusic(songInfo);
    }

    private class PlayerRunable implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(100);
                    if (player != null && player.isPlaying()) {

                        if (songInfo != null) {
                            songInfo.setPlayProgress((int) player
                                    .getCurrentMillis());

                            SongMessage msg = new SongMessage();
                            msg.setSongInfo(songInfo);
                            msg.setType(SongMessage.SERVICEPLAYINGMUSIC);
                            ObserverManage.getObserver().setMessage(msg);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void update(Observable arg0, Object data) {
        if (data instanceof SongMessage) {
            SongMessage songMessage = (SongMessage) data;
            if (songMessage.getType() == SongMessage.SERVICEPLAYMUSIC) {
                playMusic(songMessage.getSongInfo());
            } else if (songMessage.getType() == SongMessage.INITMUSIC) {
                initMusic();
            } else if (songMessage.getType() == SongMessage.SERVICEPAUSEMUSIC) {
                initMusic();
            } else if (songMessage.getType() == SongMessage.SERVICEPLAYINIT) {
                initPlayer();
            } else if (songMessage.getType() == SongMessage.SERVICESEEKTOMUSIC) {
                int progress = songMessage.getProgress();
                seekTo(progress);
            }
        }
    }
}
