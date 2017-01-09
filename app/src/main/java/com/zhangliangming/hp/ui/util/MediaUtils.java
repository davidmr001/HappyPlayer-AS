package com.zhangliangming.hp.ui.util;


import java.io.File;
import java.text.DecimalFormat;
import java.util.Date;

import com.happy.lyrics.utils.FileUtils;
import com.happy.lyrics.utils.TimeUtils;
import com.tulskiy.musique.audio.AudioFileReader;
import com.tulskiy.musique.model.TrackData;
import com.tulskiy.musique.system.TrackIO;
import com.tulskiy.musique.util.AudioMath;
import com.zhangliangming.hp.ui.model.SongInfo;

public class MediaUtils {
    /**
     * 通过文件获取mp3的相关数据信息
     *
     * @param filePath
     * @return
     */

    public static SongInfo getSongInfoByFile(String filePath) {
        File sourceFile = new File(filePath);
        if (!sourceFile.exists())
            return null;

        if (sourceFile.length() < 1024 * 1024) {
            return null;
        }

        SongInfo songInfo = null;
        try {

            AudioFileReader audioFileReader = TrackIO
                    .getAudioFileReader(sourceFile.getName());
            if (audioFileReader == null
                    || audioFileReader.read(sourceFile) == null) {
                return null;
            }
            TrackData trackData = audioFileReader.read(sourceFile);

            double totalMS = AudioMath.samplesToMillis(
                    trackData.getTotalSamples(), trackData.getSampleRate());
            long duration = Math.round(totalMS);

            String durationStr = TimeUtils.parseString((int) duration);

            if (duration < 5000) {
                return null;
            }

            songInfo = new SongInfo();

            // 文件名
            String displayName = FileUtils.removeExt(sourceFile.getName());
            String artist = trackData.getArtist();
            String title = trackData.getTitle();


            if (artist == null || (artist != null && StringUtils.isMessyCode(artist))) {

                if (displayName.contains("-")) {
                    String[] titleArr = displayName.split("-");
                    artist = titleArr[0].trim();
                } else
                    artist = "";
            }

            if (title == null || !title.contains("-")) {

                if (displayName.contains("-")) {
                    String[] titleArr = displayName.split("-");
                    if (artist == null || artist.equals(""))
                        artist = titleArr[0].trim();
                    if (title == null || title.equals(""))
                        title = titleArr[1].trim();
                } else {
                    artist = "";
                    title = displayName;
                }
            } else {
                String[] titleArr = title.split("-");
                if (artist == null || artist.equals(""))
                    artist = titleArr[0].trim();
                title = titleArr[1].trim();
            }

            if (title != null && StringUtils.isMessyCode(title)) {

                if (displayName.contains("-")) {
                    String[] titleArr = displayName.split("-");
                    title = titleArr[1].trim();
                } else
                    title = "";
            }


            displayName = artist + " - " + title;

            songInfo.setSid(IDGenerate.getId("SI-"));
            songInfo.setDisplayName(displayName);
            songInfo.setSinger(artist);
            songInfo.setTitle(title);
            songInfo.setDuration(duration);
            songInfo.setDurationStr(durationStr);
            songInfo.setSize(sourceFile.length());
            songInfo.setSizeStr(getFileSize(sourceFile.length()));
            songInfo.setFilePath(filePath);
            songInfo.setType(SongInfo.LOCALSONG);
            songInfo.setIslike(SongInfo.UNLIKE);
            songInfo.setDownloadStatus(SongInfo.DOWNLOADED);
            songInfo.setCreateTime(DateUtil.dateToString(new Date()));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return songInfo;

    }

    /**
     * 时间格式转换
     *
     * @param time
     * @return
     */
    public static String formatTime(int time) {

        time /= 1000;
        int minute = time / 60;
        // int hour = minute / 60;
        int second = time % 60;
        minute %= 60;
        return String.format("%02d:%02d", minute, second);
    }

    /**
     * 计算文件的大小，返回相关的m字符串
     *
     * @param fileS
     * @return
     */
    public static String getFileSize(long fileS) {// 转换文件大小
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    public static String removeExt(String s) {
        int index = s.lastIndexOf(".");
        if (index == -1)
            index = s.length();
        return s.substring(0, index);
    }

    public static String getFileExt(String filePath) {
        int pos = filePath.lastIndexOf(".");
        if (pos == -1)
            return "";
        return filePath.substring(pos + 1).toLowerCase();
    }


}
