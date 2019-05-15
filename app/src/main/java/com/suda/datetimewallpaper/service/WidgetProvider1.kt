package com.suda.datetimewallpaper.service

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * @author guhaibo
 * @date 2019/5/14
 */
class WidgetProvider1 : AppWidgetProvider() {

    override fun onRestored(context: Context?, oldWidgetIds: IntArray?, newWidgetIds: IntArray?) {
        super.onRestored(context, oldWidgetIds, newWidgetIds)
        Log.d("WidgetProvider1", "onRestored$this")
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
        Log.d("WidgetProvider1", "onDeleted$this")
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        Log.d("WidgetProvider1", "onDisabled$this")
        val intent = Intent(context, WidgetRefreshService1::class.java)
        context?.stopService(intent)
    }

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        val intent = Intent(context, WidgetRefreshService1::class.java)
        intent.putExtra("command", 0)
        context?.startService(intent)
    }
}
