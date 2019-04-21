package com.suda.datetimewallpaper.about;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.danielstone.materialaboutlibrary.MaterialAboutActivity;
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem;
import com.danielstone.materialaboutlibrary.items.MaterialAboutItemOnClickAction;
import com.danielstone.materialaboutlibrary.items.MaterialAboutTitleItem;
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;
import com.suda.datetimewallpaper.BuildConfig;
import com.suda.datetimewallpaper.R;
import com.suda.datetimewallpaper.util.CheckUpdateUtil;

import androidx.annotation.NonNull;


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
                                CheckUpdateUtil.checkUpdate(AboutActivity.this, true);
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

                        .build())

                .addItem(new MaterialAboutActionItem.Builder()
                        .text("更多表盘配置")
                        .subText("https://timeconf.sudamod.site/")
                        .icon(R.drawable.ic_action_github
                        )
                        .setOnClickAction(new MaterialAboutItemOnClickAction() {
                            @Override
                            public void onClick() {
                                Intent intent = new Intent();
                                intent.setAction("android.intent.action.VIEW");
                                Uri content_url = Uri.parse("https://timeconf.sudamod.site/");
                                intent.setData(content_url);
                                startActivity(intent);
                            }
                        }).build());



        return new MaterialAboutList.Builder()
                .addCard(cardAppBuilder.build())
                .addCard(cardAuthorBuilder.build())
                .addCard(cardAOtherBuilder.build())
                .build(); // This creates an empty screen, add cards with .addCard()
    }

    @Override
    protected CharSequence getActivityTitle() {
        return getString(R.string.mal_title_about);
    }


}
