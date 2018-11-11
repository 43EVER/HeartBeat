package cn.imustacm.heartbeat.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.bmob.v3.BmobObject;

/**
 * 项目名：HeartBeat
 * 包名：cn.imustacm.heartbeat.entity
 * 文件名：Card
 * 描述：卡片数据
 */

public class Card extends BmobObject implements Serializable {

    // 发布人
    private User fromUser;
    // 卡片图片
    private Integer cardImage;
    // 卡片标题
    private String cardTitle;
    // 卡片内容
    private String cardContend;
    // 心跳数据
    private List<Integer> heartRateList;

    public Card() {
        this.fromUser = User.getCurrentUser(User.class);
        this.cardTitle = "Null Title.";
        this.cardContend = "Null Content";
        this.heartRateList = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 50; i++) {
            heartRateList.add(random.nextInt(200));
        }
    }

    @Override
    public String toString() {
        String toStr =
                "[" +
                "fromUser:" + fromUser.toString() + ", " +
                "cardTitle:" + cardTitle + ", " +
                "cardContend:" + cardContend + ", " +
                "heartRateList:" + heartRateList.toString() +
                "]";
        return toStr;
    }

    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public String getCardTitle() {
        return cardTitle;
    }

    public void setCardTitle(String cardTitle) {
        this.cardTitle = cardTitle;
    }

    public String getCardContend() {
        return cardContend;
    }

    public void setCardContend(String cardContend) {
        this.cardContend = cardContend;
    }

    public List<Integer> getHeartRateList() {
        return heartRateList;
    }

    public void setHeartRateList(List<Integer> heartRateList) {
        this.heartRateList = heartRateList;
    }

    public Integer getCardImage() {
        return cardImage;
    }

    public void setCardImage(Integer cardImage) {
        this.cardImage = cardImage;
    }
}
