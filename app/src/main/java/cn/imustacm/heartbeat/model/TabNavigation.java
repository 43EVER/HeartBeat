package cn.imustacm.heartbeat.model;

/**
 * 项目名：HeartBeat
 * 包名：cn.imustacm.heartbeat.model
 * 文件名：TabNavigation
 * 描述：页面导航栏
 */

public class TabNavigation {

    // 导航栏标题
    private String tabTitle;
    // 导航栏图片资源
    private Integer tabImageNormal;
    private Integer tabImageClicked;
    // 构造函数
    public TabNavigation(String tabTitle, Integer tabImageNormal, Integer tabImageClicked) {
        this.tabTitle = tabTitle;
        this.tabImageNormal = tabImageNormal;
        this.tabImageClicked = tabImageClicked;
    }

    public String getTabTitle() {
        return tabTitle;
    }

    public void setTabTitle(String tabTitle) {
        this.tabTitle = tabTitle;
    }

    public Integer getTabImageNormal() {
        return tabImageNormal;
    }

    public void setTabImageNormal(Integer tabImageNormal) {
        this.tabImageNormal = tabImageNormal;
    }

    public Integer getTabImageClicked() {
        return tabImageClicked;
    }

    public void setTabImageClicked(Integer tabImageClicked) {
        this.tabImageClicked = tabImageClicked;
    }
}
