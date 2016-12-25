package com.zhangliangming.hp.ui.manage;

import com.zhangliangming.hp.ui.util.LyricsParserUtil;

import java.io.File;

/**
 * 歌词处理类
 *
 * @author Administrator
 */
public class LyricsManage {

    /**
     * 当前歌词的歌曲sid
     */
    private static String mSid = "";

    /**
     * 当前歌词解析器
     */
    private static LyricsParserUtil lyricsParser = null;

    /**
     * 通过歌曲的sid和歌词路径获取歌词解析器
     *
     * @param sid
     * @param lrcFile
     * @return
     */
    public static LyricsParserUtil getLyricsParser(String sid, File lrcFile) {
        if (sid.equals(mSid)) {
            if (lyricsParser == null) {
                lyricsParser = new LyricsParserUtil(lrcFile);
            }
        } else {
            mSid = sid;
            lyricsParser = new LyricsParserUtil(lrcFile);
        }
        return lyricsParser;
    }

    /**
     * @param sid
     * @return
     */
    public static LyricsParserUtil getLyricsParserByInputStream(String sid) {
        if (sid.equals(mSid)) {
            if (lyricsParser == null) {
                lyricsParser = new LyricsParserUtil();
            }
        } else {
            mSid = sid;
            lyricsParser = new LyricsParserUtil();

        }
        return lyricsParser;
    }

    /**
     * 清空数据
     */
    public static void clean() {
        lyricsParser = null;
    }
}
