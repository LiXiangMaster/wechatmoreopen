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
 * Created by lixiang on 2016/9/13.
 */
public class MoneyDetailAdapter extends BaseAdapter {
    private List<List<String>> data;
    private Context context;

    public MoneyDetailAdapter(Context context, List<List<String>> data) {
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
        if (convertView==null){
            viewHolder = new ViewHolder();
            convertView = View.inflate(context, R.layout.accout_last_item,null);
            viewHolder.tv_Time= ((TextView) convertView.findViewById(R.id.tv_time));
            viewHolder.tv_Info = ((TextView) convertView.findViewById(R.id.tv_info));
            viewHolder.tv_Money = ((TextView) convertView.findViewById(R.id.tv_money));
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_Time.setText(data.get(position).get(4));
        viewHolder.tv_Info.setText(data.get(position).get(1));
        viewHolder.tv_Money.setText(data.get(position).get(2)+"元");
        return convertView;
    }
    static class ViewHolder{
        TextView tv_Info;
        TextView tv_Time;
        TextView tv_Money;
    }
}
