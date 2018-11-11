package cn.imustacm.heartbeat.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目名：HeartBeat
 * 包名：cn.imustacm.heartbeat.entity
 * 文件名：Messages
 * 描述：对话列表数据
 */

public class Messages implements Serializable {

    private List<Message> messageList;

    public Messages() {
        this.messageList = new ArrayList<>();
    }

    @Override
    public String toString() {
        String toStr = new String();
        for (Message message : messageList) {
            toStr += message.getText() + ", ";
        }
        return toStr;
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }
}
