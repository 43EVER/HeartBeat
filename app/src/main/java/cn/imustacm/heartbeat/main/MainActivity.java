package cn.imustacm.heartbeat.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import cn.imustacm.heartbeat.R;
import cn.imustacm.heartbeat.adapter.TabLayoutAdapter;
import cn.imustacm.heartbeat.base.BaseActivity;
import cn.imustacm.heartbeat.base.BaseApplication;
import cn.imustacm.heartbeat.entity.User;
import cn.imustacm.heartbeat.fragment.ChatFragment;
import cn.imustacm.heartbeat.fragment.HomePageFragment;
import cn.imustacm.heartbeat.manager.ActivityManager;
import cn.imustacm.heartbeat.model.TabNavigation;
import cn.imustacm.heartbeat.ui.AboutActivity;
import cn.imustacm.heartbeat.ui.LoginActivity;
import cn.imustacm.heartbeat.ui.PersonalActivity;
import cn.imustacm.heartbeat.ui.ReleaseActivity;
import cn.imustacm.heartbeat.ui.TimeLineActivity;
import cn.imustacm.heartbeat.utils.LogUtils;
import cn.imustacm.heartbeat.utils.SharedUtils;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 项目名：HeartBeat
 * 包名：cn.imustacm.heartbeat.main
 * 文件名：MainActivity
 * 描述：基本页面逻辑
 */

public class MainActivity extends BaseActivity {

    // 头像
    private CircleImageView civHead;
    // 昵称
    private TextView tvName;
    // 左侧菜单
    private ImageView ivLeftMenu;
    // 抽屉布局
    private DrawerLayout drawerLayout;
    // Bar的标题View
    private TextView tvBarTitle;
    // Context
    private Context mContext;
    // TabLayout
    private TabLayout mTabLayout;
    // ViewPager
    private NoScrollViewPager mViewPager;
    // Fragment
    private List<Fragment> mFragment;
    // 导航栏(Navigation Bar)
    private List<TabNavigation> mTabNavi;
    // ImageView
    private ImageView mImageView;
    // CheckedTextView
    private List<CheckedTextView> mCheckedTextView;
    // Adapter
    private TabLayoutAdapter mAdapter;
    // 1. 导航栏标题
    private List<String> mNaviTitle;
    // 2. 导航栏图片资源
    private List<Integer> mNaviImageNormal;
    private List<Integer> mNaviImageClicked;
    // NavigationView
    private NavigationView mNavigationView;

