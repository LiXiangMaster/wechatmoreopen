# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/wangms/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
#-dontpreverify
-dontwarn
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#http://omgitsmgp.com/2013/09/09/a-conservative-guide-to-proguard-for-android/
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.billing.IInAppBillingService
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.content.Context {
    public void *(android.view.View);
    public void *(android.view.MenuItem);
}

-libraryjars libs ##需要吗?

# The official support library.
-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }


#for Umeng统计
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}


#-keep public class com.cz365.qhb.R$*{
#    public static final int *;
#}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#for Baidu MobAd
-dontwarn com.baidu.**
-keep class com.baidu.** { *; }

-keep class com.shenqi.duokai.utils.** { *;}

#for XGPush
-dontwarn com.tencent.android.tpush.**
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep class com.tencent.android.tpush.**  {* ;}
-keep class com.tencent.mid.**  {* ;}

#for wechat
-keep class com.tencent.mm.sdk.** {*;}

-keep class com.tencent.connect.** {*;}
-keep class com.tencent.open.** {*;}
-keep class com.tencent.tauth.** {*;}
-keep class com.tencent.stat.** {*;}
#jg_filter ?
#-dontwarn com.com.jg.**
#-keep class com.jg.** { *; }

#for org.apache.http.legacy

#-keepnames class org.apache.** {*;}
-keep class org.apache.** {*;}
#-dontwarn org.apache.commons.logging.LogFactory
#-dontwarn org.apache.http.annotation.ThreadSafe
#-dontwarn org.apache.http.annotation.Immutable
#-dontwarn org.apache.http.annotation.NotThreadSafe

-keep class org.apache.http.** { *; }
-dontwarn org.apache.http.**

#for alipay
-dontwarn com.alipay.**
-dontwarn com.ta.utdid2.**
-dontwarn com.ut.device.**
-dontwarn org.json.alipay.**
-keep class com.alipay.** { *; }
-keep class com.ta.utdid2.** { *; }
-keep class com.ut.device.** { *; }
-keep class org.json.alipay.** { *; }

#systembartint
-keep class com.readystatesoftware.systembartint.**{ *; }

#wup?
-dontwarn com.qq.taf.jce.**
-keep class com.qq.taf.jce.**{ *; }

#WebViewClient相关
-keep public class android.net.http.SslError
-keep public class android.webkit.WebViewClient

-dontwarn android.webkit.WebView
-dontwarn android.net.http.SslError
-dontwarn android.webkit.WebViewClient

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}





#-----------keep-------------------

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

-keepattributes Exceptions,InnerClasses
-keep public class com.alipay.android.app.** {
    public <fields>;
    public <methods>;
}

# Keep names - Native method names. Keep all native class/method names.
-keepclasseswithmembers,allowshrinking class * {
    native <methods>;
}

-keepclasseswithmembers,allowshrinking class * {
    public <init>(android.content.Context,android.util.AttributeSet);
}

-keepclasseswithmembers,allowshrinking class * {
    public <init>(android.content.Context,android.util.AttributeSet,int);
}

-keepclassmembers enum  * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * extends android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}


-ignorewarning
-keep public class * extends android.widget.TextView

#--------------unionpay 3.1.0--------------
-keep class com.unionpay.** {*;}
-keep class com.UCMobile.PayPlugin.** {*;}
-keep class cn.gov.pbc.tsm.client.mobile.android.bank.service.** {*;}

#--------------ecopay2.0--------------
-keep class com.payeco.android.plugin.** {*;}
-keep class org.payeco.http.entity.mime.** {*;}
-dontwarn com.payeco.android.plugin.**

-keepclassmembers class com.payeco.android.plugin {
  public *;
}

-keepattributes *Annotation*
-keepattributes *JavascriptInterface*


#--------------sms--------------
-keep class com.iapppay.sms.** {*;}

#--------------alipay-------------
-keep class com.ta.utdid2.** {
    public <fields>;
    public <methods>;
}
-keep class com.ut.device.** {
    public <fields>;
    public <methods>;
}
-keep class com.alipay.android.app.** {
    public <fields>;
    public <methods>;
}
-keep class com.alipay.sdk.** {
    public <fields>;
    public <methods>;
}
-keep class com.alipay.mobilesecuritysdk.** {
    public <fields>;
    public <methods>;
}
-keep class HttpUtils.** {
    public <fields>;
    public <methods>;
}


#-----------keep iapppay-------------------
-keep class com.iapppay.account.channel.ipay.IpayAccountApi {*;}
-keep class com.iapppay.account.channel.ipay.IpayOpenidApi {*;}
-keep class com.iapppay.pay.channel.oneclickpay.OnekeyPayHandler {*;}
-keep class com.iapppay.utils.RSAHelper {*;}
-keep class com.iapppay.account.channel.ipay.view.** {
    public <fields>;
    public <methods>;
}
-keep class com.iapppay.sdk.main.** {
    public <fields>;
    public <methods>;
}
-keep class com.iapppay.interfaces.callback.** {*;}

-keep class com.iapppay.interfaces.** {
    public <fields>;
    public <methods>;
}


#iapppay UI
-keep public class com.iapppay.ui.activity.** {
    public <fields>;
    public <methods>;
}

-keep public class com.iapppay.fastpay.ui.** {
    public <fields>;
    public <methods>;
}

-keep public class com.iapppay.fastpay.view.** {
    public <fields>;
    public <methods>;
}

# View
-keep public class com.iapppay.ui.widget.**{
    public <fields>;
    public <methods>;
}

-keep public class com.iapppay.ui.view.**{
    public <fields>;
    public <methods>;
}


#iapppay_sub_pay
-keep public class com.iapppay.pay.channel.** {
    public <fields>;
    public <methods>;
}

-keep class com.iapppay.tool {*;}
-keep class com.iapppay.service {*;}
-keep class com.iapppay.provider {*;}
-keep class com.iapppay.apppaysystem {*;}

#openid sdk
-keep class com.iapppay.openid.channel.IpayOpenidApi {*;}

#sdk分享;
-keep class cn.sharesdk.**{*;}
-keep class com.sina.**{*;}
-keep class **.R$* {*;}
-keep class **.R{*;}
-dontwarn cn.sharesdk.**
-dontwarn **.R$*


#极光推送;
-dontoptimize
-dontpreverify

-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }

##---------------Begin: proguard configuration for Gson ----------
-keep public class com.google.gson.**
-keep public class com.google.gson.** {public private protected *;}

-keepattributes Signature
-keepattributes *Annotation*
-keep public class com.project.mocha_patient.login.SignResponseData { private *; }

##---------------End: proguard configuration for Gson ----------

##OKHttp
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-dontwarn okio.**

