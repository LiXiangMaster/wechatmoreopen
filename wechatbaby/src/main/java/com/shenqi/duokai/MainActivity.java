package com.shenqi.duokai;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.iapppay.interfaces.callback.IPayResultCallback;
import com.iapppay.sdk.main.IAppPay;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.shenqi.duokai.activity.AccoutLastActivity;
import com.shenqi.duokai.activity.MoreOpenSettingActivity;
import com.shenqi.duokai.activity.NonetActivity;
import com.shenqi.duokai.activity.WebViewActivity;
import com.shenqi.duokai.activity.WithDrawActivity;
import com.shenqi.duokai.alipay.PayResult;
import com.shenqi.duokai.bean.AccoutMoneyCategory;
import com.shenqi.duokai.bean.AiBeiPayCategorey;
import com.shenqi.duokai.bean.AliPayCategory;
import com.shenqi.duokai.bean.CheckOpenPacketCategory;
import com.shenqi.duokai.bean.InviteDataCategrogy;
import com.shenqi.duokai.bean.IsShowGoodsCategory;
import com.shenqi.duokai.bean.OpenPacketCategory;
import com.shenqi.duokai.bean.PayWayCategory;
import com.shenqi.duokai.bean.QqLoginCategory;
import com.shenqi.duokai.bean.ShareWayCastegory;
import com.shenqi.duokai.bean.WechatLoginCategory;
import com.shenqi.duokai.constants.Contant;
import com.shenqi.duokai.constants.HttpConstant;
import com.shenqi.duokai.dao.MadedAppDao;
import com.shenqi.duokai.engine.AppEngine;
import com.shenqi.duokai.ui.MyDialog;
import com.shenqi.duokai.utils.ApkMaker;
import com.shenqi.duokai.utils.CheckVersonUpdate;
import com.shenqi.duokai.utils.MD5Utils;
import com.shenqi.duokai.utils.SPUtils;
import com.shenqi.duokai.utils.UpdateDialog;
import com.shenqi.duokai.utils.Utils;
import com.tencent.tauth.Tencent;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends Activity implements View.OnClickListener, Handler.Callback {

    private static final int REQUESTCODE_STORAGE_PERMISSION = 11;

    private static final int SDK_PAY_FLAG = 0;
    public static final int MAKE_ERROR = 1;
    private static final int AIBEIPAY = 2;
    //登录成功;

    private LinearLayout ll_customer;
    private LinearLayout mll_packet;
    private static boolean isServerSideLogin = false;
    private static String openId;
    //判断 是否用qq授权登陆过;
    private String mSp_qqlogin;
    private Call mCall;
    private Call mMadeRecordCall;
    //qq授权登录后返回的qq图像网址;
    private String mImg;
    public static Tencent mTencent;
    private ImageView mSetting;
    private ImageView mInviteFriend;
    private OkHttpClient mOkHttpClient;
    private Call mCall1;
    private Call mAiBeiPayCallnew;
    private Call mPayWayallnew;
    private Call shareWayCallBack;
    private Call mWechatCall;
    private Call isShowGoodsCall;
    private Call mAliPayCall;
    private Call mOpenPacketCall;
    private Call mCheckOpenPacketCall;
    private Call mInviteFriendCall;
    private ImageView mLastMoney;
    private ImageView mIv_made;
    private static boolean isExit;
    //底部邀请好友,
    private InviteDataCategrogy.QqBean mQq;
    private InviteDataCategrogy.TimelineBean mTimeline;
    private InviteDataCategrogy.WechatBean mWechat;
    private InviteDataCategrogy.CopyurlBean mCopyurl;
    private InviteDataCategrogy.SinaBean mSina;
    private InviteDataCategrogy.QzoneBean mQzone;
    private UpdateDialog mUpdateDlg;

    //爱呗支付的订单;
    private String mOutTradeNo;
    private ImageView mIv_small_rotate;
    private ImageView mIv_bigRotate;
    private String mUmoney1;
    //给分身取得名字;
    public String mSetName;
    private ImageView mIv_help;
    //支付方式;
    private int mPay;
    //分享;
    private ShareModel mModel;
    private TextView mTv_progress;
    private ImageView mIv_yuan;
    private ImageView mIv_install;
    //拆红包奖励的钱;
    private double mDaymoney;
    private int mInvitenum;

    private SystemBarTintManager mTintManager;
    //从服务器拿到支付宝订单信息;
    private String mStrJson;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SDK_PAY_FLAG:
                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Utils.toast(MainActivity.this, "支付成功");
                        //支付成功后要走的方法;
                        paySuccessMethod();
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确支付认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Utils.toast(MainActivity.this, "支付失败");
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Utils.toast(MainActivity.this, "取消支付");
                        }
                    }
                    break;
                case MAKE_ERROR:
                    String err = (String) msg.obj;
                    Utils.toast(MainActivity.this, err);
                    break;
                case AIBEIPAY:
                    int var1 = msg.arg1;
                    if (var1 == 0) {
                        //支付成功 调用制作安装方法:
                        Utils.toast(MainActivity.this, "支付成功");
                        //支付成功后要走的方法;
                        paySuccessMethod();

                    }
                    break;
                case CheckVersonUpdate.WHAT_APP_UPDATE:
                    CheckVersonUpdate.UpdateData data = (CheckVersonUpdate.UpdateData) msg.obj;
                    mUpdateDlg = new UpdateDialog(MainActivity.this, data);
                    //对话框关闭后运行下次到前台时检查更新
                    mUpdateDlg.setOnDismissListener(new Dialog.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            CheckVersonUpdate.finishedUpdating();
                        }
                    });
                    mUpdateDlg.show();
                    break;

            }
        }
    };


    //支付成功后走的方法;
    private void paySuccessMethod() {
        mIv_made.setVisibility(View.INVISIBLE);
        mIv_yuan.setVisibility(View.VISIBLE);
        mTv_progress.setVisibility(View.VISIBLE);
        //界面旋转动画;

        RotateAnimation animation1 = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation1.setDuration(2000);
        animation1.setRepeatCount(Animation.INFINITE);
        mIv_small_rotate.startAnimation(animation1);

        RotateAnimation animation2 = new RotateAnimation(360, 0,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation2.setDuration(2000);
        animation2.setRepeatCount(Animation.INFINITE);
        mIv_bigRotate.startAnimation(animation2);

        //开始制作
        String appPackageName = "com.tencent.mm";
        final String appName = mSetName;
        //把用户取到分身名字保存到数据库;
        ApkMaker.get().startMake(MainActivity.this, appPackageName, appName, new ApkMaker.ApkMakerCallback() {
            private void resetUIToMake() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mIv_small_rotate.clearAnimation();
                        mIv_bigRotate.clearAnimation();

                        mTv_progress.setVisibility(View.INVISIBLE);
                        mIv_yuan.setVisibility(View.INVISIBLE);
                        mIv_made.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onError(String msg) {
                resetUIToMake();

                Utils.toast(MainActivity.this, msg);
            }

            @Override
            public void onProgress(int percent) {
                final int finaln = percent;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTv_progress.setText(finaln + "%");
                    }
                });
            }

            @Override
            public void onSucceed(String path) {
                resetUIToMake();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String date = sdf.format(new Date());
                //Log.i("test", "当前年月日: " + date);
                MadedAppDao madedAppDao = new MadedAppDao(MainActivity.this);
                madedAppDao.add(mSetName + "&" + date);
                //把制作记录给后台;
                getMadeRecordToNet(appName);

                //开始安装
                Utils.installApk(MainActivity.this, path);
            }
        });
    }

    private static boolean storagePermitted(Activity activity) {

        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&

                ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return true;
        return false;
    }

    private static void requestStoragePermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUESTCODE_STORAGE_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUESTCODE_STORAGE_PERMISSION:
                if (permissions == null || permissions.length == 0)
                    return;

                // 如果用户不允许，我们视情况发起二次请求或者引导用户到应用页面手动打开
                if (PackageManager.PERMISSION_GRANTED == grantResults[0]) {
                    // permission was granted, yay!
                    //开始下载?
                    //startInstall();
                }
        }
    }

    //把制作记录传给后台;
    private void getMadeRecordToNet(String appName) {
        //获取手机的imei号;
        String imei = Utils.getID(this);
        //获取当前时间戳;
        long date = new Date().getTime();
        String time = String.valueOf(date);
        //Log.i("test", "当前时间的时间戳: " + time);
        //从首选项获取用户id;
        int qqLoginUserId = SPUtils.getInt(MainActivity.this, Contant.DUOKAI_LOGIN_USER_ID);
        String sign = MD5Utils.encode(qqLoginUserId + time + "" + "duo3kai7shen5qi");
        // 设置请求url和参数
        Uri uri = Uri.parse(HttpConstant.HOME).buildUpon()
                .appendQueryParameter("action", "duokaiinfo")
                .appendQueryParameter("uid", qqLoginUserId + "")
                .appendQueryParameter("product", appName)
                .appendQueryParameter("imei", imei)
                .appendQueryParameter("sign", sign)
                .appendQueryParameter("nostrtime", time + "")
                .build();
        Request request = new Request.Builder()
                .get()
                .url(uri.toString())
                .build();
        Call madeCall = mOkHttpClient.newCall(request);
        madeCall.enqueue(madeRecordCall);
    }

    Callback madeRecordCall = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            //Log.i("test", "没有请求到数据:  " + e.getMessage());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //分享要用的代码;
        ShareSDK.initSDK(this);
        mModel = new ShareModel();

        //爱贝支付;
        IAppPay.init(this, IAppPay.PORTRAIT, "3008011444");
        //网络请求初始化;
        mOkHttpClient = new OkHttpClient();
        //透明状态栏;
        initStatusBar();

        initView();
        initData();
    }

    @TargetApi(19)
    private void initStatusBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            mTintManager = new SystemBarTintManager(this);
            mTintManager.setStatusBarTintColor(getResources().getColor(R.color.app_main_color));
            mTintManager.setStatusBarTintEnabled(true);
        }
    }

    private void initView() {
        ll_customer = ((LinearLayout) findViewById(R.id.ll_customer));
        mll_packet = ((LinearLayout) findViewById(R.id.ll_packet));
        mSetting = ((ImageView) findViewById(R.id.iv_setting));
        mInviteFriend = ((ImageView) findViewById(R.id.iv_invite));
        mLastMoney = ((ImageView) findViewById(R.id.iv_money));
        mIv_made = ((ImageView) findViewById(R.id.iv_made));
        mIv_small_rotate = ((ImageView) findViewById(R.id.iv_small_rotate));
        mIv_bigRotate = ((ImageView) findViewById(R.id.iv_big_rotate));
        mIv_help = ((ImageView) findViewById(R.id.iv_help));
        mTv_progress = ((TextView) findViewById(R.id.tv_progress));
        mIv_yuan = ((ImageView) findViewById(R.id.iv_yuan));
        mIv_install = ((ImageView) findViewById(R.id.iv_install));
    }

    private void initData() {
        //创建一个数据库;
        //后台统计数据;
        serverGetData();
        int qqLoginUserId = SPUtils.getInt(MainActivity.this, Contant.DUOKAI_LOGIN_USER_ID);
        //Log.i("test", "qqLoginUserId: " + qqLoginUserId);
        if (qqLoginUserId != 0) {
            //获取余额 和邀请好友数量;
            getLastMoney();
        }

        ll_customer.setOnClickListener(this);
        mll_packet.setOnClickListener(this);
        mSetting.setOnClickListener(this);
        mInviteFriend.setOnClickListener(this);
        mLastMoney.setOnClickListener(this);
        mIv_made.setOnClickListener(this);
        mIv_help.setOnClickListener(this);
    }

    //后台统计数据用的;
    private void serverGetData() {
        //应用启动后-为统计用户
        //获取手机的imei号;
        String imei = Utils.getID(this);
        //Log.i("test", "手机唯一标示: " + imei);
        //对imei进行MD5处理: sign   加密参数 加密固定值：duo3kai7shen5qi    md5(imei."duo3kai7shen5qi");
        String parentID = Utils.getParentID(this);
        String sign = MD5Utils.encode(parentID + "duo3kai7shen5qi");
        OkHttpClient serverDataClient = new OkHttpClient();
        // 设置请求url和参数
        //http://api.duokaishenqi.com/user/?action=qqlogin&params=xxx&imei=xxx&sign=xxx
        Uri uri = Uri.parse(HttpConstant.HOME).buildUpon()
                .appendQueryParameter("action", "install")
                .appendQueryParameter("parentid", parentID)
                .appendQueryParameter("imei", imei)
                .appendQueryParameter("sign", sign)
                .build();
        Request request = new Request.Builder()
                .get()
                .url(uri.toString())
                .build();

        Call servercll = serverDataClient.newCall(request);
        servercll.enqueue(serverCall);
    }

    Callback serverCall = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            //Log.i("test", "没有请求到数据:  " + e.getMessage());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {

        }
    };


    //没有网络的页面;
    private void noNet() {
        startActivity(new Intent(MainActivity.this, NonetActivity.class));
        finish();
    }

    private boolean isFirstLogin() {
        return false;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_customer:
                //弹出是否 要拨打客服电话对话框；
                jumpqqchatwindow();
                break;
            case R.id.ll_packet:
                if (isLogin()) {
                    //调用后台判断,弹出那种红包形式;
//                    showOpenRedPacketDialog();
                    //判断是否拆过了;
                    isGetNetOpenPacket();

                    //,调用后台接口 返回弹出红类型;
//                    getNetOpenPacket();
                } else {
                    showLoginDialog();
                }
                break;
            case R.id.iv_setting:
                startActivity(new Intent(MainActivity.this, MoreOpenSettingActivity.class));
                break;
            case R.id.iv_invite:
                if (isLogin()) {
                    //直接弹出浮层;
                    //底部邀请好友分享 获取后台接口;
                    putRecordToNet();

                } else {
                    showLoginDialog();
                }

                break;
            case R.id.iv_money:
                if (isLogin()) {
                    Intent intent1 = new Intent();
                    intent1.putExtra("accout", mUmoney1);
                    intent1.setClass(MainActivity.this, AccoutLastActivity.class);
                    startActivity(intent1);
                } else {
                    showLoginDialog();
                }
                break;
            case R.id.iv_made:
                //点击制作弹出给分身取名的对话框

                showSetNameDialog();

                break;
            case R.id.iv_help:
                //传一个buddle到WebviewActivity
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("url", "http://m.duokaishenqi.com/help.html");
                bundle.putString("title", "新手帮助");
                intent.putExtras(bundle);
                intent.setClass(MainActivity.this, WebViewActivity.class);
                startActivity(intent);
                break;

        }
    }

    //判断是否拆过红包;
    private void isGetNetOpenPacket() {
        //http://api.duokaishenqi.com/home/?action=sharechai&uid=xxx&date=xxx&sign=xxx
        //获取当前日期;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());
        //从首选项获取用户id;
        int qqLoginUserId = SPUtils.getInt(MainActivity.this, Contant.DUOKAI_LOGIN_USER_ID);
        //加密;
        String sign = MD5Utils.encode(qqLoginUserId + date + "duo3kai7shen5qi");
        Uri uri = Uri.parse(HttpConstant.HOME).buildUpon()
                .appendQueryParameter("action", "sharechai")
                .appendQueryParameter("uid", "" + qqLoginUserId)
                .appendQueryParameter("date", "" + date)
                .appendQueryParameter("sign", "" + sign)
                .build();
        Request request = new Request.Builder()
                .get()
                .url(uri.toString())
                .build();
        mCheckOpenPacketCall = mOkHttpClient.newCall(request);
        mCheckOpenPacketCall.enqueue(checkopenPacketCallBack1);

    }

    Callback checkopenPacketCallBack1 = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            //Log.i("test", e.getMessage());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String json = response.body().string();
            Gson gson = new Gson();
            CheckOpenPacketCategory checkOpenPacketCategory = gson.fromJson(json, CheckOpenPacketCategory.class);
            int data = checkOpenPacketCategory.getData();
            if (data == 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showOpenRedPacketDialog();
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        openedPacketDialog();

                    }
                });
            }
        }
    };

    //邀请好友;
    private void putRecordToNet() {
        int duokailoginuseid = SPUtils.getInt(MainActivity.this, Contant.DUOKAI_LOGIN_USER_ID);
        // 设置请求url和参数
        Uri uri = Uri.parse(HttpConstant.HOME).buildUpon()
                .appendQueryParameter("action", "friendshare")
                .appendQueryParameter("userid", duokailoginuseid + "")
                .build();
        Request request = new Request.Builder()
                .get()
                .url(uri.toString())
                .build();

        mInviteFriendCall = mOkHttpClient.newCall(request);
        mInviteFriendCall.enqueue(inviteFriendCallback);
    }

    Callback inviteFriendCallback = new Callback() {


        @Override
        public void onFailure(Call call, IOException e) {
            //Log.i("test", e.getMessage());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String json = response.body().string();
            Gson gson = new Gson();
            InviteDataCategrogy inviteDataCategrogy = gson.fromJson(json, InviteDataCategrogy.class);
            mQq = inviteDataCategrogy.getQq();
            mTimeline = inviteDataCategrogy.getTimeline();
            mWechat = inviteDataCategrogy.getWechat();
            mQzone = inviteDataCategrogy.getQzone();
            mSina = inviteDataCategrogy.getSina();
            mCopyurl = inviteDataCategrogy.getCopyurl();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showShareDialog();

                }
            });
        }
    };

    //----------------------------------------------------------------------------
    //显示自定义 底部邀请好友分享;
    private void showShareDialog() {

        View dialogView = getLayoutInflater().inflate(R.layout.share_dialog, null);
        final MyDialog builder = new MyDialog(MainActivity.this, 0, 0, dialogView, R.style.dialog);
        builder.setCancelable(false);
        builder.show();
        ImageView iv_close = (ImageView) dialogView.findViewById(R.id.iv_share_close);
        ImageView iv_wechat = (ImageView) dialogView.findViewById(R.id.iv_wechat);
        ImageView iv_qq = (ImageView) dialogView.findViewById(R.id.iv_qq);
        ImageView iv_qzone = (ImageView) dialogView.findViewById(R.id.iv_qzone);
        ImageView iv_timeline = (ImageView) dialogView.findViewById(R.id.iv_timeline);
        ImageView iv_sina = (ImageView) dialogView.findViewById(R.id.iv_sina);
        ImageView iv_copy = (ImageView) dialogView.findViewById(R.id.iv_copy);

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });

        iv_wechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doShareWechat(Wechat.NAME);  //微信分享用这个
                //doShare(WechatMoments.NAME);
                builder.dismiss();
            }
        });
        iv_qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doShareQQ(QQ.NAME);
                builder.dismiss();
            }
        });
        iv_qzone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doShareQzone(QZone.NAME);
                builder.dismiss();
            }
        });
        iv_timeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //微信朋友圈
                doShareWechatFriendQuan(WechatMoments.NAME);
                builder.dismiss();
            }
        });
        //点击分享到 新浪微博;TODO
        iv_sina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utils.toast(MainActivity.this, "请稍候...");
                    }
                });
                doShareSinaWeibo(SinaWeibo.NAME);
                builder.dismiss();
            }
        });

        iv_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //复制;
                ClipboardManager mMnamager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                mMnamager.setText(mCopyurl.getUrl());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utils.toast(MainActivity.this, "复制成功");
                    }
                });
                builder.dismiss();
            }
        });

    }

    //微信分享;
    private void doShareWechat(String platform) {
        Platform.ShareParams sp = new Platform.ShareParams();

        sp.setTitle(mWechat.getTitle());
        sp.setTitleUrl(mWechat.getUrl()); // 标题的超链接
        sp.setText(mWechat.getTxt());
        sp.setImageUrl(mWechat.getPic());
        sp.setUrl(mWechat.getUrl());

        sp.setComment("我对此分享内容的评论");
        sp.setSite(mWechat.getTitle());
        sp.setSiteUrl(mWechat.getUrl());

        sp.setShareType(Platform.SHARE_WEBPAGE);

        Platform pf = ShareSDK.getPlatform(platform);
        pf.setPlatformActionListener(platformActionListenerBottom);
        pf.share(sp);
    }

    private void doShareWechatFriendQuan(String platform) {
        Platform.ShareParams sp = new Platform.ShareParams();

        sp.setTitle(mTimeline.getTitle()+" "+mTimeline.getTxt());
        sp.setTitleUrl(mTimeline.getUrl()); // 标题的超链接
        sp.setText(mTimeline.getTxt());
        sp.setImageUrl(mTimeline.getPic());
        sp.setUrl(mTimeline.getUrl());

        sp.setComment("我对此分享内容的评论");
        sp.setSite(mTimeline.getTitle());
        sp.setSiteUrl(mTimeline.getUrl());

        sp.setShareType(Platform.SHARE_WEBPAGE);

        Platform pf = ShareSDK.getPlatform(platform);
        pf.setPlatformActionListener(platformActionListenerBottom);
        pf.share(sp);
    }

    private void doShareQQ(String platform) {
        Platform.ShareParams sp = new Platform.ShareParams();

        sp.setTitle(mQq.getTitle());
        sp.setTitleUrl(mQq.getUrl()); // 标题的超链接
        sp.setText(mQq.getTxt());
        sp.setImageUrl(mQq.getPic());
        sp.setUrl(mQq.getUrl());

        sp.setComment("我对此分享内容的评论");
        sp.setSite(mQq.getTitle());
        sp.setSiteUrl(mQq.getUrl());

        sp.setShareType(Platform.SHARE_WEBPAGE);

        Platform pf = ShareSDK.getPlatform(platform);
        pf.setPlatformActionListener(platformActionListenerBottom);
        pf.share(sp);
    }

    private void doShareQzone(String platform) {
        Platform.ShareParams sp = new Platform.ShareParams();

        sp.setTitle(mQzone.getTitle());
        sp.setTitleUrl(mQzone.getUrl()); // 标题的超链接
        sp.setText(mQzone.getTxt());
        sp.setImageUrl(mQzone.getPic());
        sp.setUrl(mQzone.getUrl());

        sp.setComment("我对此分享内容的评论");
        sp.setSite(mQzone.getTitle());
        sp.setSiteUrl(mQzone.getUrl());

        sp.setShareType(Platform.SHARE_WEBPAGE);

        Platform pf = ShareSDK.getPlatform(platform);
        pf.setPlatformActionListener(platformActionListenerBottom);
        pf.share(sp);
    }



    private void doShareSinaWeibo(String platform) {
        Platform.ShareParams sp = new Platform.ShareParams();

        sp.setTitle(mSina.getTitle());
        sp.setTitleUrl(mSina.getUrl()); // 标题的超链接
        sp.setText(mSina.getTxt() + " " + mSina.getUrl());
        sp.setImageUrl(mSina.getPic());
        sp.setUrl(mSina.getUrl());

        sp.setComment("我对此分享内容的评论");
        sp.setSite(mSina.getTitle());
        sp.setSiteUrl(mSina.getUrl());

        sp.setShareType(Platform.SHARE_WEBPAGE);

        Platform pf = ShareSDK.getPlatform(platform);
        pf.setPlatformActionListener(platformActionListenerBottom);
        pf.share(sp);
    }


    private PlatformActionListener platformActionListenerBottom = new PlatformActionListener() {
        @Override
        public void onCancel(Platform plat, int arg1) {

        }

        @Override
        public void onComplete(Platform plat, int action,
                               HashMap<String, Object> res) {
            //Log.i("test", "Share succeed");
            //底部 邀请好友成功后,暂未做任何表示;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Utils.toast(MainActivity.this, "分享成功");
                }
            });

        }

        @Override
        public void onError(Platform plat, int arg1, Throwable arg2) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Utils.toast(MainActivity.this, "分享出错");
                }
            });
        }
    };

    //---------------------------------------------------------------------------------------------------
    //判断是否登录过;
    public boolean isLogin() {
        String isLogin = SPUtils.getString(MainActivity.this, Contant.DUOKAILOGIN_SUCCESS);
        if ("loginsuccess".equals(isLogin)) {
            return true;
        }
        return false;
    }

    //给分身取名字的对话框;
    private void showSetNameDialog() {
        boolean bModifyName = SPUtils.getBoolean(MainActivity.this, Contant.KEY_MODIFY_NAME);
        String autoName = AppEngine.genNewContainerName(this);

        if (!bModifyName) {
            MainActivity.this.mSetName = autoName;
            payMoneyDialog();
            return;
        }

        final View dialogView = this.getLayoutInflater().inflate(R.layout.pay_style_setsamename_dialog, null);
        final MyDialog builder = new MyDialog(this, 0, 0, dialogView, R.style.dialog);
        builder.setCancelable(false);
        ImageView iv_close_setName = (ImageView) dialogView.findViewById(R.id.iv_close_setName);
        final Button comfir_made = (Button) dialogView.findViewById(R.id.bt_paystyle_zhifubao);
        final EditText et_setName = (EditText) dialogView.findViewById(R.id.tv_setname_tip);
        iv_close_setName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });

        et_setName.setHint(autoName);

        comfir_made.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //变成成员变量;
                String setName = et_setName.getText().toString().trim();

                if (!TextUtils.isEmpty(setName)) {
                    //弹出收费对话框;
                    MainActivity.this.mSetName = setName;
                    payMoneyDialog();
                    builder.dismiss();
                } else {
                    Utils.toast(MainActivity.this, "您还未给分身取个小名");
                }

            }
        });
        builder.show();
    }

    //获取订单信息;
    private void getNetAliPayInfo() {
        //从服务器获取 订单详情;
        // 设置请求url和参数
        //获取手机的imei号;
        String imei = Utils.getID(this);
        //从首选项获取用户id;
        int qqLoginUserId = SPUtils.getInt(MainActivity.this, Contant.DUOKAI_LOGIN_USER_ID);
        String sign = MD5Utils.encode(qqLoginUserId + "duo3kai7shen5qi");
        Uri uri = Uri.parse(HttpConstant.ALIPAY).buildUpon()
                .appendQueryParameter("action", "alipay")
                .appendQueryParameter("userid", qqLoginUserId + "")
                .appendQueryParameter("paytxt", "支付宝支付")
                .appendQueryParameter("imei", imei)
                .appendQueryParameter("sign", sign)
                .build();
        Request request = new Request.Builder()
                .get()
                .url(uri.toString())
                .build();

        mAliPayCall = mOkHttpClient.newCall(request);
        mAliPayCall.enqueue(aliPayCallback);
    }

    Callback aliPayCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            //Log.i("test", "没有请求到数据:  " + e.getMessage());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Utils.toast(MainActivity.this, "下单失败,请稍后重试");
                }
            });
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String json = response.body().string();
            Gson gson = new Gson();
            AliPayCategory aliPayCategory = gson.fromJson(json, AliPayCategory.class);
            AliPayCategory.DataBean data = aliPayCategory.getData();
            mStrJson = data.getStrJson();
            aliPay();
            //Log.i("test", "mStrJson; " + mStrJson);
        }
    };

    //弹出支付对话框;
    private void payMoneyDialog() {
        View dialogView = this.getLayoutInflater().inflate(R.layout.pay_style_second_dialog, null);
        final MyDialog builder = new MyDialog(this, 0, 0, dialogView, R.style.dialog);
        TextView tv_weixinPay = (TextView) dialogView.findViewById(R.id.bt_weixin_pay);
        TextView tv_zhifubaoPay = (TextView) dialogView.findViewById(R.id.bt_zhifubao_pay);
        final ImageView iv_close_pay_dialog = (ImageView) dialogView.findViewById(R.id.iv_close_pay_dialog);
        iv_close_pay_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        tv_weixinPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //爱呗  微信支付接口;
                getNetAiBeiPayInfo();
                builder.dismiss();
            }

        });
        tv_zhifubaoPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //支付成功后要走的方法;
                //支付宝支付:
                getNetAliPayInfo();
                builder.dismiss();
            }
        });
        builder.show();
    }

    //从后台 获取支付方式;
    private void getpayWay() {
        //从服务器获取 订单详情;
        // 设置请求url和参数
        Request request = new Request.Builder()
                .get()
                .url(HttpConstant.PAYWAY)
                .build();
        mPayWayallnew = mOkHttpClient.newCall(request);
        mPayWayallnew.enqueue(payWayCall);
    }

    Callback payWayCall = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            //Log.i("test", "没有请求到数据:  " + e.getMessage());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String json = response.body().string();
            Gson gson = new Gson();
            PayWayCategory payWayCategory = gson.fromJson(json, PayWayCategory.class);
            mPay = payWayCategory.getPay();
            //Log.i("test", "json: " + json + "  支付方式: " + mPay);
        }
    };


    //后台 ,爱呗支付 信息 ;
    private void getNetAiBeiPayInfo() {
        //从服务器获取 订单详情;
        // 设置请求url和参数
        //获取手机的imei号;
        String imei = Utils.getID(this);
        //从首选项获取用户id;
        int qqLoginUserId = SPUtils.getInt(MainActivity.this, Contant.DUOKAI_LOGIN_USER_ID);
        String sign = MD5Utils.encode(qqLoginUserId + "duo3kai7shen5qi");
        Uri uri = Uri.parse(HttpConstant.AIBEIPAY).buildUpon()
                .appendQueryParameter("action", "LLAPPPAY")
                .appendQueryParameter("userid", qqLoginUserId + "")
                .appendQueryParameter("paytxt", "爱贝支付")
                .appendQueryParameter("imei", imei)
                .appendQueryParameter("sign", sign)
                .build();
        Request request = new Request.Builder()
                .get()
                .url(uri.toString())
                .build();
        mAiBeiPayCallnew = mOkHttpClient.newCall(request);
        mAiBeiPayCallnew.enqueue(mAiBeiPayCall);
    }

    Callback mAiBeiPayCall = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            //Log.i("test", "没有请求到数据:  " + e.getMessage());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Utils.toast(MainActivity.this, "下单失败,请稍后再试");
                }
            });
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String json = response.body().string();
            Gson gson = new Gson();
            AiBeiPayCategorey aiBeiPayCategorey = gson.fromJson(json, AiBeiPayCategorey.class);
            String msg = aiBeiPayCategorey.getMsg();
            mOutTradeNo = aiBeiPayCategorey.getOutTradeNo();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startIAppPay(mOutTradeNo);
                }
            });
        }
    };

    public void startIAppPay(String OutTradeNo) {
        String params = "transid=" + OutTradeNo + "&appid=3008011444";
        IAppPay.startPay(this, params, mPayCallback);
    }
    //爱呗支付;

    private IPayResultCallback mPayCallback = new IPayResultCallback() {
        public void onPayResult(int var1, String var2, String var3) {
            //Log.d("lele", "onPayResult: " + var1);
            //var1 == 0 ;代表爱贝支付成功;
            Message obtain = Message.obtain();
            obtain.what = AIBEIPAY;
            obtain.arg1 = var1;
            mHandler.sendMessage(obtain);

        }

    };

    //支付宝支付;
    private void aliPay() {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(MainActivity.this);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(mStrJson);
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();

    }

    //显示签到抢红包对话框;
    private void showOpenRedPacketDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.red_packet_dialog, null);
        final MyDialog builder = new MyDialog(MainActivity.this, 0, 0, dialogView, R.style.dialog);
        ImageView closeRedPacket = (ImageView) dialogView.findViewById(R.id.iv_open_packet_close);
        TextView shareAndOpen = (TextView) dialogView.findViewById(R.id.share_and_open_redpacket);
        closeRedPacket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        shareAndOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getNetShareWays();
                builder.dismiss();
            }
        });
        builder.show();
    }

    //获取服务器 分享的渠道;
    private void getNetShareWays() {
        //从首选项获取用户id;
        int qqLoginUserId = SPUtils.getInt(MainActivity.this, Contant.DUOKAI_LOGIN_USER_ID);
        Uri uri = Uri.parse(HttpConstant.SHAREWAY).buildUpon()
                .appendQueryParameter("userid", "" + qqLoginUserId)
                .build();
        Request request = new Request.Builder()
                .get()
                .url(uri.toString())
                .build();
        shareWayCallBack = mOkHttpClient.newCall(request);
        shareWayCallBack.enqueue(shareWayCall);
    }


    Callback shareWayCall = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            //Log.i("test", e.getMessage());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String json = response.body().string();
            Gson gson = new Gson();
            ShareWayCastegory shareWayCastegory = gson.fromJson(json, ShareWayCastegory.class);
            int type = shareWayCastegory.getType();
            //分享要用的代码;
            ShareWayCastegory.ShareBean share = shareWayCastegory.getShare();
            String pic = share.getPic();
            String title = share.getTitle();
            String txt = share.getTxt();
            String url = share.getUrl();
            //把从后台获取到的字符串传到分享渠道;
            mModel.setImageUrl(pic);
            mModel.setText(txt);
            mModel.setTitle(title);
            mModel.setUrl(url);
            //初始化参数;
            initShareParams(mModel);
