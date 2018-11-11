package cn.imustacm.heartbeat.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.Toolbar;

import cn.imustacm.heartbeat.R;
import cn.imustacm.heartbeat.adapter.HeadImageAdapter;
import cn.imustacm.heartbeat.base.BaseActivity;

public class HeadSelectActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head_select);

        GridView gridView = findViewById(R.id.grid_view);
        gridView.setAdapter(new HeadImageAdapter(this));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("HeadImage", position);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
