package cn.imustacm.heartbeat.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;

import cn.imustacm.heartbeat.R;
import cn.imustacm.heartbeat.adapter.ItemDragAdapter;
import cn.imustacm.heartbeat.base.BaseApplication;
import cn.imustacm.heartbeat.base.BaseFragment;
import cn.imustacm.heartbeat.entity.Dialog;
import cn.imustacm.heartbeat.entity.Dialogs;
import cn.imustacm.heartbeat.entity.Message;
import cn.imustacm.heartbeat.entity.Messages;
import cn.imustacm.heartbeat.entity.User;
import cn.imustacm.heartbeat.ui.MessagesActivity;
import cn.imustacm.heartbeat.utils.LogUtils;
import cn.imustacm.heartbeat.utils.SharedUtils;

import static cn.bmob.v3.Bmob.getApplicationContext;

/**
 * 项目名：HeartBeat
 * 包名：cn.imustacm.heartbeat.fragment
 * 文件名：ChatFragment
 * 描述：对话页面
 */

public class ChatFragment extends BaseFragment {

    // 聊天列表视图
    private RecyclerView recyclerView;
    // 聊天列表容器
    private Dialogs dialogs;
    // 聊天列表适配器
    private ItemDragAdapter itemDragAdapter;

    private ItemTouchHelper itemTouchHelper;
    private ItemDragAndSwipeCallback itemDragAndSwipeCallback;

    private String key;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        key = User.getCurrentUser(User.class).getObjectId();
        dialogs = (Dialogs) SharedUtils.getObject(getActivity(), key);
        if (dialogs == null) {
            dialogs = new Dialogs();
        }
        for (Dialog dialog : dialogs.getDialogList()) {
            addDialog(dialog.getFromUser(), dialog.getToUser(), dialog.getNewMessage());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedUtils.putObject(getActivity(), key, dialogs);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, null);
        recyclerView = view.findViewById(R.id.rv_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ((BaseApplication) getActivity().getApplication()).setChatFragment(this);

        itemDragAdapter = new ItemDragAdapter(dialogs.getDialogList());
        itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(itemDragAdapter);
        itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        itemDragAndSwipeCallback.setSwipeMoveFlags(ItemTouchHelper.START | ItemTouchHelper.END);
        itemDragAdapter.enableSwipeItem();
        itemDragAdapter.enableDragItem(itemTouchHelper);
//        itemDragAdapter.setOnItemDragListener(onItemDragListener);
//        itemDragAdapter.setOnItemSwipeListener(onItemSwipeListener);

        recyclerView.setAdapter(itemDragAdapter);

        itemDragAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                User fromUser = dialogs.getDialogList().get(position).getFromUser();
                User toUser = dialogs.getDialogList().get(position).getToUser();
                Intent intent = new Intent(getActivity(), MessagesActivity.class);
                intent.putExtra("fromUser", fromUser);
                intent.putExtra("toUser", toUser);
                startActivity(intent);
            }
        });

        return view;
    }

    private OnItemDragListener onItemDragListener = new OnItemDragListener() {
        @Override
        public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos) {
            LogUtils.e("drag start");
        }

        @Override
        public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to) {
            LogUtils.e("move from: " + source.getAdapterPosition() + " to: " + target.getAdapterPosition());
        }

        @Override
        public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {
            LogUtils.e("drag end");
        }
    };

    private OnItemSwipeListener onItemSwipeListener = new OnItemSwipeListener() {
        @Override
        public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {
            LogUtils.e("view swiped start: " + pos);
        }

        @Override
        public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {
            LogUtils.e("View reset: " + pos);
        }

        @Override
        public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {
            LogUtils.e("View Swiped: " + pos);
        }

        @Override
        public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {
            canvas.drawColor(ContextCompat.getColor(getActivity(), R.color.color_light_blue));
        }
    };

    // 添加一个新的对话
    public void addDialog(User fromUser, User toUser, String newMessage) {
        for (Dialog dialog : dialogs.getDialogList()) {
            // 如果当前对话列表已经有此人，就不添加新的对话了
            if (dialog.getToUser().getObjectId().equals(toUser.getObjectId())) {
                return;
            }
        }
        Dialog dialog = new Dialog(fromUser, toUser, newMessage);
        itemDragAdapter.addData(dialog);
    }

    // 接收聊天信息
    public void receivedMessage(User fromUser, User toUser, String content) {
        // 两人之间聊天信息数据的键值
        String key = toUser.getObjectId() + "_" + fromUser.getObjectId();
        // 获取当前用户
        User currentUser = User.getCurrentUser(User.class);
        // 不是发给我的消息，不进行处理
        if (!currentUser.getObjectId().equals(toUser.getObjectId())) {
            return;
        }
        // 是我的消息，处理该消息
        // 手机震动
        Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(300);
        // 播放提示音
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), uri);
        ringtone.play();
        // 遍历我的对话列表
        for (int i = 0; i < dialogs.getDialogList().size(); i++) {
            User tempUser = dialogs.getDialogList().get(i).getToUser();
            // 说明我的对话列表中有此人
            if (tempUser.getObjectId().equals(fromUser.getObjectId())) {
                // 更新对话列表
                dialogs.getDialogList().get(i).setNewMessage(content);
                itemDragAdapter.notifyDataSetChanged();
                refreshMessages(fromUser, toUser, content);
                return;
            }
        }
        // 说明我的对话列表中无此人
        addDialog(currentUser, fromUser, content);
        refreshMessages(fromUser, toUser, content);
    }

    // 更新聊天数据
    private void refreshMessages(User fromUser, User toUser, String content) {
        String key = toUser.getObjectId() + "_" + fromUser.getObjectId();
        Messages messages = (Messages) SharedUtils.getObject(getActivity(), key);
        if (messages == null) {
            messages = new Messages();
        }
        Message message = new Message("", fromUser, content);
        messages.getMessageList().add(message);
        SharedUtils.putObject(getActivity(), key, messages);
    }
}
