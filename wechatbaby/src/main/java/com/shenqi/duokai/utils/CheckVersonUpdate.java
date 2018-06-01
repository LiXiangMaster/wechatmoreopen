package com.shenqi.duokai.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.shenqi.duokai.MainActivity;
import com.shenqi.duokai.R;
import com.shenqi.duokai.bean.VerSionUpdateCategory;
import com.shenqi.duokai.constants.HttpConstant;
import java.io.IOException;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/10/28.
 */
public class CheckVersonUpdate {
    public static class UpdateData {
        public String versionName;
        public String urlApk;
        public String title;
        public String text;
        public String signature;
        public boolean forceUpdate;
    }

    public static final int WHAT_APP_UPDATE = 11;

    private static OkHttpClient mVerSionUpdateClient;
    private static Handler mMsgHandler;
    private static boolean mChecking = false;

    //清除正在更新标志, 允许下次检查
    //应该在更新对话框关闭后调用
    public static void finishedUpdating() {
        mChecking = false;
    }

    public static  void checkUpdate(final Context context, Handler handler) {
        if (mChecking)
            return;
        mChecking = true;

        mVerSionUpdateClient = new OkHttpClient();
        mMsgHandler = handler;
        // 设置请求url和参数

        Request request = new Request.Builder()
                .get()
                .url(HttpConstant.VERSIONUPDATE)
                .build();
        Call madeCall = mVerSionUpdateClient.newCall(request);
        madeCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                finishedUpdating();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                Gson gson = new Gson();
                VerSionUpdateCategory verSionUpdateCategory = gson.fromJson(json, VerSionUpdateCategory.class);
                String autoup = verSionUpdateCategory.getAutoup();
                String downurl = verSionUpdateCategory.getDownurl();
                String sign = verSionUpdateCategory.getSign();
                String text = verSionUpdateCategory.getText();
                String versionCode = verSionUpdateCategory.getVersionCode();
                String versionName = verSionUpdateCategory.getVersionName();
                String title = verSionUpdateCategory.getTitle();

                int currentVer = getVersionCode(context);
                int nNewVer = Integer.parseInt(versionCode);
                if (currentVer >= nNewVer)
                    return;

                final UpdateData ud = new UpdateData();
                ud.versionName = versionName;
                ud.urlApk = downurl;
                ud.title = title;
                ud.text = text;
                if (sign!=null && !sign.isEmpty())
                    ud.signature = sign;

                if (autoup!=null && "1".equals(autoup))
                    ud.forceUpdate = true;
                else
                    ud.forceUpdate = false;

                //有新版本了, 需要更新
                //有新版本了, 需要更新
                Message message = new Message();
                message.what = WHAT_APP_UPDATE;
                message.obj = ud;
                if (mMsgHandler != null)
                    mMsgHandler.sendMessage(message);
            }
        });
    }


    public static int getVersionCode(Context context) {
        // 获取版本号;
        PackageManager pm = context.getPackageManager();
        try {
            // 得到应用的管理者;
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}



