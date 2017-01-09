package com.zhangliangming.hp.ui.model;

import com.tulskiy.musique.model.TrackData;

/**
 * 音频数据类
 * Created by zhangliangming on 2017/1/9.
 */

public class AudioInfo {
    /**
     * 歌曲基本信息
     */
    private SongInfo songInfo;
    /**
     * 歌曲解码数据
     */
    private TrackData trackData;

    public SongInfo getSongInfo() {
        return songInfo;
    }

    public void setSongInfo(SongInfo songInfo) {
        this.songInfo = songInfo;
    }

    public TrackData getTrackData() {
        return trackData;
    }

    public void setTrackData(TrackData trackData) {
        this.trackData = trackData;
    }
}
