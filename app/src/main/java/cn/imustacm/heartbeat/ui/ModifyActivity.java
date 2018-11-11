package cn.imustacm.heartbeat.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.imustacm.heartbeat.R;
import cn.imustacm.heartbeat.base.BaseActivity;
import cn.imustacm.heartbeat.entity.User;

public class ModifyActivity extends BaseActivity implements View.OnClickListener, TextView.OnEditorActionListener {

    // 返回
    private ImageView ivBack;
    // 完成
    private TextView tvComplete;
    // 用户名
    private EditText etName;
    // 签名
    private EditText etDesc;
    // 性格
    private EditText etCharacter;
    // 爱好
    private EditText etHobby;
    // 最近心情
    private EditText etMood;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        // 初始化
        initialize();
        // 设置编辑框内容
        setEditContent();
    }

    private void initialize() {
        // 返回
        ivBack = findViewById(R.id.iv_back);
        // 完成
        tvComplete = findViewById(R.id.tv_complete);
        // 用户名
        etName = findViewById(R.id.et_name);
        // 签名
        etDesc = findViewById(R.id.et_desc);
        // 性格
        etCharacter = findViewById(R.id.et_character);
        // 爱好
        etHobby = findViewById(R.id.et_hobby);
        // 最近心情
        etMood = findViewById(R.id.et_mood);
        // 添加监听器
        ivBack.setOnClickListener(this);
        tvComplete.setOnClickListener(this);
        etName.setOnEditorActionListener(this);
        etDesc.setOnEditorActionListener(this);
        etCharacter.setOnEditorActionListener(this);
        etHobby.setOnEditorActionListener(this);
        etMood.setOnEditorActionListener(this);
    }

    private void setEditContent() {
        // 获取当前用户
        User user = User.getCurrentUser(User.class);
        // 用户名编辑框
        etName.setText(user.getName());
        etName.setHint(user.getName());
        // 签名编辑框
        etDesc.setText(user.getDesc());
        etDesc.setHint(user.getDesc());
        // 性格编辑框
        etCharacter.setText(user.getCharacter());
        etCharacter.setHint(user.getCharacter());
        // 爱好编辑框
        etHobby.setText(user.getHobby());
        etHobby.setHint(user.getHobby());
        // 最近心情编辑框
        etMood.setText(user.getMood());
        etMood.setHint(user.getMood());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_complete:
                // 用户名
                String name = etName.getText().toString();
                // 签名
                String desc = etDesc.getText().toString();
                // 性格
                String character = etCharacter.getText().toString();
                // 爱好
                String hobby = etHobby.getText().toString();
                // 最近心情
                String mood = etMood.getText().toString();
                // 检测用户名是否为空
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(ModifyActivity.this, "用户名不能为空", Toast.LENGTH_LONG).show();
                    return;
                }
                // 检测签名是否为空
                if (TextUtils.isEmpty(desc)) {
                    desc = "这个人很懒，什么也没有留下...";
                }
                // 获取当前用户
                User user = User.getCurrentUser(User.class);
                // 重新设置用户属性
                // 设置用户名
                user.setName(name);
                // 设置签名
                user.setDesc(desc);
                // 设置性格
                user.setCharacter(character);
                // 设置爱好
                user.setHobby(hobby);
                // 设置最近心情
                user.setMood(mood);
                // 更新用户数据
                user.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            // 用户数据更新成功
                            Toast.makeText(ModifyActivity.this, "个人信息数据更新成功", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            // 用户数据更新失败
                            Toast.makeText(ModifyActivity.this, "个人信息数据更新失败", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        return event.getKeyCode() == KeyEvent.KEYCODE_ENTER;
    }
}
