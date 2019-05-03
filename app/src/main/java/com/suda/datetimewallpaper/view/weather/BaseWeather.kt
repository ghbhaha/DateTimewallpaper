package com.suda.datetimewallpaper.view.weather

import android.graphics.Canvas

/**
 * @author guhaibo
 * @date 2019/5/3
 */
abstract class BaseWeather(val width: Int, val height: Int) {

    /**
     * 动画逻辑
     */
    abstract fun animLogic()

    /**
     * 绘制
     */
    abstract fun draw(canvas: Canvas)

    fun getFitSize(size: Float): Float {
        return size * width / 1080f
    }
}