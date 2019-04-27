package com.suda.datetimewallpaper.util

import android.graphics.Color
import android.util.Log

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