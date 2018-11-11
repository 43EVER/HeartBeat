package cn.imustacm.heartbeat.rate;

/**
 * 项目名：HeartBeat
 * 包名：cn.imustacm.heartbeat.heartrate
 * 文件名：ImageProcessing
 * 描述：图像处理类
 */

public class ImageProcessing {

    // YUV420SP格式图像
    private byte[] mYUV420SP;
    // 图像宽度
    private int mWidth;
    // 图像高度
    private int mHeight;

    // 构造函数
    public ImageProcessing(byte[] mYUV420SP, int mWidth, int mHeight) {
        this.mYUV420SP = mYUV420SP;
        this.mWidth = mWidth;
        this.mHeight = mHeight;
    }

    // 控制数值范围
    private int controlValue(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    // 返回图片属性
    public int getImageRedSum() {
        if (mYUV420SP == null) {
            return 0;
        }
        int imageSize = mWidth * mHeight;
        int redSum = 0;
        for (int i = 0, yp = 0; i < mHeight; i++) {
            int uvp = imageSize + (i >> 1) * mWidth;
            int u = 0, v = 0;
            for (int j = 0; j < mWidth; j++, yp++) {
                int y = (0xff & ((int) mYUV420SP[yp])) - 16;
                y = Math.max(0, y);
                if ((j & 1) == 0) {
                    v = (0xff & mYUV420SP[uvp++]) - 128;
                    u = (0xff & mYUV420SP[uvp++]) - 128;
                }
                int y1192 = y * 1192;
                int r = controlValue(y1192 + 1634 * v, 0, 262143);
                int g = controlValue(y1192 - 833 * v - 400 * u, 0, 262143);
                int b = controlValue(y1192 + 2066 * u, 0, 262143);

                int pixel = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
                int red = (pixel >> 16) & 0xff;
                redSum += red;
            }
        }
        return redSum / imageSize;
    }
}