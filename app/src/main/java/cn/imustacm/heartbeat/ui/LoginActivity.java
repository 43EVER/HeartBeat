package cn.imustacm.heartbeat.ui;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.imustacm.heartbeat.R;
import cn.imustacm.heartbeat.base.BaseActivity;
import cn.imustacm.heartbeat.entity.User;
import cn.imustacm.heartbeat.main.MainActivity;
import cn.imustacm.heartbeat.utils.LogUtils;
import cn.imustacm.heartbeat.utils.RandomUtils;

/**
 * 项目名：HeartBeat
 * 包名：cn.imustacm.heartbeat.ui
 * 文件名：LoginActivity
 * 描述：登录页面
 */

public class LoginActivity extends BaseActivity implements TextWatcher, View.OnClickListener{

    // Context
    private Context mContext;
    // 登录账号输入框
    private EditText etAccount;
    // 登录密码输入框
    private EditText etPassword;
    // 登录按钮
    private Button btnLogin;
    // 注册按钮
    private FloatingActionButton btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 检查本地是否有缓存用户，如果有则不需要登录
        if (User.getCurrentUser(User.class) != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        // 初始化
        initialize();
        // 为了方便，记得删除
        // btnLogin.setEnabled(true);
    }

    // 初始化
    private void initialize() {
        // Content
        mContext = LoginActivity.this;
        // 登录账号输入框
        etAccount = (EditText) findViewById(R.id.et_account);
        // 登录密码输入框
        etPassword = (EditText) findViewById(R.id.et_password);
        // 登录按钮
        btnLogin = (Button) findViewById(R.id.btn_login);
        // 注册按钮
        btnRegister = (FloatingActionButton) findViewById(R.id.btn_register);
        // 添加监听事件
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        etAccount.addTextChangedListener(this);
        etPassword.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (TextUtils.isEmpty(etAccount.getText().toString().trim()) || TextUtils.isEmpty(etPassword.getText().toString().trim())) {
            setButtonState(false);
        } else {
            setButtonState(true);
        }
    }

    private void setButtonState(boolean state) {
        GradientDrawable gradientDrawable = (GradientDrawable)btnLogin.getBackground();
        if (state) {
            btnLogin.setEnabled(true);
            btnLogin.setTextColor(Color.parseColor("#ffffff"));
            gradientDrawable.setColor(Color.parseColor("#00ccff"));
        } else {
            btnLogin.setEnabled(false);
            gradientDrawable.setColor(Color.parseColor("#ffffff"));
            btnLogin.setTextColor(Color.parseColor("#d1d1d1"));
        }
    }

    @Override
    public void onClick(View v) {
        final Intent intent;
        final Bundle bundle;
        switch (v.getId()) {
            // 登录按钮
            case R.id.btn_login:
                // Intent
                intent = new Intent(mContext, MainActivity.class);
                // 用户名
                String account = etAccount.getText().toString();
                // 用户密码
                String password = etPassword.getText().toString();
                // 处理用户登录逻辑
                final User user = new User();
                // 设置用户名
                user.setUsername(account);
                // 设置密码
                user.setPassword(password);
                user.login(new SaveListener<User>() {
                    @Override
                    public void done(User o, BmobException e) {
                        if (e == null) {
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_LONG).show();
                            LogUtils.e("用户登录时出现错误，错误代码为" + e.toString());
                        }
                    }
                });
                break;
            // 注册按钮
            case R.id.btn_register:
                // 设置退出动画为空
                getWindow().setExitTransition(null);
                // 设置进入动画为空
                getWindow().setEnterTransition(null);
                // Intent
                intent = new Intent(mContext, RegisterActivity.class);
                // Bundle
                bundle = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, btnRegister, btnRegister.getTransitionName()).toBundle();
                startActivity(intent, bundle);
                break;
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onRestart() {
        super.onRestart();
        btnRegister.setVisibility(View.GONE);
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onResume() {
        super.onResume();
        btnRegister.setVisibility(View.VISIBLE);
    }

}
