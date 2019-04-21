package com.suda.datetimewallpaper.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.suda.datetimewallpaper.R;
import com.suda.datetimewallpaper.util.FileUtil;

import java.io.File;
import java.io.FileFilter;


/**
 * @author guhaibo
 * @date 2019/3/17
 */
final public class CusAdapter extends BaseAdapter {


    private File[] backFiles = null;

    public CusAdapter() {
        refresh();
    }

    public void refresh() {
        backFiles = FileUtil.getBaseFile().listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().contains(".json") || pathname.getName().contains(".txt");
            }
        });
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return backFiles.length + 1;
    }

    @Override
    public Object getItem(int position) {
        return position == 0 ? null : backFiles[position - 1];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cus, parent, false);
        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new ViewHolder();
            convertView.setTag(viewHolder);
            viewHolder.tvName = convertView.findViewById(R.id.tv_cus);
        }

        if (position == 0) {
                viewHolder.tvName.setText(R.string.select_conf_default);
        } else {
            viewHolder.tvName.setText(backFiles[position - 1].getName());
        }


        return convertView;
    }

    class ViewHolder {
        TextView tvName;
    }
}
