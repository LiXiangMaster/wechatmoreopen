package com.shenqi.duokai.bean;

/**
 * Created by lixiang on 2016/10/10.
 */
public class ShareStrCategory {

    /**
     * title : 多开神器
     * url : http://share.duokaishenqi.com/
     * pic : http://m.duokaishenqi.com/assets/images/share.jpg
     * txt : 多开神器是一款软件
     */

    private WechatBean wechat;
    /**
     * title : 多开神器
     * url : http://share.duokaishenqi.com/
     * pic : http://m.duokaishenqi.com/assets/images/share.jpg
     * txt : 多开神器是一款软件
     */

    private TimelineBean timeline;
    /**
     * title : 多开神器
     * url : http://share.duokaishenqi.com/
     * pic : http://m.duokaishenqi.com/assets/images/share.jpg
     * txt : 多开神器是一款软件
     */

    private QqBean qq;

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
}
