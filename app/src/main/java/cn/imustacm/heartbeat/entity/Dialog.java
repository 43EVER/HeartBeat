package cn.imustacm.heartbeat.entity;

import java.io.Serializable;
import java.util.Calendar;

/**
 * 项目名：HeartBeat
 * 包名：cn.imustacm.heartbeat.entity
 * 文件名：Dialog
 * 描述：对话数据
 */


public class Dialog implements Serializable {

    private User fromUser;
    private User toUser;
    private String newMessage;
    private Calendar createAt;

    @Override
    public String toString() {
        return "[" +
                "fromUser:" + fromUser.toString() + ", " +
                "toUser:" + toUser.toString() + ", " +
                "newMessage:" + newMessage + ", " +
                "createAt:" + createAt +
                "]";
    }

    public Dialog(User fromUser, User toUser) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.createAt = Calendar.getInstance();
    }

    public Dialog(User fromUser, User toUser, String newMessage) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.newMessage = newMessage;
        this.createAt = Calendar.getInstance();
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

    public Calendar getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Calendar createAt) {
        this.createAt = createAt;
    }

    public String getNewMessage() {
        return newMessage;
    }

    public void setNewMessage(String newMessage) {
        this.createAt = Calendar.getInstance();
        this.newMessage = newMessage;
    }
}
