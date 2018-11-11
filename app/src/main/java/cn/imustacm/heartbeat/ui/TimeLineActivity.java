package cn.imustacm.heartbeat.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.qap.ctimelineview.TimelineRow;
import org.qap.ctimelineview.TimelineViewAdapter;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.imustacm.heartbeat.R;
import cn.imustacm.heartbeat.base.BaseActivity;
import cn.imustacm.heartbeat.entity.Card;
import cn.imustacm.heartbeat.entity.LikeOrHate;
import cn.imustacm.heartbeat.entity.User;
import cn.imustacm.heartbeat.utils.LogUtils;
import cn.imustacm.heartbeat.utils.RandomUtils;
import cn.imustacm.heartbeat.utils.StaticUtils;

/**
 * 项目名：HeartBeat
 * 包名：cn.imustacm.heartbeat.ui
 * 文件名：TimeLineActivity
 * 描述：时光轴
 */

public class TimeLineActivity extends BaseActivity {

    // 图片
    private ImageView ivTimeLine;
    // TimeLine
    private ListView mTimeLineView;
    // 时间轴数据
    private ArrayList<TimelineRow> mTimeLineList = new ArrayList<>();
    // 时间轴适配器
    private ArrayAdapter<TimelineRow> mTimeLineAdapter;

    // 查询自己发布的动态成功
    private final static int RELEASE_QUERY_RETURN_TRUE = 0x01;
    // 查询自己发布的动态失败
    private final static int RELEASE_QUERY_RETURN_FALSE = 0x02;
    // 查询自己喜欢的动态成功
    private final static int LIKEORHATE_QUERY_RETURN_TRUE = 0x03;
    // 查询自己喜欢的动态失败
    private final static int LIKEORHATE_QUERY_RETURN_FALSE = 0x04;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                // 查询自己发布的动态成功
                case RELEASE_QUERY_RETURN_TRUE:
                    // 提取数据
                    List<Card> cardList = (List<Card>) msg.obj;
                    if (cardList == null || cardList.size() == 0) {
                        return;
                    }
                    // 向List中添加自己发布的动态
                    addReleaseData((List<Card>) msg.obj);
                    // 刷新数据
                    refreshTimeLine();
                    break;
                // 查询自己发布的动态失败
                case RELEASE_QUERY_RETURN_FALSE:

