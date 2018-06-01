package com.shenqi.duokai.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.format.Formatter;

import com.shenqi.duokai.bean.AppInfo;
import com.shenqi.duokai.dao.MadedAppDao;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class AppEngine {


    /**
     * 获取所有已安装的应用程序信息
     *
     * @param context
     * @return
     */
    /*public static List<AppInfo> getAllInstalledApps(Context context) {
        List<AppInfo> appInfos = new ArrayList<>();
        // 获取PackageManger
        PackageManager pm = context.getPackageManager();

        // 获取所有已安装的包信息  flag没有任何意义
        List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        for (PackageInfo packageInfo : installedPackages) {
            //app第一次安装的时间;
            Date date = new Date(packageInfo.firstInstallTime);
            String firstInstallTime = sf.format(date);
            //包名
            String packageName = packageInfo.packageName;
            //获取应用程序信息
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            //获取应用名
            String label = (String) applicationInfo.loadLabel(pm);
            Drawable icon = applicationInfo.loadIcon(pm);
            // applicationInfo.sourceDir应用安装绝对路径
            // 应用程序安装在Android系统中都存放在/data/data/包名  这个目录中
            // 所以获取应用大小  就是获取/data/data/包名 目录 的大小
            String size = Formatter.formatFileSize(context, new File(applicationInfo.sourceDir).length());
            // 与运算
            // 00001111  应用程序的标记
            // 00000001  系统定义常量: 系统应用标记
            // 00000001

            // 00000010  系统定义常量: 外部存储应用标记
            // 00100000
            // 32

            //是否安装在sd卡
            boolean isInstallSd = (applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0;
            //系统应用
            boolean isSysApp = (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
            AppInfo appInfo = new AppInfo(packageName, label, icon,isSysApp,firstInstallTime);
            appInfos.add(appInfo);

        }

        return appInfos;
    }*/
    public static List<AppInfo> getAllInstalledApps(Context context) {
        List<AppInfo> appInfos = new ArrayList<>();
        // 获取PackageManger
        PackageManager pm = context.getPackageManager();

        // 获取所有已安装的包信息  flag没有任何意义

        /*SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        for (int i=1; i<100; i++) {
            String packageName = "com.shenqi.pluginstub" + String.format("%02d",i);
            PackageInfo packageInfo = null;
            ApplicationInfo applicationInfo = null;

            try {
                packageInfo = pm.getPackageInfo(packageName, 0);
                applicationInfo = pm.getApplicationInfo(packageName, 0);
            }catch (PackageManager.NameNotFoundException ext) {
                continue;
            }

            //app第一次安装的时间;
            Date date = new Date(packageInfo.firstInstallTime);
            String firstInstallTime = sf.format(date);
            //包名
            //获取应用名
            String label = (String) applicationInfo.loadLabel(pm);
            Drawable icon = applicationInfo.loadIcon(pm);
            // applicationInfo.sourceDir应用安装绝对路径
            // 应用程序安装在Android系统中都存放在/data/data/包名  这个目录中
            // 所以获取应用大小  就是获取/data/data/包名 目录 的大小
            String size = Formatter.formatFileSize(context, new File(applicationInfo.sourceDir).length());
            // 与运算
            // 00001111  应用程序的标记
            // 00000001  系统定义常量: 系统应用标记
            // 00000001

            // 00000010  系统定义常量: 外部存储应用标记
            // 00100000
            // 32

            //是否安装在sd卡
            boolean isInstallSd = (applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0;
            //系统应用
            boolean isSysApp = (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
            AppInfo appInfo = new AppInfo(packageName, label, icon,isSysApp,firstInstallTime, nu);
            appInfos.add(appInfo);

        }*/

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String strTemp = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DuoKai";
            File folder = new File(strTemp);
            File[] lists = folder.listFiles();
            if (lists == null)
                return appInfos;

            for (File file : lists) {
                if (file.getName().substring(0,5).compareTo("dksq_") == 0) {
                    try {
                        PackageInfo info = pm.getPackageArchiveInfo(file.getCanonicalPath(), 0);

                        Date lastModified = new Date(file.lastModified());
                        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
                        String strTime = sf.format(lastModified);

                        ApplicationInfo appinfo = info.applicationInfo;
                        String label = (String) appinfo.loadLabel(pm);
                        Drawable icon = loadWeixinIcon(pm);
                        boolean isSysApp = (appinfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;


                        AppInfo appInfo = new AppInfo(info.packageName, label, icon,isSysApp, strTime , file.getCanonicalPath());
                        appInfos.add(appInfo);
                    }catch (IOException ex) {
                        continue;
                    }

                }
            }
        }

        return appInfos;
    }

    public static boolean isPackageInstalled(PackageManager pm, String packageName) {
        boolean installed = true;
        try {
            PackageInfo info = pm.getPackageInfo(packageName, 0);

        }catch (PackageManager.NameNotFoundException ext) {
            installed = false;
        }

        return installed;
    }

    private static Drawable loadWeixinIcon(PackageManager pm) {
        Drawable icon = null;

        try {
            ApplicationInfo info = pm.getApplicationInfo("com.tencent.mm", 0);
            icon = info.loadIcon(pm);
        }catch (PackageManager.NameNotFoundException ext) {
            icon = null;
        }

        return icon;
    }

    public static String genNewContainerName(Context context) {
        PackageManager pm = context.getPackageManager();

        int max = 0;

        for (int i=1; i<100; i++) {
            String packageName = "com.shenqi.pluginstub" + String.format("%02d", i);
            PackageInfo packageInfo = null;
            ApplicationInfo applicationInfo = null;

            try {
                packageInfo = pm.getPackageInfo(packageName, 0);
                applicationInfo = pm.getApplicationInfo(packageName, 0);
                max = i;
            } catch (PackageManager.NameNotFoundException ext) {
                continue;
            }
        }

        //再搜索已制作没安装的
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String strTemp = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DuoKai";
            File folder = new File(strTemp);
            File[] lists = folder.listFiles();
            if (lists != null) {
                for (File file : lists) {
                    if (file.getName().substring(0, 5).compareTo("dksq_") == 0) {
                        try {
                            PackageInfo info = pm.getPackageArchiveInfo(file.getCanonicalPath(), 0);
                            if (info.packageName.substring(0, 21).equals("com.shenqi.pluginstub")) {
                                String sequence = info.packageName.substring(21, 23);
                                int n = 0;
                                try {
                                    n = Integer.parseInt(sequence);
                                }catch (NumberFormatException ex){
                                }

                                if (n > max)
                                    max = n;

                            }
                        } catch (IOException ex) {
                            continue;
                        }

                    }
                }
            }
        }

        return "分身" + String.format("%02d", max+1);
    }
}
