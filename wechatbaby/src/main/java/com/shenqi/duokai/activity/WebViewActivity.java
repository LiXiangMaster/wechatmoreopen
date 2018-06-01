package com.shenqi.duokai.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

//import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.shenqi.duokai.R;
import com.shenqi.duokai.utils.Utils;


public class WebViewActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener {
//    private SystemBarTintManager tintManager;

    private String mUrl;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private SystemBarTintManager mTintManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && !bundle.isEmpty()) {
            TextView webViewBar = (TextView) findViewById(R.id.wv_title);
            webViewBar.setText(bundle.getString("title"));
            mUrl = bundle.getString("url");
        }
        initStatusBar();
       /* TextView webViewBar = (TextView) findViewById(R.id.wv_title);
        webViewBar.setText("jjj");

        mUrl = "http://www.163.com";
*/
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        WebView webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                //Utils.showShortToast(view.getContext(), message);
                result.cancel();
                return true;
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    //mProgressbar.setVisibility(View.GONE);
                } else {
                    //if (mProgressbar.getVisibility() == View.GONE)
                    //    mProgressbar.setVisibility(View.VISIBLE);
                    //mProgressbar.setProgress(newProgress);
                }
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mSwipeRefreshLayout.setRefreshing(false);

                CookieSyncManager.getInstance().sync();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                view.stopLoading();
                //view.loadUrl(NO_WIFI_URL);

                //if (mListener != null) {
                //   mListener.getError(view, errorCode, description, failingUrl);
                //}
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });
        webView.addJavascriptInterface(new WebViewJSCallback(), "android");

        webView.loadUrl(mUrl);
    }

    @TargetApi(19)
    private void initStatusBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            mTintManager = new SystemBarTintManager(this);
            mTintManager.setStatusBarTintColor(getResources().getColor(R.color.app_main_color));
            mTintManager.setStatusBarTintEnabled(true);
        }
    }

    public void onButtonBack(View view) {
        finish();
    }


    @Override
    //OnRefreshListener method, called by SwipeRefreshLayout
    public void onRefresh() {
        WebView webView = (WebView) findViewById(R.id.webview);
        webView.loadUrl(mUrl);

    }

    private class WebViewJSCallback {
        @JavascriptInterface
        public void openqqservice(String uid) {
            implOpenQQ(uid);
        }

        @JavascriptInterface
        public void openQQGroup(String key) {
            implOpenQQGroup(key);
        }
    }

    private void implOpenQQ(String uid) {
        String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + uid;

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        try {
            startActivity(intent);
        } catch (Exception e) {
            //未安装手Q或安装的版本不支持
            Utils.toast(this, "请检查QQ是否安装并已登录");
        }
    }

    private void implOpenQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            Utils.toast(this, "请检查QQ是否安装,登录");
        }
    }
}
