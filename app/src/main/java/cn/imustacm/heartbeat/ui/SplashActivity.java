package cn.imustacm.heartbeat.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.security.Permission;

import cn.imustacm.heartbeat.R;
import cn.imustacm.heartbeat.base.BaseActivity;
import cn.imustacm.heartbeat.manager.ActivityManager;
import cn.imustacm.heartbeat.utils.SharedUtils;

/**
 * 项目名：HeartBeat
 * 包名：cn.imustacm.heartbeat.ui
 * 文件名：SplashActivity
 * 描述：闪屏页面
 */

public class SplashActivity extends BaseActivity {

    private final static int SPLASH_MSG_CODE = 1000;
    private final static int SPLASH_TIME = 3000;
    private final static int SPLASH_TIME_FIRST = 30000;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SPLASH_MSG_CODE:
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                    break;
            }
        }
    };

    public static final int CAMERA_CODE = 0x01;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 动态申请权限
        if (!hasPermission(Manifest.permission.CAMERA)) {
            requestPermission(CAMERA_CODE, Manifest.permission.CAMERA);
        } else {
            SharedUtils.putInt(this, "Camera", 1);
        }
        int state = SharedUtils.getInt(this, "Camera", 0);
        if (state == 0) {
            handler.sendEmptyMessageDelayed(SPLASH_MSG_CODE, SPLASH_TIME_FIRST);
        } else {
            handler.sendEmptyMessageDelayed(SPLASH_MSG_CODE, SPLASH_TIME);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SharedUtils.putInt(this, "Camera", 1);
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                } else {
                    Toast.makeText(this, "未成功授权，软件关闭", Toast.LENGTH_LONG).show();
                    ActivityManager.finishAllActivity();
                    finish();
                }
                break;
        }
    }
}
