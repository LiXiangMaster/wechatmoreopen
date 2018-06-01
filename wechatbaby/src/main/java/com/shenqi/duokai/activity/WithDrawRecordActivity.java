package com.shenqi.duokai.activity;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import com.google.gson.Gson;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.shenqi.duokai.R;
import com.shenqi.duokai.adapter.WithDrawRecordAdapter;
import com.shenqi.duokai.bean.DepositListCategory;
import com.shenqi.duokai.constants.Contant;
import com.shenqi.duokai.constants.HttpConstant;
import com.shenqi.duokai.utils.MD5Utils;
import com.shenqi.duokai.utils.SPUtils;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lixiang on 2016/9/14.
 */
public class WithDrawRecordActivity extends CommonActivity implements View.OnClickListener {
    private ListView mLv_wr;
    private ImageView mIv_commonback;
    private OkHttpClient mClient;
    private Call mCall;

    private SystemBarTintManager mTintManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_record);
        //初始化网络请求;
        mClient = new OkHttpClient();
        initView();
        initData();
        //透明状态栏;
        initStatusBar();
    }

    @TargetApi(19)
    private void initStatusBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            mTintManager = new SystemBarTintManager(this);
            mTintManager.setStatusBarTintColor(getResources().getColor(R.color.app_main_color));
            mTintManager.setStatusBarTintEnabled(true);
        }
    }

    private void initData() {
        initHeader("提现记录");
        mIv_commonback.setOnClickListener(this);

        //对imei进行MD5处理: sign   加密参数 加密固定值：duo3kai7shen5qi    md5(imei."duo3kai7shen5qi");
        int qqLoginUserId = SPUtils.getInt(WithDrawRecordActivity.this, Contant.DUOKAI_LOGIN_USER_ID);
        String sign = MD5Utils.encode(qqLoginUserId + "duo3kai7shen5qi");
        // 设置请求url和参数
       // http://api.duokaishenqi.com/user/?action=depositlist&uid=xxx&page=xxx&sign=xxx
        Uri uri = Uri.parse(HttpConstant.USER).buildUpon()
                .appendQueryParameter("action", "depositlist")
                .appendQueryParameter("uid", "" + qqLoginUserId)
                .appendQueryParameter("page", ""+ 1)
                .appendQueryParameter("sign", sign)
                .build();
        Request request = new Request.Builder()
                .get()
                .url(uri.toString())
                .build();

        mCall = mClient.newCall(request);
        mCall.enqueue(mCallback);
    }

    Callback mCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            //Log.i("test", e.getMessage());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String json = response.body().string();
            //Log.i("test", "response:  " + json);
            Gson gson = new Gson();
            DepositListCategory depositListCategory = gson.fromJson(json, DepositListCategory.class);
            final List<List<String>> data = depositListCategory.getData();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    WithDrawRecordAdapter adapter = new WithDrawRecordAdapter(WithDrawRecordActivity.this, data);
                    mLv_wr.setAdapter(adapter);
                    mLv_wr.setEmptyView(findViewById(R.id.iv_empty_nodata));
                }
            });
        }
    };

    private void initView() {
        mLv_wr = ((ListView) findViewById(R.id.lv_withdraw_record));
        mIv_commonback = ((ImageView) findViewById(R.id.iv_common_back));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCall != null) {
            mCall.cancel();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_common_back:
                finish();
                break;
        }
    }
}
