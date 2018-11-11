package cn.imustacm.heartbeat.manager;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名：HeartBeat
 * 包名：cn.imustacm.heartbeat.utils
 * 文件名：ActivityManagerUtils
 * 描述：活动管理工具类
 */

public class ActivityManager{

    // 活动List集合
    public static List<Activity> activityList = new ArrayList<Activity>();
    // 添加活动
    public static void addActivity(Activity activity){
        if (!activityList.contains(activity)){
            activityList.add(activity);
        }
    }
    // 移除活动
    public static void removeActivity(Activity activity){
        activityList.remove(activity);
    }
    // 终止所有活动
    public static void finishAllActivity(){
        for (Activity activity : activityList){
            if (!activity.isFinishing()){
                activity.finish();
            }
        }
    }

}