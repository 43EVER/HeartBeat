package cn.imustacm.heartbeat.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import cn.imustacm.heartbeat.model.TabNavigation;

/**
 * 项目名：HeartBeat
 * 包名：cn.imustacm.heartbeat.adapter
 * 文件名：TabLayoutAdapter
 * 描述：底栏适配器
 */

public class TabLayoutAdapter extends FragmentPagerAdapter {

    // Tab页面指示器
    private List<TabNavigation> mTabNavi;
    // Tab页面元素
    private List<Fragment> mFragment;

    // 构造函数
    public TabLayoutAdapter(FragmentManager fragmentManager, List<TabNavigation> mTabNavi, List<Fragment> mFragment) {
        super(fragmentManager);
        this.mTabNavi = mTabNavi;
        this.mFragment = mFragment;
    }

    // 选中的Item
    @Override
    public Fragment getItem(int position) {
        return mFragment.get(position);
    }

    // Item的个数
    @Override
    public int getCount() {
        return mTabNavi.size();
    }

    // 设置标题
    @Override
    public CharSequence getPageTitle(int position) {
        return mTabNavi.get(position).getTabTitle();
    }
}
