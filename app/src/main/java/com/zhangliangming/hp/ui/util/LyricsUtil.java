package com.zhangliangming.hp.ui.util;

import com.happy.lyrics.system.LyricsInfoIO;
import com.zhangliangming.hp.ui.common.Constants;
import com.zhangliangming.hp.ui.model.SongMessage;
import com.zhangliangming.hp.ui.observable.ObserverManage;

import java.io.File;
import java.util.List;


/**
 * 歌词处理类
 *
 * @author zhangliangming
 */
public class LyricsUtil {

    /**
     * 加载歌词文件
     *
     * @param sid
     * @param title
     * @param singer
     * @param lrcUrl
     */
    public static void loadLyrics(final String sid, String title,
                                  String singer, final String displayName, String lrcUrl,
                                  final int type) {

        File lrcFile = getLrcFile(displayName);
        if (lrcFile == null) {
            return;
        }

        SongMessage songMessage = new SongMessage();

        if (type == SongMessage.LRCTYPELRC) {

            songMessage.setType(SongMessage.LRCLOADED);
        } else if (type == SongMessage.LRCTYPEDES) {

            songMessage.setType(SongMessage.DESLRCLOADED);
        } else if (type == SongMessage.LRCTYPELOCK) {

            songMessage.setType(SongMessage.LOCKLRCLOADED);
        }

        songMessage.setLrcFilePath(lrcFile.getPath());
        songMessage.setSid(sid);
        // 通知
        ObserverManage.getObserver().setMessage(songMessage);
    }

    /**
     * 通过音频文件名获取歌词文件
     *
     * @param displayName
     * @return
     */
    public static File getLrcFile(String displayName) {
        File lrcFile = null;
        List<String> lrcExts = LyricsInfoIO.getSupportLyricsExts();
        for (int i = 0; i < lrcExts.size(); i++) {
            String lrcFilePath = Constants.PATH_LYRICS + File.separator
                    + displayName + "." + lrcExts.get(i);
            lrcFile = new File(lrcFilePath);
            if (lrcFile.exists()) {
                break;
            }
        }
        return lrcFile;
    }

}
