package com.suda.datetimewallpaper.base

import android.graphics.PorterDuff
import android.os.Bundle
import com.danielstone.materialaboutlibrary.MaterialAboutActivity
import com.suda.datetimewallpaper.R
import com.umeng.analytics.MobclickAgent

/**
 * @author guhaibo
 * @date 2019/4/27
 */
abstract class BaseAboutActivity : MaterialAboutActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val upArrow = resources.getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        supportActionBar?.setHomeAsUpIndicator(upArrow);
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this)
    }
}
