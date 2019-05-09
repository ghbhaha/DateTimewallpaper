package com.suda.datetimewallpaper.service

import android.service.dreams.DreamService
import android.view.View
import com.suda.datetimewallpaper.R
import com.suda.datetimewallpaper.util.SharedPreferencesUtil
import com.suda.datetimewallpaper.view.DateTimeView

/**
 * @author guhaibo
 * @date 2019/4/9
 */
class DateTimeDreamService : DreamService() {
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isFullscreen = true
        setContentView(R.layout.layout_dream_service)
        val dateTimeView = findViewById<View>(R.id.dtv) as DateTimeView
        val sharedPreferencesUtil = SharedPreferencesUtil(this)
        dateTimeView.resetPaperId(sharedPreferencesUtil.nextWallPaperDream)
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN)
        decorView.systemUiVisibility = uiOptions
    }
}
