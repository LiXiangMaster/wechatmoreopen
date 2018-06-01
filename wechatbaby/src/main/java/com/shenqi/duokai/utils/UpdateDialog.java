package com.shenqi.duokai.utils;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.shenqi.duokai.R;
import com.tencent.open.utils.Util;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

public class UpdateDialog extends Dialog implements
        View.OnClickListener {
    private static final int DOWN_NOSDCARD = 0;
    private static final int DOWN_PROGRESS = 1;
    private static final int DOWN_FINISHED = 2;
    private static final int DOWN_ERROR = 3;
    private static final int DOWN_CANCELED = 4;//未实现

    private static final int BAD_PACKAGE = 5;//无法取到包名
    private static final int WRONG_PACKAGE_NAME = 6; //包名不对
    private static final int WRONG_SIGNATURE = 7;//md5值不对

    public Activity mActivity;
    private CheckVersonUpdate.UpdateData mData;
    private ProgressBar mProgressBar;
    private int mProgressStatus = 0;

    //apk保存完整路径
    String mApkFilePath = "";
    private boolean mInterceptFlag = false; //取消下载, 未实现

    public UpdateDialog(Activity a, CheckVersonUpdate.UpdateData data) {
        super(a);
        this.mActivity = a;
        mData = data;
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_PROGRESS:
                    mProgressBar.setProgress(mProgressStatus);

                    TextView textView = (TextView)findViewById(R.id.text_progress);
                    textView.setText(mProgressStatus + "%");
                    break;
                case DOWN_FINISHED:
                    UpdateDialog.this.dismiss();
                    installApk();
                    break;
                case DOWN_NOSDCARD:
                    UpdateDialog.this.dismiss();
                    Toast.makeText(mActivity, "无法下载安装文件，请检查SD卡是否挂载", Toast.LENGTH_LONG).show();
                    break;
                case DOWN_ERROR:
                    UpdateDialog.this.dismiss();
                    Toast.makeText(mActivity, "下载失败, 请稍后再试", Toast.LENGTH_LONG).show();
                    break;
                case BAD_PACKAGE:
                    UpdateDialog.this.dismiss();
                    Toast.makeText(mActivity, "安装包已损坏，请重新升级", Toast.LENGTH_LONG).show();
                    break;
                case WRONG_PACKAGE_NAME:
                    UpdateDialog.this.dismiss();
                    Toast.makeText(mActivity, "安装包错误，您可能遭到DNS劫持，请稍后再重新升级", Toast.LENGTH_LONG).show();
                    break;
                case WRONG_SIGNATURE:
                    UpdateDialog.this.dismiss();
                    Toast.makeText(mActivity, "安装包错误，请重新升级", Toast.LENGTH_LONG).show();
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.update_dialog);
        //必须有这句, 否则没有圆角效果
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        //按空白区域不取消
        setCanceledOnTouchOutside(false);

        TextView title = (TextView)findViewById(R.id.dialog_title);
        title.setText(mData.title);

        TextView text = (TextView)findViewById(R.id.dialog_content);
        text.setText(mData.text);

        FrameLayout frame = (FrameLayout) findViewById(R.id.buttons_frame);
        frame.removeAllViews();

        if (mData.forceUpdate) {
            setCancelable(false);//后退按钮不关闭对话框
            LayoutInflater.from(mActivity).inflate(R.layout.update_forced_buttons, frame, true);
        }else
            LayoutInflater.from(mActivity).inflate(R.layout.update_buttons, frame, true);

        Button yes = (Button) findViewById(R.id.btn_yes);
        if (yes != null)
            yes.setOnClickListener(this);

        Button no = (Button) findViewById(R.id.btn_no);
        if (no != null)
            no.setOnClickListener(this);

    }

    private static String APPNAME = "ddqhb";
    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                //下载包保存路径
                String savePath = "";
                //临时下载文件路径
                String tmpFilePath = "";
                //下载文件大小
                String apkFileSize;
                //已下载文件大小
                String tmpFileSize;

                String apkName = APPNAME + "_" + mData.versionName + ".apk";
                String tmpApk = APPNAME + "_" +  mData.versionName + ".tmp";
                //判断是否挂载了SD卡
                String storageState = Environment.getExternalStorageState();
                if(storageState.equals(Environment.MEDIA_MOUNTED)){
                    savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + APPNAME + "/Update/";
                    File file = new File(savePath);
                    if(!file.exists()){
                        file.mkdirs();
                    }
                    mApkFilePath = savePath + apkName;
                    tmpFilePath = savePath + tmpApk;
                }

                //没有挂载SD卡，无法下载文件
                if(mApkFilePath == null || mApkFilePath == ""){
                    mHandler.sendEmptyMessage(DOWN_NOSDCARD);
                    return;
                }

                File ApkFile = new File(mApkFilePath);

                //是否已下载更新文件
                if(ApkFile.exists()){
                    ApkFile.delete(); //总是重新下载
                    //mHandler.sendEmptyMessage(DOWN_FINISHED);
                    //return;
                }

                //输出临时下载文件
                File tmpFile = new File(tmpFilePath);
                if(tmpFile.exists()){
                    tmpFile.delete();
                }
                FileOutputStream fos = new FileOutputStream(tmpFile);

                URL url = new URL(mData.urlApk);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream is = conn.getInputStream();

                //显示文件大小格式：2个小数点显示
                DecimalFormat df = new DecimalFormat("0.00");
                //进度条下面显示的总文件大小
                apkFileSize = df.format((float) length / 1024 / 1024) + "MB";

                int count = 0;
                byte buf[] = new byte[1024];

                do{
                    int numread = is.read(buf);
                    count += numread;
                    //进度条下面显示的当前下载文件大小
                    tmpFileSize = df.format((float) count / 1024 / 1024) + "MB";
                    //当前进度值
                    mProgressStatus =(int)(((float)count / length) * 100);
                    //更新进度
                    mHandler.sendEmptyMessage(DOWN_PROGRESS);
                    if(numread <= 0){
                        //下载完成 - 将临时下载文件转成APK文件
                        if(tmpFile.renameTo(ApkFile)){
                            //检查包名及md5值, 因为比较耗时, 应该在线程中做
                            String strPackage = Utils.getPackageNameFromFile(mActivity, mApkFilePath);
                            if (strPackage==null || strPackage.isEmpty()) {//取不到包名
                                mHandler.sendEmptyMessage(BAD_PACKAGE);
                                break;
                            }

                            if (mData.signature!=null && !mData.signature.isEmpty()) {
                                //检查md5值
                                if (!Utils.checkMD5(mData.signature, new File(mApkFilePath))) {
                                    mHandler.sendEmptyMessage(WRONG_SIGNATURE);
                                    break;
                                }
                            }

                            //通知安装
                            mHandler.sendEmptyMessage(DOWN_FINISHED);
                        }
                        break;
                    }
                    fos.write(buf,0,numread);
                }while(!mInterceptFlag);//点击取消就停止下载

                fos.close();
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();

                mHandler.sendEmptyMessage(DOWN_ERROR);
            } catch(IOException e){
                e.printStackTrace();

                mHandler.sendEmptyMessage(DOWN_ERROR);
            }
        }
    };

    private void startDownload() {
        FrameLayout frame = (FrameLayout) findViewById(R.id.buttons_frame);
        frame.removeAllViews();
        LayoutInflater.from(mActivity).inflate(R.layout.update_downloading, frame, true);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mProgressStatus = 0;

        new Thread(mdownApkRunnable).start();
    }

    public void startUpdate() {
        if (storagePermitted(mActivity)) {
            startDownload();
        }else {
            //Utils.toast(mActivity, "下载升级包需要允许");
            //requestStoragePermission(mActivity);
        }
    }

    private static boolean storagePermitted(Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&

                ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)

            return true;

        return false;

    }

     @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                startUpdate();
                break;
            case R.id.btn_no:
                dismiss();
                break;
            default:
                break;
        }

    }

    private void installApk(){
        File apkfile = new File(mApkFilePath);
        if (!apkfile.exists()) {
            return;
        }

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mActivity.startActivity(i);
    }
}
