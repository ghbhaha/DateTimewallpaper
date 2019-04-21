package com.suda.datetimewallpaper.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.suda.datetimewallpaper.BuildConfig;

import java.io.IOException;

import me.drakeet.materialdialog.MaterialDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author guhaibo
 * @date 2019/4/21
 */
public class CheckUpdateUtil {

    public static void checkUpdate(final Activity activity, final boolean showDialog) {
        final MaterialDialog innerDialog = new MaterialDialog(activity);
        if (showDialog) {
            innerDialog.setContentView(new ProgressBar(activity));
            innerDialog.setTitle("检查更新");
            innerDialog.show();
        }
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url("https://raw.githubusercontent.com/ghbhaha/DateTimewallpaper_Communication/master/update.json").get().build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (showDialog){
                            innerDialog.dismiss();
                        }
                        Toast.makeText(activity, "请求失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String body = response.body().string();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (showDialog){
                            innerDialog.dismiss();
                        }
                        try {
                            JSONObject jsonObject = JSON.parseObject(body);
                            int verCode = jsonObject.getInteger("verCode");
                            String verName = jsonObject.getString("verName");
                            String info = jsonObject.getString("info");
                            final String downUrl = jsonObject.getString("downUrl");
                            if (BuildConfig.VERSION_CODE < verCode) {
                                final MaterialDialog innerDialog = new MaterialDialog(activity);
                                innerDialog.setTitle("更新日志" + verName);
                                innerDialog.setMessage(info);
                                innerDialog.setNegativeButton("取消", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        innerDialog.dismiss();
                                    }
                                });
                                innerDialog.setPositiveButton("更新", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent();
                                        intent.setAction("android.intent.action.VIEW");
                                        Uri content_url = Uri.parse(downUrl);
                                        intent.setData(content_url);
                                        activity.startActivity(intent);

                                        innerDialog.dismiss();
                                    }
                                });
                                innerDialog.show();
                            } else {
                                Toast.makeText(activity, "未检查到更新", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(activity, "请求失败,请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
