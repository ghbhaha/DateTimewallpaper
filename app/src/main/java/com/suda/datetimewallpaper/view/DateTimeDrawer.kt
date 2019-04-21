package com.suda.datetimewallpaper.view

import android.content.Context
import android.graphics.*
import android.media.ExifInterface
import android.os.Build
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.SurfaceHolder
import android.view.WindowManager
import android.widget.Toast
import androidx.collection.ArrayMap
import com.alibaba.fastjson.JSON
import com.suda.datetimewallpaper.R
import com.suda.datetimewallpaper.bean.DrawBean
import com.suda.datetimewallpaper.bean.TextBean
import com.suda.datetimewallpaper.util.*
import com.suda.datetimewallpaper.util.SharedPreferencesUtil.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author guhaibo
 * @date 2019/4/9
 */
class DateTimeDrawer {

    private var circleBaseline = 0f

    private var context: Context? = null
    private val clockPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val centerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var surfaceHolder: SurfaceHolder? = null
    private val matrix = Matrix()
    private val bgMatrix = Matrix()

    private val scheduledThreadPool = Executors.newScheduledThreadPool(1)
    private var scheduledFuture: ScheduledFuture<*>? = null

    private val mCurCalendar = Calendar.getInstance()

    private var verticalPos = 0.5f
    private var horizontalPos = 0.5f

    private var rotate = 0f
    private var scale = 0f

    private var textColor = Color.WHITE
    private var darkenTextColor = Color.WHITE
    private var bgColor = Color.BLACK

    private var bgImg = ""
    private var bgBitmap: Bitmap? = null

    private var drawConfName = ""
    private var changeConf = false

    private val schedule = 16

    private var monthIndex = 0
    private var dayIndex = 0
    private var weekIndex = 0
    internal var secondDelta = 0f
    internal var secondIndex = 0
    private var amOrPm = 0
    private var hourIndex = 0
    private var minusIndex = 0
    private var current: Date? = null

    private var useHardCanvas = false
    private var canUseHardCanvas = false
    private val textBeans = ArrayList<String>()

    private var lunarCalendar: LunarCalendar? = null

    private val start = AtomicBoolean(true)
    private var drawBean: DrawBean? = null

    private var cusTypeFace: Typeface? = null

    /**
     * 用于刷新农历
     */
    private var lastDayIndex = -1
    /**
     * 优化反复创建SimpleDateFormat
     */
    private val simpleDateFormatMap = ArrayMap<String, SimpleDateFormat>()

    private val refreshTask = object : TimerTask() {
        override fun run() {
            val currentTimeInMillis = System.currentTimeMillis()
            mCurCalendar.timeInMillis = currentTimeInMillis
            monthIndex = mCurCalendar.get(Calendar.MONTH)
            dayIndex = mCurCalendar.get(Calendar.DATE) - 1
            weekIndex = mCurCalendar.get(Calendar.DAY_OF_WEEK) - 1
            hourIndex = mCurCalendar.get(Calendar.HOUR_OF_DAY) - 1
            amOrPm = mCurCalendar.get(Calendar.AM_PM)
            minusIndex = mCurCalendar.get(Calendar.MINUTE) - 1
            secondIndex = mCurCalendar.get(Calendar.SECOND) - 1
            current = mCurCalendar.time


            if (lastDayIndex != dayIndex) {
                lunarCalendar = LunarCalendar(mCurCalendar)
                simpleDateFormatMap.clear()
                lastDayIndex = dayIndex
            }

            if (hourIndex == -1) {
                hourIndex = 23
            }

            if (minusIndex == -1) {
                minusIndex = 59
            }

            if (secondIndex == -1) {
                secondIndex = 59
            }

            //处理动画
            val sd = currentTimeInMillis % 1000 / 500f

            //静止时不再绘制，降低功耗
            if (secondDelta == 0f && sd >= 1 && !changeConf) {
                return
            }
            changeConf = false
            secondDelta = sd
            if (sd >= 1f) {
                secondDelta = 1f
            }
            secondDelta = secondDelta - 1
            if (start.get()) {
                onDraw()
            }
        }
    }

