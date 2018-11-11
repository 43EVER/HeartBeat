package cn.imustacm.heartbeat.adapter;

import android.animation.Animator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.imustacm.heartbeat.R;
import cn.imustacm.heartbeat.entity.Card;
import cn.imustacm.heartbeat.utils.LogUtils;
import cn.imustacm.heartbeat.utils.RandomUtils;

/**
 * 项目名：HeartBeat
 * 包名：cn.imustacm.heartbeat.adapter
 * 文件名：CardAdapter
 * 描述：主页卡片适配器
 */

public class CardAdapter extends BaseItemDraggableAdapter<Card, BaseViewHolder>{
    // 构造器
    public CardAdapter(int layoutResId, List<Card> data) {
        super(layoutResId, data);
    }

    // 设置视图内容
    @Override
    protected void convert(BaseViewHolder helper, Card item) {
        helper.setImageResource(R.id.img, item.getCardImage());
        helper.setText(R.id.tweetName, item.getCardTitle());
        helper.setText(R.id.tweetText, item.getCardContend());
        helper.setText(R.id.tweetDate, item.getCreatedAt());
    }

    @Override
    protected void startAnim(Animator anim, int index) {
        super.startAnim(anim, index);
        if (index < 5)
            anim.setStartDelay(index * 150);
    }
}
