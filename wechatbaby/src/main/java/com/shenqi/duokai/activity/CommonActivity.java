package com.shenqi.duokai.activity;

import android.app.Activity;
import android.widget.TextView;

import com.shenqi.duokai.R;


/**
 * Created by lixiang on 2016/9/10.
 */
public class CommonActivity extends Activity {
    protected void initHeader(String text) {
        TextView tvTitle = (TextView) findViewById(R.id.tv_common_title);
        tvTitle.setText(text);
    }
}
