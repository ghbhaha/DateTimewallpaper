package com.suda.datetimewallpaper.util

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.suda.datetimewallpaper.R

/**
 * @author guhaibo
 * @date 2019/4/21
 */

/**
 * 扩展int加深颜色
 */
fun Int.dark(): Int {
    val hsv = FloatArray(3)
    Color.colorToHSV(this, hsv)
    hsv[2] = hsv[2] * 0.5f
    return Color.HSVToColor(hsv)
}

/**
 * 扩展Long 用于获取方法执行时间
 */
fun Long.getSpend(tag: String) {
    Log.d("###spend", tag + (System.currentTimeMillis() - this))
}

fun Activity.setExcludeFromRecents(exclude: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        try {
            val service = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (appTask in service.appTasks) {
                if (appTask.taskInfo.id == taskId) {
                    appTask.setExcludeFromRecents(exclude)
                }
            }
            Log.d("###exclude", "$taskId:$exclude")
        } catch (e: Exception) {
        }
    } else {
        Toast.makeText(this, R.string.version_low, Toast.LENGTH_SHORT).show()
    }
}