    // 退出登录按钮
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 初始化
        initialize();
        // 设置适配器
        mViewPager.setAdapter(mAdapter);
        // 绑定
        mTabLayout.setupWithViewPager(mViewPager);
        // 自定义布局
        for (int i = 0; i < mTabNavi.size(); i++) {

            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            tab.setCustomView(R.layout.tab_navi);

            CheckedTextView checkedTextView = (CheckedTextView) tab.getCustomView().findViewById(R.id.ctv_tab);

            setNaviTitleAndImage(checkedTextView, mTabNavi.get(i).getTabTitle(), mTabNavi.get(i).getTabImageNormal());

            if (i == 0) {
                checkedTextView.setPadding(0, 0, 50, 0);
            } else if (i == 1) {
                checkedTextView.setPadding(50, 0, 0, 0);
            }

            mCheckedTextView.add(checkedTextView);
        }
        // mTabLayout选择监听器
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            // 选中了Tab
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    tvBarTitle.setText("主页");
                } else if (position == 1) {
                    tvBarTitle.setText("对话");
                }
                CheckedTextView checkedTextView = mCheckedTextView.get(position);
                setNaviTitleAndImage(checkedTextView, mTabNavi.get(position).getTabTitle(), mTabNavi.get(position).getTabImageClicked());
            }
            // 未选中Tab
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                CheckedTextView checkedTextView = mCheckedTextView.get(position);
                setNaviTitleAndImage(checkedTextView, mTabNavi.get(position).getTabTitle(), mTabNavi.get(position).getTabImageNormal());
            }
            // 重复选中Tab
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        setNaviTitleAndImage(mCheckedTextView.get(0), mTabNavi.get(0).getTabTitle(), mTabNavi.get(0).getTabImageClicked());
        tvBarTitle.setText(mTabNavi.get(0).getTabTitle());
    }

    @Override
    protected void onResume() {
        super.onResume();
        setHeadAndName();
    }

    private void setHeadAndName() {
        // 获取当前用户
        User user = User.getCurrentUser(User.class);
        // 设置头像
        civHead.setImageResource(user.getHead());
        // 设置昵称
        tvName.setText(user.getName());
    }

    // 设置导航栏图片和文字
    private void setNaviTitleAndImage(CheckedTextView checkedTextView, String tabTitle, int tabImage) {
        Drawable drawable = ContextCompat.getDrawable(mContext, tabImage);
        drawable.setBounds(0, 0, 50, 50);
        checkedTextView.setCompoundDrawables(null, drawable, null, null);
        checkedTextView.setText(tabTitle);
    }

    // 初始化
    private void initialize() {
        // Bar的标题View
        tvBarTitle = findViewById(R.id.tv_bar_title);
        // Context
        mContext = MainActivity.this;
        // TabLayout
        mTabLayout = (TabLayout) findViewById(R.id.tl_tab);
        // ViewPager
        mViewPager = (NoScrollViewPager) findViewById(R.id.vp_tab);
        // ImageView
        mImageView = (ImageView) findViewById(R.id.iv_release);
        // Listener
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, ReleaseActivity.class));
            }
        });
        // 抽屉Layout
        drawerLayout = findViewById(R.id.drawer_layout);
        // 左侧图片
        ivLeftMenu = findViewById(R.id.iv_left_menu);
        ivLeftMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        // ArrayList
        // Fragment
        mFragment = new ArrayList<>();
        mFragment.add(new HomePageFragment());
        mFragment.add(new ChatFragment());

        // NaviTitle
        mNaviTitle = new ArrayList<>();
        mNaviTitle.add("主页");
        mNaviTitle.add("对话");

        // NaviImageNormal
        mNaviImageNormal = new ArrayList<>();
        mNaviImageNormal.add(R.drawable.icon_homepage_no_click);
        mNaviImageNormal.add(R.drawable.icon_dialog_no_click);

        // NaviImageClicked
        mNaviImageClicked = new ArrayList<>();
        mNaviImageClicked.add(R.drawable.icon_homepage_click);
        mNaviImageClicked.add(R.drawable.icon_dialog_click);

        // TabNavi
        mTabNavi = new ArrayList<>();
        mTabNavi.add(new TabNavigation(mNaviTitle.get(0), mNaviImageNormal.get(0), mNaviImageClicked.get(0)));
        mTabNavi.add(new TabNavigation(mNaviTitle.get(1), mNaviImageNormal.get(1), mNaviImageClicked.get(1)));

        // CheckedTextView
        mCheckedTextView = new ArrayList<>();

        // Adapter
        mAdapter = new TabLayoutAdapter(getSupportFragmentManager(), mTabNavi, mFragment);

        // NavigationView
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        // 用户头像
        civHead = mNavigationView.getHeaderView(0).findViewById(R.id.civ_head);
        // 用户昵称
        tvName = mNavigationView.getHeaderView(0).findViewById(R.id.tv_name);
        // 侧滑菜单头部点击事件
        mNavigationView.getHeaderView(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PersonalActivity.class));
            }
        });
        // 侧滑菜单点击事件
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    // 个人信息
                    case R.id.nav_personal:
                        startActivity(new Intent(mContext, PersonalActivity.class));
                        break;
                    // 时间轴
                    case R.id.nav_timeline:
                        startActivity(new Intent(mContext, TimeLineActivity.class));
                        break;
                    // 设置
                    case R.id.nav_clear:
                        Toast.makeText(mContext, "已经清空本地缓存的数据", Toast.LENGTH_LONG).show();
                        SharedUtils.deleteAllKey(mContext);
                        break;
                    // 关于
                    case R.id.nav_about:
                        startActivity(new Intent(mContext, AboutActivity.class));
                        break;
                }
                return false;
            }
        });
        // 退出登录按钮
        btnLogout = (Button) findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User.logOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                ActivityManager.finishAllActivity();
            }
        });


    }

}
