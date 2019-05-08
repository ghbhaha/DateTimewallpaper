package com.suda.datetimewallpaper.view.weather

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.suda.datetimewallpaper.view.weather.line.BaseLine
import com.suda.datetimewallpaper.view.weather.line.HazeLine
import com.suda.datetimewallpaper.view.weather.line.RainOrSnowLine

/**
 * @author guhaibo
 * @date 2019/5/3
 */
const val RAIN_COUNT = 120 //雨点个数

class RainSnowHazeWeather(width: Int, height: Int, private val type: Type = Type.RAIN) : BaseWeather(width, height) {

    private var rainLines: MutableList<BaseLine> = mutableListOf<BaseLine>()
    private var paint: Paint? = null

    init {
        rainLines = ArrayList()
        when (type) {
            RainSnowHazeWeather.Type.RAIN_SNOW, RainSnowHazeWeather.Type.SNOW, RainSnowHazeWeather.Type.RAIN -> for (i in 0 until RAIN_COUNT) {
                rainLines!!.add(RainOrSnowLine(width, height))
            }
            RainSnowHazeWeather.Type.HAZE -> for (i in 0 until RAIN_COUNT) {
                rainLines!!.add(HazeLine(width, height))
            }
        }

        paint = Paint()
        paint!!.strokeWidth = getFitSize(3f)
        if (paint != null) {
            paint!!.color = Color.WHITE
        }
    }


    /**
     * 画子类
     *
     * @param canvas
     */
    override fun draw(canvas: Canvas) {
        paint!!.setShadowLayer(getFitSize(10f), 0f, 0f, Color.WHITE)

        var rain = true
        for (rainLine in rainLines!!) {
            paint!!.alpha = rainLine.alpha
            if (type == Type.HAZE) {
                paint!!.alpha = 100
                val rect1 = RectF(
                    rainLine.startX - getFitSize(5f), rainLine.startY - getFitSize(5f),
                    rainLine.startX + getFitSize(5f), rainLine.startY + getFitSize(5f)
                )
                canvas.drawArc(rect1, 0f, 360f, false, paint!!)
            } else if (type == Type.SNOW) {
                val rect3 = RectF(
                    rainLine.startX - getFitSize(8f), rainLine.startY - getFitSize(8f),
                    rainLine.startX + getFitSize(8f), rainLine.startY + getFitSize(8f)
                )
                canvas.drawArc(rect3, 0f, 360f, false, paint!!)
            } else {
                if (type == Type.RAIN_SNOW)
                    rain = !rain
                if (rain) {
                    paint!!.setShadowLayer(0f, 0f, 0f, Color.WHITE)
                    canvas.drawLine(
                        rainLine.startX.toFloat(),
                        rainLine.startY.toFloat(),
                        rainLine.stopX.toFloat(),
                        rainLine.getStopY() + getFitSize(8f),
                        paint!!
                    )
                } else {
                    val rect3 = RectF(
                        rainLine.startX - getFitSize(8f),
                        rainLine.startY - getFitSize(8f),
                        rainLine.startX + getFitSize(8f),
                        rainLine.startY + getFitSize(5f)
                    )
                    canvas.drawArc(rect3, 0f, 360f, false, paint!!)
                }
            }
        }

    }

    /**
     * 动画逻辑处理
     */
    override fun animLogic() {
        for (rainLine in rainLines!!) {
            rainLine.rain()
        }
    }

    enum class Type {
        RAIN, SNOW, RAIN_SNOW, HAZE
    }
}