package com.shenqi.duokai.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.shenqi.duokai.MainActivity;
import com.shenqi.duokai.R;
import com.shenqi.duokai.bean.AccoutMoneyCategory;
import com.shenqi.duokai.bean.ShareWayCastegory;
import com.shenqi.duokai.constants.Contant;
import com.shenqi.duokai.constants.HttpConstant;
import com.shenqi.duokai.fragment.MoneyDetailFragment;
import com.shenqi.duokai.fragment.MyFriendFragment;
import com.shenqi.duokai.interf.OnSegmentButtonSelectedListener;
import com.shenqi.duokai.ui.SegmentButton;
import com.shenqi.duokai.utils.MD5Utils;
import com.shenqi.duokai.utils.SPUtils;

import org.apache.http.conn.MultihomePlainSocketFactory;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by lixiang on 2016/9/13.
 */
public class AccoutLastActivity extends FragmentActivity{

    private ListView mLv_accout_last;
    private TextView total_money;
    private FragmentManager fragmentManager;
    private SegmentButton mSb_Accout_last;
    private ImageView mIv_back;
    private TextView mTv_now_Money;
    private TextView mTv_total_Money;
    private TextView mTv_invite_Num;
    private OkHttpClient mOkHttpClient;
    private Call mMoneyAndNumCall;
    private TextView mTv_WithDraw;
    private String mUmoney;
    private View main;

    private SystemBarTintManager mTintManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accoutlast);
        //初始化网路情求;
        mOkHttpClient = new OkHttpClient();
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
        RequestNetGetMoneyAndNum();

        //点击返回键;
        mIv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //点击提现;
        mTv_WithDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("accoutlast", mUmoney);
                intent.setClass(AccoutLastActivity.this, WithDrawActivity.class);
                startActivity(intent);
//                startActivity(new Intent(AccoutLastActivity.this, WithDrawActivity.class));
            }
        });
        //获取 ,fragment管理器
        fragmentManager = getSupportFragmentManager();
        final MoneyDetailFragment moneyDetailFragment = new MoneyDetailFragment();
        final MyFriendFragment myFriendFragment = new MyFriendFragment();
        //默认是 余额详情;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fl_main, moneyDetailFragment);
        fragmentTransaction.commit();
        mSb_Accout_last.setLeftSelected(true);
        mSb_Accout_last.setOnSegmentButtonSelectedListener(new OnSegmentButtonSelectedListener() {

            @Override
            public void onLeftSelected(boolean isLeftSelected) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                if (isLeftSelected) {
                    //收入明细
                    fragmentTransaction.replace(R.id.fl_main, moneyDetailFragment);
                } else {
                    //我的好友;
                    fragmentTransaction.replace(R.id.fl_main, myFriendFragment);
                }
                fragmentTransaction.commit();
            }
        });

    }

    //请求网络,获取好友数;和当前余额,以及总余额;
    private void RequestNetGetMoneyAndNum() {
        //从首选项获取用户id;
        int qqLoginUserId = SPUtils.getInt(AccoutLastActivity.this, Contant.DUOKAI_LOGIN_USER_ID);
        //Log.i("test","userId;  "+qqLoginUserId);
        String sign = MD5Utils.encode(qqLoginUserId + "duo3kai7shen5qi");
        Uri uri = Uri.parse(HttpConstant.HOME).buildUpon()
                .appendQueryParameter("action", "" + "home")
                .appendQueryParameter("uid", "" + qqLoginUserId)
                .appendQueryParameter("sign", "" + sign)
                .build();
        Request request = new Request.Builder()
                .get()
                .url(uri.toString())
                .build();
        mMoneyAndNumCall = mOkHttpClient.newCall(request);
        mMoneyAndNumCall.enqueue(moneyCallBack);
    }

    Callback moneyCallBack = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            //Log.i("test", e.getMessage());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String json = response.body().string();
            Gson gson = new Gson();
            AccoutMoneyCategory accoutMoneyCategory = gson.fromJson(json, AccoutMoneyCategory.class);
//            String invitenum = accoutMoneyCategory.getInvitenum();
            int invitenum = accoutMoneyCategory.getInvitenum();
            String tolmoney = accoutMoneyCategory.getTolmoney();
            mUmoney = accoutMoneyCategory.getUmoney();
            showMoneyInviteFriend(invitenum, tolmoney, mUmoney);
            //Log.i("test", "onResponse: " + invitenum + "  " + tolmoney + "  " + mUmoney);
        }
    };

    //显示 当前余额 和当前邀请好友数;
    private void showMoneyInviteFriend(final int invitenum, final String totalmoney, final String umoney) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTv_now_Money.setText(umoney);
                mTv_invite_Num.setText(invitenum + "");
                mTv_total_Money.setText(totalmoney);
            }
        });
    }


    private void initView() {
        mSb_Accout_last = ((SegmentButton) findViewById(R.id.sb_accout_last));
        mIv_back = (ImageView) findViewById(R.id.iv_common_back);
        total_money = ((TextView) findViewById(R.id.tv_total_money));
        mTv_now_Money = ((TextView) findViewById(R.id.tv_now_money));
        mTv_total_Money = ((TextView) findViewById(R.id.tv_total_money));
        mTv_invite_Num = ((TextView) findViewById(R.id.tv_invite_num));
        mTv_WithDraw = ((TextView) findViewById(R.id.go_withdraw));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMoneyAndNumCall != null) {
            mMoneyAndNumCall.cancel();
        }
    }

}
