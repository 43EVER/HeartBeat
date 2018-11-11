package cn.imustacm.heartbeat.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 项目名：HeartBeat
 * 包名：cn.imustacm.heartbeat.utils
 * 文件名：SharedUtils
 * 描述：存储工具类
 */

public class SharedUtils {

    public static final String NAME = "HeartBeat";

    // Object
    public static void putObject(Context context, String key, Object object) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            String value = new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
            sharedPreferences.edit().putString(key, value).commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object getObject(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(NAME, context.MODE_PRIVATE);
        String value = sharedPreferences.getString(key, "");
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.decode(value.getBytes(), Base64.DEFAULT));
        Object object = null;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            object = objectInputStream.readObject();
            return object;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return object;
    }

    // String
    public static void putString(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key, value).commit();
    }

    public static String getString(Context context, String key, String defValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defValue);
    }

    // int
    public static void putInt(Context context, String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(key, value).commit();
    }

    public static int getInt(Context context, String key, int defValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, defValue);
    }

    // long
    public static void putLong(Context context, String key, long value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putLong(key, value).commit();
    }

    public static long getLong(Context context, String key, long defValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(key, defValue);
    }

    // boolean
    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(key, value).commit();
    }

    public static boolean getBoolean(Context context, String key, boolean defValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, defValue);
    }

    // float
    public static void putFloat(Context context, String key, float value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putFloat(key, value).commit();
    }

    public static float getFloat(Context context, String key, float defValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(key, defValue);
    }

    // 删除单个键值对
    public static void deleteKey(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(key).commit();
    }

    // 删除所有键值对
    public static void deleteAllKey(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();
    }
}