//            weChatFriend();

            doShare(QQ.NAME);

            type = 10;
            if (type == 0) {
                //微信好友;
                doShare(Wechat.NAME);
            } else if (type == 1) {
                //微信朋友圈
                doShare(WechatMoments.NAME);
            } else if (type == 2) {
                //qq好友
                doShare(QQ.NAME);
            } else if (type == 3) {
                //qq空间
                doShare(QZone.NAME);
            }
            //Log.i("test", "json:  " + json + "分享方式: " + type);
        }
    };

    /**
     * 初始化分享参数
     *
     * @param shareModel
     */
    private Platform.ShareParams shareParams;

    public void initShareParams(ShareModel shareModel) {
        if (shareModel != null) {
            Platform.ShareParams sp = new Platform.ShareParams();
            sp.setShareType(Platform.SHARE_TEXT);
            sp.setShareType(Platform.SHARE_WEBPAGE);
            sp.setTitle(shareModel.getTitle());
            sp.setText(shareModel.getText());
            sp.setUrl(shareModel.getUrl());
            sp.setImageUrl(shareModel.getImageUrl());
            shareParams = sp;
        }
    }

    private void doShare(String platform) {
        Platform.ShareParams sp = new Platform.ShareParams();

        sp.setTitle(shareParams.getTitle());
        sp.setTitleUrl(shareParams.getUrl()); // 标题的超链接
        sp.setText(shareParams.getText());
        sp.setImageUrl(shareParams.getImageUrl());
        sp.setUrl(shareParams.getUrl());

        sp.setComment("我对此分享内容的评论");
        sp.setSite(shareParams.getTitle());
        sp.setSiteUrl(shareParams.getUrl());

        sp.setShareType(Platform.SHARE_WEBPAGE);

        Platform pf = ShareSDK.getPlatform(platform);
        pf.setPlatformActionListener(platformActionListener);
        pf.share(sp);
    }

    private PlatformActionListener platformActionListener = new PlatformActionListener() {
        @Override
        public void onCancel(Platform arg0, int arg1) {
            //Message msg = new Message();
            //msg.what = 0;
            //UIHandler.sendMessage(msg, MainActivity.this);
        }

        @Override
        public void onComplete(Platform plat, int action,
                               HashMap<String, Object> res) {
            //Message msg = new Message();
            //msg.arg1 = 1;
            //msg.arg2 = action;
            //msg.obj = plat;
            //UIHandler.sendMessage(msg, MainActivity.this);

            //Log.i("test", "Oncomplet: " + "分享成功了 ;");

            //分享成功 调用后台拆红包接口;
//            getNetOpenPacket();
            //分享成功后显示 判断要不要弹出抢更多福利对话框;
            isShowMoreGoods();

        }


        @Override
        public void onError(Platform arg0, int arg1, Throwable arg2) {
            //Message msg = new Message();
            //msg.what = 1;
            //UIHandler.sendMessage(msg, MainActivity.this);
        }
    };

    //难道这个方法是 分享成功后的回调方法;
    @Override
    public boolean handleMessage(Message msg) {
        int what = msg.what;
        if (what == 1) {
            Toast.makeText(this, "分享失败", Toast.LENGTH_SHORT).show();
        }
        //Log.i("test", "handleMessage: " + "测试看看 ");

        return false;
    }

    //,调用拆那种类型红包接口;
    private void getNetOpenPacket() {
        //获取当前日期;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());
        //从首选项获取用户id;
        int qqLoginUserId = SPUtils.getInt(MainActivity.this, Contant.DUOKAI_LOGIN_USER_ID);
        //加密;
        String sign = MD5Utils.encode(qqLoginUserId + date + "duo3kai7shen5qi");
        Uri uri = Uri.parse(HttpConstant.HOME).buildUpon()
                .appendQueryParameter("action", "chaihb")
                .appendQueryParameter("uid", "" + qqLoginUserId)
                .appendQueryParameter("date", "" + date)
                .appendQueryParameter("sign", "" + sign)
                .build();
        Request request = new Request.Builder()
                .get()
                .url(uri.toString())
                .build();
        mOpenPacketCall = mOkHttpClient.newCall(request);
        mOpenPacketCall.enqueue(openPacketCallBack);
    }

    Callback openPacketCallBack = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            //Log.i("test", e.getMessage());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String json = response.body().string();
            Gson gson = new Gson();

            OpenPacketCategory openPacketCategory = gson.fromJson(json, OpenPacketCategory.class);
            OpenPacketCategory.DataBean data = openPacketCategory.getData();
            mDaymoney = data.getDaymoney();
            int doaccess = data.getDoaccess();
            String errtxt = data.getErrtxt();
            if (errtxt == null || errtxt.isEmpty())
                errtxt = "未知错误,请稍后再试";
            //根doaccess 的返回值弹出不同的对框;
            ///Log.i("test", "daymoney:  " + daymoney + "doaccess:  " + doaccess + "errtxt:  " + errtxt);
            //showNoSamePacketDialog(daymoney, doaccess, errtxt);
            if (doaccess != 1) {
                final String finalErrtxt = errtxt;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utils.toast(MainActivity.this, finalErrtxt);
                    }
                });
                return;
            }

            if (mShowMoreServicesButton) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showGoodsDialog(mMoreServicesTxt, mMoreServicesUrl);
                    }
                });

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        noShowGoodsDialog();
                    }
                });

            }
        }
    };

    //已经拆开过了;
    private void openedPacketDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.opened_packet_dialog, null);
        final MyDialog builder = new MyDialog(MainActivity.this, 0, 0, dialogView, R.style.dialog);
        TextView tv_close_today_opened = (TextView) dialogView.findViewById(R.id.tv_close_today_opended);
        final TextView tv_accout_last_money = (TextView) dialogView.findViewById(R.id.tv_opened_packet);
        TextView tv_recored = (TextView) dialogView.findViewById(R.id.tv_record_dialog);
        TextView tv_withdraw = (TextView) dialogView.findViewById(R.id.tv_withdraw_dialog);
        tv_close_today_opened.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_accout_last_money.setText(mUmoney1+"元");
            }
        });
        tv_recored.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到余额详细界面;
                Intent intent = new Intent();
                //当前余额加上 ,现在奖励的钱;
                intent.putExtra("accout", mUmoney1);
                intent.setClass(MainActivity.this, AccoutLastActivity.class);
                startActivity(intent);
                builder.dismiss();
            }
        });
        tv_withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到提现界面;
                Intent intent = new Intent();
                intent.putExtra("accoutlast", mUmoney1);
                //Log.i("test", "多少钱; " + mUmoney1);
                intent.setClass(MainActivity.this, WithDrawActivity.class);
                startActivity(intent);
