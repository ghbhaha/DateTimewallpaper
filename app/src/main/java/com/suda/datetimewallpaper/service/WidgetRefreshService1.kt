package com.suda.datetimewallpaper.service

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.graphics.Bitmap
import android.widget.RemoteViews
import com.suda.datetimewallpaper.R

class WidgetRefreshService1 : WidgetRefreshService() {
    override fun createBitmap(): Bitmap {
        return Bitmap.createBitmap(720, 360, Bitmap.Config.ARGB_4444)
    }

    override fun getRv(): RemoteViews {
        return RemoteViews(packageName, R.layout.widget1)
    }

    override fun refreshRv(rv: RemoteViews) {
        AppWidgetManager.getInstance(this)
            .updateAppWidget(ComponentName(this, WidgetProvider1::class.java), rv)
    }
}
