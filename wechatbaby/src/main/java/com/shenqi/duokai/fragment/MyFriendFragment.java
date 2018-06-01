package com.shenqi.duokai.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;
import com.shenqi.duokai.R;
import com.shenqi.duokai.adapter.InviteFriendsAdapter;
import com.shenqi.duokai.bean.InviteFriendCategory;
import com.shenqi.duokai.constants.Contant;
import com.shenqi.duokai.constants.HttpConstant;
import com.shenqi.duokai.utils.MD5Utils;
import com.shenqi.duokai.utils.SPUtils;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lixiang on 2016/9/30.
 */
public class MyFriendFragment extends Fragment {
    View view;
    private ListView mLv_MyFriend;
    private OkHttpClient mOkHttpClient;
    private Call mMYFriendCall;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_myfriend, container, false);
        }
        //初始化网络请求;
        mOkHttpClient = new OkHttpClient();
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        initData();
    }

    private void initView(View view) {
        mLv_MyFriend = (ListView) view.findViewById(R.id.lv_friend);
    }

    private void initData() {
        reQuestNetData();
    }

    //网络请求;
    private void reQuestNetData() {
        //从首选项获取用户id;
        int qqLoginUserId = SPUtils.getInt(getActivity(), Contant.DUOKAI_LOGIN_USER_ID);
//        qqLoginUserId = 9;
        String sign = MD5Utils.encode(qqLoginUserId + "duo3kai7shen5qi");
        Uri uri = Uri.parse(HttpConstant.USER).buildUpon()
                .appendQueryParameter("action", "" + "invitelist")
                .appendQueryParameter("uid", "" + qqLoginUserId)
                .appendQueryParameter("page", "" + 1)
                .appendQueryParameter("sign", sign)
                .build();
        Request request = new Request.Builder()
                .get()
                .url(uri.toString())
                .build();
        //Log.i("test", "sign" + sign + "   qqlogniuserid: " + qqLoginUserId + "  ");
        mMYFriendCall = mOkHttpClient.newCall(request);
        mMYFriendCall.enqueue(myFriendCallBack);
    }

    Callback myFriendCallBack = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            //Log.i("test", e.getMessage());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String json = response.body().string();
            Gson gson = new Gson();
            //Log.i("test", "onresponse 我的好友:  " + json);
            InviteFriendCategory inviteFriendCategory = gson.fromJson(json, InviteFriendCategory.class);
            InviteFriendCategory.DataBean data = inviteFriendCategory.getData();
            String invitenum = data.getInvitenum();
            final List<List<String>> list = data.getList();
            //Log.i("test","我的好友页面;  "+invitenum+"   集合: "+list);
            //把集合传到 适配器中;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    InviteFriendsAdapter adpter = new InviteFriendsAdapter(getContext(), list);
                    mLv_MyFriend.setEmptyView(getActivity().findViewById(R.id.iv_must_app_no_data));
                    mLv_MyFriend.setAdapter(adpter);
                }
            });
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMYFriendCall != null) {
            mMYFriendCall.cancel();
        }
    }


}
