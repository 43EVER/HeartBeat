package cn.imustacm.heartbeat.ui;

import android.app.Activity;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.imustacm.heartbeat.R;
import cn.imustacm.heartbeat.base.BaseActivity;
import cn.imustacm.heartbeat.entity.User;
import cn.imustacm.heartbeat.utils.LogUtils;
import cn.imustacm.heartbeat.utils.RandomUtils;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 项目名：HeartBeat
 * 包名：cn.imustacm.heartbeat.ui
 * 文件名：PersonalActivity
 * 描述：个人信息页面
 */

public class PersonalActivity extends BaseActivity implements View.OnClickListener {

    // 返回
    private ImageView ivBack;
    // 修改
    private TextView tvModify;
    // 头像
    private CircleImageView civHead;
    // 昵称
    private TextView tvName;
    // 个性签名
    private TextView tvDesc;
    // 性格
    private TextView tvCharacter;
    // 爱好
    private TextView tvHobby;
    // 最近心情
    private TextView tvMood;
    // 最近心率
    private TextView tvRate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        // 初始化
        initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 设置内容
        setPersonalContent();
    }

    private void initialize() {
        // 返回
        ivBack = findViewById(R.id.iv_back);
        // 修改
        tvModify = findViewById(R.id.tv_modify);
        // 头像
        civHead = findViewById(R.id.civ_head);
        // 昵称
        tvName = findViewById(R.id.tv_name);
        // 个性签名
        tvDesc = findViewById(R.id.tv_desc);
        // 性格
        tvCharacter = findViewById(R.id.tv_character);
        // 爱好
        tvHobby = findViewById(R.id.tv_hobby);
        // 最近心情
        tvMood = findViewById(R.id.tv_mood);
        // 最近心率
        tvRate = findViewById(R.id.tv_rate);
        // 添加监听器
        civHead.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        tvModify.setOnClickListener(this);
    }


    private void setPersonalContent() {
        // 获取当前用户
        User user = User.getCurrentUser(User.class);
        // 设置头像
        civHead.setImageResource(user.getHead());
        // 设置昵称
        tvName.setText(user.getName());
        // 设置个性签名
        tvDesc.setText(user.getDesc());
        // 设置性格
        tvCharacter.setText(user.getCharacter());
        // 设置爱好
        tvHobby.setText(user.getHobby());
        // 设置最近心情
        tvMood.setText(user.getMood());
        // 设置最近心率
        tvRate.setText(user.getRate());
    }

    private static final int HEAD_CHANGE_CODE = 0x21;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.civ_head:
                startActivityForResult(new Intent(PersonalActivity.this, HeadSelectActivity.class), HEAD_CHANGE_CODE);
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_modify:
                startActivity(new Intent(PersonalActivity.this, ModifyActivity.class));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case HEAD_CHANGE_CODE:
                    // 获取用户选择的头像图片
                    Bundle bundle = data.getExtras();
                    if (bundle == null) {
                        LogUtils.e("PersonalActivity...onActivityResult(int, int, Intent):" + "bundle is null.");
                        return;
                    }
                    final Integer postion = bundle.getInt("HeadImage");
                    // 更新用户数据
                    User user = User.getCurrentUser(User.class);
                    user.setHead(RandomUtils.headImages[postion]);
                    user.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                // 头像更新成功
                                Toast.makeText(PersonalActivity.this, "头像更新成功", Toast.LENGTH_LONG).show();
                                // 设置新头像
                                civHead.setImageResource(RandomUtils.headImages[postion]);
                            } else {
                                // 头像更新失败
                                Toast.makeText(PersonalActivity.this, "头像更新失败", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    break;
            }
        }
    }
}
