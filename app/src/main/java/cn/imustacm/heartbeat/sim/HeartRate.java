package cn.imustacm.heartbeat.sim;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import java.util.Random;

import cn.imustacm.heartbeat.chart.DynamicLineChartManager;
import cn.imustacm.heartbeat.utils.LogUtils;

/**
 * 项目名：HeartBeat
 * 包名：cn.imustacm.heartbeat.sim
 * 文件名：HeartRate
 * 创建时间：2018/9/18
 * 描述：心率模拟
 */

public class HeartRate {

    private DynamicLineChartManager dynamicLineChartManager;

    private static final int SIM_RATA_START = 0x11;
    private static final int SIM_RATA_STOP = 0x12;

    private boolean isStart = false;

    private TextView textView;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SIM_RATA_START:
                    if (isStart) {
                        int endValue = dynamicLineChartManager.getEndData();
                        int offset = new Random().nextInt(40) - 20;
                        int value = endValue + offset;
                        dynamicLineChartManager.addEntry(value);
                        if (textView != null) {
                            if (value <= 150) {
                                textView.setText("心率：" + value);
                            }
                        }
                        handler.sendEmptyMessageDelayed(SIM_RATA_START, 500);
                    }
                    break;
                case SIM_RATA_STOP:
                    isStart = false;
                    break;
            }
        }
    };

    public HeartRate(DynamicLineChartManager dynamicLineChartManager) {
        this(dynamicLineChartManager, null);
    }

    public HeartRate(DynamicLineChartManager dynamicLineChartManager, TextView textView) {
        this.dynamicLineChartManager = dynamicLineChartManager;
        this.textView = textView;
    }

    public boolean getState() {
        return isStart;
    }

    public void start() {
        LogUtils.e("Open HeartRate");
        isStart = true;
        handler.sendEmptyMessage(SIM_RATA_START);
    }

    public void stop() {
        LogUtils.e("Stop HeartRate");
        isStart = false;
        handler.sendEmptyMessage(SIM_RATA_STOP);
    }
}
