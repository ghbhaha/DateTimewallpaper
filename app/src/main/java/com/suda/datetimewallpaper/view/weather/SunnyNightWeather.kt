package com.suda.datetimewallpaper.view.weather

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import java.util.*


/**
 * @author guhaibo
 * @date 2019/5/3
 */
class SunnyNightWeather(width: Int, height: Int) : BaseWeather(width, height) {
    private var paint = Paint()
    private val STAR_COUNT = 150 //星星数
    private val stars = mutableListOf<Star>()

    init {
        paint.strokeWidth = getFitSize(3f)
        paint.color = Color.WHITE
        paint.setShadowLayer(getFitSize(10f), 0f, 0f, Color.WHITE)
        for (i in 0 until STAR_COUNT) {
            stars.add(Star(width, height))
        }
    }

    override fun draw(canvas: Canvas) {
        for (star in stars) {
            //虚化边缘
            paint.alpha = star.getCurrentAlpha()
            val fitRadius = getFitSize(star.getRadius())
            val rect = RectF(
                star.x - fitRadius, star.y - fitRadius,
                star.x + fitRadius, star.y + fitRadius
            )
            canvas.drawArc(rect, 0f, 360f, false, paint)
        }
    }

    override fun animLogic() {
        for (star in stars) {
            star.shine()
        }
    }

    class Star(maxX: Int, maxY: Int) {
        var x: Int = 0 //x最大范围
        var y: Int = 0 //y最大范围
        var random = Random()
        private var radius = 4f
        private val minAlpha = 30
        private val maxAlpha = 140
        private var currentAlpha = minAlpha
        private var alphaDelta = 2

        init {
            this.x = random.nextInt(maxX)
            this.y = random.nextInt(maxY)
            this.radius = 2f + random.nextInt(2)
            currentAlpha = minAlpha + random.nextInt(110)
        }


        fun shine() {
            if (outOfBounds())
                alphaDelta = -alphaDelta
            currentAlpha += alphaDelta
        }

        fun outOfBounds(): Boolean {
            return currentAlpha >= maxAlpha || currentAlpha < minAlpha
        }

        fun getCurrentAlpha(): Int {
            return currentAlpha
        }


        fun getRadius(): Float {
            return radius
        }

        /////////////////////////////////////////////////////////////////

    }

}

