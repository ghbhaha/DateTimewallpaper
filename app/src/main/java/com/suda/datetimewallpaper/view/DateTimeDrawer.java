package com.suda.datetimewallpaper.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import com.suda.datetimewallpaper.util.SharedPreferencesUtil;

import java.io.IOException;
import java.util.Calendar;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.suda.datetimewallpaper.util.SharedPreferencesUtil.SP_BG_COLOR;
import static com.suda.datetimewallpaper.util.SharedPreferencesUtil.SP_BG_IMAGE;
import static com.suda.datetimewallpaper.util.SharedPreferencesUtil.SP_HORIZONTAL_POS;
import static com.suda.datetimewallpaper.util.SharedPreferencesUtil.SP_NUM_FORMAT;
import static com.suda.datetimewallpaper.util.SharedPreferencesUtil.SP_ROTATE;
import static com.suda.datetimewallpaper.util.SharedPreferencesUtil.SP_SCALE;
import static com.suda.datetimewallpaper.util.SharedPreferencesUtil.SP_TEXT_COLOR;
import static com.suda.datetimewallpaper.util.SharedPreferencesUtil.SP_VERTICAL_POS;

/**
 * @author guhaibo
 * @date 2019/4/9
 */
final public class DateTimeDrawer {

    private Context context;
    private Paint clockPaint = new Paint();
    private Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private SurfaceHolder surfaceHolder;
    private Matrix matrix = new Matrix();
    private Matrix bgMatrix = new Matrix();

