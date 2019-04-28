package com.suda.datetimewallpaper.service

import android.service.wallpaper.WallpaperService
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import com.suda.datetimewallpaper.util.SharedPreferencesUtil
import com.suda.datetimewallpaper.view.DateTimeDrawer

/**
 * @author guhaibo
 * @date 2019/4/9
 */
class LiveWallPaperService : WallpaperService() {

    override fun onCreateEngine(): WallpaperService.Engine {
        return LiveWallpaperEngine()
    }

    private inner class LiveWallpaperEngine : WallpaperService.Engine() {

        private var dateTimeDrawer: DateTimeDrawer? = null

        internal var clickTime = 0
        internal var lastTime = System.currentTimeMillis()

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
            dateTimeDrawer = DateTimeDrawer()
            Log.d("@@@@@", "onCreate")
        }

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            val sharedPreferencesUtil = SharedPreferencesUtil(this@LiveWallPaperService)
            dateTimeDrawer!!.init(holder, this@LiveWallPaperService, true, sharedPreferencesUtil.lastPaperId)
            Log.d("@@@@@", "onSurfaceCreated")
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            if (visible) {
                val sharedPreferencesUtil = SharedPreferencesUtil(this@LiveWallPaperService)
                dateTimeDrawer!!.resetPaperId(sharedPreferencesUtil.lastPaperId, false)
            }
            dateTimeDrawer!!.onVisibilityChanged(visible)
            Log.d("@@@@@", "onVisibilityChanged$visible")
        }

        override fun onSurfaceRedrawNeeded(holder: SurfaceHolder) {
            super.onSurfaceRedrawNeeded(holder)
            dateTimeDrawer!!.resetConf(true)
            Log.d("@@@@@", "onSurfaceRedrawNeeded")
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            dateTimeDrawer!!.onSurfaceChange(width, height)
            Log.d("@@@@@", "onSurfaceChanged:$width,$height")
        }

        override fun onDesiredSizeChanged(desiredWidth: Int, desiredHeight: Int) {
            super.onDesiredSizeChanged(desiredWidth, desiredHeight)
            Log.d("@@@@@", "onDesiredSizeChanged")
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            dateTimeDrawer!!.onVisibilityChanged(false)
            Log.d("@@@@@", "onSurfaceDestroyed")
        }

        override fun onTouchEvent(event: MotionEvent) {
            super.onTouchEvent(event)
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (event.downTime - lastTime > 200) {
                    clickTime = 1
                } else {
                    clickTime++
                }
                lastTime = event.downTime
                if (clickTime % 3 == 0) {
                    if (SharedPreferencesUtil.getAppDefault(this@LiveWallPaperService).getData("click_change", true)) {
                        val sharedPreferencesUtil = SharedPreferencesUtil(this@LiveWallPaperService)
                        dateTimeDrawer!!.resetPaperId(sharedPreferencesUtil.nextWallPaper)
                    }
                }
            }
        }
    }
}
