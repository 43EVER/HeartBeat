package cn.imustacm.heartbeat.base;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.ValueEventListener;
import cn.imustacm.heartbeat.entity.LikeOrHate;
import cn.imustacm.heartbeat.entity.User;
import cn.imustacm.heartbeat.fragment.ChatFragment;
import cn.imustacm.heartbeat.utils.LogUtils;
import cn.imustacm.heartbeat.utils.StaticUtils;

import static android.media.MediaRecorder.VideoSource.CAMERA;

/**
 * 项目名：HeartBeat
 * 包名：cn.imustacm.heartbeat.base
 * 文件名：BaseApplication
 * 描述：Application基类
 */

public class BaseApplication extends Application {

    // 对话列表页面
    private ChatFragment chatFragment;
    // 是否喜欢
    private LikeOrHate likeOrHate;

    @Override
    public void onCreate() {
        super.onCreate();
        // Bmob初始化
        Bmob.initialize(this, StaticUtils.BOMB_APPLICATION_ID);
        // 实时数据监听
        final BmobRealTimeData bmobRealTimeData = new BmobRealTimeData();
        bmobRealTimeData.start(new ValueEventListener() {
            @Override
            public void onConnectCompleted(Exception e) {
                if (e == null) {
                    if (bmobRealTimeData.isConnected()) {
                        // 成功连接
                        bmobRealTimeData.subTableUpdate(StaticUtils.CHAT_TABLE);
                    } else {
                        // 未成功连接
                        LogUtils.e("BaseApplication...onConnectCompleted(Exception):" + "连接Bmob失败");
                    }
                } else {
                    // 出现异常
                    LogUtils.e("BaseApplication...onConnectCompleted(Exception):" + e.toString());
                }
            }

            @Override
            public void onDataChange(JSONObject jsonObject) {
                // 发送者的ID
                final String fromUserId = jsonObject.optJSONObject("data").optString("fromUser");
                // 接收者的ID
                final String toUserId = jsonObject.optJSONObject("data").optString("toUser");
                // 发送的消息
                final String content = jsonObject.optJSONObject("data").optString("content");

                // 发送者的User
                final User[] fromUser = new User[1];
                // 接收者的User
                final User[] toUser = new User[1];

                // 查询发送者和接收者的User，方便后续的操作
                String[] objectIds = {fromUserId, toUserId};
                BmobQuery<User> query = new BmobQuery<>();
                query.addWhereContainedIn("objectId", Arrays.asList(objectIds));
                query.findObjects(new FindListener<User>() {
                    @Override
                    public void done(List<User> list, BmobException e) {
                        if (e == null) {
                            // 查询成功
                            if (list.size() == 2) {
                                if (list.get(0).getObjectId().equals(fromUserId)) {
                                    fromUser[0] = list.get(0);
                                    toUser[0] = list.get(1);
                                } else {
                                    toUser[0] = list.get(0);
                                    fromUser[0] = list.get(1);
                                }
                                chatFragment.receivedMessage(fromUser[0], toUser[0], content);
                            } else {
                                LogUtils.e("BaseApplication...done(List<User>, BmobException):" + "数据个数不为2");
                            }
                        } else {
                            // 查询失败
                            LogUtils.e("BaseApplication...onDataChange(JSONObject):" + e.getErrorCode());
                        }
                    }
                });
            }
        });

    }

    public ChatFragment getChatFragment() {
        return chatFragment;
    }

    public void setChatFragment(ChatFragment chatFragment) {
        this.chatFragment = chatFragment;
    }

    public LikeOrHate getLikeOrHate() {
        return likeOrHate;
    }

    public void setLikeOrHate(LikeOrHate likeOrHate) {
        this.likeOrHate = likeOrHate;
    }
}
