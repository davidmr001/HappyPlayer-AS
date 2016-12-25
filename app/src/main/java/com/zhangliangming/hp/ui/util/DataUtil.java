package com.zhangliangming.hp.ui.util;

import java.io.File;
import java.util.Map;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.zhangliangming.hp.ui.common.Constants;
import com.zhangliangming.hp.ui.manage.MediaManage;


/**
 * SharedPreferences配置文件处理和皮肤数据初始化处理
 *
 * @author zhangliangming
 */
public class DataUtil {
    private static SharedPreferences preferences;

    /**
     * 初始化，将所有配置文件里的数据赋值给Constants
     *
     * @param context
     */
    public static void init(Context context) {
        if (preferences == null) {
            preferences = context.getSharedPreferences(
                    Constants.PREFERENCE_NAME, 0);
        }
        initFont(context);
        initSharedPreferences();
        initFile(context);
        // 应用第一次使用，先将assets里面的皮肤包解压
        if (Constants.isFrist) {

            Constants.isFrist = false;
            saveValue(context, Constants.isFrist_KEY, Constants.isFrist);
        }
        initSongData(context);
    }

    /**
     * 初始化歌曲数据
     *
     * @param context
     */
    private static void initSongData(Context context) {
        MediaManage.getMediaManage(context);
    }

    /**
     * 加载字体
     *
     * @param context
     */
    private static void initFont(Context context) {
        FontUtil.getInstance(context);
    }

    /**
     * @throws
     * @Title: initSharedPreferences
     * @Description: (初始化基本的数据配置)
     * @param:
     * @return: void
     */
    private static void initSharedPreferences() {

        // 是否是第一次启动
        Constants.isFrist = preferences.getBoolean(Constants.isFrist_KEY,
                Constants.isFrist);
        // 是否是wifi网络设置
        Constants.isWifi = preferences.getBoolean(Constants.isWifi_KEY,
                Constants.isWifi);
        // 播放模式
        Constants.playModel = preferences.getInt(Constants.playModel_KEY,
                Constants.playModel);
        // 桌面歌词
        Constants.showDesktopLyrics = preferences.getBoolean(
                Constants.showDesktopLyrics_KEY, Constants.showDesktopLyrics);
        // 桌面歌词的位置x轴
        Constants.LRCX = preferences.getInt(Constants.LRCX_KEY, Constants.LRCX);

        // 桌面歌词的位置Y轴
        Constants.LRCY = preferences.getInt(Constants.LRCY_KEY, Constants.LRCY);

        // 桌面歌词是否可以移动
        Constants.desktopLyricsIsMove = preferences.getBoolean(
                Constants.desktopLyricsIsMove_KEY,
                Constants.desktopLyricsIsMove);

        // 锁屏歌词
        Constants.showLockScreen = preferences.getBoolean(
                Constants.showLockScreen_KEY, Constants.showLockScreen);
        // 是否线控
        Constants.isWire = preferences.getBoolean(Constants.isWire_KEY,
                Constants.isWire);
        // 是否开启辅助操控
        Constants.isEasyTouch = preferences.getBoolean(
                Constants.isEasyTouch_KEY, Constants.isEasyTouch);
        // 是否开启问候音
        Constants.isSayHello = preferences.getBoolean(Constants.isSayHello_KEY,
                Constants.isSayHello);
        // 音质索引
        Constants.soundIndex = preferences.getInt(Constants.soundIndex_KEY,
                Constants.soundIndex);
        // 播放列表类型
        Constants.playListType = preferences.getInt(Constants.playListType_KEY,
                Constants.playListType);
        // 歌曲id
        Constants.playInfoID = preferences.getString(Constants.playInfoID_KEY,
                Constants.playInfoID);

        // 标题颜色索引
        Constants.colorIndex = preferences.getInt(Constants.colorIndex_KEY,
                Constants.colorIndex);
        // 歌词颜色索引
        Constants.lrcColorIndex = preferences.getInt(
                Constants.lrcColorIndex_KEY, Constants.lrcColorIndex);
        // 歌词字体大小
        Constants.lrcFontSize = preferences.getInt(Constants.lrcFontSize_KEY,
                Constants.lrcFontSize);

        // 桌面歌词字体大小
        Constants.desktopLrcFontSize = preferences.getInt(
                Constants.desktopLrcFontSize_KEY, Constants.desktopLrcFontSize);

        // 桌面歌词颜色索引
        Constants.desktopLrcIndex = preferences.getInt(
                Constants.desktopLrcIndex_KEY, Constants.desktopLrcIndex);

        // 是否是第一次点击显示桌面歌词
        Constants.isFristSettingDesLrc = preferences.getBoolean(
                Constants.isFristSettingDesLrc_KEY,
                Constants.isFristSettingDesLrc);

    }

