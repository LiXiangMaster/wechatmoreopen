package com.shenqi.duokai.bean;

/**
 * Created by Administrator on 2016/10/27.
 */
public class WechatLoginCategory {

    /**
     * doaccess : 0
     * userid : 0
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private int doaccess;
        private int userid;

        public int getDoaccess() {
            return doaccess;
        }

        public void setDoaccess(int doaccess) {
            this.doaccess = doaccess;
        }

        public int getUserid() {
            return userid;
        }

        public void setUserid(int userid) {
            this.userid = userid;
        }
    }
}
