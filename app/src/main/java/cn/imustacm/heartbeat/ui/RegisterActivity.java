package cn.imustacm.heartbeat.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Random;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.imustacm.heartbeat.R;
import cn.imustacm.heartbeat.base.BaseActivity;
import cn.imustacm.heartbeat.entity.User;
import cn.imustacm.heartbeat.utils.CheckUtils;
import cn.imustacm.heartbeat.utils.LogUtils;
import cn.imustacm.heartbeat.utils.RandomUtils;
import cn.imustacm.heartbeat.utils.StaticUtils;

/**
 * 项目名：HeartBeat
 * 包名：cn.imustacm.heartbeat.ui
 * 文件名：RegisterActivity
 * 描述：注册页面
 */

public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    // Context
    private Context mContext;
    // 手机号码编辑框
    private EditText etPhone;
    // 验证码编辑框
    private EditText etCode;
    // 密码编辑框
    private EditText etPassword;
    // 获取验证码按钮
    private Button btnGetCode;
    // 注册按钮
    private Button btnRegister;
    // 返回按钮
    private FloatingActionButton btnBack;
    // 注册CardView
    private CardView cvRegister;

    // 手机号码
    private String phone;
    // 验证码
    private String code;
    // 密码
    private String password;

    // 验证码倒计时
    private final static int TIME_COUNT_MSG_CODE = 0x01;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TIME_COUNT_MSG_CODE:
                    if (msg.arg1 == 0) {
                        btnGetCode.setText("重新获取");
                        btnGetCode.setTextColor(Color.parseColor("#2FA881"));
                        btnGetCode.setClickable(true);
                    } else {
                        btnGetCode.setText("（" + msg.arg1 + "）");
                        btnGetCode.setTextColor(Color.parseColor("#AAAAAA"));
                        btnGetCode.setClickable(false);
                    }
                    break;
            }
        }
    };

    private void sendMessageClick(View view, final int timeCount) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = timeCount - 1; i >= 0; i--) {
                    Message msg = handler.obtainMessage();
                    msg.what = TIME_COUNT_MSG_CODE;
                    msg.arg1 = i;
                    handler.sendMessage(msg);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ShowEnterAnimation();
        initialize();
    }

    // 初始化
    private void initialize() {
        // Context
        mContext = RegisterActivity.this;
        // 手机号码编辑框
        etPhone = (EditText) findViewById(R.id.et_phone);
        // 验证码编辑框
        etCode = (EditText) findViewById(R.id.et_code);
        // 密码编辑框
        etPassword = (EditText) findViewById(R.id.et_password);
        // 获取验证码按钮
        btnGetCode = (Button) findViewById(R.id.btn_get_code);
        // 注册按钮
        btnRegister = (Button) findViewById(R.id.btn_register);
        // 返回按钮
        btnBack = (FloatingActionButton) findViewById(R.id.btn_back);
        // 注册CardView
        cvRegister = (CardView) findViewById(R.id.cv_register);
        // 添加监听器
        btnGetCode.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 获取验证码按钮
            case R.id.btn_get_code:
                phone = etPhone.getText().toString().trim();
                // TODO: 2018/9/29 判断手机号是否合法
                // 判断手机号码是否为空
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(mContext, "手机号码不能为空", Toast.LENGTH_LONG).show();
                    return;
                }
                // 判断手机号码位数是否合法
                if (phone.length() != StaticUtils.LEGAL_PHONE_LENGTH) {
                    Toast.makeText(mContext, "不是一个有效的手机号码", Toast.LENGTH_LONG).show();
                    return;
                }
                // TODO: 2018/9/29 进行验证码的发送
                BmobSMS.requestSMSCode(phone, "PIN", new QueryListener<Integer>() {
                    @Override
                    public void done(Integer integer, BmobException e) {
                        if (e == null) {
                            Toast.makeText(mContext, "验证码发送成功", Toast.LENGTH_LONG).show();
                            sendMessageClick(btnGetCode, 60);
                        } else {
                            Toast.makeText(mContext, "验证码发送失败:" + e.getErrorCode(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                break;
            // 注册按钮
            case R.id.btn_register:
                phone = etPhone.getText().toString().trim();
                code = etCode.getText().toString().trim();
                password = etPassword.getText().toString();
                // TODO: 2018/9/29 判断上面三个字符串的合法性
                // 判断手机号码
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(mContext, "手机号码不能为空", Toast.LENGTH_LONG).show();
                    return;
                }
                // 判断手机号码位数是否合法
                if (phone.length() != StaticUtils.LEGAL_PHONE_LENGTH) {
                    Toast.makeText(mContext, "不是一个有效的手机号码", Toast.LENGTH_LONG).show();
                    return;
                }
                // 判断验证码是否为空
                if (TextUtils.isEmpty(code)) {
                    Toast.makeText(mContext, "验证码不能为空", Toast.LENGTH_LONG).show();
                    return;
                }
                // 判断密码是否为空
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(mContext, "密码不能为空", Toast.LENGTH_LONG).show();
                    return;
                }
                // 判断密码合法性
                Integer returnCode = CheckUtils.judgePassword(password);
                // 密码长度过短状态码
                if (returnCode == CheckUtils.PASSWORD_STATUS_SHORT) {
                    Toast.makeText(mContext, "密码长度过短，长度要求限制在8-15位之间", Toast.LENGTH_LONG).show();
                    return;
                }
                // 密码长度过长状态码
                if (returnCode == CheckUtils.PASSWORD_STATUS_LONG) {
                    Toast.makeText(mContext, "密码长度过长，长度要求限制在8-15位之间", Toast.LENGTH_LONG).show();
                    return;
                }
                // 密码中含有非法字符状态码
                if (returnCode == CheckUtils.PASSWORD_STATUS_NOLEGAL) {
                    Toast.makeText(mContext, "密码中含有非法字符，密码只能由数字和字母组成", Toast.LENGTH_LONG).show();
                    return;
                }
                // 密码过于简单状态码
                if (returnCode == CheckUtils.PASSWORD_STATUS_SIMPLE) {
                    Toast.makeText(mContext, "密码过于简单，密码必须包含数字和字母", Toast.LENGTH_LONG).show();
                    return;
                }
                // 密码合法状态码
                if (returnCode == CheckUtils.PASSWORD_STATUS_LEGAL) {

                }
                // TODO: 2018/9/29 检验验证码
                // 判断验证码是否正确
                BmobSMS.verifySmsCode(phone, code, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            // 验证码正确
                            // TODO: 2018/9/29 进行注册
                            User user = new User();
                            // 设置昵称
                            user.setName(RandomUtils.getRandomName());
                            // 设置用户名
                            user.setUsername(phone);
                            // 设置密码
                            user.setPassword(password);
                            // 设置手机号码
                            user.setMobilePhoneNumber(phone);
                            // 设置手机号码验证状态
                            user.setMobilePhoneNumberVerified(true);

                            user.signUp(new SaveListener<Object>() {
                                @Override
                                public void done(Object o, BmobException e) {
                                    if (e == null) {
                                        // 注册成功
                                        Toast.makeText(mContext, "注册成功", Toast.LENGTH_LONG).show();
                                        finish();
                                    } else {
                                        // 注册失败
                                        Toast.makeText(mContext, "注册失败:" + e.getErrorCode(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            // 验证码错误
                            Toast.makeText(mContext, "验证码错误:" + e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                break;
            // 返回按钮
            case R.id.btn_back:
                // 返回到注册页
                animateRevealClose();
                break;
        }
    }

    private void ShowEnterAnimation() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fabtransition);
        getWindow().setSharedElementEnterTransition(transition);

        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                cvRegister.setVisibility(View.GONE);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }


        });
    }

    public void animateRevealShow() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvRegister, cvRegister.getWidth() / 2, 0, btnBack.getWidth() / 2, cvRegister.getHeight());
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                cvRegister.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    public void animateRevealClose() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvRegister, cvRegister.getWidth() / 2, 0, cvRegister.getHeight(), btnBack.getWidth() / 2);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cvRegister.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
                btnBack.setImageResource(R.drawable.plus);
                RegisterActivity.super.onBackPressed();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

}