    private fun onDraw() {
        var canvas: Canvas? = null
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && useHardCanvas && canUseHardCanvas) {
                // miui 清理最近任务时会报错，蛋疼
                canvas = surfaceHolder!!.lockHardwareCanvas()
            } else {
                canvas = surfaceHolder!!.lockCanvas()
            }
            if (canvas == null) {
                return
            }
            if (bgBitmap != null) {
                canvas.drawBitmap(bgBitmap!!, bgMatrix, bgPaint)
            } else {
                canvas.drawColor(bgColor)
            }
            drawCenter(canvas)
            drawCircle(canvas)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (canvas != null) {
                surfaceHolder!!.unlockCanvasAndPost(canvas)
            }
        }
    }

    private fun drawCenter(canvas: Canvas?) {
        centerPaint.textSize = drawBean!!.centerTextSize * 1.0f / drawBean!!.centerText.size
        centerPaint.color = textColor
        val fontMetrics = centerPaint.fontMetrics
        val halfTextHeight = (fontMetrics.bottom - fontMetrics.top) * 2

        drawBean!!.centerText.forEachIndexed { i, textBean ->
            if (!textBean.array.isEmpty()) {
                var centerText = ""
                try {
                    if ("dateformat" == textBean.type) {
                        var simpleDateFormat = simpleDateFormatMap[textBean.array[0]]
                        if (simpleDateFormat == null) {
                            //农历解析
                            val format = textBean.array[0]
                                .replace("LA", lunarCalendar!!.animalsYear())
                                .replace("LM", lunarCalendar!!.monthStr)
                                .replace("LD", lunarCalendar!!.dayStr)
                            simpleDateFormat = SimpleDateFormat(format)
                            simpleDateFormatMap[textBean.array[0]] = simpleDateFormat
                        }
                        centerText = simpleDateFormat.format(current)
                    } else {
                        val index = getIndex(textBean)
                        if (index.index < textBean.array.size) {
                            centerText = textBean.array[index.index]
                        }
                    }
                } catch (e: Exception) {
                    return
                }

                centerPaint.typeface = if (textBean.useCusFont == 1) cusTypeFace else null
                val strLength = centerPaint.measureText(centerText)
                matrix.reset()
                matrix.postTranslate(canvas!!.width * horizontalPos, canvas.height * verticalPos)
                matrix.postTranslate(-strLength / 2, 0f)
                matrix.postRotate(rotate * 360, canvas.width * horizontalPos, canvas.height * verticalPos)
                matrix.postScale(this.scale, this.scale, canvas.width * horizontalPos, canvas.height * verticalPos)
                canvas.matrix = matrix
                val h = i * halfTextHeight - halfTextHeight / 2 * (drawBean!!.centerText.size - 1)
                val baseline = (h - (fontMetrics.descent - fontMetrics.ascent)) / 2 - fontMetrics.ascent
                centerPaint.isFakeBoldText = textBean.bold == 1
                canvas.drawText(centerText, 0f, baseline, centerPaint)
            }
        }
    }

    /**
     * @param canvas
     */
    private fun drawCircle(canvas: Canvas?) {
        drawBean!!.circleText.forEach {
            val index = getIndex(it)
            clockPaint.isFakeBoldText = it.bold == 1
            clockPaint.textSize = drawBean!!.circleTextSize.toFloat()
            if (circleBaseline == 0f) {
                val fontMetrics = clockPaint.fontMetrics
                circleBaseline = (0 - (fontMetrics.descent - fontMetrics.ascent)) / 2 - fontMetrics.ascent
            }
            val width = canvas!!.width
            val height = canvas.height
            val addD = 360f / it.array.size

            textBeans.clear()
            textBeans.addAll(it.array)

            val clockwise = it.clockwise
            val select: Int
            var degree: Float
            if (clockwise == 1) {
                select = textBeans.size - 1 - index.index
                degree = 0f - (addD * (index.index + 1) + addD * index.delta) * -1
                Collections.reverse(textBeans)
            } else {
                select = index.index
                degree = 0f - (addD * index.index + addD * index.delta) * 1
            }

            textBeans.forEachIndexed { i, str ->
                matrix.reset()
                matrix.postTranslate(width * horizontalPos, height * verticalPos)
                matrix.postTranslate(it.dis, 0f)
                matrix.postRotate(degree + rotate * 360, width * horizontalPos, height * verticalPos)
                matrix.postScale(this.scale, this.scale, width * horizontalPos, height * verticalPos)
                canvas.matrix = matrix
                if ("text" == it.type) {
                    clockPaint.color = textColor
                } else {
                    if (index.delta == -1f || index.delta == 0f) {
                        clockPaint.color = if (i == select) textColor else darkenTextColor
                    } else {
                        clockPaint.color = darkenTextColor
                    }
                }
                clockPaint.typeface = if (it.useCusFont == 1) cusTypeFace else null
                canvas.drawText(str, 0f, circleBaseline, clockPaint)
                degree += addD
            }
        }
    }

    private fun getIndex(textBean: TextBean): Index {
        var curIndex: Int
        var delta = 0f
        when (textBean.type) {
            "month" -> {
                curIndex = monthIndex
            }
            "day" -> {
                curIndex = dayIndex
            }
            "lunarAnimal" -> {
                curIndex = lunarCalendar!!.animalsYearInt()
            }
            "lunarMonth" -> {
                curIndex = lunarCalendar!!.month - 1
            }
            "lunarDay" -> {
                curIndex = lunarCalendar!!.day - 1
            }
            "hour" -> {
                curIndex = hourIndex % textBean.array.size
            }
            "hour_23_23" -> {
                hourIndex = hourIndex + 1
                if (hourIndex == 23) {
                    curIndex = 0
                } else if (hourIndex % 2 == 1) {
                    curIndex = (hourIndex + 1) / 2
                } else {
                    curIndex = hourIndex / 2
                }
            }
            "minute" -> {
                curIndex = minusIndex
                //secondIndex==-1表示到达下一分钟，处理分钟动画
                delta = if (secondIndex == 59) secondDelta else 0f
            }
            "second" -> {
                delta = secondDelta
                curIndex = secondIndex
            }
            "week" -> {
                curIndex = weekIndex
            }
            "ampm" -> {
                curIndex = amOrPm
            }
            else -> {
                curIndex = 0
            }
        }
        return Index(curIndex, delta)
    }

    internal class Index(var index: Int, var delta: Float)

    /**
     * 初始化
     *
     * @param holder
     * @param context
     * @param userHardCanvas
     */
    fun init(holder: SurfaceHolder, context: Context, userHardCanvas: Boolean) {
        this.useHardCanvas = userHardCanvas
        this.canUseHardCanvas = !OSHelper.isMIUI()
        clockPaint.isAntiAlias = true
        clockPaint.isDither = true
        this.context = context
        surfaceHolder = holder
    }

    /**
     * 重新配置
     */
    fun resetConf(force: Boolean) {
        start.set(false)
        verticalPos = SharedPreferencesUtil.getData(SP_VERTICAL_POS, 0.5f) as Float
        horizontalPos = SharedPreferencesUtil.getData(SP_HORIZONTAL_POS, 0.5f) as Float
        rotate = SharedPreferencesUtil.getData(SP_ROTATE, 0f) as Float


        val windowManager = context!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(dm)
        scale = (2 * SharedPreferencesUtil.getData(SP_SCALE, 0.25f) as Float + 0.5f) * 0.52f * (dm.widthPixels / 1080f)

        textColor = SharedPreferencesUtil.getData(SP_TEXT_COLOR, Color.WHITE) as Int
        darkenTextColor = SharedPreferencesUtil.getData(SP_TEXT_COLOR_DARK, textColor.dark()) as Int
        if (force) {
            drawConfName = ""
        }
        resetJsonConf()
        setBg()

        circleBaseline = 0f
        lastDayIndex = -1

        changeConf = true
        start.set(true)
    }

    private fun resetJsonConf() {
        var cus: String = SharedPreferencesUtil.getData(SP_CUS_CONF, "") as String
        if (!TextUtils.isEmpty(cus)) {
            if (cus != drawConfName) {
                drawConfName = cus
                try {
                    drawBean = JSON.parseObject(FileUtil.getFromFile(File(drawConfName)), DrawBean::class.java)
                } catch (e: Exception) {
                    Toast.makeText(context, R.string.conf_error, Toast.LENGTH_SHORT).show()
                }

                if (drawBean == null) {
                    drawBean =
                        JSON.parseObject(AssetsUtil.getFromAssets("default1.json", context), DrawBean::class.java)
                }
                setCircleTextAndCalDis()
            }
        } else {
            val numFormat = SharedPreferencesUtil.getData(SP_NUM_FORMAT, true) as Boolean
            if (numFormat) {
                cus = "default1.json"
            } else {
                cus = "default2.json"
            }
            if (cus != drawConfName) {
                drawBean = JSON.parseObject(AssetsUtil.getFromAssets(cus, context), DrawBean::class.java)
                drawConfName = cus
            }
            setCircleTextAndCalDis()
        }
    }

    /**
     * 计算文字及距离
     */
    private fun setCircleTextAndCalDis() {
        if (!TextUtils.isEmpty(drawBean!!.cusFont)) {
            val file = File(FileUtil.getBaseFile(), drawBean!!.cusFont)
            if (file.exists()) {
                cusTypeFace = Typeface.createFromFile(file)
            } else {
                cusTypeFace = null
            }
        } else {
            cusTypeFace = null
        }

        //计算绘制距离
        var dis = 90f
        for (textBean in drawBean!!.circleText) {
            textBean.dis = dis
            var length = 0f
            clockPaint.textSize = drawBean!!.circleTextSize.toFloat()
            clockPaint.typeface = if (textBean.useCusFont == 1) cusTypeFace else null
            for (drawStr in textBean.array) {
                length = Math.max(length, clockPaint.measureText(drawStr))
            }
            dis += length + 4
        }
    }

    /**
     * 设置背景
     */
    private fun setBg() {
        bgColor = SharedPreferencesUtil.getData(SP_BG_COLOR, Color.BLACK) as Int
        val tmpBg = SharedPreferencesUtil.getData(SP_BG_IMAGE, "") as String
        if (!TextUtils.isEmpty(tmpBg) && tmpBg != bgImg) {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(tmpBg, options)
            //图片的宽高
            val outHeight = options.outHeight
            val outWidth = options.outWidth
            val windowManager = context!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val dm = DisplayMetrics()
            windowManager.defaultDisplay.getRealMetrics(dm)
            val sHeigth = dm.heightPixels
            val sWidth = dm.widthPixels

            val p1 = outHeight * 1.0f / outWidth
            val p2 = sHeigth * 1.0f / sWidth
            val matrix = Matrix()
            matrix.postTranslate((sWidth - outWidth) * 1f / 2, (sHeigth - outHeight) * 1f / 2)

            val scale: Float
            if (p1 < p2) {
                scale = sHeigth * 1f / outHeight
            } else {
                scale = sWidth * 1f / outWidth
            }

            var tag = 0
            var exifInterface: ExifInterface? = null
            try {
                exifInterface = ExifInterface(tmpBg)
                tag = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            var degree = 0
            if (tag == ExifInterface.ORIENTATION_ROTATE_90) {
                degree = 90
            } else if (tag == ExifInterface.ORIENTATION_ROTATE_180) {
                degree = 180
            } else if (tag == ExifInterface.ORIENTATION_ROTATE_270) {
                degree = 270
            }

            matrix.postRotate(degree.toFloat(), (sWidth / 2).toFloat(), (sHeigth / 2).toFloat())
            matrix.postScale(scale, scale, (sWidth / 2).toFloat(), (sHeigth / 2).toFloat())
            //图片格式压缩
            options.inJustDecodeBounds = false
            val bitmap = BitmapFactory.decodeFile(tmpBg, options)
            bgBitmap = Bitmap.createBitmap(sWidth, sHeigth, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bgBitmap!!)
            canvas.drawBitmap(bitmap, matrix, Paint())
        } else if (TextUtils.isEmpty(tmpBg)) {
            bgBitmap = null
        }
        bgImg = tmpBg
    }

    /**
     * 显示隐藏
     *
     * @param visible
     */
    fun onVisibilityChanged(visible: Boolean) {
        if (context == null) {
            return
        }
        if (visible) {
            resetConf(true)
            scheduledFuture =
                scheduledThreadPool.scheduleAtFixedRate(refreshTask, 0, schedule.toLong(), TimeUnit.MILLISECONDS)
        } else {
            if (scheduledFuture != null) {
                scheduledFuture!!.cancel(true)
                bgImg = ""
                bgBitmap = null
            }
            System.gc()
        }
    }

}