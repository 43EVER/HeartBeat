package cn.imustacm.heartbeat.entity;

import com.stfalcon.chatkit.commons.models.IMessage;

import java.io.Serializable;
import java.util.Date;

/**
 * 项目名：HeartBeat
 * 包名：cn.imustacm.heartbeat.entity
 * 文件名：Message
 * 描述：聊天数据
 */

public class Message implements IMessage, Serializable {

    private String id;
    private String text;
    private Date createdAt;
    private User user;

    public Message(String id, User user, String text) {
        this(id, user, text, new Date());
    }

    public Message(String id, User user, String text, Date createdAt) {
        this.id = id;
        this.text = text;
        this.user = user;
        this.createdAt = createdAt;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }

    @Override
    public User getUser() {
        return this.user;
    }

}
