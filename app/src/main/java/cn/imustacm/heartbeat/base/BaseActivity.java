package cn.imustacm.heartbeat.base;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import cn.imustacm.heartbeat.manager.ActivityManager;


/**
 * 项目名：HeartBeat
 * 包名：cn.imustacm.heartbeat.base
 * 文件名：BaseActivity
 * 描述：Activity基类
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.removeActivity(this);
    }

    // 权限检查
    public boolean hasPermission(String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    // 权限请求
    public void requestPermission(int code, String... Permissions) {
        ActivityCompat.requestPermissions(this, Permissions, code);
    }

}
