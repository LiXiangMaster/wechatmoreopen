package com.shenqi.duokai.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.shenqi.duokai.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import kellinwood.security.zipsigner.ProgressEvent;
import kellinwood.security.zipsigner.ProgressListener;
import kellinwood.security.zipsigner.ZipSigner;
import kellinwood.zipio.ZioEntry;
import kellinwood.zipio.ZipInput;
import kellinwood.zipio.ZipOutput;

public class ApkMaker {
    private static String MANIFEST_NAME = "AndroidManifest.xml";
    private static String TARGET_FILE_NAME = "assets/target";
    private static String VERIFY_FILE_NAME = "assets/verify";
    private static final char[] HEX = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70};


    private static ApkMaker _instance;

    private Context mContext;
    private ApkMakerCallback mCallback;

    private int mSequenceNumber;
    private String mTargetPackageName;
    private String mLabel;
    private byte[] mIcoData;

    private File mCacheDir;
    private InputStream mTemplateAsset;

    private boolean mRunning;

    private ApkMaker() {

    }

    public static ApkMaker get() {
        if (_instance == null) {
            _instance = new ApkMaker();
        }
        return _instance;
    }

    public interface ApkMakerCallback {
        void onError(String msg);
        void onProgress(int percent);
        void onSucceed(String path);
    }

    public void startMake(Activity context, String targetPackageName, String label, ApkMakerCallback callback) {
        mContext = context;
        mCallback = callback;
        mSequenceNumber = FindNextAvailableSlot(context);
        mTargetPackageName = targetPackageName;
        mLabel = label;

        if (mSequenceNumber<=0) {
            Utils.toast(mContext, "分身数超过限制");
            return;
        }

        mIcoData = getIconData(context, targetPackageName);
        if (mIcoData == null) {
            Utils.toast(mContext, "无法获取应用图标");
            return;
        }

        mCacheDir = context.getCacheDir();
        if (mCacheDir==null || !mCacheDir.exists()) {
            Utils.toast(mContext, "没有临时目录");
            return;
        }

        try {
            mTemplateAsset = context.getAssets().open("template");
        } catch (IOException ex) {
            Utils.toast(mContext, "应用异常,请重新安装本应用");
            return;
        }

        new Thread(mMakeRunnable).start();
    }



    private byte[] getIconData(Context context, String packageName) {
        Drawable ico = null;
        try {
            ico = context.getPackageManager().getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException ex) {
            return null;
        }

        Bitmap bitmap = ((BitmapDrawable) ico).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public boolean isRunning() {
        return mRunning;
    }

    private void reportError(String str) {
        mCallback.onError(str);
    }

    private ProgressListener mSignListerner = new ProgressListener() {
        @Override
        public void onProgress(ProgressEvent progressEvent) {
            //int n = progressEvent.getPercentDone();
            //Log.d("DUOKAI", "percent: " + n);
        }
    };

    private Runnable mMakeRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                mRunning = true;

                File template = new File(mCacheDir, "template");

                if (!copyInputStreamToFile(mTemplateAsset, template)) {
                    reportError("解压模板出错");
                    return;
                }

                mCallback.onProgress(10);

                mTemplateAsset = null;
                File filesDir = null;
                File tempAPK = null;
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    String strTemp = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DuoKai";
                    filesDir = new File(strTemp);
                } else {
                    // sdcard不存在;
                    filesDir = mContext.getFilesDir();
                }
                if (!filesDir.exists()) {
                    if (!filesDir.mkdir()) {
                        reportError("创建存储目录失败");
                        return;
                    }
                }

                mCallback.onProgress(15);

                tempAPK = new File(filesDir, "temp.apk");

                if (tempAPK.exists())
                    tempAPK.delete();

                try {
                    makeNewAPK(template, tempAPK);
                }catch (Exception ex) {
                    reportError("制作APK出错");
                    return;
                }

                for (int k=20; k<80; k+=5) {
                    mCallback.onProgress(k);
                    Thread.sleep(200);
                }

                //File outFile = new File(mCacheDir, "dksq.apk");
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
                Date date =new java.util.Date();
                String strDate = sdf.format(date);

                final File outFile = new File(filesDir, "dksq_"+strDate + ".apk");
                Random r = new Random();
                int i1 = r.nextInt(95 - 80) + 80;
                mCallback.onProgress(i1);

                //签名
                ZipSigner signer = new ZipSigner();
                signer.addAutoKeyObserver(new Observer() {
                    @Override
                    public void update(Observable observable, Object o) {
                        //System.out.println("Signing with key: " + o);
                    }
                });
                signer.setKeymode("auto-testkey");

                //签名
                try {
                    signer.addProgressListener(mSignListerner);
                    signer.signZip(tempAPK.getCanonicalPath(), outFile.getCanonicalPath());
                }catch (Exception ex) {
                    reportError("签名出错");
                    return;
                }

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            mCallback.onProgress(100);
                            mCallback.onSucceed(outFile.getCanonicalPath());
                        }catch (IOException ex) {

                        }
                    }
                }, 1000);

            } catch (Throwable t) {
                reportError("制作APK异常");
            } finally {
                mRunning = false;
            }
        }
    };

    private void makeNewAPK(File templateFile, File outputFile)
            throws IOException, GeneralSecurityException {
        ZipInput input = null;
        OutputStream outStream = null;
        ZipOutput zipOutput = null;

        try {

            input = ZipInput.read(templateFile.getCanonicalPath());
            outStream = new FileOutputStream(outputFile);
            // processEntries(input.getEntries(), outStream, outputZipFilename);

            zipOutput = new ZipOutput(outStream);

            for (ZioEntry inEntry : input.getEntries().values()) {
                if (inEntry.getName().equals(MANIFEST_NAME)) {
                    zipOutput.write(newManifestEntry(inEntry.getData(), mSequenceNumber, mLabel));


                } else if (inEntry.getName().equals(TARGET_FILE_NAME)) {
                    zipOutput.write(newTargetFileEntry(mTargetPackageName));

                } else if (inEntry.getName().equals(VERIFY_FILE_NAME)) {
                    zipOutput.write(newVerifyFileEntry(mTargetPackageName));


                } else if (inEntry.getName().equals("res/drawable-xxhdpi-v4/ic_launcher.png")
                        || inEntry.getName().equals("res/drawable-xhdpi-v4/ic_launcher.png")
                        || inEntry.getName().equals("res/drawable-hdpi-v4/ic_launcher.png")
                        || inEntry.getName().equals("res/drawable-mdpi-v4/ic_launcher.png")) {
                    zipOutput.write(newLaunchIconEntry(inEntry.getName(), mIcoData));

                } else {
                    zipOutput.write(inEntry);

                }
            }
        } finally {
            if (zipOutput != null) zipOutput.close();

            if (input != null) input.close();
            if (outStream != null) outStream.close();

        }
    }

    private static ZioEntry newManifestEntry(byte[] oldData, int number, String label) {
        byte[] newData = ManifestEditor.ModifyAPKPackageName(oldData, number, label);

        ZioEntry ze = new ZioEntry(MANIFEST_NAME);

        try {
            ze.getOutputStream().write(newData);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return ze;
    }

    private static ZioEntry newLaunchIconEntry(String name, byte[] data) {
        ZioEntry ze = new ZioEntry(name);

        try {
            ze.getOutputStream().write(data);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return ze;
    }

    private static ZioEntry newTargetFileEntry(String targetPackage) {
        ZioEntry ze = new ZioEntry(TARGET_FILE_NAME);

        try {
            String str = "1 " + targetPackage;

            ze.getOutputStream().write(str.getBytes("UTF-8"));
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return ze;
    }

    public static String mymd5(byte[] paramArrayOfByte) {
        try {
            paramArrayOfByte = MessageDigest.getInstance("MD5").digest(paramArrayOfByte);
            StringBuilder localStringBuilder = new StringBuilder();
            int i = 0;
            while (true) {
                if (i >= paramArrayOfByte.length)
                    return localStringBuilder.toString();
                int j = paramArrayOfByte[i];
                localStringBuilder.append(HEX[((j & 0xF0) >> 4)]);
                localStringBuilder.append(HEX[(j & 0xF)]);
                i += 1;
            }
        } catch (NoSuchAlgorithmException ex) {
        }
        return null;
    }

    private static ZioEntry newVerifyFileEntry(String targetPackage) {
        ZioEntry ze = new ZioEntry(VERIFY_FILE_NAME);

        try {
            String str = mymd5((Build.BOARD + Build.BRAND + Build.PRODUCT + Build.MODEL + Build.SERIAL).getBytes());

            ze.getOutputStream().write(str.getBytes("UTF-8"));
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return ze;
    }

    private boolean copyInputStreamToFile(InputStream in, File file) {
        boolean ret = true;

        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            ret = false;
        }

        return ret;
    }

    public int FindNextAvailableSlot(Context context) {
        PackageManager pm = context.getPackageManager();

        int i;
        for (i=1; i<100; i++) {
            String packageName = "com.shenqi.pluginstub" + String.format("%02d", i);
            if (!isPackageInstalled(pm, packageName))
                return i;
        }

        return -1;
    }

    public static boolean isPackageInstalled(PackageManager pm, String targetPackage){
        boolean ret = true;
        try {
            PackageInfo info= pm.getPackageInfo(targetPackage, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            ret =  false;
        }
        return ret;
    }


}
