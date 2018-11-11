package cn.imustacm.heartbeat.utils;

import android.content.Context;

public class UnitUtils {

    // Dp单位转Px单位
    public static int dpToPx(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    // Px单位转Dp单位
    public static int pxToDp(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}