                    break;
                // 查询自己喜欢的动态成功
                case LIKEORHATE_QUERY_RETURN_TRUE:
                    // 提取数据
                    List<LikeOrHate> likeList = (List<LikeOrHate>) msg.obj;
                    if (likeList == null || likeList.size() == 0) {
                        return;
                    }
                    // 向List中添加自己喜欢的动态
                    addLikeData((List<LikeOrHate>) msg.obj);
                    // 刷新数据
                    refreshTimeLine();
                    break;
                // 查询自己喜欢的动态失败
                case LIKEORHATE_QUERY_RETURN_FALSE:

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ivTimeLine = findViewById(R.id.timelint_img);
        mTimeLineView = findViewById(R.id.timeline_listView);
        // 图片点击监听器
        ivTimeLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(TimeLineActivity.this, TimeLineImageSelectActivity.class), IMAGE_CHANGE_CODE);
            }
        });
        // 返回点击监听器
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTimeLineView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        getTimeLineDate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 获取当前用户
        User user = User.getCurrentUser(User.class);
        // 设置图片
        ivTimeLine.setImageResource(user.getTimeLineImg());
    }

    // 获取数据
    private void getTimeLineDate() {
        // 查询自己发布的动态
        BmobQuery<Card> cardQuery = new BmobQuery<>();
        cardQuery.addWhereEqualTo("fromUser", StaticUtils.getUser());
        cardQuery.findObjects(new FindListener<Card>() {
            @Override
            public void done(List<Card> list, BmobException e) {
                if (e == null) {
                    // 查询成功
                    LogUtils.d("发布动态数目：" + list.size());
                    Message msg = new Message();
                    msg.obj = list;
                    msg.what = RELEASE_QUERY_RETURN_TRUE;
                    handler.sendMessage(msg);
                } else {
                    // 查询失败
                    LogUtils.e("查询自己发布的动态时出现错误：" + e.getErrorCode());
                    Message msg = new Message();
                    msg.what = RELEASE_QUERY_RETURN_FALSE;
                    handler.sendMessage(msg);
                }
            }
        });
        // 查询自己喜欢的动态
        BmobQuery<LikeOrHate> likeOrHateQuery = new BmobQuery<>();
        likeOrHateQuery.addWhereEqualTo("user", StaticUtils.getUser());
        likeOrHateQuery.addWhereEqualTo("state", 1);
        likeOrHateQuery.include("card");
        likeOrHateQuery.findObjects(new FindListener<LikeOrHate>() {
            @Override
            public void done(List<LikeOrHate> list, BmobException e) {
                if (e == null) {
                    // 查询成功
                    LogUtils.d("喜欢动态数目：" + list.size());
                    for (int i = 0; i < list.size(); i++) {
                        LogUtils.e(list.get(i).toString());
                    }
                    Message msg = new Message();
                    msg.obj = list;
                    msg.what = LIKEORHATE_QUERY_RETURN_TRUE;
                    handler.sendMessage(msg);
                } else {
                    // 查询失败
                    LogUtils.e("查询自己喜欢的动态时出现错误：" + e.getErrorCode());
                    Message msg = new Message();
                    msg.what = LIKEORHATE_QUERY_RETURN_FALSE;
                    handler.sendMessage(msg);
                }
            }
        });
    }

    private void addReleaseData(List<Card> cardList) {
        for (int i = 0; i < cardList.size(); i++) {
            Card card = cardList.get(i);

            TimelineRow timelineRow = null;
            try {
                timelineRow = setAttribute(card, "分享了一条心情");
                // 设置时间日期
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = format.parse(card.getCreatedAt());
                timelineRow.setDate(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            mTimeLineList.add(timelineRow);
        }
    }

    private void addLikeData(List<LikeOrHate> likeList) {
        for (int i = 0; i < likeList.size(); i++) {

            Card card = likeList.get(i).getCard();

            TimelineRow timelineRow = null;
            try {
                timelineRow = setAttribute(card, "收藏了一条心情");
                // 设置时间日期
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = format.parse(likeList.get(i).getUpdatedAt());
                timelineRow.setDate(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            mTimeLineList.add(timelineRow);
        }
    }

    private TimelineRow setAttribute(Card card, String state) {
        TimelineRow timelineRow = new TimelineRow(1);
        // 设置标题
        timelineRow.setTitle(state);
        // 设置描述
        timelineRow.setDescription("\u3000\u3000" + card.getCardContend());
        // 设置连接线的颜色
        timelineRow.setBellowLineColor(Color.parseColor("#39cdb7"));
        // 设置连接线的尺寸
        timelineRow.setBellowLineSize(2);
        // 设置时间轴背景颜色
        timelineRow.setBackgroundColor(Color.parseColor("#ff8f97"));
        // 设置时间轴背景尺寸
        timelineRow.setBackgroundSize(12);
        // 设置日期的颜色
        timelineRow.setDateColor(Color.parseColor("#AEAEAE"));
        // 设置标题的颜色
        timelineRow.setTitleColor(Color.parseColor("#515151"));
        // 设置描述的颜色
        timelineRow.setDescriptionColor(Color.parseColor("#505050"));
        return timelineRow;
    }

    private void refreshTimeLine() {
        // 重新初始化适配器
        mTimeLineAdapter = new TimelineViewAdapter(TimeLineActivity.this, 0, mTimeLineList, true);
        // 设置适配器
        mTimeLineView.setAdapter(mTimeLineAdapter);
    }

    private static final int IMAGE_CHANGE_CODE = 0x31;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case IMAGE_CHANGE_CODE:
                    // 获取用户选择的图片
                    Bundle bundle = data.getExtras();
                    // 防止异常退出
                    if (bundle == null) {
                        LogUtils.e("TimeLineActivity...onActivityResult(int, int, Intent):" + "bundle is null.");
                        return;
                    }
                    final Integer postion = bundle.getInt("TimeLineImage");
                    // 更新用户数据
                    User user = User.getCurrentUser(User.class);
                    user.setTimeLineImg(RandomUtils.timeLineImages[postion]);
                    user.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                // 更新成功
                                Toast.makeText(TimeLineActivity.this, "图片更新成功", Toast.LENGTH_LONG).show();
                                // 设置新图片
                                ivTimeLine.setImageResource(RandomUtils.timeLineImages[postion]);
                            } else {
                                // 头像更新失败
                                Toast.makeText(TimeLineActivity.this, "图片更新失败", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    break;
            }
        }
    }

}
