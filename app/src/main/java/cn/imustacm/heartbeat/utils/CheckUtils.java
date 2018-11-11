package cn.imustacm.heartbeat.utils;

/**
 * 项目名：HeartBeat
 * 包名：cn.imustacm.heartbeat.utils
 * 文件名：CheckUtils
 * 描述：检查工具类
 */

public class CheckUtils {

    // 最短的密码长度
    private final static Integer PASSWORD_MIN_LENGTH = 8;
    // 最长的密码长度
    private final static Integer PASSWORD_MAX_LENGTH = 15;
    // 密码的合法字符
    private final static String LEGAL = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    // 密码合法状态码
    public final static Integer PASSWORD_STATUS_LEGAL = 0;
    // 密码长度过短状态码
    public final static Integer PASSWORD_STATUS_SHORT = 1;
    // 密码长度过长状态码
    public final static Integer PASSWORD_STATUS_LONG = 2;
    // 密码中含有非法字符状态码
    public final static Integer PASSWORD_STATUS_NOLEGAL = 3;
    // 密码过于简单状态码
    public final static Integer PASSWORD_STATUS_SIMPLE = 4;

    // 密码合法性检测
    public static Integer judgePassword(String password) {
        // 判断密码长度是否过短
        if (password.length() < PASSWORD_MIN_LENGTH) {
            return PASSWORD_STATUS_SHORT;
        }
        // 判断密码长度是否过长
        if (password.length() > PASSWORD_MAX_LENGTH) {
            return PASSWORD_STATUS_LONG;
        }
        // 判断密码是否具有非法字符
        int letterNum = 0;
        int digitalNum = 0;
        for (int i = 0; i < password.length(); i++) {
            if (LEGAL.indexOf(password.charAt(i)) == -1) {
                return PASSWORD_STATUS_NOLEGAL;
            }
            if (Character.isLetter(password.charAt(i))) {
                ++letterNum;
            }
            if (Character.isDigit(password.charAt(i))) {
                ++digitalNum;
            }
        }
        // 判断密码复杂程度
        if (letterNum == 0 || digitalNum == 0) {
            return PASSWORD_STATUS_SIMPLE;
        }
        return PASSWORD_STATUS_LEGAL;
    }
}
