package cn.imustacm.heartbeat.utils;

import android.util.Log;

/**
 * 项目名：HeartBeat
 * 包名：cn.imustacm.heartbeat.utils
 * 文件名：LogUtils
 * 描述：调试工具类
 */

public class LogUtils {
    // 调试开关
    public static final boolean DEBUG = true;
    // TAG
    public static final String TAG = "HeartBeat";
    // 语句级别：V
    public static void v(String text) {
        if (DEBUG) {
            Log.v(TAG, text);
        }
    }
    public static void v(String tag, String text) {
        if (DEBUG) {
            Log.e(TAG + "/" + tag, text);
        }
    }
    // 语句级别：D
    public static void d(String text) {
        if (DEBUG) {
            Log.d(TAG, text);
        }
    }
    public static void d(String tag, String text) {
        if (DEBUG) {
            Log.e(TAG + "/" + tag, text);
        }
    }
    // 语句级别：I
    public static void i(String text) {
        if (DEBUG) {
            Log.i(TAG, text);
        }
    }
    public static void i(String tag, String text) {
        if (DEBUG) {
            Log.e(TAG + "/" + tag, text);
        }
    }
    // 语句级别：W
    public static void w(String text) {
        if (DEBUG) {
            Log.w(TAG, text);
        }
    }
    public static void w(String tag, String text) {
        if (DEBUG) {
            Log.e(TAG + "/" + tag, text);
        }
    }
    // 语句级别：E
    public static void e(String text) {
        if (DEBUG) {
            Log.e(TAG, text);
        }
    }
    public static void e(String tag, String text) {
        if (DEBUG) {
            Log.e(TAG + "/" + tag, text);
        }
    }
}
