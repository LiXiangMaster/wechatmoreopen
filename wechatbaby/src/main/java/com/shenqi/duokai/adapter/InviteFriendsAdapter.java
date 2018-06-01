package com.shenqi.duokai.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shenqi.duokai.R;
import com.shenqi.duokai.utils.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by lixiang on 2016/9/12.
 */
public class InviteFriendsAdapter extends BaseAdapter {
    private final List<List<String>> data;
    private Context context;

    public InviteFriendsAdapter(Context context, List<List<String>> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(context, R.layout.invite_details_item, null);
            viewHolder.useIcon = (ImageView) convertView.findViewById(R.id.iv_user_icon);
            viewHolder.userName = (TextView) convertView.findViewById(R.id.tv_usename);
            viewHolder.inviteFriendNum = (TextView) convertView.findViewById(R.id.tv_invite_friend_num);
            viewHolder.madeNum = (TextView) convertView.findViewById(R.id.tv_mymade_num);
            viewHolder.madeNum_Money = (TextView) convertView.findViewById(R.id.tv_mymade_money);
            viewHolder.friednMadeNum = (TextView) convertView.findViewById(R.id.tv_friend_friend_num);
            viewHolder.friednMadeNum_Money = (TextView) convertView.findViewById(R.id.tv_friend_money);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();

        }
        viewHolder.userName.setText(data.get(position).get(1));
        viewHolder.inviteFriendNum.setText("TA共邀请" + data.get(position).get(4) + "位好友");
        viewHolder.madeNum_Money.setText("(+" + data.get(position).get(6) + "元)");
        viewHolder.madeNum.setText("制作" + data.get(position).get(5) + "个分身");
        viewHolder.friednMadeNum.setText("TA的好友制作" + data.get(position).get(7) + "个分身");
        viewHolder.friednMadeNum_Money.setText("(+" + data.get(position).get(8) + "元)");
        Picasso.with(context).load(data.get(position).get(2)).transform(new CircleTransform()).into(viewHolder.useIcon);

        /*
        * 用户ID，用户昵称，用户头像，注册时间，邀请好友数，好友多开数量，好友多开奖励金额，
        * 好友的好友多开数量，好友的好友多开奖励金额

        * */
        return convertView;
    }

    static class ViewHolder {
        ImageView useIcon;
        TextView userName;
        TextView inviteFriendNum;
        TextView madeNum;
        TextView madeNum_Money;
        TextView friednMadeNum;
        TextView friednMadeNum_Money;
    }
}
