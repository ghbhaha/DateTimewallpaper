package com.suda.datetimewallpaper.ui.about

import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.PersistableBundle

import com.danielstone.materialaboutlibrary.ConvenienceBuilder
import com.danielstone.materialaboutlibrary.MaterialAboutActivity
import com.danielstone.materialaboutlibrary.model.MaterialAboutList
import com.danielstone.materialaboutlibrary.util.OpenSourceLicense
import com.suda.datetimewallpaper.R
import com.suda.datetimewallpaper.base.BaseAboutActivity

/**
 * @author guhaibo
 * @date 2019/4/15
 */
class LicenseActivity : BaseAboutActivity() {
    protected var colorIcon = R.color.mal_color_icon_light_theme

    override fun getMaterialAboutList(context: Context): MaterialAboutList {
        return createMaterialAboutLicenseList(context, colorIcon)
    }


    override fun getActivityTitle(): CharSequence? {
        return "License"
    }


    private fun createMaterialAboutLicenseList(c: Context, colorIcon: Int): MaterialAboutList {

        val materialAboutLIbraryLicenseCard = ConvenienceBuilder.createLicenseCard(
            c,
            c.resources.getDrawable(R.drawable.ic_book),
            "material-about-library", "2016", "Daniel Stone",
            OpenSourceLicense.APACHE_2
        )

        val GlideLicenseCard = ConvenienceBuilder.createLicenseCard(
            c,
            c.resources.getDrawable(R.drawable.ic_book),
            "Glide", "2014", "Google, Inc.",
            OpenSourceLicense.APACHE_2
        )

        val QuadFlaskLicenseCard = ConvenienceBuilder.createLicenseCard(
            c,
            c.resources.getDrawable(R.drawable.ic_book),
            "Color Picker", "", "",
            OpenSourceLicense.APACHE_2
        )

        val EasyPermissionsLicenseCard = ConvenienceBuilder.createLicenseCard(
            c,
            c.resources.getDrawable(R.drawable.ic_book),
            "EasyPermissions", "2017", "Google, Inc",
            OpenSourceLicense.APACHE_2
        )


        val FastjsonLicenseCard = ConvenienceBuilder.createLicenseCard(
            c,
            c.resources.getDrawable(R.drawable.ic_book),
            "Fastjson", "1999-2019", "Alibaba",
            OpenSourceLicense.APACHE_2
        )


        val MatisseLicenseCard = ConvenienceBuilder.createLicenseCard(
            c,
            c.resources.getDrawable(R.drawable.ic_book),
            "Matisse", "2017", "Zhihu, Inc",
            OpenSourceLicense.APACHE_2
        )

        return MaterialAboutList(
            materialAboutLIbraryLicenseCard,
            GlideLicenseCard, MatisseLicenseCard, QuadFlaskLicenseCard, FastjsonLicenseCard, EasyPermissionsLicenseCard
        )
    }
}
