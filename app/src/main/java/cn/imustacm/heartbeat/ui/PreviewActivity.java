package cn.imustacm.heartbeat.ui;

import android.app.Service;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.imustacm.heartbeat.R;
import cn.imustacm.heartbeat.base.BaseActivity;
import cn.imustacm.heartbeat.chart.DynamicLineChartManager;
import cn.imustacm.heartbeat.entity.Card;
import cn.imustacm.heartbeat.entity.User;
import cn.imustacm.heartbeat.utils.LogUtils;
import cn.imustacm.heartbeat.utils.RandomUtils;
import cn.imustacm.heartbeat.utils.StaticUtils;

import static android.provider.ContactsContract.Intents.Insert.ACTION;

/**
 * 项目名：HeartBeat
 * 包名：cn.imustacm.heartbeat.ui
 * 文件名：PreviewActivity
 * 描述：发布前的预览页面
 */

public class PreviewActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {

    // 返回
    private ImageView ivBack;
    // 发布
    private TextView tvRelease;
    // 标题
    private TextView tvTitle;
    // 内容
    private TextView tvContent;
    // 心率图表
    private LineChart lineChart;
    // 心率值
    private TextView tvRate;
    // 感受区域
    private RelativeLayout feel;
    // 遮盖
    private ImageView ivMask;
    // 图表管理器
    private DynamicLineChartManager dynamicLineChartManager;

    // 模拟真实心跳
    private List<Long> jumps;
    // 震动服务对象
    private Vibrator vibrator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        // 初始化
        initialize();
        // 设置内容
        setContent();
    }

    private void initialize() {
        // 返回
        ivBack = findViewById(R.id.iv_back);
        // 发布
        tvRelease = findViewById(R.id.tv_release);
        // 标题
        tvTitle = findViewById(R.id.tv_title);
        // 内容
        tvContent = findViewById(R.id.tv_content);
        // 心率图表
        lineChart = findViewById(R.id.line_chart);
        // 心率值
        tvRate = findViewById(R.id.tv_rate);
        // 感受区域
        feel = findViewById(R.id.feel);
        // 遮盖
        ivMask = findViewById(R.id.iv_mask);
        // 图表管理器
        dynamicLineChartManager = new DynamicLineChartManager(lineChart);
        // 设置监听器
        ivBack.setOnClickListener(this);
        tvRelease.setOnClickListener(this);
        feel.setOnTouchListener(this);
    }

    private void setContent() {
        // 标题
        String title = getIntent().getStringExtra("Title");
        // 内容
        String content = getIntent().getStringExtra("Content");
        // 图表List
        List<Integer> heartRateList = getIntent().getIntegerArrayListExtra("HeartRateList");
        // 心率
        Integer rate = StaticUtils.getListAverage(heartRateList);
        // 设置标题
        tvTitle.setText(title);
        // 设置内容
        tvContent.setText(content);
        // 设置图表
        dynamicLineChartManager.addEntryList(heartRateList);
        // 设置心率
        tvRate.setText(rate + " " + "次/分");
        // 真实心跳List
        jumps = StaticUtils.toJumpList(heartRateList);
        // 震动服务对象
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_release:
                // 发布人
                User fromUser = User.getCurrentUser(User.class);
                // 标题
                String title = getIntent().getStringExtra("Title");
                // 内容
                String content = getIntent().getStringExtra("Content");
                // 图表List
                List<Integer> heartRateList = getIntent().getIntegerArrayListExtra("HeartRateList");
                // 设置卡片内容
                Card card = new Card();
                card.setFromUser(fromUser);
                card.setCardImage(RandomUtils.getRandomCardImage());
                card.setCardTitle(title);
                card.setCardContend(content);
                card.setHeartRateList(heartRateList);
                // 发布动态
                card.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            // 发布成功
                            Toast.makeText(PreviewActivity.this, "发布成功", Toast.LENGTH_LONG).show();
                            vibrator.cancel();
                            finish();
                        } else {
                            // 发布失败
                            Toast.makeText(PreviewActivity.this, "发布失败", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                // 更新用户的最近心率信息
                fromUser.setRate(String.valueOf(StaticUtils.getListAverage(heartRateList)));
                fromUser.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            // 更新成功
                            LogUtils.d("PreviewActivity...done(BmobException):" + "更新最近一次的心率信息成功");
                        } else {
                            // 更新失败
                            LogUtils.e("PreviewActivity...done(BmobException):" + "更新最近一次的心率信息失败->" + e.getErrorCode());
                        }
                    }
                });
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.feel:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ivMask.setVisibility(View.VISIBLE);
                    long[] temp = new long[jumps.size()];
                    for (int i = 0; i < jumps.size(); i++) {
                        temp[i] = jumps.get(i);
                    }
                    vibrator.vibrate(temp, -1);
                } else if (event.getAction() == MotionEvent.ACTION_UP){
                    ivMask.setVisibility(View.GONE);
                    vibrator.cancel();
                }
                break;
        }
        return true;
    }
}
