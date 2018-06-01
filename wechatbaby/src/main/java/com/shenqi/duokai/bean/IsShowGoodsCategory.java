package com.shenqi.duokai.bean;

/**
 * Created by Administrator on 2016/10/27.
 */
public class IsShowGoodsCategory {

    /**
     * data : 1
     * info : {"txt":"抢更多福利>>","url":"http://m.duokaishenqi.com/help.html"}
     */

    private int data;
    /**
     * txt : 抢更多福利>>
     * url : http://m.duokaishenqi.com/help.html
     */

    private InfoBean info;

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public InfoBean getInfo() {
        return info;
    }

    public void setInfo(InfoBean info) {
        this.info = info;
    }

    public static class InfoBean {
        private String txt;
        private String url;

        public String getTxt() {
            return txt;
        }

        public void setTxt(String txt) {
            this.txt = txt;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
