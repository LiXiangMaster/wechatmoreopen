package com.shenqi.duokai.bean;

/**
 * Created by Administrator on 2016/10/27.
 */
public class OpenPacketCategory {

    /**
     * doaccess : 0
     * daymoney : 0
     * errtxt : 拆红包失败
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
        private double daymoney;
        private String errtxt;

        public int getDoaccess() {
            return doaccess;
        }

        public void setDoaccess(int doaccess) {
            this.doaccess = doaccess;
        }

        public double getDaymoney() {
            return daymoney;
        }

        public void setDaymoney(double daymoney) {
            this.daymoney = daymoney;
        }

        public String getErrtxt() {
            return errtxt;
        }

        public void setErrtxt(String errtxt) {
            this.errtxt = errtxt;
        }
    }
}
