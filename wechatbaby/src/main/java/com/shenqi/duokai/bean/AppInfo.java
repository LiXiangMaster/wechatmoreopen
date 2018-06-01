package com.shenqi.duokai.bean;

import android.graphics.drawable.Drawable;

public class AppInfo {
    private String packageName;
    private String label;
    private Drawable icon;
    private boolean isSysApp;
    private String firstInStallTime;
    private String apkPath;

    public AppInfo() {
    }

    public AppInfo(String packageName, String label, Drawable icon, boolean isSysApp, String firstInStallTime, String apkPath) {
        this.packageName = packageName;
        this.label = label;
        this.icon = icon;
        this.isSysApp = isSysApp;
        this.firstInStallTime = firstInStallTime;
        this.apkPath = apkPath;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public boolean isSysApp() {
        return isSysApp;
    }

    public void setIsSysApp(boolean isSysApp) {
        this.isSysApp = isSysApp;
    }

    public String getFirstInStallTime() {
        return firstInStallTime;
    }

    public void setFirstInStallTime(String firstInStallTime) {
        this.firstInStallTime = firstInStallTime;
    }

    public String getApkPath() {
        return apkPath;
    }

    public void setApkPath(String apkPath) {
        this.apkPath = apkPath;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "packageName='" + packageName + '\'' +
                ", label='" + label + '\'' +
                ", icon=" + icon +
                ", isSysApp=" + isSysApp +
                ", firstInStallTime='" + firstInStallTime + '\'' +
                ", apkPath='" + apkPath + '\'' +
                '}';
    }
}

