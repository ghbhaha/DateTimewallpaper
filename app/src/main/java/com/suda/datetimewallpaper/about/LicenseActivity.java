package com.suda.datetimewallpaper.about;

import android.content.Context;

import com.danielstone.materialaboutlibrary.ConvenienceBuilder;
import com.danielstone.materialaboutlibrary.MaterialAboutActivity;
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;
import com.danielstone.materialaboutlibrary.util.OpenSourceLicense;
import com.suda.datetimewallpaper.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author guhaibo
 * @date 2019/4/15
 */
public class LicenseActivity extends MaterialAboutActivity {
    protected int colorIcon = R.color.mal_color_icon_light_theme;


    @NonNull
    @Override
    protected MaterialAboutList getMaterialAboutList(@NonNull Context context) {
        return createMaterialAboutLicenseList(context, colorIcon);
    }


    @Nullable
    @Override
    protected CharSequence getActivityTitle() {
        return "License";
    }


    private MaterialAboutList createMaterialAboutLicenseList(final Context c, int colorIcon) {

        MaterialAboutCard materialAboutLIbraryLicenseCard = ConvenienceBuilder.createLicenseCard(c,
                c.getResources().getDrawable(R.drawable.ic_book),
                "material-about-library", "2016", "Daniel Stone",
                OpenSourceLicense.APACHE_2);

        MaterialAboutCard GlideLicenseCard = ConvenienceBuilder.createLicenseCard(c,
                c.getResources().getDrawable(R.drawable.ic_book),
                "Glide", "2014", "Google, Inc.",
                OpenSourceLicense.APACHE_2);

        MaterialAboutCard QuadFlaskLicenseCard = ConvenienceBuilder.createLicenseCard(c,
                c.getResources().getDrawable(R.drawable.ic_book),
                "Color Picker", "", "",
                OpenSourceLicense.APACHE_2);

        MaterialAboutCard EasyPermissionsLicenseCard = ConvenienceBuilder.createLicenseCard(c,
                c.getResources().getDrawable(R.drawable.ic_book),
                "EasyPermissions", "2017", "Google, Inc",
                OpenSourceLicense.APACHE_2);


        MaterialAboutCard FastjsonLicenseCard = ConvenienceBuilder.createLicenseCard(c,
                c.getResources().getDrawable(R.drawable.ic_book),
                "Fastjson", "1999-2019", "Alibaba",
                OpenSourceLicense.APACHE_2);


        MaterialAboutCard MatisseLicenseCard = ConvenienceBuilder.createLicenseCard(c,
                c.getResources().getDrawable(R.drawable.ic_book),
                "Matisse", "2017", "Zhihu, Inc",
                OpenSourceLicense.APACHE_2);

        return new MaterialAboutList(materialAboutLIbraryLicenseCard,
                GlideLicenseCard, MatisseLicenseCard, QuadFlaskLicenseCard, FastjsonLicenseCard, EasyPermissionsLicenseCard);
    }
}
