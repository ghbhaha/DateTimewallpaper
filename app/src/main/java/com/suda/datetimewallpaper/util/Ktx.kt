package com.suda.datetimewallpaper.util

import android.graphics.Color

/**
 * @author guhaibo
 * @date 2019/4/21
 */
fun Int.dark(): Int {
    val hsv = FloatArray(3)
    Color.colorToHSV(this, hsv)
    hsv[2] = hsv[2] * 0.5f
    return Color.HSVToColor(hsv)
}