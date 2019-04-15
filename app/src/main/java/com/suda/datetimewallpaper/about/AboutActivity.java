package com.suda.datetimewallpaper.about;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.danielstone.materialaboutlibrary.MaterialAboutActivity;
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem;
import com.danielstone.materialaboutlibrary.items.MaterialAboutItemOnClickAction;
import com.danielstone.materialaboutlibrary.items.MaterialAboutTitleItem;
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;
import com.suda.datetimewallpaper.BuildConfig;
import com.suda.datetimewallpaper.R;

import java.io.IOException;

import androidx.annotation.NonNull;
import me.drakeet.materialdialog.MaterialDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * @author guhaibo
 * @date 2019/4/15
 */
public class AboutActivity extends MaterialAboutActivity {

    @Override
    @NonNull
    protected MaterialAboutList getMaterialAboutList(@NonNull final Context context) {

        MaterialAboutCard.Builder cardAppBuilder = new MaterialAboutCard.Builder();
        cardAppBuilder
                .addItem(new MaterialAboutTitleItem.Builder()
                        .text(R.string.app_name)
                        .desc("一款酷炫的动态壁纸")
                        .icon(R.mipmap.ic_launcher)
                        .build())
                .addItem(new MaterialAboutActionItem.Builder()
                        .text("Version")
                        .subText(BuildConfig.VERSION_NAME)
                        .icon(R.drawable.ic_info_outline)
                        .build())
                .addItem(new MaterialAboutActionItem.Builder()
                        .text("Licenses")
                        .icon(R.drawable.ic_book)

                        .setOnClickAction(new MaterialAboutItemOnClickAction() {
                            @Override
                            public void onClick() {
                                Intent intent = new Intent(context, LicenseActivity.class);
                                startActivity(intent);
                            }
                        })
                        .build())
                .addItem(new MaterialAboutActionItem.Builder()
                        .text("检查更新")
                        .icon(R.drawable.ic_system_update)
                        .setOnClickAction(new MaterialAboutItemOnClickAction() {
                            @Override
                            public void onClick() {
                                checkUpdate(context);
                            }
                        })
                        .build());


        MaterialAboutCard.Builder cardAuthorBuilder = new MaterialAboutCard.Builder();
        cardAuthorBuilder.title("作者")
                .addItem(new MaterialAboutActionItem.Builder()
                        .text("Ghbhaha")
                        .subText("https://github.com/ghbhaha")
                        .icon(R.drawable.ic_action_github
                        )
                        .setOnClickAction(new MaterialAboutItemOnClickAction() {
                            @Override
                            public void onClick() {
                                Intent intent = new Intent();
                                intent.setAction("android.intent.action.VIEW");
                                Uri content_url = Uri.parse("https://github.com/ghbhaha");
                                intent.setData(content_url);
                                startActivity(intent);
                            }
                        })
                        .build());


        MaterialAboutCard.Builder cardAOtherBuilder = new MaterialAboutCard.Builder();
        cardAOtherBuilder.title("其他")
                .addItem(new MaterialAboutActionItem.Builder()
                        .text("自定义表盘教程")
                        .subText("https://github.com/ghbhaha/DateTimewallpaper_Communication/issues/1")
                        .icon(R.drawable.ic_action_github
                        )
                        .setOnClickAction(new MaterialAboutItemOnClickAction() {
                            @Override
                            public void onClick() {
                                Intent intent = new Intent();
                                intent.setAction("android.intent.action.VIEW");
                                Uri content_url = Uri.parse("https://github.com/ghbhaha/DateTimewallpaper_Communication/issues/1");
                                intent.setData(content_url);
                                startActivity(intent);
                            }
                        })
                        .build());



        return new MaterialAboutList.Builder()
                .addCard(cardAppBuilder.build())
                .addCard(cardAuthorBuilder.build())
                .addCard(cardAOtherBuilder.build())
                .build(); // This creates an empty screen, add cards with .addCard()
    }

    private void checkUpdate(final Context context){
        final MaterialDialog innerDialog = new MaterialDialog(AboutActivity.this);
        innerDialog.setContentView(new ProgressBar(AboutActivity.this));
        innerDialog.setTitle("检查更新");
        innerDialog.show();
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url("https://raw.githubusercontent.com/ghbhaha/DateTimewallpaper_Communication/master/update.json").get().build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        innerDialog.dismiss();
                        Toast.makeText(context, "请求失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String body = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        innerDialog.dismiss();
                        try {
                            JSONObject jsonObject = JSON.parseObject(body);
                            int verCode = jsonObject.getInteger("verCode");
                            String verName = jsonObject.getString("verName");
                            String info = jsonObject.getString("info");
                            final String downUrl = jsonObject.getString("downUrl");
                            if (BuildConfig.VERSION_CODE < verCode) {
                                final MaterialDialog innerDialog = new MaterialDialog(AboutActivity.this);
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
                                        startActivity(intent);

                                        innerDialog.dismiss();
                                    }
                                });
                                innerDialog.show();
                            } else {
                                Toast.makeText(context, "未检查到更新", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(context, "请求失败,请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }



    @Override
    protected CharSequence getActivityTitle() {
        return getString(R.string.mal_title_about);
    }


}
