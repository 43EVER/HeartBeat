package cn.imustacm.heartbeat.entity;

import com.stfalcon.chatkit.commons.models.IUser;

import java.io.Serializable;

import cn.bmob.v3.BmobUser;
import cn.imustacm.heartbeat.R;
import cn.imustacm.heartbeat.utils.RandomUtils;

/**
 * 项目名：HeartBeat
 * 包名：cn.imustacm.heartbeat.entity
 * 文件名：User
 * 描述：用户类
 */

public class User extends BmobUser implements Serializable, IUser {

    // 用户名
    private String name;
    // 用户头像
    private Integer head;
    // 用户时间轴背景图片
    private Integer timeLineImg;
    // 用户描述（个性签名）
    private String desc;
    // 性格
    private String character;
    // 爱好
    private String hobby;
    // 最近心情
    private String mood;
    // 最近心率
    private String rate;

    public User() {
        this("defaultName", R.drawable.icon_heart, "这个人很懒，什么也没有留下...");
    }

    public User(String name, Integer head, String desc) {
        this.name = name;
        this.desc = desc;
        this.head = RandomUtils.getRandomHeadImage();
        this.timeLineImg = RandomUtils.getRandomTimeLineImage();
        this.character = "无";
        this.hobby = "无";
        this.mood = "无";
        this.rate = "无";
    }

    @Override
    public String toString() {
        String toStr =
                "[" +
                "name:" + getName() + ", " +
                "desc:" + getDesc() + ", " +
                "phone:" + getMobilePhoneNumber() +
                "]";
        return toStr;
    }

    @Override
    public String getId() {
        return getObjectId();
    }

    @Override
    public String getAvatar() {
        return null;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getHead() {
        return head;
    }

    public void setHead(Integer head) {
        this.head = head;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public Integer getTimeLineImg() {
        return timeLineImg;
    }

    public void setTimeLineImg(Integer timeLineImg) {
        this.timeLineImg = timeLineImg;
    }
}
