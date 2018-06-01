package com.shenqi.duokai.bean;

/**
 * Created by lixiang on 2016/10/18.
 */
public class QqLoginCategory {


    /**
     * doaccess : 0
     * userid : 0
     * img : null
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
        private Object img;

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

        public Object getImg() {
            return img;
        }

        public void setImg(Object img) {
            this.img = img;
        }
    }
}
