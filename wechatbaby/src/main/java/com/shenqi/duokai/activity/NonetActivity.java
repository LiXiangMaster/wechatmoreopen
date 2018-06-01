package com.shenqi.duokai.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.shenqi.duokai.MainActivity;
import com.shenqi.duokai.R;
import com.shenqi.duokai.utils.Utils;

/**
 * Created by lixiang on 2016/9/29.
 */
public class NonetActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nonet);
        TextView tv_nonet = (TextView) findViewById(R.id.tv_agin_show);
        tv_nonet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.checkNetwork(NonetActivity.this)){
                    startActivity(new Intent(NonetActivity.this,MainActivity.class));
                    finish();
                }else {
                    return;
                }

            }
        });
    }
}
