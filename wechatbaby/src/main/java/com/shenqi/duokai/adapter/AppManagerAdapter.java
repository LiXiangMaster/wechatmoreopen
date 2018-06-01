package com.shenqi.duokai.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shenqi.duokai.R;
import com.shenqi.duokai.bean.AppInfo;
import com.shenqi.duokai.engine.AppEngine;
import com.shenqi.duokai.utils.Utils;

import java.io.File;
import java.util.List;

/**
 * Created by lixiang on 2016/9/12.
 */
public class AppManagerAdapter extends BaseAdapter {
    private Context context;
    private List<AppInfo> appManagerList;

    public List<AppInfo> getAppInfos() {
        return appManagerList;
    }

    public AppManagerAdapter(List<AppInfo> appManagerList, Context context) {
        this.appManagerList = appManagerList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return appManagerList == null ? 0 : appManagerList.size();
    }

    @Override
    public Object getItem(int position) {
        return appManagerList == null ? null : appManagerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(context, R.layout.lv_app_manager_item, null);
            viewHolder.ivIcon = ((ImageView) convertView.findViewById(R.id.iv_icon));
            viewHolder.tvLabel = ((TextView) convertView.findViewById(R.id.tv_label));
            viewHolder.tvMadeTime = ((TextView) convertView.findViewById(R.id.tv_madetime));
            viewHolder.ivRemove = ((ImageView) convertView.findViewById(R.id.iv_remove_app));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final AppInfo appInfo = appManagerList.get(position);
        viewHolder.ivIcon.setImageDrawable(appInfo.getIcon());
        viewHolder.tvLabel.setText(appInfo.getLabel());
        viewHolder.tvMadeTime.setText("制作时间:" + appInfo.getFirstInStallTime());
        final boolean installed = AppEngine.isPackageInstalled(context.getPackageManager(), appInfo.getPackageName());
        if (installed) {
            viewHolder.ivRemove.setImageResource(R.drawable.ivremove);

        } else {
            viewHolder.ivRemove.setImageResource(R.drawable.install_item);
        }
        viewHolder.ivRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //卸载  制作的app;
                if (installed) {
                    File file = new File(appInfo.getApkPath());
                    file.delete();
                    unInStanllMadeApp(appInfo);
                } else {
                    Utils.installApk(context, appInfo.getApkPath());
                }
            }
        });

        return convertView;
    }

    //卸载已经多开的app
    private void unInStanllMadeApp(AppInfo appInfo) {
        Intent uninstallIntent = new Intent();
        uninstallIntent.setAction(Intent.ACTION_DELETE);
        uninstallIntent.addCategory(Intent.CATEGORY_DEFAULT);
        uninstallIntent.setData(Uri.parse("package:" + appInfo.getPackageName()));
        context.startActivity(uninstallIntent);

    }

    static class ViewHolder {
        ImageView ivIcon;
        TextView tvLabel;
        TextView tvMadeTime;
        ImageView ivRemove;

    }
}
