package com.shenqi.duokai.bean;

/**
 * Created by lixiang on 2016/10/28.
 */
public class InviteDataCategrogy {


    /**
     * title : 多开神器
     * url : http://crime.goudefan.cn/?uid=2
     * pic : http://m.duokaishenqi.com/assets/images/share_logo.png
     * txt : 大号、小号，一个手机同时登录N个帐号，互不干扰！
     */

    private WechatBean wechat;
    /**
     * title : 多开神器
     * url : http://crime.goudefan.cn/?uid=2
     * pic : http://m.duokaishenqi.com/assets/images/share_logo.png
     * txt : 大号、小号，一个手机同时登录N个帐号，互不干扰！
     */

    private TimelineBean timeline;
    /**
     * title : 一款微商必备神器
     * url : http://preacher.bokesys.cn/?uid=2
     * pic : http://m.duokaishenqi.com/assets/images/share.jpg
     * txt : 一个手机开多个同样的应用，工作生活两不误。
     */

    private QqBean qq;
    /**
     * title : 一款微商必备神器
     * url : http://preacher.bokesys.cn/?uid=2
     * pic : http://m.duokaishenqi.com/assets/images/share.jpg
     * txt : 一个手机开多个同样的应用，工作生活两不误。
     */

    private QzoneBean qzone;
    /**
     * title : 一款微商必备神器
     * url : http://preacher.bokesys.cn/?uid=2
     * pic : http://m.duokaishenqi.com/assets/images/share.jpg
     * txt : 一个手机开多个同样的应用，工作生活两不误。
     */

    private SinaBean sina;
    /**
     * url : http://preacher.bokesys.cn/?uid=2
     */

    private CopyurlBean copyurl;

    public WechatBean getWechat() {
        return wechat;
    }

    public void setWechat(WechatBean wechat) {
        this.wechat = wechat;
    }

    public TimelineBean getTimeline() {
        return timeline;
    }

    public void setTimeline(TimelineBean timeline) {
        this.timeline = timeline;
    }

    public QqBean getQq() {
        return qq;
    }

    public void setQq(QqBean qq) {
        this.qq = qq;
    }

    public QzoneBean getQzone() {
        return qzone;
    }

    public void setQzone(QzoneBean qzone) {
        this.qzone = qzone;
    }

    public SinaBean getSina() {
        return sina;
    }

    public void setSina(SinaBean sina) {
        this.sina = sina;
    }

    public CopyurlBean getCopyurl() {
        return copyurl;
    }

    public void setCopyurl(CopyurlBean copyurl) {
        this.copyurl = copyurl;
    }

    public static class WechatBean {
        private String title;
        private String url;
        private String pic;
        private String txt;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getTxt() {
            return txt;
        }

        public void setTxt(String txt) {
            this.txt = txt;
        }
    }

    public static class TimelineBean {
        private String title;
        private String url;
        private String pic;
        private String txt;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getTxt() {
            return txt;
        }

        public void setTxt(String txt) {
            this.txt = txt;
        }
    }

    public static class QqBean {
        private String title;
        private String url;
        private String pic;
        private String txt;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getTxt() {
            return txt;
        }

        public void setTxt(String txt) {
            this.txt = txt;
        }
    }

    public static class QzoneBean {
        private String title;
        private String url;
        private String pic;
        private String txt;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getTxt() {
            return txt;
        }

        public void setTxt(String txt) {
            this.txt = txt;
        }
    }

    public static class SinaBean {
        private String title;
        private String url;
        private String pic;
        private String txt;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getTxt() {
            return txt;
        }

        public void setTxt(String txt) {
            this.txt = txt;
        }
    }

    public static class CopyurlBean {
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
