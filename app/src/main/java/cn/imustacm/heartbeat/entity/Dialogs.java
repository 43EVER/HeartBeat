package cn.imustacm.heartbeat.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目名：HeartBeat
 * 包名：cn.imustacm.heartbeat.entity
 * 文件名：Dialogs
 * 描述：对话列表数据
 */

public class Dialogs implements Serializable {

    private List<Dialog> dialogList;

    public Dialogs() {
        this.dialogList = new ArrayList<>();
    }

    @Override
    public String toString() {
        String toStr = "[";
        for (Dialog dialog : dialogList) {
            toStr += dialog.toString() + ", ";
        }
        toStr += "]";
        return toStr;
    }

    public List<Dialog> getDialogList() {
        return dialogList;
    }

    public void setDialogList(List<Dialog> dialogList) {
        this.dialogList = dialogList;
    }
}
