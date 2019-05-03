package com.suda.datetimewallpaper.ui.about

import android.content.Context
import android.content.Intent
import android.net.Uri

import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem
import com.danielstone.materialaboutlibrary.items.MaterialAboutTitleItem
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard
import com.danielstone.materialaboutlibrary.model.MaterialAboutList
import com.suda.datetimewallpaper.BuildConfig
import com.suda.datetimewallpaper.R
import com.suda.datetimewallpaper.base.BaseAboutActivity
import com.suda.datetimewallpaper.util.CheckUpdateUtil


/**
 * @author guhaibo
 * @date 2019/4/15
 */
class AboutActivity : BaseAboutActivity() {

    override fun getMaterialAboutList(context: Context): MaterialAboutList {

        val cardAppBuilder = MaterialAboutCard.Builder()
        cardAppBuilder
            .addItem(
                MaterialAboutTitleItem.Builder()
                    .text(R.string.app_name)
                    .desc("一款酷炫的动态壁纸")
                    .icon(R.mipmap.ic_launcher)
                    .build()
            )
            .addItem(
                MaterialAboutActionItem.Builder()
                    .text("Version")
                    .subText(BuildConfig.VERSION_NAME)
                    .icon(R.drawable.ic_info_outline)
                    .build()
            )
            .addItem(MaterialAboutActionItem.Builder()
                .text("Licenses")
                .icon(R.drawable.ic_book)

                .setOnClickAction {
                    val intent = Intent(context, LicenseActivity::class.java)
                    startActivity(intent)
                }
                .build())
            .addItem(MaterialAboutActionItem.Builder()
                .text("检查更新")
                .icon(R.drawable.ic_system_update)
                .setOnClickAction { CheckUpdateUtil.checkUpdate(this@AboutActivity, true) }
                .build())


        val cardAuthorBuilder = MaterialAboutCard.Builder()
        cardAuthorBuilder.title("作者")
            .addItem(MaterialAboutActionItem.Builder()
                .text("Ghbhaha")
                .subText("https://github.com/ghbhaha")
                .icon(
                    R.drawable.ic_action_github
                )
                .setOnClickAction {
                    val intent = Intent()
                    intent.action = "android.intent.action.VIEW"
                    val content_url = Uri.parse("https://github.com/ghbhaha")
                    intent.data = content_url
                    startActivity(intent)
                }
                .build())

        val cardAOtherBuilder = MaterialAboutCard.Builder()
        cardAOtherBuilder.title("其他")
            .addItem(MaterialAboutActionItem.Builder()
                .text("自定义表盘教程")
                .subText("https://github.com/ghbhaha/DateTimewallpaper_Communication/issues/1")
                .icon(
                    R.drawable.ic_action_github
                )
                .setOnClickAction {
                    val intent = Intent()
                    intent.action = "android.intent.action.VIEW"
                    val content_url = Uri.parse("https://github.com/ghbhaha/DateTimewallpaper_Communication/issues/1")
                    intent.data = content_url
                    startActivity(intent)
                }

                .build())

        return MaterialAboutList.Builder()
            .addCard(cardAppBuilder.build())
            .addCard(cardAuthorBuilder.build())
            .addCard(cardAOtherBuilder.build())
            .build() // This creates an empty screen, add cards with .addCard()
    }

    override fun getActivityTitle(): CharSequence? {
        return getString(R.string.mal_title_about)
    }


}
