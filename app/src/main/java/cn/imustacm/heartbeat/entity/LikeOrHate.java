package cn.imustacm.heartbeat.entity;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

/**
 * 项目名：HeartBeat
 * 包名：cn.imustacm.heartbeat.entity
 * 文件名：LikeOrHate
 * 描述：某用户对某动态的评价（喜欢：1，讨厌：2）
 */

public class LikeOrHate extends BmobObject implements Serializable{

    // 用户
    private User user;
    // 动态
    private Card card;
    // 状态
    private Integer state;

    public LikeOrHate(User user, Card card, Integer state) {
        this.user = user;
        this.card = card;
        this.state = state;
    }

    @Override
    public String toString() {
        String toStr = "[" +
                "user:" + user.toString() + ", " +
                "card:" + card.toString() +
                "]";
        return toStr;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public Card getCard() {
        return card;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}
