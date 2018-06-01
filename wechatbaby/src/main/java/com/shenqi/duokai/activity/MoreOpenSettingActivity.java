package com.shenqi.duokai.activity;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.shenqi.duokai.R;
import com.shenqi.duokai.adapter.AppManagerAdapter;
import com.shenqi.duokai.bean.AppInfo;
import com.shenqi.duokai.constants.Contant;
import com.shenqi.duokai.dao.MadedAppDao;
import com.shenqi.duokai.engine.AppEngine;
import com.shenqi.duokai.utils.SPUtils;
import com.shenqi.duokai.utils.Utils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lixiang on 2016/9/10.
 */
public class MoreOpenSettingActivity extends CommonActivity implements View.OnClickListener {

    private static final int LOAD_SUCCESS = 0;
    private ImageView mIv_update_name;
    private ImageView mIv_protect;
    private ImageView mIv_stable;
    boolean isUpadateNameOpen = true;
    boolean isProtectOpen = true;
    boolean isStableOpen = true;
    boolean isSisson = true;

    private ListView mLv_appManager;
    private MadedAppDao mMadedAppDao;
    private List<AppInfo> mMadedAppLists;
    private AppManagerAdapter mAdapter;
    private PackageReceiver receiver;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOAD_SUCCESS:
                    final List<AppInfo> appManagerList = (List<AppInfo>) msg.obj;
                    mAdapter = new AppManagerAdapter(appManagerList, MoreOpenSettingActivity.this);
                    mLv_appManager.setAdapter(mAdapter);
                    break;
            }
        }
    };
    private ImageView mIv_session;

    private SystemBarTintManager mTintManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moreopensetting);
        initHeader("设置");
        initView();
        initData();
        initStatusBar();
        receiver = new PackageReceiver();
        IntentFilter filter = new IntentFilter();
        // 卸载应用的广播
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        // 接受系统发过来的data(Uri) 定下规范 系统已该格式发送包名给我
        filter.addDataScheme("package");
        registerReceiver(receiver, filter);
    }

    @TargetApi(19)
    private void initStatusBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            mTintManager = new SystemBarTintManager(this);
            mTintManager.setStatusBarTintColor(getResources().getColor(R.color.app_main_color));
            mTintManager.setStatusBarTintEnabled(true);
        }
    }

    private class PackageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 获取当前卸载或安装的包名
            String packageName = intent.getDataString().split(":")[1]; // package:cn.itcast.mobilesafe22
            // 1.获取集合
            List<AppInfo> appInfos = mAdapter.getAppInfos();
            switch (intent.getAction()) {
                case Intent.ACTION_PACKAGE_REMOVED:
                    // 应用卸载
                    // 2.遍历集合 获取所有的appinfo
                    for (AppInfo appInfo : appInfos) {
                        // 3.判断如果找到了当前卸载的应用 避免空指针
                        if (packageName.equals(appInfo.getPackageName())) {
                            // 4.移除总集合以及用户集合的对象
                            appInfos.remove(appInfo);
                            mMadedAppLists.remove(appInfo);
                            // 6.刷新适配器
                            mAdapter.notifyDataSetChanged();
                            // 避免并发修改异常
                            break;
                        }
                    }
                    break;

                default:
                    break;
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void initData() {
        boolean is_iv_update_name = SPUtils.getBoolean(MoreOpenSettingActivity.this, Contant.KEY_MODIFY_NAME);
        boolean is_iv_protect = SPUtils.getBoolean(MoreOpenSettingActivity.this, "iv_protect");
        boolean is_open_stable = SPUtils.getBoolean(MoreOpenSettingActivity.this, "iv_open_stable");
        boolean is_open_sision = SPUtils.getBoolean(MoreOpenSettingActivity.this, "iv_open_session");
        mLv_appManager.setEmptyView(findViewById(R.id.iv_setting_empty));
        mLv_appManager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openMadeApp(mMadedAppLists.get(position));
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                mMadedAppLists = new ArrayList<AppInfo>();
                //获取所有的 app;
                List<AppInfo> allInstalledApps = AppEngine.getAllInstalledApps(MoreOpenSettingActivity.this);
                for (AppInfo allInstalledApp : allInstalledApps) {
                    String packageName = allInstalledApp.getPackageName();
                    if (!allInstalledApp.isSysApp() && packageName.contains("com.shenqi.pluginstub")) {
                        mMadedAppLists.add(allInstalledApp);
                    }
                }
                mHandler.obtainMessage(LOAD_SUCCESS, mMadedAppLists).sendToTarget();
            }
        }).start();
        if (is_iv_update_name) {
            mIv_update_name.setImageResource(R.drawable.btn_switch_on);
            isUpadateNameOpen = false;
        } else {
            mIv_update_name.setImageResource(R.drawable.btn_switch_off);
            isUpadateNameOpen = true;
        }

        if (is_iv_protect) {
            mIv_protect.setImageResource(R.drawable.btn_switch_on);
            isProtectOpen = false;
        } else {
            mIv_protect.setImageResource(R.drawable.btn_switch_off);
            isProtectOpen = true;
        }

        if (is_open_sision) {
            mIv_stable.setImageResource(R.drawable.btn_switch_on);
            isSisson = false;
        } else {
            mIv_stable.setImageResource(R.drawable.btn_switch_off);
            isSisson = true;
        }
    }

    //打开已经制作的app;
    private void openMadeApp(AppInfo appInfo) {
        PackageManager pm = getPackageManager();
        // 获取应用的启动Activity意图
        Intent launchIntent = pm.getLaunchIntentForPackage(appInfo.getPackageName());
        // 如果没有获取到启动的Activity意图 就不启动
        if (launchIntent == null) {
            Utils.toast(MoreOpenSettingActivity.this, "打开失败");
        } else {
            startActivity(launchIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 6.刷新适配器
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void initView() {
        ImageView iv_common_back = (ImageView) findViewById(R.id.iv_common_back);
        mIv_update_name = (ImageView) findViewById(R.id.iv_update_name);
        mIv_protect = (ImageView) findViewById(R.id.iv_protect);
        mIv_stable = (ImageView) findViewById(R.id.iv_open_stable);
        mIv_session = ((ImageView) findViewById(R.id.iv_sisson));
        mLv_appManager = (ListView) findViewById(R.id.lv_appmanager);

        iv_common_back.setOnClickListener(this);
        mIv_update_name.setOnClickListener(this);
        mIv_protect.setOnClickListener(this);
        mIv_stable.setOnClickListener(this);
        mIv_session.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_common_back:
                finish();
                break;
            case R.id.iv_update_name:
                if (isUpadateNameOpen) {
                    mIv_update_name.setImageResource(R.drawable.btn_switch_on);
                    isUpadateNameOpen = false;
                } else {
                    mIv_update_name.setImageResource(R.drawable.btn_switch_off);
                    isUpadateNameOpen = true;
                }
                SPUtils.put(MoreOpenSettingActivity.this, Contant.KEY_MODIFY_NAME, !isUpadateNameOpen);
                break;
            case R.id.iv_protect:
                if (isProtectOpen) {
                    mIv_protect.setImageResource(R.drawable.btn_switch_on);
                    isProtectOpen = false;
                } else {
                    mIv_protect.setImageResource(R.drawable.btn_switch_off);
                    isProtectOpen = true;
                }
                SPUtils.put(MoreOpenSettingActivity.this, "iv_protect", !isProtectOpen);
                break;
            case R.id.iv_open_stable:
                if (isStableOpen) {
                    mIv_stable.setImageResource(R.drawable.btn_switch_on);
                    isStableOpen = false;
                } else {
                    mIv_stable.setImageResource(R.drawable.btn_switch_off);
                    isStableOpen = true;
                }
                SPUtils.put(MoreOpenSettingActivity.this, "iv_open_stable", !isStableOpen);
                break;
            case R.id.iv_sisson:
                if (isSisson) {
                    mIv_session.setImageResource(R.drawable.btn_switch_on);
                    isSisson = false;
                } else {
                    mIv_session.setImageResource(R.drawable.btn_switch_off);
                    isSisson = true;
                }
                SPUtils.put(MoreOpenSettingActivity.this, "iv_open_session", !isSisson);
                break;
        }
    }
}
