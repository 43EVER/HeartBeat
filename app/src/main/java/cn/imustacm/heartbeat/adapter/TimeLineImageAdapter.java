package cn.imustacm.heartbeat.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import cn.imustacm.heartbeat.utils.RandomUtils;
import cn.imustacm.heartbeat.utils.UnitUtils;

public class TimeLineImageAdapter extends BaseAdapter {

    // Context
    private Context context;

    public TimeLineImageAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return RandomUtils.timeLineImages.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // Item没有被加载过
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(UnitUtils.dpToPx(context, 160), UnitUtils.dpToPx(context, 90)));

            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setImageResource(RandomUtils.timeLineImages[position]);
        return imageView;
    }
}
