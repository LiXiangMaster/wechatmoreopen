package com.shenqi.duokai.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;
import com.shenqi.duokai.R;
import com.shenqi.duokai.adapter.MoneyDetailAdapter;
import com.shenqi.duokai.bean.AccoutDetailCategory;
import com.shenqi.duokai.bean.AccoutMoneyCategory;
import com.shenqi.duokai.bean.AppInfo;
import com.shenqi.duokai.constants.Contant;
import com.shenqi.duokai.constants.HttpConstant;
import com.shenqi.duokai.engine.AppEngine;
import com.shenqi.duokai.utils.MD5Utils;
import com.shenqi.duokai.utils.SPUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lixiang on 2016/9/29.
 */
public class MoneyDetailFragment extends Fragment {
    View view;
    private ListView mLv_Money;
    private OkHttpClient mOkHttpClient;
    private Call mAccoutDetailCall;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_money, container, false);
        }
        //初始化网络请求;
        mOkHttpClient = new OkHttpClient();
        initView(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        initData();
    }

    private void initView(View view) {
        mLv_Money = (ListView) view.findViewById(R.id.lv_money);

    }

    private void initData() {
        //情求网罗 获取数据;
        questNetGetMoneyInfo();
    }

    private void questNetGetMoneyInfo() {
        //从首选项获取用户id;
        int qqLoginUserId = SPUtils.getInt(getActivity(), Contant.DUOKAI_LOGIN_USER_ID);
        String sign = MD5Utils.encode(qqLoginUserId + "duo3kai7shen5qi");
        Uri uri = Uri.parse(HttpConstant.USER).buildUpon()
                .appendQueryParameter("action", "" + "account")
                .appendQueryParameter("uid", "" + qqLoginUserId)
                .appendQueryParameter("page", "" + 1)
                .appendQueryParameter("sign", sign)
                .build();
        Request request = new Request.Builder()
                .get()
                .url(uri.toString())
                .build();
        //Log.i("test", "sign" + sign + "   qqlogniuserid: " + qqLoginUserId + "  ");
        mAccoutDetailCall = mOkHttpClient.newCall(request);
        mAccoutDetailCall.enqueue(accoutDetailCallBack);
    }

    Callback accoutDetailCallBack = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            //Log.i("test", e.getMessage());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String json = response.body().string();

            //Log.i("test", "onresponse:  " + json);
            Gson gson = new Gson();
            AccoutDetailCategory accoutDetailCategory = gson.fromJson(json, AccoutDetailCategory.class);
            final List<List<String>> data = accoutDetailCategory.getData();
            //把这个集合传到适配器中;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MoneyDetailAdapter adapter =new MoneyDetailAdapter(getContext(),data);
                    mLv_Money.setEmptyView(getActivity().findViewById(R.id.iv_must_app_no_data));
                    mLv_Money.setAdapter(adapter);
                }
            });
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAccoutDetailCall!=null){
            mAccoutDetailCall.cancel();
        }
    }
}
