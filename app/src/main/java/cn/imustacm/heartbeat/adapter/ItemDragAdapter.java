package cn.imustacm.heartbeat.adapter;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import cn.imustacm.heartbeat.R;
import cn.imustacm.heartbeat.entity.Dialog;

/**
 * 项目名：HeartBeat
 * 包名：cn.imustacm.heartbeat.adapter
 * 文件名：ItemDragAdapter
 * 描述：对话列表元素适配器
 */

public class ItemDragAdapter extends BaseItemDraggableAdapter<Dialog, BaseViewHolder> {

    // 构造器
    public ItemDragAdapter(List<Dialog> data) {
        super(R.layout.item_draggable_view, data);
    }

    // 设置视图内容
    @Override
    protected void convert(BaseViewHolder helper, Dialog item) {
        helper.setText(R.id.tv, item.getToUser().getName());
        helper.setText(R.id.new_message, item.getNewMessage());
        helper.setImageResource(R.id.civ_head, item.getToUser().getHead());

        // 判断显示时间还是日期
        if (Calendar.getInstance().getTime().getTime() - item.getCreateAt().getTime().getTime() < 86400 * 1000) {
            helper.setText(R.id.date, new SimpleDateFormat("HH:mm").format(item.getCreateAt().getTime()));
        } else {
            helper.setText(R.id.date, new SimpleDateFormat("yyyy/MM/dd").format(item.getCreateAt().getTime()));
        }
    }
}