    private ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);
    private ScheduledFuture scheduledFuture;

    private Calendar mCurCalendar = Calendar.getInstance();

    private float verticalPos = 0.5f;
    private float horizontalPos = 0.5f;

    private float rotate = 0f;
    private float scale = 0f;

    private int textColor = Color.WHITE;
    private int bgColor = Color.BLACK;

    private String bgImg = "";
    private Bitmap bgBitmap;

    private int schedule = 10;

    private int monthIndex = 0;
    private String[] months = null;

    private int dayIndex = 0;
    private String[] days = null;

    private int weekIndex = 0;
    private String[] weeks = {
            "日", "一", "二", "三", "四", "五", "六"
    };

    private boolean amOrPm = true;

    private int hourIndex = 0;
    private String[] hours = null;

    private int minusIndex = 0;
    private String[] minutes = null;

    private boolean userHardCanvas = true;


    float secondDelta = 0f;
    int secondIndex = 0;
    private String[] seconds = minutes;
    private AtomicBoolean start = new AtomicBoolean(true);

    private TimerTask refreshTask = new TimerTask() {
        @Override
        public void run() {
            mCurCalendar.setTimeInMillis(System.currentTimeMillis());
            monthIndex = mCurCalendar.get(Calendar.MONTH);
            dayIndex = mCurCalendar.get(Calendar.DATE) - 1;
            weekIndex = mCurCalendar.get(Calendar.DAY_OF_WEEK) - 1;
            hourIndex = (mCurCalendar.get(Calendar.HOUR_OF_DAY) - 1) % 12;

            amOrPm = mCurCalendar.get(Calendar.AM_PM) == 0;

            if (hourIndex == -1) {
                hourIndex = 11;
            }
            minusIndex = mCurCalendar.get(Calendar.MINUTE) - 1;
            secondIndex = mCurCalendar.get(Calendar.SECOND) - 1;

            //处理动画
            secondDelta = (System.currentTimeMillis() % 1000) * 1.0f / (schedule * 30);
            if (secondDelta > 1f) {
                secondDelta = 1f;
            }
            secondDelta = secondDelta - 1;
            if (start.get()) {
                onDraw();
            }
        }
    };

    private void onDraw() {
        Canvas canvas;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && userHardCanvas) {
            canvas = surfaceHolder.lockHardwareCanvas();
        } else {
            canvas = surfaceHolder.lockCanvas();
        }

        try {
            if (canvas == null) {
                return;
            }

            if (bgBitmap != null) {
                canvas.drawBitmap(bgBitmap, bgMatrix, bgPaint);
            } else {
                canvas.drawColor(bgColor);
            }

            drawCenter(canvas);

            drawText(canvas, 90, months, monthIndex, 0);
            drawText(canvas, 190, new String[]{"月"}, 0, 0);
            drawText(canvas, 250, days, dayIndex, 0);
            drawText(canvas, 400, new String[]{"号"}, 0, 0);
//        drawText(canvas, 450, new String[]{"周"}, 0, 0);
//        drawText(canvas, 500, weeks, weekIndex, 0);
            drawText(canvas, 460, hours, hourIndex, 0);
            drawText(canvas, 560, new String[]{"点"}, 0, 0);

            //secondIndex==-1表示到达下一分钟，处理分钟动画
            drawText(canvas, 620, minutes, minusIndex, secondIndex == -1 ? secondDelta : 0);
            drawText(canvas, 770, new String[]{"分"}, 0, 0);
            drawText(canvas, 830, seconds, secondIndex, secondDelta);
            drawText(canvas, 980, new String[]{"秒"}, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (canvas != null) {
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    private void drawCenter(Canvas canvas) {
        clockPaint.setTextSize(45);
        String week = "周" + weeks[weekIndex];
        float strLength = clockPaint.measureText(week);
        matrix.reset();
        float scale = 0.52f * (canvas.getWidth() / 1080f);
        matrix.postTranslate(canvas.getWidth() * horizontalPos, canvas.getHeight() * verticalPos);
        matrix.postTranslate(-strLength / 2, 0);
        matrix.postRotate(rotate * 360, canvas.getWidth() * horizontalPos, canvas.getHeight() * verticalPos);
        matrix.postScale(scale * this.scale, scale * this.scale, canvas.getWidth() * horizontalPos, canvas.getHeight() * verticalPos);
        clockPaint.setColor(textColor);
        canvas.setMatrix(matrix);
        canvas.drawText(week, 0, -10, clockPaint);
        canvas.drawText(amOrPm ? "上午" : "下午", 0, 50, clockPaint);

    }

    /**
     * @param canvas
     * @param radius
     * @param strArray
     * @param curIndex
     * @param delta
     */
    private void drawText(Canvas canvas, float radius, String[] strArray, int curIndex, float delta) {
        clockPaint.setTextSize(50);
        float scale = 0.52f * (canvas.getWidth() / 1080f);
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        float addD = 360f / strArray.length;
        float degree = 0f - addD * curIndex - addD * delta;
        int index = 0;
        for (String str : strArray) {
            matrix.reset();
            matrix.postTranslate(width * horizontalPos, height * verticalPos);
            matrix.postTranslate(radius, 20);
            matrix.postRotate(degree + rotate * 360, width * horizontalPos, height * verticalPos);
            matrix.postScale(scale * this.scale, scale * this.scale, width * horizontalPos, height * verticalPos);
            canvas.setMatrix(matrix);

            if (delta == -1f || delta == 0f) {
                clockPaint.setColor(index == curIndex ? textColor : darkenColor(textColor));
            } else {
                clockPaint.setColor(darkenColor(textColor));
            }
            canvas.drawText(str, 0, 0, clockPaint);
            degree += addD;
            index++;
        }
    }

    public static int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = hsv[2] * 0.5f;
        return Color.HSVToColor(hsv);
    }

    public void init(SurfaceHolder holder, Context context, boolean userHardCanvas) {
        this.userHardCanvas = userHardCanvas;
        clockPaint.setAntiAlias(true);
        clockPaint.setDither(true);
        clockPaint.setTextSize(50);
        this.context = context;
        surfaceHolder = holder;
    }

    public void resetConf() {
        verticalPos = (float) SharedPreferencesUtil.getData(SP_VERTICAL_POS, 0.5f);
        horizontalPos = (float) SharedPreferencesUtil.getData(SP_HORIZONTAL_POS, 0.5f);
        rotate = (float) SharedPreferencesUtil.getData(SP_ROTATE, 0f);
        scale = 2 * (float) SharedPreferencesUtil.getData(SP_SCALE, 0.25f) + 0.5f;
        boolean numFormat = (boolean) SharedPreferencesUtil.getData(SP_NUM_FORMAT, true);
        if (numFormat) {
            months = new String[]{
                    "壹", "贰", "仨", "肆", "伍", "陆",
                    "柒", "捌", "玖", "拾", "拾壹", "拾贰"
            };


            days = new String[]{
                    "\t\t\t\t壹", "\t\t\t\t贰", "\t\t\t\t叁", "\t\t\t\t肆", "\t\t\t\t伍", "\t\t\t\t陆",
                    "\t\t\t\t柒", "\t\t\t\t捌", "\t\t\t\t玖", "\t\t\t\t拾", "拾壹", "拾贰",
                    "拾叁", "拾肆", "拾伍", "拾陆", "拾柒", "拾捌",
                    "拾玖", "贰拾", "贰拾壹", "贰拾贰", "贰拾叁", "贰拾肆",
                    "贰拾伍", "贰拾陆", "贰拾柒", "贰拾捌", "贰拾玖", "叁拾",
                    "叁拾壹",
            };


            hours = new String[]{
                    "\t\t壹", "\t\t贰", "\t\t叁", "\t\t肆", "\t\t伍",
                    "\t\t陆", "\t\t柒", "\t\t捌", "\t\t玖", "\t\t拾", "拾壹", "拾贰"
            };

            minutes = new String[]{
                    "\t\t\t\t壹", "\t\t\t\t贰", "\t\t\t\t叁", "\t\t\t\t肆", "\t\t\t\t伍", "\t\t\t\t陆"
                    , "\t\t\t\t柒", "\t\t\t\t捌", "\t\t\t\t玖", "\t\t\t\t拾",
                    "拾壹", "拾贰", "拾叁", "拾肆", "拾伍", "拾陆", "拾柒", "拾捌", "拾玖", "贰拾",
                    "贰拾壹", "贰拾贰", "贰拾叁", "贰拾肆", "贰拾伍", "贰拾陆", "贰拾柒", "贰拾捌", "贰拾玖", "叁拾",
                    "叁拾壹", "叁拾贰", "叁拾叁", "叁拾肆", "叁拾伍", "叁拾陆", "叁拾柒", "叁拾捌", "叁拾玖", "肆拾",
                    "肆拾壹", "肆拾贰", "肆拾叁", "肆拾肆", "肆拾伍", "肆拾陆", "肆拾柒", "肆拾捌", "肆拾玖", "伍拾",
                    "伍拾壹", "伍拾贰", "伍拾叁", "伍拾肆", "伍拾伍", "伍拾陆", "伍拾柒", "伍拾捌", "伍拾玖", "\t\t\t\t零"
            };

            seconds = new String[]{
                    "\t\t\t\t壹", "\t\t\t\t贰", "\t\t\t\t叁", "\t\t\t\t肆", "\t\t\t\t伍", "\t\t\t\t陆"
                    , "\t\t\t\t柒", "\t\t\t\t捌", "\t\t\t\t玖", "\t\t\t\t拾",
                    "拾壹", "拾贰", "拾叁", "拾肆", "拾伍", "拾陆", "拾柒", "拾捌", "拾玖", "贰拾",
                    "贰拾壹", "贰拾贰", "贰拾叁", "贰拾肆", "贰拾伍", "贰拾陆", "贰拾柒", "贰拾捌", "贰拾玖", "叁拾",
                    "叁拾壹", "叁拾贰", "叁拾叁", "叁拾肆", "叁拾伍", "叁拾陆", "叁拾柒", "叁拾捌", "叁拾玖", "肆拾",
                    "肆拾壹", "肆拾贰", "肆拾叁", "肆拾肆", "肆拾伍", "肆拾陆", "肆拾柒", "肆拾捌", "肆拾玖", "伍拾",
                    "伍拾壹", "伍拾贰", "伍拾叁", "伍拾肆", "伍拾伍", "伍拾陆", "伍拾柒", "伍拾捌", "伍拾玖", "\t\t\t\t零"
            };


        } else {
            months = new String[]{
                    "一", "二", "三", "四", "五", "六",
                    "七", "八", "九", "十", "十一", "十二"
            };


            days = new String[]{
                    "\t\t\t\t一", "\t\t\t\t二", "\t\t\t\t三", "\t\t\t\t四", "\t\t\t\t五", "\t\t\t\t六",
                    "\t\t\t\t七", "\t\t\t\t八", "\t\t\t\t九", "\t\t\t\t十", "十一", "十二",
                    "十三", "十四", "十五", "十六", "十七", "十八",
                    "十九", "二十", "二十一", "二十二", "二十三", "二十四",
                    "二十五", "二十六", "二十七", "二十八", "二十九", "三十",
                    "三十一",
            };

            hours = new String[]{
                    "\t\t一", "\t\t二", "\t\t三", "\t\t四", "\t\t五",
                    "\t\t六", "\t\t七", "\t\t八", "\t\t九", "\t\t十", "十一", "十二"
            };

            minutes = new String[]{
                    "\t\t\t\t一", "\t\t\t\t二", "\t\t\t\t三", "\t\t\t\t四", "\t\t\t\t五", "\t\t\t\t六"
                    , "\t\t\t\t七", "\t\t\t\t八", "\t\t\t\t九", "\t\t\t\t十",
                    "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十",
                    "二十一", "二十二", "二十三", "二十四", "二十五", "二十六", "二十七", "二十八", "二十九", "三十",
                    "三十一", "三十二", "三十三", "三十四", "三十五", "三十六", "三十七", "三十八", "三十九", "四十",
                    "四十一", "四十二", "四十三", "四十四", "四十五", "四十六", "四十七", "四十八", "四十九", "五十",
                    "五十一", "五十二", "五十三", "五十四", "五十五", "五十六", "五十七", "五十八", "五十九", ""
            };

            seconds = new String[]{
                    "\t\t\t\t一", "\t\t\t\t二", "\t\t\t\t三", "\t\t\t\t四", "\t\t\t\t五", "\t\t\t\t六"
                    , "\t\t\t\t七", "\t\t\t\t八", "\t\t\t\t九", "\t\t\t\t十",
                    "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十",
                    "二十一", "二十二", "二十三", "二十四", "二十五", "二十六", "二十七", "二十八", "二十九", "三十",
                    "三十一", "三十二", "三十三", "三十四", "三十五", "三十六", "三十七", "三十八", "三十九", "四十",
                    "四十一", "四十二", "四十三", "四十四", "四十五", "四十六", "四十七", "四十八", "四十九", "五十",
                    "五十一", "五十二", "五十三", "五十四", "五十五", "五十六", "五十七", "五十八", "五十九", ""
            };
        }
        textColor = (int) SharedPreferencesUtil.getData(SP_TEXT_COLOR, Color.WHITE);
        setBg();
    }


    private void setBg() {
        bgColor = (int) SharedPreferencesUtil.getData(SP_BG_COLOR, Color.BLACK);
        String tmpBg = (String) SharedPreferencesUtil.getData(SP_BG_IMAGE, "");
        if (!TextUtils.isEmpty(tmpBg) && !tmpBg.equals(bgImg)) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(tmpBg, options);
            //图片的宽高
            int outHeight = options.outHeight;
            int outWidth = options.outWidth;
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            windowManager.getDefaultDisplay().getRealMetrics(dm);
            int sHeigth = dm.heightPixels;
            int sWidth = dm.widthPixels;

            float p1 = outHeight * 1.0f / outWidth;
            float p2 = sHeigth * 1.0f / sWidth;
            Matrix matrix = new Matrix();
            matrix.postTranslate((sWidth - outWidth) * 1f / 2, (sHeigth - outHeight) * 1f / 2);

            float scale = 1f;
            if (p1 < p2) {
                scale = sHeigth * 1f / outHeight;
            } else {
                scale = sWidth * 1f / outWidth;
            }

            int tag = 0;
            ExifInterface exifInterface = null;
            try {
                exifInterface = new ExifInterface(tmpBg);
                tag = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int degree = 0;
            if (tag == ExifInterface.ORIENTATION_ROTATE_90) {
                degree = 90;
            } else if (tag == ExifInterface.ORIENTATION_ROTATE_180) {
                degree = 180;
            } else if (tag == ExifInterface.ORIENTATION_ROTATE_270) {
                degree = 270;
            }

            matrix.postRotate(degree, sWidth / 2, sHeigth / 2);
            matrix.postScale(scale, scale, sWidth / 2, sHeigth / 2);
            //图片格式压缩
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(tmpBg, options);
            bgBitmap = Bitmap.createBitmap(sWidth, sHeigth, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bgBitmap);
            canvas.drawBitmap(bitmap, matrix, new Paint());
        } else if (TextUtils.isEmpty(tmpBg)) {
            bgBitmap = null;
        }
        bgImg = tmpBg;
    }

    public void onVisibilityChanged(boolean visible) {
        if (context == null) {
            return;
        }
        start.set(visible);
        if (visible) {
            resetConf();
            scheduledFuture = scheduledThreadPool.scheduleAtFixedRate(refreshTask, 0, schedule, TimeUnit.MILLISECONDS);
        } else {
            if (scheduledFuture != null) {
                scheduledFuture.cancel(true);
                bgImg = "";
                bgBitmap = null;
            }
        }
    }
}
