package com.zhangliangming.hp.ui.util;

import android.graphics.Paint;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.happy.lyrics.LyricsFileReader;
import com.happy.lyrics.model.LyricsInfo;
import com.happy.lyrics.model.LyricsLineInfo;
import com.happy.lyrics.model.LyricsTag;
import com.happy.lyrics.system.LyricsInfoIO;
import com.happy.lyrics.utils.TimeUtils;

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
    /**
     * 默认的歌词集合
     */
    private TreeMap<Integer, LyricsLineInfo> defLyricsLineTreeMap = null;

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
            defLyricsLineTreeMap = lyricsIfno.getLyricsLineInfos();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LyricsParserUtil() {
        if (defLyricsLineTreeMap == null)
            defLyricsLineTreeMap = new TreeMap<Integer, LyricsLineInfo>();
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
    //--------重构歌词-------//

    /**
     * 获取重构后的歌词
     *
     * @param viewWidth
     * @param paint
     * @return
     */
    private TreeMap<Integer, LyricsLineInfo> getReconstructLyrics(int viewWidth, Paint paint) {
        // 这里面key为该行歌词的开始时间，方便后面排序
        SortedMap<Integer, LyricsLineInfo> lyricsLineInfosTemp = new TreeMap<Integer, LyricsLineInfo>();
        for (int i = 0; i < defLyricsLineTreeMap.size(); i++) {

            LyricsLineInfo lyricsLineInfo = defLyricsLineTreeMap.get(i);
            reconstructLyrics(lyricsLineInfo, lyricsLineInfosTemp,
                    paint, viewWidth);

        }
        // 重新排序封装
        lyricsLineTreeMap = new TreeMap<Integer, LyricsLineInfo>();
        int index = 0;
        Iterator<Integer> it = lyricsLineInfosTemp.keySet().iterator();
        while (it.hasNext()) {
            lyricsLineTreeMap.put(index++, lyricsLineInfosTemp.get(it.next()));
        }
//        for (int i = 0; i < lyricsLineTreeMap.size(); i++) {
//            LyricsLineInfo lyricsLineInfo = lyricsLineTreeMap.get(i);
//            System.out.println(lyricsLineInfo.getStartTimeStr() + "   " + lyricsLineInfo.getEndTimeStr() + "  " + lyricsLineInfo.getLineLyrics());
//        }

        return lyricsLineTreeMap;
    }


    /**
     * 重构歌词
     *
     * @param lyricsLineInfo
     * @param lyricsLineInfosTemp
     * @param paint
     * @param viewWidth
     */
    private void reconstructLyrics(LyricsLineInfo lyricsLineInfo,
                                   SortedMap<Integer, LyricsLineInfo> lyricsLineInfosTemp,
                                   Paint paint, int viewWidth) {
        String lineLyrics = lyricsLineInfo.getLineLyrics();
        // 行歌词数组
        String[] lyricsWords = lyricsLineInfo.getLyricsWords();
        // 每行的歌词长度
        int lineWidth = (int) paint.measureText(lineLyrics);
        int maxLineWidth = viewWidth / 3 * 2;
        if (lineWidth > maxLineWidth) {
            // 最大的歌词行数
            int maxLrcLineNum = lineWidth % maxLineWidth == 0 ? lineWidth
                    / maxLineWidth : (lineWidth / maxLineWidth + 1);
            // 最大的行歌词长度
            int maxLrcLineWidth = lineWidth / maxLrcLineNum;
            // 大于视图的宽度
            int lastIndex = lyricsWords.length - 1;
            int lyricsWordsWidth = 0;
            for (int i = lyricsWords.length - 1; i >= 0; i--) {

                // 当前的歌词宽度
                lyricsWordsWidth += (int) paint.measureText(lyricsWords[i]);
                // 上一个字的宽度
                int preLyricsWordWidth = 0;
                if ((i - 1) > 0) {
                    preLyricsWordWidth = (int) paint.measureText(lyricsWords[(i - 1)]);
                }
                if (lyricsWordsWidth + preLyricsWordWidth > maxLrcLineWidth) {

                    LyricsLineInfo newLyricsLineInfo = getNewLyricsLineInfo(
                            lyricsLineInfo, i, lastIndex);

                    if (newLyricsLineInfo != null)
                        //
                        lyricsLineInfosTemp.put(
                                newLyricsLineInfo.getStartTime(),
                                newLyricsLineInfo);

                    //
                    lastIndex = i - 1;
                    lyricsWordsWidth = 0;
                } else if (i == 0) {
                    LyricsLineInfo newLyricsLineInfo = getNewLyricsLineInfo(
                            lyricsLineInfo, 0, lastIndex);

                    if (newLyricsLineInfo != null)
                        //
                        lyricsLineInfosTemp.put(
                                newLyricsLineInfo.getStartTime(),
                                newLyricsLineInfo);
                }
            }

        } else {
            lyricsLineInfosTemp.put(lyricsLineInfo.getStartTime(),
                    lyricsLineInfo);
        }
    }

    /**
     * 根据新歌词的索引和旧歌词数据，构造新的歌词数据
     *
     * @param lyricsLineInfo 旧的行歌词数据
     * @param startIndex     开始歌词索引
     * @param lastIndex      结束歌词索引
     * @return
     */
    private LyricsLineInfo getNewLyricsLineInfo(
            LyricsLineInfo lyricsLineInfo, int startIndex, int lastIndex) {

        if (lastIndex < 0)
            return null;
        LyricsLineInfo newLyricsLineInfo = new LyricsLineInfo();
        // 行开始时间
        int lineStartTime = lyricsLineInfo.getStartTime();
        int startTime = lineStartTime;
        int endTime = 0;
        String lineLyrics = "";
        List<String> lyricsWordsList = new ArrayList<String>();
        List<Integer> wordsDisIntervalList = new ArrayList<Integer>();
        String[] lyricsWords = lyricsLineInfo.getLyricsWords();
        int[] wordsDisInterval = lyricsLineInfo.getWordsDisInterval();
        for (int i = 0; i <= lastIndex; i++) {
            if (i < startIndex) {
                startTime += wordsDisInterval[i];
            } else {
                lineLyrics += lyricsWords[i];
                wordsDisIntervalList.add(wordsDisInterval[i]);
                lyricsWordsList.add(lyricsWords[i]);
                endTime += wordsDisInterval[i];
            }
        }
        endTime += startTime;
        //
        String[] newLyricsWords = lyricsWordsList
                .toArray(new String[lyricsWordsList.size()]);
        int newWordsDisInterval[] = getWordsDisIntervalList(wordsDisIntervalList);
        newLyricsLineInfo.setEndTimeStr(TimeUtils.parseString(endTime));
        newLyricsLineInfo.setEndTime(endTime);
        newLyricsLineInfo.setStartTime(startTime);
        newLyricsLineInfo.setStartTimeStr(TimeUtils.parseString(startTime));
        newLyricsLineInfo.setLineLyrics(lineLyrics);
        newLyricsLineInfo.setLyricsWords(newLyricsWords);
        newLyricsLineInfo.setWordsDisInterval(newWordsDisInterval);

        return newLyricsLineInfo;
    }

    /**
     * 获取每个歌词的时间
     *
     * @param wordsDisIntervalList
     * @return
     */
    private int[] getWordsDisIntervalList(
            List<Integer> wordsDisIntervalList) {
        int wordsDisInterval[] = new int[wordsDisIntervalList.size()];
        for (int i = 0; i < wordsDisIntervalList.size(); i++) {
            wordsDisInterval[i] = wordsDisIntervalList.get(i);
        }
        return wordsDisInterval;
    }
    //--------------//

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

    /**
     * 获取重构后的歌词集合
     *
     * @param viewWidth
     * @param paint
     * @return
     */
    public TreeMap<Integer, LyricsLineInfo> getReconstructLyricsLineTreeMap(int viewWidth, Paint paint) {
        return getReconstructLyrics(viewWidth, paint);
    }

    public void setDefLyricsLineTreeMap(TreeMap<Integer, LyricsLineInfo> defLyricsLineTreeMap) {
        this.defLyricsLineTreeMap = defLyricsLineTreeMap;
    }

    public TreeMap<Integer, LyricsLineInfo> getDefLyricsLineTreeMap() {
        return defLyricsLineTreeMap;
    }

    public LyricsInfo getLyricsIfno() {
        return lyricsIfno;
    }
}
