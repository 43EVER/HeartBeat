package cn.imustacm.heartbeat.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.imustacm.heartbeat.R;
import cn.imustacm.heartbeat.base.BaseActivity;
import cn.imustacm.heartbeat.chart.DynamicLineChartManager;
import cn.imustacm.heartbeat.rate.ImageProcessing;
import cn.imustacm.heartbeat.sim.HeartRate;
import cn.imustacm.heartbeat.utils.LogUtils;

import static android.media.MediaRecorder.VideoSource.CAMERA;

/**
 * 项目名：HeartBeat
 * 包名：cn.imustacm.heartbeat.ui
 * 文件名：ReleaseActivity
 * 描述：发布页面
 */

public class ReleaseActivity extends BaseActivity implements View.OnClickListener, TextView.OnEditorActionListener {

    // 返回
    private TextView tvBack;
    // 提交
    private Button btnSubmit;
    // 心率图表
    private LineChart lineChart;
    // 实时心率文本
    private TextView tvHeartRate;
    // 标题
    private EditText etTitle;
    // 内容
    private EditText etContent;
    // 图表管理器
    private DynamicLineChartManager dynamicLineChartManager;
    private HeartRate heartRate;
    // 相机预览
    private SurfaceView preview;
    // 预览设置
    private SurfaceHolder previewHolder;
    // 相机
    private Camera camera;

