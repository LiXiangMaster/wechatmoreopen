package com.shenqi.duokai.bean;

/**
 * Created by lixiang on 2016/10/31.
 */
public class WithDrawCategory {
    /**
     * doaccess : 4
     * errtxt : 提现最小金额为30元
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
        private String errtxt;

        public int getDoaccess() {
            return doaccess;
        }

        public void setDoaccess(int doaccess) {
            this.doaccess = doaccess;
        }

        public String getErrtxt() {
            return errtxt;
        }

        public void setErrtxt(String errtxt) {
            this.errtxt = errtxt;
        }
    }
/*
    *//**
     * doaccess : 0
     *//*

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private int doaccess;

        public int getDoaccess() {
            return doaccess;
        }

        public void setDoaccess(int doaccess) {
            this.doaccess = doaccess;
        }
    }*/


}
