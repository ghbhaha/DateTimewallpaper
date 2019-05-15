package com.suda.datetimewallpaper.service

import android.app.Service
import android.content.Intent
import android.graphics.*
import android.os.IBinder
import android.widget.RemoteViews
import com.suda.datetimewallpaper.R
import com.suda.datetimewallpaper.util.SharedPreferencesUtil
import com.suda.datetimewallpaper.view.DateTimeDrawer
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

abstract class WidgetRefreshService : Service() {
    private var canvas = Canvas()
    private var bitmap: Bitmap? = null
    private lateinit var dateTimeDrawer: DateTimeDrawer
    private var scheduledFuture: ScheduledFuture<*>? = null
    var clearPaint = Paint()
    var isStart = false

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        dateTimeDrawer = DateTimeDrawer()
        dateTimeDrawer.init(null, this, false, SharedPreferencesUtil(this).lastWidgetId)
        dateTimeDrawer.isWidget = true
        bitmap = createBitmap()
        dateTimeDrawer.onSurfaceChange(bitmap!!.width, bitmap!!.height)
        dateTimeDrawer.resetConf(true)
        canvas.setBitmap(bitmap)
        clearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        val scheduledThreadPool = Executors.newScheduledThreadPool(1)
        scheduledFuture =
            scheduledThreadPool.scheduleAtFixedRate({
                refresh()
            }, 0, 1000, TimeUnit.MILLISECONDS)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.run {
            val command = intent!!.getIntExtra("command", -1)
            if (command == 1) {
                if (isStart) {
                    dateTimeDrawer.resetPaperId(SharedPreferencesUtil(this@WidgetRefreshService).lastWidgetId, true)
                } else {
                    stopSelf()
                    return super.onStartCommand(intent, flags, startId)
                }
            } else if (command == 0) {
                dateTimeDrawer.refreshWeather()
                isStart = true
            }
            return START_STICKY
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        scheduledFuture?.cancel(true)
    }

    private fun refresh() {
        val rv = getRv()
        dateTimeDrawer.resetTime()
        canvas.drawPaint(clearPaint)
        dateTimeDrawer.drawCenter(canvas, Matrix())
        dateTimeDrawer.drawCircle(canvas, Matrix())
        rv.setImageViewBitmap(R.id.date_img, bitmap)
        refreshRv(rv)
    }

    abstract fun getRv(): RemoteViews
    abstract fun refreshRv(rv: RemoteViews)
    abstract fun createBitmap(): Bitmap


}
