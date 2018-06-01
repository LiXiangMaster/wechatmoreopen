package com.shenqi.duokai.bean;

/**
 * Created by Administrator on 2016/10/28.
 */
public class VerSionUpdateCategory {

    /**
     * versionCode : 1
     * versionName : 1.0
     * downurl : https://down.feifanqiming.com:442/feifanqiming.apk
     * title : 多开神器升级了
     * text : 1、修复BUG
     2、更好的体验
     * autoup : 0
     * sign : e8673a27f430ffc91b27dd75206b547a
     */

    private String versionCode;
    private String versionName;
    private String downurl;
    private String title;
    private String text;
    private String autoup;
    private String sign;

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getDownurl() {
        return downurl;
    }

    public void setDownurl(String downurl) {
        this.downurl = downurl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAutoup() {
        return autoup;
    }

    public void setAutoup(String autoup) {
        this.autoup = autoup;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
