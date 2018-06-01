package com.shenqi.duokai.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.shenqi.duokai.MainActivity;
import com.shenqi.duokai.R;
import com.shenqi.duokai.bean.WithDrawCategory;
import com.shenqi.duokai.bean.WithDrawGetDataCategory;
import com.shenqi.duokai.constants.Contant;
import com.shenqi.duokai.constants.HttpConstant;
import com.shenqi.duokai.ui.MyDialog;
import com.shenqi.duokai.utils.MD5Utils;
import com.shenqi.duokai.utils.SPUtils;
import com.shenqi.duokai.utils.Utils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lixiang on 2016/9/14.
 */
public class WithDrawActivity extends CommonActivity implements View.OnClickListener {

    private ImageView mIv_back;
    private TextView mTv_WithDraw_Record;
    private EditText mEt_name;
    private EditText mEt_Num;
    private EditText mEd_Money;
    private TextView mTv_WithDraw;
    private OkHttpClient mOkHttpClient;
    private Call mWithDrawCall;
    private Call mUserDateCall;
    private TextView mTv_accout_last;
    private String mAccoutlast;

    private SystemBarTintManager mTintManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        //初始化 网络请求;
        mOkHttpClient = new OkHttpClient();
        initView();
        initData();
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
        initHeader("提现");
        mIv_back.setOnClickListener(this);
        mTv_WithDraw_Record.setOnClickListener(this);
        mTv_WithDraw.setOnClickListener(this);
        Intent intent = getIntent();
        mAccoutlast = intent.getStringExtra("accoutlast");
        //Log.i("test", "收到多少钱了: " + mAccoutlast);
        mTv_accout_last.setText(mAccoutlast);
        //获取用户资料; 如果有 就不需要填了;
        getUserData();
    }

    //获取用户资料;
    private void getUserData() {

        //从首选项获取用户id;
        int qqLoginUserId = SPUtils.getInt(WithDrawActivity.this, Contant.DUOKAI_LOGIN_USER_ID);
        String sign = MD5Utils.encode(qqLoginUserId + "duo3kai7shen5qi");
        //Log.i("test", "##############################" + sign + "    userid:   " + qqLoginUserId);
        // 设置请求url和参数
        Uri uri = Uri.parse(HttpConstant.USER).buildUpon()
                .appendQueryParameter("action", "userdepoist")
                .appendQueryParameter("uid", qqLoginUserId + "")
                .appendQueryParameter("sign", sign)
                .build();
        Request request = new Request.Builder()
                .get()
                .url(uri.toString())
                .build();
        mUserDateCall = mOkHttpClient.newCall(request);
        mUserDateCall.enqueue(userDateCallback);
    }

    Callback userDateCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            //Log.i("test", "没有请求到数据:  " + e.getMessage());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String json = response.body().string();
            Gson gson = new Gson();
            WithDrawGetDataCategory withDrawGetDataCategory = gson.fromJson(json, WithDrawGetDataCategory.class);
            final String zfbcode = withDrawGetDataCategory.getZfbcode();
            final String zfbname = withDrawGetDataCategory.getZfbname();
            if (!TextUtils.isEmpty(zfbcode) || !TextUtils.isEmpty(zfbname)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mEt_name.setText(zfbname);
                        mEt_Num.setText(zfbcode);
                        mEt_name.setFocusableInTouchMode(false);
                        mEt_Num.setFocusableInTouchMode(false);
                        mEt_Num.setFocusable(false);
                        mEt_name.setFocusable(false);
                    }
                });


            }
        }
    };

    private void initView() {
        mIv_back = ((ImageView) findViewById(R.id.iv_common_back));
        mTv_WithDraw_Record = ((TextView) findViewById(R.id.tv_withdraw_record));
        mEt_name = ((EditText) findViewById(R.id.et_name));
        mEt_Num = ((EditText) findViewById(R.id.et_input_num));
        mEd_Money = ((EditText) findViewById(R.id.et_input_money));
        mTv_WithDraw = ((TextView) findViewById(R.id.tv_withdraw_to_zhifubao));
        mTv_accout_last = ((TextView) findViewById(R.id.tv_accout_money));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWithDrawCall!=null){
            mWithDrawCall.cancel();
        }
        if (mWithDrawCall!=null){
            mWithDrawCall.cancel();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_common_back:
                finish();
                break;
            case R.id.tv_withdraw_record:
                //跳转到提现记录activity
                startActivity(new Intent(WithDrawActivity.this, WithDrawRecordActivity.class));
                break;
            case R.id.tv_withdraw_to_zhifubao:

                //判断 是否为空
                String name = mEt_name.getText().toString().trim();
                String num = mEt_Num.getText().toString().trim();
                String money = mEd_Money.getText().toString().trim();
//               String.valueOf(Math.floor(Double.parseDouble(money)));
                double withdraw_money = Double.parseDouble(mAccoutlast);

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(num) || TextUtils.isEmpty(money)) {
                    Utils.toast(WithDrawActivity.this, "账号,姓名或金额输入不能为空");
                    return;
                }
                if(withdraw_money<30.0){
                    showNoWithDrawDialog();
                    return;
                }


                //调用后台接口 ;
                requestNetToData(name, num, money);
                break;

        }
    }
    //不能提现对话框;
    private void showNoWithDrawDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.show_nowithdraw_dialog, null);
        final MyDialog builder = new MyDialog(WithDrawActivity.this, 0, 0, dialogView, R.style.dialog);
        TextView tv_yes_iknow = (TextView) dialogView.findViewById(R.id.yes_iknow);
        ImageView iv_close_yes = (ImageView) dialogView.findViewById(R.id.iv_close_yes);
        tv_yes_iknow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        iv_close_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        builder.show();
    }

    private void requestNetToData(String name, String num, String money) {
        //http://api.duokaishenqi.com/user/?action=deposit&uid=xxx&umoney=xxx&sign=xxx&rname=xxx&zcode=xxx
        //从首选项获取用户id;
        int qqLoginUserId = SPUtils.getInt(WithDrawActivity.this, Contant.DUOKAI_LOGIN_USER_ID);
        String sign = MD5Utils.encode(qqLoginUserId + money + "duo3kai7shen5qi");
        // 设置请求url和参数
        Uri uri = Uri.parse(HttpConstant.USER).buildUpon()
                .appendQueryParameter("action", "deposit")
                .appendQueryParameter("uid", qqLoginUserId + "")
                .appendQueryParameter("umoney", money)
                .appendQueryParameter("sign", sign)
                .appendQueryParameter("rname", name)
                .appendQueryParameter("zcode", num)
                .build();
        Request request = new Request.Builder()
                .get()
                .url(uri.toString())
                .build();
        mWithDrawCall = mOkHttpClient.newCall(request);
        mWithDrawCall.enqueue(withDrawCallBack);
    }

    Callback withDrawCallBack = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            //Log.i("test", "没有请求到数据:  " + e.getMessage());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String json = response.body().string();
            Gson gson = new Gson();
            WithDrawCategory withDrawCategory = gson.fromJson(json, WithDrawCategory.class);
            WithDrawCategory.DataBean data = withDrawCategory.getData();
            String errtxt = data.getErrtxt();
            if (errtxt == null||TextUtils.isEmpty(errtxt)) {
                errtxt = "未知错误";
            }
            final int doaccess = data.getDoaccess();
            final String finalErrtxt = errtxt;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (doaccess == 0) {
                        Utils.toast(WithDrawActivity.this, "提现失败");
                    } else if (doaccess == 1) {
//                        Utils.toast(WithDrawActivity.this, "提现成功");
                        showWithSuccessDialog();
                    } else if (doaccess == 2) {
                        Utils.toast(WithDrawActivity.this, "资料请填写完整");
                    } else if (doaccess == 3) {
                        Utils.toast(WithDrawActivity.this, "支付宝账号已存在");
                    } else {
                        Utils.toast(WithDrawActivity.this, finalErrtxt);
                    }

                }
            });

        }
    };
    //提现成功的对话框
    private void showWithSuccessDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.show_withdraw_success_dialog, null);
        final MyDialog builder = new MyDialog(WithDrawActivity.this, 0, 0, dialogView, R.style.dialog);
        TextView tv_yes_iknow = (TextView) dialogView.findViewById(R.id.yes_iknow);
        ImageView iv_close_yes = (ImageView) dialogView.findViewById(R.id.iv_close_yes);
        tv_yes_iknow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        iv_close_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        builder.show();
    }
}
