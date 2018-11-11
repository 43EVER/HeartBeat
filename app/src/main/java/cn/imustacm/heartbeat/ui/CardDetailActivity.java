package cn.imustacm.heartbeat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.imustacm.heartbeat.R;
import cn.imustacm.heartbeat.base.BaseApplication;
import cn.imustacm.heartbeat.chart.DynamicLineChartManager;
import cn.imustacm.heartbeat.entity.Card;
import cn.imustacm.heartbeat.entity.LikeOrHate;
import cn.imustacm.heartbeat.entity.User;
import cn.imustacm.heartbeat.utils.LogUtils;
import cn.imustacm.heartbeat.utils.RandomUtils;
import cn.imustacm.heartbeat.utils.StaticUtils;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 项目名：HeartBeat
 * 包名：cn.imustacm.heartbeat.ui
 * 文件名：CardDetailActivity
 * 描述：卡片详情页面
 */

public class CardDetailActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener {

    // 返回
    private ImageView ivBack;
    // 私聊
    private TextView tvChat;
    // 用户头像
    private CircleImageView civHead;
    // 用户昵称
    private TextView tvName;
    // 发布时间
    private TextView tvTime;
    // 心率图表
    private LineChart lineChart;
    // 心率值
    private TextView tvRate;
    // 内容
    private TextView tvContent;
    // 图表管理器
    private DynamicLineChartManager dynamicLineChartManager;
    // 感受视图
    private RelativeLayout feelView;
    // 感受视图的遮罩
    private ImageView feelMask;
    // LikeOrHate
    private LikeOrHate likeOrHate;
    // 是否喜欢
    private boolean isLike;
    // 喜欢或收藏视图
    private RelativeLayout starView;
    // 喜欢或收藏视图的遮罩
    private ImageView starMask;
    // 卡片
    private Card card;
    // 模拟真实心跳
    private List<Long> jumps;
    // 震动服务对象
    private Vibrator vibrator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);

        likeOrHate = (LikeOrHate) getIntent().getSerializableExtra("State");
        card = likeOrHate.getCard();
        if (likeOrHate.getState() == 1) {
            isLike = true;
        } else {
            isLike = false;
        }
        // 初始化
        initialize();
        // 设置内容
        setContent();
    }

    private void initialize() {
        // 卡片
        card = likeOrHate.getCard();
        // 返回
        ivBack = findViewById(R.id.iv_back);
        // 私聊
        tvChat = findViewById(R.id.tv_chat);
        // 用户头像
        civHead = findViewById(R.id.civ_head);
        // 用户昵称
        tvName = findViewById(R.id.tv_name);
        // 发布时间
        tvTime = findViewById(R.id.tv_time);
        // 心率图表
        lineChart = findViewById(R.id.line_chart);
        // 心率值
        tvRate = findViewById(R.id.tv_rate);
        // 内容
        tvContent = findViewById(R.id.tv_content);
        // 图表管理器
        dynamicLineChartManager = new DynamicLineChartManager(lineChart);
        // 感受视图
        feelView = findViewById(R.id.feel);
        // 感受视图的遮罩
        feelMask = findViewById(R.id.iv_mask);
        // 喜欢或收藏视图
        starView = findViewById(R.id.star);
        // 喜欢或收藏视图的遮罩
        starMask = findViewById(R.id.star_mask);
        // 真实心跳List
        jumps = StaticUtils.toJumpList(card.getHeartRateList());
        // 震动服务对象
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        // 添加监听器
        feelView.setOnTouchListener(this);
        ivBack.setOnClickListener(this);
        tvChat.setOnClickListener(this);
        starView.setOnClickListener(this);
    }

    private void setContent() {
        // 用户头像
        civHead.setImageResource(card.getFromUser().getHead());
        // 用户昵称
        tvName.setText(card.getFromUser().getName());
        // 发布时间
        tvTime.setText(card.getCreatedAt());
        // 心率图表
        dynamicLineChartManager.addEntryList(card.getHeartRateList());
        // 心率值
        tvRate.setText(String.valueOf(StaticUtils.getListAverage(card.getHeartRateList())) + " 次/分");
        // 内容
        tvContent.setText(card.getCardContend());
        // 是否喜欢
        if (isLike) {
            // 显示遮罩
            starMask.setVisibility(View.VISIBLE);
        } else {
            // 不显示遮罩
            starMask.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.feel:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    feelMask.setVisibility(View.VISIBLE);
                    long[] temp = new long[jumps.size()];
                    for (int i = 0; i < jumps.size(); i++) {
                        temp[i] = jumps.get(i);
                    }
                    vibrator.vibrate(temp, -1);
                } else if (event.getAction() == MotionEvent.ACTION_UP){
                    feelMask.setVisibility(View.GONE);
                    vibrator.cancel();
                }
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_chat:
                User fromUser = User.getCurrentUser(User.class);
                User toUser = card.getFromUser();
                // 发布该卡片的用户可能已经被注销
                if (fromUser == null || toUser == null) {
                    return;
                }
                if (fromUser.getObjectId() == null || toUser.getObjectId() == null) {
                    return;
                }
                if (fromUser.getObjectId().equals(toUser.getObjectId())) {
                    Toast.makeText(CardDetailActivity.this, "聊天对象不能是自己", Toast.LENGTH_SHORT).show();
                    return;
                }
                BaseApplication application = (BaseApplication) getApplication();
                Intent intent = new Intent(CardDetailActivity.this, MessagesActivity.class);
                intent.putExtra("fromUser", fromUser);
                intent.putExtra("toUser", toUser);
                startActivity(intent);
                finish();
                break;
            case R.id.star:
                if (isLike) {
                    // 当前是喜欢状态，设置成不喜欢
                    isLike = !isLike;
                    likeOrHate.setState(0);
                    // 隐藏遮罩
                    starMask.setVisibility(View.GONE);
                } else {
                    // 当前是不喜欢状态，设置成喜欢
                    isLike = !isLike;
                    likeOrHate.setState(1);
                    // 显示遮罩
                    starMask.setVisibility(View.VISIBLE);
                }
                // 更新数据
                likeOrHate.update(likeOrHate.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            // 更新成功
                            if (isLike) {
                                Toast.makeText(CardDetailActivity.this, "喜欢该动态", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CardDetailActivity.this, "取消喜欢", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // 更新失败
                            if (isLike) {
                                Toast.makeText(CardDetailActivity.this, "喜欢失败", Toast.LENGTH_SHORT).show();
                                isLike = !isLike;
                                starMask.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(CardDetailActivity.this, "取消喜欢失败", Toast.LENGTH_SHORT).show();
                                isLike = !isLike;
                                starMask.setVisibility(View.VISIBLE);
                            }
                            LogUtils.e("CardDetailActivity...dong(BmobException):" + e.toString());
                        }
                    }
                });
                break;
        }
    }
}