    private PowerManager.WakeLock wakeLock;
    // 手指是否放置在摄像头
    private boolean finger = false;
    private int imgAvg = 0;
    // 使用Toast对象能防止刷新速度过快而导致上一次的Toast未结束便显示了当前Toast
    private Toast toast;
    // 定时任务，检测手指输出Toast
    private Timer timer = new Timer();
    private static final int UPDATE_CODE = 0x01;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_CODE:
                    judgeFinger();
                    break;
            }
        }
    };

    private void judgeFinger() {
        // 判断手指是否处于摄像头前
        if (finger) {
            // 判断是否已经处于开启状态
            if (!heartRate.getState()) {
                heartRate.start();
            }
        } else {
            heartRate.stop();
        }
        // 判断是否已经将手指放置在摄像头
        if (imgAvg < 190 && !finger) {
            toast.setText("请用您的指尖盖住摄像头镜头");
            toast.show();
        } else {
            finger = true;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        return event.getKeyCode() == KeyEvent.KEYCODE_ENTER;
    }

    public enum TYPE {
        GREEN, RED
    }

    //设置默认类型
    private TYPE currentType = TYPE.GREEN;
    //心跳下标值
    private int beatsIndex = 0;
    //心跳数组的大小
    private final int beatsArraySize = 3;
    //心跳数组
    private final int[] beatsArray = new int[beatsArraySize];
    //心跳脉冲
    private double beats = 0;
    //开始时间
    private long startTime = 0;

    private static final AtomicBoolean processing = new AtomicBoolean(false);

    // 计算心率时候需要处理的数据
    private int averageIndex = 0;
    private final int averageArraySize = 4;
    private final int[] averageArray = new int[averageArraySize];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release);
        // 初始化
        initialize();
        // 动态申请权限
        if (hasPermission(Manifest.permission.CAMERA)) {
            initConfig();
        } else {
            requestPermission(CAMERA_CODE, Manifest.permission.CAMERA);
        }
    }

    private static final int CAMERA_CODE = 0x22;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_CODE:
                initConfig();
                break;
        }
    }

    // 初始化
    private void initialize() {
        // 初始化Toast
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        // 返回
        tvBack = findViewById(R.id.tv_back);
        // 提交
        btnSubmit = findViewById(R.id.btn_submit);
        // 心率图表
        lineChart = findViewById(R.id.lc_heart_rate);
        // 实时心率文本
        tvHeartRate = findViewById(R.id.tv_heart);
        // 标题
        etTitle = findViewById(R.id.et_title);
        // 内容
        etContent = findViewById(R.id.et_content);
        // 相机预览
        preview = findViewById(R.id.surface_view);
        // 图表管理器
        dynamicLineChartManager = new DynamicLineChartManager(lineChart);
        heartRate = new HeartRate(dynamicLineChartManager, tvHeartRate);
        // 绑定监听
        tvBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        etTitle.setOnEditorActionListener(this);
        // 启动定时任务
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(UPDATE_CODE);
            }
        }, 1000, 500);
    }

    // 配置初始化
    private void initConfig() {
        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.btn_submit:
                // 标题
                String title = etTitle.getText().toString();
                // 内容
                String content = etContent.getText().toString();
                // 心率List
                List<Integer> heartRateList = dynamicLineChartManager.getDataList();
                // 判断标题是否为空
                if (TextUtils.isEmpty(title)) {
                    Toast.makeText(ReleaseActivity.this, "标题不能为空", Toast.LENGTH_LONG).show();
                    return;
                }
                // 判断内容是否为空
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(ReleaseActivity.this, "内容不能为空", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(ReleaseActivity.this, PreviewActivity.class);
                intent.putExtra("Title", title);
                intent.putExtra("Content", content);
                intent.putIntegerArrayListExtra("HeartRateList", (ArrayList<Integer>) heartRateList);
                startActivity(intent);
                finish();
                break;
        }
    }

    // 相机预览方法：通过获取手机摄像头的参数来实时动态计算平均像素值、脉冲数，进而实时动态计算心率值。
    private Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera cam) {
            if (data == null)
                throw new NullPointerException();
            Camera.Size size = cam.getParameters().getPreviewSize();
            if (size == null)
                throw new NullPointerException();
            if (!processing.compareAndSet(false, true))
                return;
            int width = size.width;
            int height = size.height;

            // 图像处理
            imgAvg = new ImageProcessing(data.clone(), height, width).getImageRedSum();
            LogUtils.e("平均像素值：" + imgAvg);

            if (imgAvg == 0 || imgAvg == 255) {
                processing.set(false);
                return;
            }
            // 计算平均值
            int averageArrayAvg = 0;
            int averageArrayCnt = 0;
            for (int i = 0; i < averageArray.length; i++) {
                if (averageArray[i] > 0) {
                    averageArrayAvg += averageArray[i];
                    averageArrayCnt++;
                }
            }
            int rollingAverage = (averageArrayCnt > 0) ? (averageArrayAvg / averageArrayCnt) : 0;

            TYPE newType = currentType;
            if (imgAvg < rollingAverage) {
                newType = TYPE.RED;
                if (newType != currentType) {
                    beats++;
                    finger = false;
                    LogUtils.e("脉冲数：" + beats);
                }
            } else if (imgAvg > rollingAverage) {
                newType = TYPE.GREEN;
            }

            if (averageIndex == averageArraySize)
                averageIndex = 0;
            averageArray[averageIndex++] = imgAvg;

            // 从当前状态转化到另一种状态
            if (newType != currentType) {
                currentType = newType;
            }
            // 获取结束时间，进行本次心率数据的计算
            long endTime = System.currentTimeMillis();
            double totalTimeInSecs = (endTime - startTime) / 1000;
            if (totalTimeInSecs >= 2) {
                double bps = (beats / totalTimeInSecs);
                int dpm = (int) (bps * 60);
                if (dpm < 30 || dpm > 180 || imgAvg < 200) {
                    // 获取开始时间
                    startTime = System.currentTimeMillis();
                    //beats心跳总数
                    beats = 0;
                    processing.set(false);
                    return;
                }
                if (beatsIndex == beatsArraySize)
                    beatsIndex = 0;
                beatsArray[beatsIndex++] = dpm;
                int beatsArrayAvg = 0;
                int beatsArrayCnt = 0;
                for (int i = 0; i < beatsArray.length; i++) {
                    if (beatsArray[i] > 0) {
                        beatsArrayAvg += beatsArray[i];
                        beatsArrayCnt++;
                    }
                }
                int beatsAvg = (beatsArrayAvg / beatsArrayCnt);

                dynamicLineChartManager.addEntry(beatsAvg);
                if (beatsArrayAvg < 150) {
                    tvHeartRate.setText("心率：" + beatsArrayAvg);
                }

                LogUtils.e("心率：" + beatsAvg);
                // 获取开始时间，开始新一轮心率的计算
                startTime = System.currentTimeMillis();
                beats = 0;
            }
            processing.set(false);
        }
    };

    // 预览回调
    private SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        // 预览视图被创建
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera.setPreviewDisplay(previewHolder);
                camera.setPreviewCallback(previewCallback);
            } catch (Exception t) {
                new Exception("SurfaceCreated Failure");
            }
        }

        // 预览视图改变
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            Camera.Size size = getSmallestPreviewSize(width, height, parameters);
            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);
            }
            camera.setParameters(parameters);
            camera.startPreview();
        }

        // 预览视图被销毁
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    };

    // 相机最小预览尺寸
    private static Camera.Size getSmallestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;
        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;
                    if (newArea < resultArea) {
                        result = size;
                    }
                }
            }
        }
        return result;
    }

    @Override
    public void onResume() {
        super.onResume();
        wakeLock.acquire();
        camera = Camera.open();
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onPause() {
        super.onPause();
        // 释放资源
        wakeLock.release();
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
        // 关闭定时任务
        timer.cancel();
    }
}
