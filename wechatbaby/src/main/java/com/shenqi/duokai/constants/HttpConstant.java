package com.shenqi.duokai.constants;

/**
 * 关于网络请求的常量类
 */
public class HttpConstant {

    /**
     * 服务器url
     */
//    public static final String HOST = "http://192.168.16.18:8080/market";
    public static final String HOST = "https://api.duokaishenqi.com:442";
    public static final String ALIPAY = "https://api.duokaishenqi.com:442/app/pay/alipay/new_payment/ajaxpay.php";
    //?action=alipay&userid=55&paytxt=微信&imei=xxx&sign=519adf398730f3efc5ec0cf54e583811"
    public static final String USER = "https://api.duokaishenqi.com:442/user/";
    public static final String HOME = "https://api.duokaishenqi.com:442/home/";
    //爱呗支付接口;
    public static final String AIBEIPAY= "https://api.duokaishenqi.com:442/app/pay/iapppay/order.php";
//            "?action=LLAPPPAY&userid=55&paytxt=微信&imei=xxx&sign=519adf398730f3efc5ec0cf54e583811";
    //支付方式;
    public static final String PAYWAY = "https://api.duokaishenqi.com:442/home/?action=paytype";
    //分享拆红包;
    public static final String SHAREWAY = "https://api.duokaishenqi.com:442/home/?action=hongbaoshare";
    //获取是否显示更多福利接口;
    public static final String ISSHOWGOODS = "https://api.duokaishenqi.com:442/home/?action=moreservices";
    //版本更新;
    public static final String VERSIONUPDATE = "https://api.duokaishenqi.com:442/update/android.json";
    //邀请好友界面的 邀请好友;
//    public static final String INVITEFRIEND = "https://api.duokaishenqi.com:442/home/";
    /**
     * 对网络地址的格式化
     * @param url
     * @return
     */
    public static String formatUrl(String url) {
        if (url.startsWith("http")){
            return url;
        }
        return HOST + url;
    }
    public static final int HTTP_TIMEOUT = 500;


}
