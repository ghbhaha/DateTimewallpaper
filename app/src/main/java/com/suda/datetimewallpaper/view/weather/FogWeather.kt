package com.suda.datetimewallpaper.view.weather

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF


/**
 * @author guhaibo
 * @date 2019/5/3
 */
/**
 * @author guhaibo
 * @date 2019/5/3
 */
class FogWeather(width: Int, height: Int) : BaseWeather(width, height) {

    private var paint = Paint()

    //圆最小半径
    private val MIN = getFitSize(1080f)
    //圆最大半径
    private val MAX = MIN + getFitSize(80f)
    //圆半径
    private var radius = MIN
    private var deltaRadius = 0.5f

    init {
        paint.strokeWidth = getFitSize(3f);
        paint.isAntiAlias = true;
        paint.color = Color.WHITE;
        paint.alpha = 70;
    }


    override fun draw(canvas: Canvas) {
        if (radius > MAX) {
            deltaRadius = -deltaRadius
        }
        if (radius < MIN) {
            deltaRadius = -deltaRadius
        }

        val rect1 = RectF(-radius, -radius, radius, radius)
        val rect2 = RectF(width - radius, height - radius, width + radius, height + radius)

        canvas.drawArc(rect1, 0f, 360f, false, paint)
        canvas.drawArc(rect2, 0f, 360f, false, paint)
    }

    override fun animLogic() {
        radius += deltaRadius.toFloat()
    }
}