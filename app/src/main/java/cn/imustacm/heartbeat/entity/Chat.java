package cn.imustacm.heartbeat.entity;

import cn.bmob.v3.BmobObject;

/**
 * 项目名：HeartBeat
 * 包名：cn.imustacm.heartbeat.entity
 * 文件名：Chat
 * 描述：聊天数据
 */


public class Chat extends BmobObject {

    // 发送者
    private User fromUser;
    // 接收者
    private User toUser;
    // 消息内容
    private String content;

    public Chat(User fromUser, User toUser, String content) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.content = content;
    }

    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public User getToUser() {
        return toUser;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
