package com.shenqi.duokai.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.shenqi.duokai.bean.ShareStrCategory;
import com.shenqi.duokai.constants.HttpConstant;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lixiang on 2016/10/10.
 */
public class JsonUtils {
    private Context context;
    private  OkHttpClient mClient;
    private  Call mCall;
    private ShareStrCategory shareStrCategory;
    public JsonUtils(Context  context){
        this.context = context;
    }
    public  ShareStrCategory shareStr() {
        mClient = new OkHttpClient();
        Uri uri = Uri.parse(HttpConstant.HOST + "/home/").buildUpon()
                .appendQueryParameter("action", "share")
                .build();
        Request request = new Request.Builder()
                .get()
                .url(uri.toString())
                .build();

        mCall = mClient.newCall(request);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //Log.i("test", "json解析错误的异常: "+e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
//                Log.i("test", "json 数据:  " + json);
                Gson gson = new Gson();
                shareStrCategory = gson.fromJson(json, ShareStrCategory.class);
                //Log.i("test","onResponse:是不是空"+shareStrCategory.getWechat().getTxt());
            }
        });

        return shareStrCategory;
    }

}
