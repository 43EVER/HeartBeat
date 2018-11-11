package cn.imustacm.heartbeat.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.imustacm.heartbeat.R;
import cn.imustacm.heartbeat.base.BaseActivity;
import cn.imustacm.heartbeat.entity.Chat;
import cn.imustacm.heartbeat.entity.Message;
import cn.imustacm.heartbeat.entity.Messages;
import cn.imustacm.heartbeat.entity.User;
import cn.imustacm.heartbeat.utils.LogUtils;
import cn.imustacm.heartbeat.utils.SharedUtils;

/**
 * 项目名：HeartBeat
 * 包名：cn.imustacm.heartbeat.ui
 * 文件名：LoginActivity
 * 描述：聊天页面
 */

public class MessagesActivity extends BaseActivity {
    // 聊天消息适配器
    private MessagesListAdapter<Message> messagesAdapter;
    // 发送者
    private User fromUser;
    // 接收者
    private User toUser;
    // 两人聊天记录对应键值
    private String key;
    // 聊天消息序列，存储到本地
    private Messages messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        // 获取User对象
        fromUser = (User) getIntent().getSerializableExtra("fromUser");;
        toUser = (User) getIntent().getSerializableExtra("toUser");
        // 初始化键值
        key = fromUser.getObjectId() + "_" + toUser.getObjectId();
        // 设置适配器
        MessagesList chatListView = findViewById(R.id.messagesList);
        messagesAdapter = new MessagesListAdapter<>(fromUser.getObjectId(), null);
        chatListView.setAdapter(messagesAdapter);
        // 获取聊天记录
        messages = (Messages) SharedUtils.getObject(this, key);
        if (messages == null) {
            messages = new Messages();
        }
        // 显示聊天记录
        for (Message message : messages.getMessageList()) {
            messagesAdapter.addToStart(message, true);
        }
        // 设置定时任务：刷新聊天信息
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                android.os.Message msg = new android.os.Message();
                msg.what = UPDATE_DATA;
                handler.sendMessage(msg);
            }
        }, 0, 1000);

        // 获取输入框组件
        MessageInput input = findViewById(R.id.input);
        // 为输入框组件绑定监听器
        input.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                Message message = new Message(fromUser.getObjectId(), fromUser, input.toString());
                // 更新当前的聊天界面
                messagesAdapter.addToStart(message, true);
                // 存储自己发送的数据
                saveSendMessages(message);
                // 上传聊天信息到服务器
                Chat chat = new Chat(fromUser, toUser, input.toString());
                chat.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            // 上传成功
                        } else {
                            // 上传失败
                            LogUtils.e("MessagesActivity...done:" + e.getErrorCode());
                        }
                    }
                });
                return true;
            }
        });
        // 返回键
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // 设置标题栏Name
        ((TextView) findViewById(R.id.chat_name)).setText(toUser.getName());
    }

    // 存储发送的聊天数据
    private void saveSendMessages(Message message) {
        messages.getMessageList().add(message);
        SharedUtils.putObject(this, key, messages);
    }

    // 定时任务刷新聊天页面
    private Timer timer = new Timer();
    private final int UPDATE_DATA = 0x01;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_DATA:
                    refreshMessages();
                    break;
            }
        }
    };

    // 刷新收到的聊天数据
    private void refreshMessages() {
        Messages newMessages = (Messages) SharedUtils.getObject(this, key);
        if (newMessages == null) {
            newMessages = new Messages();
        }
        for (int i = messages.getMessageList().size(); i < newMessages.getMessageList().size(); i++) {
            Message message = newMessages.getMessageList().get(i);
            messagesAdapter.addToStart(message, true);
        }
        messages.setMessageList(newMessages.getMessageList());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 存储聊天数据
        SharedUtils.putObject(this, key, messages);
        // 取消定时任务
        timer.cancel();
    }
}
