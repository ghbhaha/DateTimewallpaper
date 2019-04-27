package com.suda.datetimewallpaper.base

import androidx.appcompat.app.AppCompatActivity
import com.umeng.analytics.MobclickAgent

/**
 * @author guhaibo
 * @date 2019/4/23
 */
abstract class BaseAct : AppCompatActivity() {
    override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this)
    }
}