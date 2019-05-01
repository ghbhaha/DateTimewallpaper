package com.suda.datetimewallpaper.service

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
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

        private var clickTime = 0
        private var lastTime = System.currentTimeMillis()

        private val sensorManager by lazy {
            getSystemService(SENSOR_SERVICE) as SensorManager
        }

        val rvListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

            }

            override fun onSensorChanged(event: SensorEvent?) {
                event?.run {
                    val rotationMatrix = FloatArray(16)
                    SensorManager.getRotationMatrixFromVector(
                        rotationMatrix, this.values
                    )
                    // Remap coordinate system
                    val remappedRotationMatrix = FloatArray(16)
                    SensorManager.remapCoordinateSystem(
                        rotationMatrix,
                        SensorManager.AXIS_X,
                        SensorManager.AXIS_Z,
                        remappedRotationMatrix
                    )

                    // Convert to orientations
                    val orientations = FloatArray(3)
                    SensorManager.getOrientation(rotationMatrix, orientations)
                    for (i in 0..2) {
                        orientations[i] = Math.toDegrees(orientations[i].toDouble()).toFloat()
                    }
                    dateTimeDrawer?.resetCameraMatrix(orientations[1]/2, orientations[2]/2, 0f)

                }
            }

        }

        val accelerometerListener = object : SensorEventListener {
            private val SPEED_SHRESHOLD = 4000
            // 两次检测的时间间隔
            private val UPTATE_INTERVAL_TIME = 50
            private var lastUpdateTime: Long = 0
            private var lastX: Float = 0.toFloat()
            private var lastY: Float = 0.toFloat()
            private var lastZ: Float = 0.toFloat()
            private var isShaking = false

            override fun onSensorChanged(event: SensorEvent?) {
                event?.run {
                    // 现在检测时间
                    val currentUpdateTime = System.currentTimeMillis()
                    // 两次检测的时间间隔
                    val timeInterval = currentUpdateTime - lastUpdateTime
                    // 判断是否达到了检测时间间隔
                    if (isShaking || timeInterval < UPTATE_INTERVAL_TIME) return
                    // 现在的时间变成last时间
                    lastUpdateTime = currentUpdateTime
                    // 获得x,y,z坐标
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]
                    // 获得x,y,z的变化值
                    val deltaX = x - lastX
                    val deltaY = y - lastY
                    val deltaZ = z - lastZ
                    // 将现在的坐标变成last坐标
                    lastX = x
                    lastY = y
                    lastZ = z
                    val speed =
                        Math.sqrt((deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ).toDouble()) / timeInterval * 10000
                    // 达到速度阀值，发出提示
                    if (speed >= SPEED_SHRESHOLD) {
                        isShaking = true
                        Handler().postDelayed({
                            isShaking = false
                        }, 500)
                        val sharedPreferencesUtil = SharedPreferencesUtil(this@LiveWallPaperService)
                        dateTimeDrawer!!.resetPaperId(sharedPreferencesUtil.nextWallPaper)
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

            }
        }

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
                if (SharedPreferencesUtil.getAppDefault(this@LiveWallPaperService).getData("shake_change", false)) {
                    val accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                    accelerometerSensor?.run {
                        sensorManager.registerListener(accelerometerListener, this, SensorManager.SENSOR_DELAY_UI)
                    }
                }
                if (SharedPreferencesUtil.getAppDefault(this@LiveWallPaperService).getData(
                        "perspective_mode_sensor",
                        false
                    )
                ) {
                    val rotateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
                    rotateSensor?.run {
                        sensorManager.registerListener(rvListener, this, SensorManager.SENSOR_DELAY_UI)
                    }
                }
            } else {
                sensorManager.unregisterListener(accelerometerListener)
                sensorManager.unregisterListener(rvListener)
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
                dateTimeDrawer!!.resetCameraRotate(event)
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
            } else if (event.action == MotionEvent.ACTION_MOVE) {
                dateTimeDrawer!!.resetCameraRotate(event)
            } else if (event.action == MotionEvent.ACTION_UP) {
                dateTimeDrawer!!.resetCameraRotate(event)
                dateTimeDrawer!!.startShakeAnim()
            }
        }
    }
}
