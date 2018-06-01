package com.shenqi.duokai.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.shenqi.duokai.constants.HttpConstant;
import com.shenqi.duokai.interf.OnDownloadListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Utils {

    protected static final int DOWNLOAD_SUCCESS = 0;
    protected static final int DOWNLOAD_FAILED = 1;


    /**
     * 启动Activity方法抽取
     *
     * @param context
     * @param clazz
     */
    public static void startActivity(Context context, Class clazz) {
        context.startActivity(new Intent(context, clazz));
    }

    /**
     * 判断文件是否存在于SD卡
     *
     * @param fileName
     * @return
     */
    public static boolean existsInSd(String fileName) {
        return new File(Environment.getExternalStorageDirectory(), fileName).exists();
    }

    /**
     * 封装toast方法
     *
     * @param context
     * @param text
     */
    public static void toast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    //检查是否有网络;
    public static boolean checkNetwork(Context context) {
        //通过ConnectivityManager来获取网络状态NetworkInfo
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null) {
            return false;
        }

        int type = networkInfo.getType();

        //数据网络TYPE_MOBILE  wifi网络TYPE_WIFI
        if (type == ConnectivityManager.TYPE_MOBILE || type == ConnectivityManager.TYPE_WIFI) {
            return true;
        }

        return false;
    }


    /**
     * 通用的下载方法
     *
     * @param url
     * @param destFile
     */
    public static void download(final String url, final File destFile, final OnDownloadListener listener) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case DOWNLOAD_SUCCESS:
                        // 如果传递Activity引用过来 耦合度过高  扩展性低
                        // 接口可以降低耦合度
                        // 定义规范  就是实现我的接口,必须重写抽象方法    实现了该接口的类 一定具备接口中的方法
                        if (listener != null) {
                            listener.onDownloadSuccess(destFile);
                        }
                        break;
                    case DOWNLOAD_FAILED:
                        if (listener != null) {
                            listener.onDownlaodFailed();
                        }
                        break;

                    default:
                        break;
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                super.run();
                InputStream is = null;
                OutputStream os = null;
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(url);
                HttpParams params = get.getParams();
                params.setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, HttpConstant.HTTP_TIMEOUT);
                params.setParameter(HttpConnectionParams.SO_TIMEOUT, HttpConstant.HTTP_TIMEOUT);
                get.setParams(params);
                try {
                    HttpResponse response = client.execute(get);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        is = response.getEntity().getContent();
                        os = new FileOutputStream(destFile);
                        int len = -1;
                        byte[] buffer = new byte[1024];
                        while ((len = is.read(buffer)) != -1) {
                            os.write(buffer, 0, len);
                            os.flush();
                        }
                        //下载成功
                        handler.sendEmptyMessage(DOWNLOAD_SUCCESS);
                    }
                } catch (Exception e) {
                    //下载失败
                    handler.sendEmptyMessage(DOWNLOAD_FAILED);
                } finally {
                    // 只用关闭自己new出来的流
                    // 所有get出来的流都不需要关闭
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException e) {
                            handler.sendEmptyMessage(DOWNLOAD_FAILED);
                        }
                    }
                }
            }
        }.start();
    }

    //jj
    private static String CHANNEL = null;
    public static String getParentID(Context context) {
        if (CHANNEL != null) {
            return CHANNEL;
        }

        ApplicationInfo appinfo = context.getApplicationInfo();
        String sourceDir = appinfo.sourceDir;
        String ret = "";
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(sourceDir);
            Enumeration<?> entries = zipfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String entryName = entry.getName();
                //LogUtils.d("-----sourceDir:::" + entryName);
                if (entryName.contains("channel")) {
                    //LogUtils.d("-----sourceDir:::-----" + entryName);
                    ret = entryName;
                    break;
                }
            }
        } catch (IOException e) {
            //LogUtils.e(e.getMessage());
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException e) {
                    //LogUtils.e(e.getMessage());
                }
            }
        }

        if (ret.isEmpty()) {
            CHANNEL = "0";
        } else {
            CHANNEL = ret.substring(ret.indexOf("_") + 1, ret.length());
        }

        return CHANNEL;
    }

    //获取设备id;
    private static String ID = null;
    // return a cached unique ID for each device
    public static String getID(Context context) {
        // if the ID isn't cached inside the class itself
        if (ID == null) {
            //get it from database / settings table (implement your own method here)
//            ID = PreferencesUtils.getString(context, "DuoduoDeviceID");
            ID=SPUtils.getString(context,"DuoKaiID");
        }

        // if the saved value was incorrect
        if (ID==null || ID.isEmpty()) {
            // generate a new ID
            ID = generateID(context);

            if (ID != null) {
                // save it to database / setting (implement your own method here)
//                PreferencesUtils.putString(context, "DuoduoDeviceID", ID);
                SPUtils.put(context,"DuoKaiID",ID);

            }
        }

        return ID;
    }

    // generate a unique ID for each device
    // use available schemes if possible / generate a random signature instead
    private static String generateID(Context context) {

        // use the ANDROID_ID constant, generated at the first device boot
        String deviceId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        // in case known problems are occured
        if ("9774d56d682e549c".equals(deviceId) || deviceId == null) {

            // get a unique deviceID like IMEI for GSM or ESN for CDMA phones
            // don't forget:
            //
            deviceId = ((TelephonyManager) context
                    .getSystemService( Context.TELEPHONY_SERVICE ))
                    .getDeviceId();

            // if nothing else works, generate a random number
            if (deviceId == null) {

                Random tmpRand = new Random();
                deviceId = String.valueOf(tmpRand.nextLong());
            }

        }

        // any value is hashed to have consistent format
        return getHash(deviceId);
    }

    // generates a SHA-1 hash for any string
    public static String getHash(String stringToHash) {

        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        byte[] result = null;

        try {
            result = digest.digest(stringToHash.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();

        for (byte b : result)
        {
            sb.append(String.format("%02X", b));
        }

        String messageDigest = sb.toString();
        return messageDigest;
    }

    public static String getPackageNameFromFile(Context context,  String path) {
        final PackageManager pm = context.getPackageManager();
        if (pm == null)
            return null;

        PackageInfo info = pm.getPackageArchiveInfo(path, 0);
        if (info != null) {
            return info.packageName;
        }else
            return null;
    }

    public static boolean checkMD5(String md5, File updateFile) {
        if (TextUtils.isEmpty(md5) || updateFile == null) {
            return false;
        }

        String calculatedDigest = calculateMD5(updateFile);
        if (calculatedDigest == null) {
            return false;
        }

        return calculatedDigest.equalsIgnoreCase(md5);
    }

    public static String calculateMD5(File updateFile) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }

        InputStream is;
        try {
            is = new FileInputStream(updateFile);
        } catch (FileNotFoundException e) {
            return null;
        }

        byte[] buffer = new byte[8192];
        int read;
        try {
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String output = bigInt.toString(16);
            // Fill to 32 chars
            output = String.format("%32s", output).replace(' ', '0');
            return output;
        } catch (IOException e) {
            throw new RuntimeException("Unable to process file for MD5", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {

            }
        }
    }

    public static void installApk(Context context, String path) {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(Uri.parse("file://" + path), "application/vnd.android.package-archive");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } catch (Exception ex) {

        }
    }
}
