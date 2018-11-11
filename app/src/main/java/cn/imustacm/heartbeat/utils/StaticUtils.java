package cn.imustacm.heartbeat.utils;

import java.util.ArrayList;
import java.util.List;

import cn.imustacm.heartbeat.entity.User;

/**
 * 项目名：HeartBeat
 * 包名：cn.imustacm.heartbeat.utils
 * 文件名：StaticUtils
 * 描述：静态工具类(静态函数，静态变量)
 */

public class StaticUtils {

    // Bmob Application ID
    public static final String BOMB_APPLICATION_ID = "90447881dbfe23214303699a2c2e3847";
    // 聊天数据表名称
    public static final String CHAT_TABLE = "Chat";
    // 合法手机号长度
    public static final Integer LEGAL_PHONE_LENGTH = 11;

    // 当前用户
    public static User getUser() {
        return User.getCurrentUser(User.class);
    }

    // ArrayList->Long[]
    public static long[] toLongArray(List<Integer> list) {
        long[] arrays = new long[list.size()];
        int index = 0;
        for (Integer temp : list) {
            arrays[index++] = temp;
        }
        return arrays;
    }

    // 计算List元素平均值
    public static Integer getListAverage(List<Integer> dataList) {
        if (dataList.size() == 0) {
            return 0;
        }
        Integer dataSum = 0;
        for (Integer data : dataList) {
            dataSum += data;
        }
        return dataSum / dataList.size();
    }

    // 心率数组转化心跳数组
    public static List<Long> toJumpList(List<Integer> dataList) {
        List<Long> list = new ArrayList<>();
        long[] temp = {0, 20, 80, 40};
        for (Integer elem : dataList) {
            list.add((long) (60 * 1000 / elem - 140));
            list.add(temp[1]);
            list.add(temp[2]);
            list.add(temp[3]);
        }
        return list;
    }
}
