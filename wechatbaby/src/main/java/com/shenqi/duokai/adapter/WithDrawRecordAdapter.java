package com.shenqi.duokai.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shenqi.duokai.R;

import java.util.List;

/**
 * Created by lixiang on 2016/9/14.
 */
public class WithDrawRecordAdapter extends BaseAdapter {

    private List<List<String>> data;
    private Context context;

    public WithDrawRecordAdapter(Context context,List<List<String>> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data==null?0:data.size();

    }

    @Override
    public Object getItem(int position) {
        return data==null?null:data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView== null){
            viewHolder = new ViewHolder();
            convertView = View.inflate(context, R.layout.withdraw_record_item,null);
            viewHolder.withDdrawWay = (TextView) convertView.findViewById(R.id.tv_withdraw_way);
            viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_withdraw_time);
            viewHolder.tv_zhuangtai = (TextView) convertView.findViewById(R.id.tv_withdraw_zhuangtai);
            viewHolder.tv_payMoney = (TextView) convertView.findViewById(R.id.tv_pay_money);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.withDdrawWay.setText(data.get(position).get(3));
        viewHolder.tv_time.setText(data.get(position).get(2));
        viewHolder.tv_zhuangtai.setText(data.get(position).get(4));
        viewHolder.tv_payMoney.setText(data.get(position).get(0));
        return convertView;
    }
    static class ViewHolder{
        TextView withDdrawWay;
        TextView tv_time;
        TextView tv_zhuangtai;
        TextView tv_payMoney;
    }
}
