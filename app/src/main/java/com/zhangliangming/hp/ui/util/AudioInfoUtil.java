package com.zhangliangming.hp.ui.util;

import com.tulskiy.musique.audio.AudioFileReader;
import com.tulskiy.musique.model.TrackData;
import com.tulskiy.musique.system.TrackIO;
import com.zhangliangming.hp.ui.model.AudioInfo;
import com.zhangliangming.hp.ui.model.SongInfo;

import java.io.File;

/**
 * Created by zhangliangming on 2017/1/9.
 */

public class AudioInfoUtil {

    /**
     * @param songInfo
     * @return
     */
    public static AudioInfo getAudioInfo(SongInfo songInfo) {
        File file = new File(songInfo.getFilePath());
        AudioFileReader audioFileReader = TrackIO
                .getAudioFileReader(file.getName());
        if (audioFileReader == null
                || audioFileReader.read(file) == null) {
            return null;
        }

        TrackData trackData = audioFileReader.read(file);
        AudioInfo audioInfo = new AudioInfo();
        audioInfo.setSongInfo(songInfo);
        audioInfo.setTrackData(trackData);

        return audioInfo;
    }
}
