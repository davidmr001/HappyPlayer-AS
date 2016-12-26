package com.zhangliangming.hp.ui.logger;

import java.util.Hashtable;

import android.content.Context;
import android.util.Log;

import com.zhangliangming.hp.ui.common.Constants;


public class LoggerManage {
    /**
     * 是否输出log
     */
    private final static boolean logFlag = true;

    /**
     * 应用名标签
     */
    public final static String tag = "[" + Constants.APPNAME + "]";
    private String userName = null;
    /**
     * 保存用户对应的Logger
     */
    private static Hashtable<String, LoggerManage> sLoggerTable = new Hashtable<String, LoggerManage>();


    public LoggerManage(String userName) {
        this.userName = userName;
    }

    /**
     * @param userName 用戶名
     * @return
     */
    public static LoggerManage getLogger(String userName) {
        LoggerManage userLogger = sLoggerTable.get(userName);
        if (userLogger == null) {
            userLogger = new LoggerManage(userName);
            sLoggerTable.put(userName, userLogger);
        }
        return userLogger;

    }

    /**
     * 创建zhangliangming用法
     *
     * @return
     */
    public static LoggerManage getZhangLogger(Context mContext) {
        String name = "zhangliangming";
        LoggerManage userLogger = sLoggerTable.get(name);
        if (userLogger == null) {
            userLogger = new LoggerManage(name);
            sLoggerTable.put(name, userLogger);
        }
        return userLogger;
    }

    /**
     * 获取方法名
     *
     * @return
     */
    private String getFunctionName() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts == null) {
            return null;
        }
        for (StackTraceElement st : sts) {
            if (st.isNativeMethod()) {
                continue;
            }
            if (st.getClassName().equals(Thread.class.getName())) {
                continue;
            }
            if (st.getClassName().equals(this.getClass().getName())) {
                continue;
            }
            return "@" + userName + "@ " + "[ "
                    + Thread.currentThread().getName() + ": "
                    + st.getClassName() + ":" + st.getLineNumber() + " "
                    + st.getMethodName() + " ]";
        }
        return null;
    }

    public void i(String str) {
        if (logFlag) {
            String name = getFunctionName();
            if (name != null) {
                Log.i(tag, name + " - " + str);
            } else {
                Log.i(tag, str.toString());
            }
        }
    }

    public void d(String str) {
        if (logFlag) {
            String name = getFunctionName();
            if (name != null) {
                Log.d(tag, name + " - " + str);
            } else {
                Log.d(tag, str.toString());
            }
        }
    }

    public void f(String str) {
        if (logFlag) {
            String name = getFunctionName();
            if (name != null) {
                Log.wtf(tag, name + " - " + str);
            } else {
                Log.wtf(tag, str.toString());
            }
        }
    }

    public void w(String str) {
        if (logFlag) {
            String name = getFunctionName();
            if (name != null) {
                Log.w(tag, name + " - " + str);
            } else {
                Log.w(tag, str.toString());
            }
        }
    }

    public void e(String str) {
        if (logFlag) {
            String name = getFunctionName();
            if (name != null) {
                Log.e(tag, name + " - " + str);
            } else {
                Log.e(tag, str.toString());
            }
        }
    }

}
