package com.shenqi.duokai.bean;

/**
 * Created by Administrator on 2016/10/25.
 */
public class AliPayCategory {

    /**
     * status : 1
     * data : {"strJson":"partner=\"2088911994101078\"&seller_id=\"ehuobang@163.com\"&out_trade_no=\"310252025583079\"&subject=\"多开支付\"&body=\"多开支付\"&total_fee=\"0.01\"¬ify_url=\"http://api.duokaishenqi.com/app/pay/alipay/alipay_order_laozhong.php\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"&return_url=\"http://api.duokaishenqi.com/buy/pay_success.html\"&sign=\"ZwOsZLZNkBkRDf3DWHn7poLCkNI2Ph5THu30ESlnkXUrJTEPHk58wC28%2FlWymVbNvQoCbnh2yRityUuqKNwnD9jOXCWif3h%2FG8v%2FQf%2BVYBMGnj4rkMw58G9APrT3IJKXSgqUtFI3gcOP%2FGL%2F5LQcwM%2B7ZJFsUgN46vmLQckq8H4%3D\"&sign_type=\"RSA\"","OutTradeNo":"310252025583079"}
     */

    private int status;
    /**
     * strJson : partner="2088911994101078"&seller_id="ehuobang@163.com"&out_trade_no="310252025583079"&subject="多开支付"&body="多开支付"&total_fee="0.01"¬ify_url="http://api.duokaishenqi.com/app/pay/alipay/alipay_order_laozhong.php"&service="mobile.securitypay.pay"&payment_type="1"&_input_charset="utf-8"&it_b_pay="30m"&return_url="http://api.duokaishenqi.com/buy/pay_success.html"&sign="ZwOsZLZNkBkRDf3DWHn7poLCkNI2Ph5THu30ESlnkXUrJTEPHk58wC28%2FlWymVbNvQoCbnh2yRityUuqKNwnD9jOXCWif3h%2FG8v%2FQf%2BVYBMGnj4rkMw58G9APrT3IJKXSgqUtFI3gcOP%2FGL%2F5LQcwM%2B7ZJFsUgN46vmLQckq8H4%3D"&sign_type="RSA"
     * OutTradeNo : 310252025583079
     */

    private DataBean data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private String strJson;
        private String OutTradeNo;

        public String getStrJson() {
            return strJson;
        }

        public void setStrJson(String strJson) {
            this.strJson = strJson;
        }

        public String getOutTradeNo() {
            return OutTradeNo;
        }

        public void setOutTradeNo(String OutTradeNo) {
            this.OutTradeNo = OutTradeNo;
        }
    }
}
