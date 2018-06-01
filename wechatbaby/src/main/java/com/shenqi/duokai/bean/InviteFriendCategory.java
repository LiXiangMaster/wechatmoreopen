package com.shenqi.duokai.bean;

import java.util.List;

/**
 * Created by lixiang on 2016/10/21.
 */
public class InviteFriendCategory {

    /**
     * invitenum : 1
     * list : [["11","杨娟","http://q.qlogo.cn/qqapp/1105627339/9C197DF3FB5AFC2A9EC58685BA08BDB1/40","10-28 14:14","1","1",2,"1",1]]
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private String invitenum;
        private List<List<String>> list;

        public String getInvitenum() {
            return invitenum;
        }

        public void setInvitenum(String invitenum) {
            this.invitenum = invitenum;
        }

        public List<List<String>> getList() {
            return list;
        }

        public void setList(List<List<String>> list) {
            this.list = list;
        }
    }
}
