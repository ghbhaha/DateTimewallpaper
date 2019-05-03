package com.suda.datetimewallpaper.view.weather

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF

/**
 * @author guhaibo
 * @date 2019/5/3
 */
class SunnyDayWeather(width: Int, height: Int) : BaseWeather(width, height) {

    private var paint = Paint()

    //最内圆最小半径
    private val MIN = getFitSize(200f)
    //最内圆最大半径
    private val MAX = getFitSize(260f)

    private val addRadius = getFitSize(200f)


    //最内圆半径
    private var radius = MIN.toFloat()
    private var deltaRadius = 1f / 2

    init {
        paint.strokeWidth = 3f
        paint.isAntiAlias = true
        paint.color = Color.WHITE
    }


    override fun draw(canvas: Canvas) {
        if (radius > MAX) {
            deltaRadius = -deltaRadius
        }
        if (radius < MIN) {
            deltaRadius = -deltaRadius
        }

        val rect1 = RectF(-radius, -radius, radius, radius)
        val rect2 = RectF(-(radius + addRadius), -(radius + addRadius), radius + addRadius, radius + addRadius)
        val rect3 =
            RectF(-(radius + 2 * addRadius), -(radius + 2 * addRadius), radius + 2 * addRadius, radius + 2 * addRadius)
        paint.alpha = 50
        canvas.drawArc(rect3, 0f, 360f, false, paint)
        paint.alpha = 30
        canvas.drawArc(rect2, 0f, 360f, false, paint)
        paint.alpha = 15
        canvas.drawArc(rect1, 0f, 360f, false, paint)

    }

    override fun animLogic() {
        radius += deltaRadius.toFloat()
    }
}