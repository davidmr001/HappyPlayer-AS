package com.zhangliangming.hp.ui.util;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import com.happy.lyrics.LyricsFileReader;
import com.happy.lyrics.model.LyricsInfo;
import com.happy.lyrics.model.LyricsLineInfo;
import com.happy.lyrics.model.LyricsTag;
import com.happy.lyrics.system.LyricsInfoIO;

/**
 * @author zhangliangming
 * @功能 歌词解析器
 */
public class LyricsParserUtil {
    /**
     * 时间补偿值,其单位是毫秒，正值表示整体提前，负值相反。这是用于总体调整显示快慢的。
     */
    private int defOffset = 0;
    /**
     * 增量
     */
    private int offset = 0;

    private LyricsInfo lyricsIfno;

    /**
     * TreeMap，用于封装每行的歌词信息
     */
    private TreeMap<Integer, LyricsLineInfo> lyricsLineTreeMap = null;

    public LyricsParserUtil(File lyricsFile) {
        LyricsFileReader lyricsFileReader = LyricsInfoIO.getLyricsFileReader(lyricsFile);
        try {
            lyricsIfno = lyricsFileReader.readFile(lyricsFile);

            Map<String, Object> tags = lyricsIfno.getLyricsTags();
            if (tags.containsKey(LyricsTag.TAG_OFFSET)) {
                defOffset = Integer.parseInt((String) tags.get(LyricsTag.TAG_OFFSET));
            } else {
                defOffset = 0;
            }
            lyricsLineTreeMap = lyricsIfno.getLyricsLineInfos();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LyricsParserUtil() {
        if (lyricsLineTreeMap == null)
            lyricsLineTreeMap = new TreeMap<Integer, LyricsLineInfo>();
    }

    /**
     * 通过播放的进度，获取所唱歌词行数
     *
     * @param curPlayingTime
     * @return
     */
    public int getLineNumber(int curPlayingTime) {
        for (int i = 0; i < lyricsLineTreeMap.size(); i++) {
            if (curPlayingTime >= lyricsLineTreeMap.get(i).getStartTime()
                    && curPlayingTime <= lyricsLineTreeMap.get(i).getEndTime()) {
                return i;
            }
            if (curPlayingTime > lyricsLineTreeMap.get(i).getEndTime()
                    && i + 1 < lyricsLineTreeMap.size()
                    && curPlayingTime < lyricsLineTreeMap.get(i + 1).getStartTime()) {
                return i;
            }
        }
        if (curPlayingTime >= lyricsLineTreeMap.get(lyricsLineTreeMap.size() - 1)
                .getEndTime()) {
            return lyricsLineTreeMap.size() - 1;
        }
        return 0;
    }

    /**
     * 获取当前时间正在唱的歌词的第几个字
     *
     * @param lyricsLineNum  行数
     * @param curPlayingTime
     * @return
     */
    public int getDisWordsIndex(int lyricsLineNum, int curPlayingTime) {
        if (lyricsLineNum == -1)
            return -1;
        LyricsLineInfo lyrLine = lyricsLineTreeMap.get(lyricsLineNum);
        int elapseTime = lyrLine.getStartTime();
        for (int i = 0; i < lyrLine.getLyricsWords().length; i++) {
            elapseTime += lyrLine.wordsDisInterval[i];
            if (curPlayingTime < elapseTime) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 获取当前歌词的第几个歌词的播放进度
     *
     * @param lyricsLineNum  行数
     * @param curPlayingTime
     * @return
     */
    public int getDisWordsIndexLen(int lyricsLineNum, int curPlayingTime) {
        if (lyricsLineNum == -1)
            return 0;
        LyricsLineInfo lyrLine = lyricsLineTreeMap.get(lyricsLineNum);
        int elapseTime = lyrLine.getStartTime();
        for (int i = 0; i < lyrLine.getLyricsWords().length; i++) {
            elapseTime += lyrLine.wordsDisInterval[i];
            if (curPlayingTime < elapseTime) {
                return lyrLine.wordsDisInterval[i] - (elapseTime - curPlayingTime);
            }
        }
        return 0;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * 播放的时间补偿值
     *
     * @return
     */
    public int getPlayOffset() {
        return defOffset + offset;
    }

    public TreeMap<Integer, LyricsLineInfo> getLyricsLineTreeMap() {
        return lyricsLineTreeMap;
    }

    public void setLyricsLineTreeMap(
            TreeMap<Integer, LyricsLineInfo> lyricsLineTreeMap) {
        this.lyricsLineTreeMap = lyricsLineTreeMap;
    }

    public LyricsInfo getLyricsIfno() {
        return lyricsIfno;
    }
}
