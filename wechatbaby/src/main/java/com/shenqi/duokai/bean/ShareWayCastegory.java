package com.shenqi.duokai.bean;

/**
 * Created by lixiang on 2016/10/27.
 */
public class ShareWayCastegory {

    /**
     * type : 0
     * share : {"title":"微商必备神器","url":"http://share.duokaishenqi.com/?uid=2","pic":"http://m.duokaishenqi.com/assets/images/share.jpg","txt":"一个手机开多个同样的应用，工作生活两不误。"}
     */

    private int type;
    /**
     * title : 微商必备神器
     * url : http://share.duokaishenqi.com/?uid=2
     * pic : http://m.duokaishenqi.com/assets/images/share.jpg
     * txt : 一个手机开多个同样的应用，工作生活两不误。
     */

    private ShareBean share;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ShareBean getShare() {
        return share;
    }

    public void setShare(ShareBean share) {
        this.share = share;
    }

    public static class ShareBean {
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