    /**
     * @throws
     * @Title: initFile
     * @Description: (初始化文件夹)
     * @param:
     * @return: void
     */
    public static void initFile(Context context) {
        // 创建相关的文件夹
        File file = new File(Constants.PATH_AUDIO);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(Constants.PATH_LYRICS);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(Constants.PATH_ARTIST);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(Constants.PATH_ALBUM);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(Constants.PATH_LOGCAT);
        if (!file.exists()) {
            file.mkdirs();
        }

        file = new File(Constants.PATH_CRASH);
        if (!file.exists()) {
            file.mkdirs();
        }

        file = new File(Constants.PATH_CACHE);
        if (!file.exists()) {
            file.mkdirs();
        }

        file = new File(Constants.PATH_CACHE_IMAGE);
        if (!file.exists()) {
            file.mkdirs();
        }

        file = new File(Constants.PATH_CACHE_AUDIO);
        if (!file.exists()) {
            file.mkdirs();
        }

        file = new File(Constants.PATH_SKIN);
        if (!file.exists()) {
            file.mkdirs();
        }

        file = new File(Constants.PATH_APK);
        if (!file.exists()) {
            file.mkdirs();
        }

        file = new File(Constants.PATH_SPLASH);
        if (!file.exists()) {
            file.mkdirs();
        }

        file = new File(Constants.PATH_EasyTouch);
        if (!file.exists()) {
            file.mkdirs();
        }

        file = new File(Constants.PATH_AUDIOTEMP);
        if (!file.exists()) {
            file.mkdirs();
        }

    }

    /**
     * 保存数据到SharedPreferences配置文件
     *
     * @param context
     * @param datas   数据集合
     */
    public static void saveValues(Context context, Map<String, Object> datas) {
        if (preferences == null) {
            preferences = context.getSharedPreferences(
                    Constants.PREFERENCE_NAME, 0);
        }
        Editor editor = preferences.edit();

        for (String key : datas.keySet()) {
            Object value = datas.get(key);
            if (value instanceof Boolean) {
                editor.putBoolean(key, (Boolean) value);
            } else if (value instanceof Integer) {
                editor.putInt(key, (Integer) value);
            } else if (value instanceof String) {
                editor.putString(key, (String) value);
            } else if (value instanceof Float) {
                editor.putFloat(key, (Float) value);
            } else if (value instanceof Long) {
                editor.putFloat(key, (Long) value);
            }
        }
        // 提交修改
        editor.commit();
    }


    /**
     * 保存数据到SharedPreferences配置文件
     *
     * @param context
     * @param key     关键字
     * @param data    要保存的数据
     */
    public static void saveValue(Context context, String key, Object data) {
        if (preferences == null) {
            preferences = context.getSharedPreferences(
                    Constants.PREFERENCE_NAME, 0);
        }
        Editor editor = preferences.edit();
        if (data instanceof Boolean) {
            editor.putBoolean(key, (Boolean) data);
        } else if (data instanceof Integer) {
            editor.putInt(key, (Integer) data);
        } else if (data instanceof String) {
            editor.putString(key, (String) data);
        } else if (data instanceof Float) {
            editor.putFloat(key, (Float) data);
        } else if (data instanceof Long) {
            editor.putFloat(key, (Long) data);
        }

        // 提交修改
        editor.commit();
    }

    /**
     * 从SharedPreferences配置文件中获取数据
     *
     * @param context
     * @param key     关键字
     * @param defData 默认获取的数据
     * @return
     */
    public static Object getValue(Context context, String key, Object defData) {
        if (preferences == null) {
            preferences = context.getSharedPreferences(
                    Constants.PREFERENCE_NAME, 0);
        }

        if (defData instanceof Boolean) {
            return preferences.getBoolean(key, (Boolean) defData);
        } else if (defData instanceof Integer) {
            return preferences.getInt(key, (Integer) defData);
        } else if (defData instanceof String) {
            return preferences.getString(key, (String) defData);
        } else if (defData instanceof Float) {
            return preferences.getFloat(key, (Float) defData);
        } else if (defData instanceof Long) {
            return preferences.getLong(key, (Long) defData);
        }

        return null;

    }
}