//                startActivity(new Intent(MainActivity.this, WithDrawActivity.class));
                builder.dismiss();
            }
        });
        builder.show();

    }

    //是否显示更多福利;
    private void isShowMoreGoods() {
        Request request = new Request.Builder()
                .get()
                .url(HttpConstant.ISSHOWGOODS)
                .build();
        isShowGoodsCall = mOkHttpClient.newCall(request);
        isShowGoodsCall.enqueue(isShowMoreGoodsCallBack);
    }

    private boolean mShowMoreServicesButton = false;
    private String mMoreServicesTxt;
    private String mMoreServicesUrl;

    Callback isShowMoreGoodsCallBack = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            //Log.i("test", e.getMessage());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String json = response.body().string();
            Gson gson = new Gson();
            IsShowGoodsCategory isShowGoodsCategory = gson.fromJson(json, IsShowGoodsCategory.class);
            int data = isShowGoodsCategory.getData();
            IsShowGoodsCategory.InfoBean info = isShowGoodsCategory.getInfo();
            mMoreServicesTxt = info.getTxt();
            mMoreServicesUrl = info.getUrl();
            //Log.i("test", "现不现实 那个按钮;" + data);
            if (data == 0)
                mShowMoreServicesButton = false;
            else
                mShowMoreServicesButton = true;
            //正真的拆红包;
            getNetOpenPacket();

        }
    };

    //显示商品dialog;
    private void showGoodsDialog(final String txt, final String url) {
        View dialogView = getLayoutInflater().inflate(R.layout.show_goods_dialog, null);
        final MyDialog builder = new MyDialog(MainActivity.this, 0, 0, dialogView, R.style.dialog);
        TextView tv_close_today = (TextView) dialogView.findViewById(R.id.tv_show_close_today);
        final TextView tv_show_dayMoney = (TextView) dialogView.findViewById(R.id.tv_show_daymoney);
        TextView tv_show_more_goods = (TextView) dialogView.findViewById(R.id.tv_more_goods);
        tv_show_more_goods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到webview页面;
                //传一个buddle到WebviewActivity
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("url", url);
                bundle.putString("title", txt);
                intent.putExtras(bundle);
                intent.setClass(MainActivity.this, WebViewActivity.class);
                startActivity(intent);
                builder.dismiss();
            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_show_dayMoney.setText(mDaymoney + "");
            }
        });
        tv_close_today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });

        builder.show();
    }

    //不显示更多福利对话框;
    private void noShowGoodsDialog() {

        View dialogView = getLayoutInflater().inflate(R.layout.noshow_goods_dialog, null);
        final MyDialog builder = new MyDialog(MainActivity.this, 0, 0, dialogView, R.style.dialog);
        TextView tv_close_today = (TextView) dialogView.findViewById(R.id.tv_close_today);
        final TextView tv_daymoney = (TextView) dialogView.findViewById(R.id.tv_daymoney);
        tv_close_today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_daymoney.setText(mDaymoney + "");
            }
        });
        builder.show();
    }

    //-------------------------------------显示登录对话框;--------------------------------------------------
    private void showLoginDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.login_dialog, null);
        final MyDialog builder = new MyDialog(MainActivity.this, 0, 0, dialogView, R.style.dialog);
        ImageView iv_close_dialog = (ImageView) dialogView.findViewById(R.id.iv_close_dialog);
        iv_close_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        LinearLayout ll_wechatLogin = (LinearLayout) dialogView.findViewById(R.id.ll_wechatlogin);
        LinearLayout ll_qqLogin = (LinearLayout) dialogView.findViewById(R.id.ll_qqlogin);
        TextView tv_call_customer = (TextView) dialogView.findViewById(R.id.tv_call_customer);
        tv_call_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpqqchatwindow();
                builder.dismiss();
            }
        });
        ll_wechatLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Utils.toast(MainActivity.this, "等待登录");
                weChatSdklogin();
                builder.dismiss();
            }
        });

        ll_qqLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.toast(MainActivity.this, "等待登录");
                qqSdklogin();
                builder.dismiss();
            }
        });
        builder.show();
    }

    //微信登录;
    private void weChatSdklogin() {
        Platform wechat = ShareSDK.getPlatform(this, Wechat.NAME);
        if (wechat.isValid())
            wechat.removeAccount();

        wechat.setPlatformActionListener(new PlatformActionListener() {

            public void onError(Platform platform, int action, Throwable t) {

            }

            public void onComplete(Platform platform, int action,
                                   HashMap<String, Object> res) {
                //登录成功提示用户;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utils.toast(MainActivity.this, "登录成功");
                    }
                });
                PlatformDb db = platform.getDb();
                //登录成功 保存到首选项;
                SPUtils.put(MainActivity.this, Contant.DUOKAILOGIN_SUCCESS, "loginsuccess");
                JSONObject obj = new JSONObject();
                //微信登录成功获取用户的信息;
                try {
                    String userId = db.getUserId();
                    String nickname = db.get("nickname");

                    obj.put("unionid", db.get("unionid"));
                    obj.put("openid", userId);
                    obj.put("nickname", URLEncoder.encode(nickname));//db.get("nickname"));
                    obj.put("headimgurl", db.get("icon")); //sharesdk把这个改了
                    obj.put("sex", db.get("gender")); //sharesdk把这个改了

                    obj.put("parentid", Utils.getParentID(MainActivity.this));

                    weChatLoginCallBack(obj.toString());
//                    obj.put("unionid", db.get("unionid"));
//                    obj.put("city", db.get("city"));
//                    obj.put("province", db.get("province"));
//                    obj.put("country", db.get("country"));
//                    obj.put("language", ""); //sharesdk没有保存这个

                } catch (JSONException ex) {

                }
            }

            public void onCancel(Platform platform, int action) {

            }
        });
        wechat.authorize();
    }

    //微信登录的获取后台返回的数据;
    private void weChatLoginCallBack(String encode) {
        //获取手机的imei号;
        String imei = Utils.getID(this);
        //Log.i("test", "手机唯一标示: " + imei);
        //对imei进行MD5处理: sign   加密参数 加密固定值：duo3kai7shen5qi    md5(imei."duo3kai7shen5qi");
        String sign = MD5Utils.encode(imei + "duo3kai7shen5qi");
        //Log.i("test", "imei:  " + imei + "encode: " + encode + "sign:  " + sign);
        // 设置请求url和参数
        //http://api.duokaishenqi.com/user/?action=qqlogin&params=xxx&imei=xxx&sign=xxx
        Uri uri = Uri.parse(HttpConstant.USER).buildUpon()
                .appendQueryParameter("action", "wxlogin")
                .appendQueryParameter("params", encode)
                .appendQueryParameter("imei", imei)
                .appendQueryParameter("sign", sign)
                .build();
        Request request = new Request.Builder()
                .get()
                .url(uri.toString())
                .build();

        mWechatCall = mOkHttpClient.newCall(request);
        mWechatCall.enqueue(mWechatCallBack);
    }

    Callback mWechatCallBack = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            //Log.i("test", e.getMessage());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String json = response.body().string();
            Gson gson = new Gson();
            WechatLoginCategory wechatLoginCategory = gson.fromJson(json, WechatLoginCategory.class);
            WechatLoginCategory.DataBean data = wechatLoginCategory.getData();
            //用户id
            int userid = data.getUserid();
            //把用户id保存到首选项,为了方便其他接口的需要;,
            SPUtils.put(MainActivity.this, Contant.DUOKAI_LOGIN_USER_ID, userid);
            //是否登录成功;
            int doaccess = data.getDoaccess();
            //Log.i("test", "微信登录: 返回的userid: " + userid + "doaccess: " + doaccess);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //qq登陆成功后,账号余额显示;
                    getLastMoney();
                }
            });
        }
    };

    //qq聊天回话窗口;
    private void jumpqqchatwindow() {
        boolean isExist = checkApkExist(MainActivity.this, "com.tencent.mobileqq");
        if (isExist) {
            String url = "mqqwpa://im/chat?chat_type=wpa&uin=2580163370";
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }catch (Exception ex) {
                Utils.toast(MainActivity.this, "请先安装腾讯qq");
            }
        } else {
            Utils.toast(MainActivity.this, "请先安装腾讯qq");
            return;
        }
    }

    private void qqSdklogin() {
        Platform qqPlat = ShareSDK.getPlatform(this, QQ.NAME);
        if (qqPlat.isValid())
            qqPlat.removeAccount();

        qqPlat.setPlatformActionListener(new PlatformActionListener() {

            public void onError(Platform platform, int action, Throwable t) {
                //sendLoginMessage(handler, WHAT_WX_LOGIN_FAILED, null);
                //Log.i("test", "qqSdklogin error");
            }

            //qq登录成功;
            public void onComplete(Platform platform, int action,
                                   HashMap<String, Object> res) {
                //登录成功提示用户;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utils.toast(MainActivity.this, "登录成功");
                    }
                });
                //保存登录成功的记录;
                SPUtils.put(MainActivity.this, Contant.DUOKAILOGIN_SUCCESS, "loginsuccess");
                PlatformDb db = platform.getDb();

                JSONObject obj = new JSONObject();
                try {
                    String userid = db.getUserId();
                    //把用户id保存到首选项,为了方便其他接口的需要;,
                    //SPUtils.put(MainActivity.this, "qqLoginUserId", userid);
                    //qq登陆成功后,账号余额显示;
                    //getLastMoney();

                    String nickname = db.get("nickname");
                    //obj.put("unionid", db.get("unionid"));
                    obj.put("openid", userid);//跟后台确认
                    obj.put("nickname", URLEncoder.encode(nickname));
                    obj.put("figureurl", db.get("icon")); //sharesdk把这个改了
                    obj.put("sex", db.get("gender")); //sharesdk把这个改了
                    obj.put("parentid", Utils.getParentID(MainActivity.this));
                    //obj.put("city",db.get("city"));
                    //obj.put("province", db.get("province"));
                    //obj.put("country",db.get("country"));
                    //obj.put("language",""); //sharesdk没有保存这个
                    qqLoginCallBack(obj.toString());
                } catch (JSONException ex) {

                }

            }

            public void onCancel(Platform platform, int action) {
                //sendLoginMessage(handler, WHAT_WX_LOGIN_CANCELED, null);
                //Log.i("test", "qqSdklogin cancel");
            }

        });
        qqPlat.authorize();

    }

    //-----------------------------qq登录后请求服务器,返回字符串-----------------------------------------------------------
    private void qqLoginCallBack(String encode) {
        //Log.i("test", "qq登录: @@@@@@@@@@@@@@@@@@@@@@" + encode);
        //获取手机的imei号;
        String imei = Utils.getID(this);
        //Log.i("test", "手机唯一标示: " + imei);
        //对imei进行MD5处理: sign   加密参数 加密固定值：duo3kai7shen5qi    md5(imei."duo3kai7shen5qi");
        String sign = MD5Utils.encode(imei + "duo3kai7shen5qi");
        // 设置请求url和参数
        //http://api.duokaishenqi.com/user/?action=qqlogin&params=xxx&imei=xxx&sign=xxx
        Uri uri = Uri.parse(HttpConstant.USER).buildUpon()
                .appendQueryParameter("action", "qqlogin")
                .appendQueryParameter("params", encode)
                .appendQueryParameter("imei", imei)
                .appendQueryParameter("sign", sign)
                .build();
        Request request = new Request.Builder()
                .get()
                .url(uri.toString())
                .build();

        mCall = mOkHttpClient.newCall(request);
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
            Gson gson = new Gson();
            QqLoginCategory qqOrWechatLoginCategory = gson.fromJson(json, QqLoginCategory.class);
            QqLoginCategory.DataBean data = qqOrWechatLoginCategory.getData();
            //用户id
            int userid = data.getUserid();
            //把用户id保存到首选项,为了方便其他接口的需要;,
            SPUtils.put(MainActivity.this, Contant.DUOKAI_LOGIN_USER_ID, userid);
            //是否登录成功;
            int doaccess = data.getDoaccess();
            mImg = (String) data.getImg();
            //Log.i("test", "useid: " + userid + "doaccess: " + doaccess + "img: " + mImg);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    qqOrweixinLoginshow();
                    //qq登陆成功后,账号余额显示;
                    getLastMoney();
                }
            });
        }
    };

    //qq登陆成功后 获取余额和邀请好友数量;
    private void getLastMoney() {
        //从首选项获取用户id;
        int qqLoginUserId = SPUtils.getInt(MainActivity.this, Contant.DUOKAI_LOGIN_USER_ID);
        String sign = MD5Utils.encode(qqLoginUserId + "duo3kai7shen5qi");
        Uri uri = Uri.parse(HttpConstant.HOME).buildUpon()
                .appendQueryParameter("action", "home")
                .appendQueryParameter("uid", "" + qqLoginUserId)
                .appendQueryParameter("sign", sign)
                .build();
        //"http://api.duokaishenqi.com/home/?action=home&uid=" + qqLoginUserId + "&sign" + sign
        Request request = new Request.Builder()
                .get()
                .url(uri.toString())
                .build();
        //Log.i("test", "qqLoginUserId: " + qqLoginUserId + "sign: " + sign);
//        qqLoginUserId: 2sign: 614368e366cbb9c8d20eacfdaa4f2557
        mOkHttpClient = new OkHttpClient();
        mCall1 = mOkHttpClient.newCall(request);
        mCall1.enqueue(mCallback1);
    }

    Callback mCallback1 = new Callback() {


        @Override
        public void onFailure(Call call, IOException e) {
            //Log.i("test", e.getMessage());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String json = response.body().string();
            Gson gson = new Gson();
            AccoutMoneyCategory accoutMoneyCategory = gson.fromJson(json, AccoutMoneyCategory.class);
//            mInvitenum1 = accoutMoneyCategory.getInvitenum();
            mInvitenum = accoutMoneyCategory.getInvitenum();
            mUmoney1 = accoutMoneyCategory.getUmoney();
            //Log.i("test", "onResponse: 进来没" + mInvitenum1 + "  : " + mUmoney1);
            //Log.i("test", "json:  " + json);
        }
    };

    public boolean checkApkExist(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager()
                    .getApplicationInfo(packageName,
                            PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShareSDK.stopSDK(this);
        if (mCall != null) {
            mCall.cancel();
        }
        if (mCall1 != null) {
            mCall1.cancel();
        }
        if (mAliPayCall != null) {
            mAliPayCall.cancel();
        }
        if (mAiBeiPayCallnew != null) {
            mAiBeiPayCallnew.cancel();
        }
        if (mPayWayallnew != null) {
            mPayWayallnew.cancel();
        }
        if (shareWayCallBack != null) {
            shareWayCallBack.cancel();
        }
        if (mWechatCall != null) {
            mWechatCall.cancel();
        }
        if (mOpenPacketCall != null) {
            mOpenPacketCall.cancel();
        }
        if (mInviteFriendCall != null) {
            mInviteFriendCall.cancel();
        }
        if (mCheckOpenPacketCall != null) {
            mCheckOpenPacketCall.cancel();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //判断是否有网络;
        if (!Utils.checkNetwork(this)) {
            noNet();
        }
        MobclickAgent.onResume(this);
        //联网检测 更新;
//        CheckVersonUpdate.checkUpdate(this, mHandler);
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //友盟统计;
        MobclickAgent.onPause(this);
        //极光推送  用到的;
        JPushInterface.onPause(this);
    }

    //点击两次返回键 退出运用程序;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isExit) {
                isExit = true;
                Utils.toast(MainActivity.this, "再按一次退出应用");
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        isExit = false;
                    }
                }, 2000);

            } else {
                finish();
            }
        }
        return false;
    }
}
