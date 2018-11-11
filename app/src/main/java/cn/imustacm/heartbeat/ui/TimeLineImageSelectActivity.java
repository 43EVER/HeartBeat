package cn.imustacm.heartbeat.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import cn.imustacm.heartbeat.R;
import cn.imustacm.heartbeat.adapter.HeadImageAdapter;
import cn.imustacm.heartbeat.adapter.TimeLineImageAdapter;
import cn.imustacm.heartbeat.base.BaseActivity;

public class TimeLineImageSelectActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_image_select);

        GridView gridView = findViewById(R.id.grid_view);
        gridView.setAdapter(new TimeLineImageAdapter(this));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("TimeLineImage", position);
                Toast.makeText(TimeLineImageSelectActivity.this, "点击" + position, Toast.LENGTH_LONG).show();
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
