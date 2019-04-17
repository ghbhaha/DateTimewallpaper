package com.suda.datetimewallpaper.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.suda.datetimewallpaper.bean.DrawBean;
import com.suda.datetimewallpaper.bean.TextBean;
import com.suda.datetimewallpaper.util.AssetsUtil;
import com.suda.datetimewallpaper.util.FileUtil;
import com.suda.datetimewallpaper.util.SharedPreferencesUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.suda.datetimewallpaper.util.SharedPreferencesUtil.SP_BG_COLOR;
import static com.suda.datetimewallpaper.util.SharedPreferencesUtil.SP_BG_IMAGE;
import static com.suda.datetimewallpaper.util.SharedPreferencesUtil.SP_CUS_CONF;
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
    private Paint clockPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint centerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
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
    private int darkenTextColor = Color.WHITE;
    private int bgColor = Color.BLACK;

    private String bgImg = "";
    private Bitmap bgBitmap;

    private String drawConfName = "";
    private boolean changeConf = false;

    private int schedule = 16;

    private int monthIndex = 0;
    private int dayIndex = 0;
    private int weekIndex = 0;
    float secondDelta = 0f;
    int secondIndex = 0;
    private int amOrPm = 0;
    private int hourIndex = 0;
    private int minusIndex = 0;
    private Date current;

    private boolean userHardCanvas = true;
    private AtomicBoolean start = new AtomicBoolean(true);
    private DrawBean drawBean;

    private Typeface cusTypeFace;

    private TimerTask refreshTask = new TimerTask() {
        @Override
        public void run() {
            mCurCalendar.setTimeInMillis(System.currentTimeMillis());
            monthIndex = mCurCalendar.get(Calendar.MONTH);
            dayIndex = mCurCalendar.get(Calendar.DATE) - 1;
            weekIndex = mCurCalendar.get(Calendar.DAY_OF_WEEK) - 1;
            hourIndex = (mCurCalendar.get(Calendar.HOUR_OF_DAY) - 1);
            amOrPm = mCurCalendar.get(Calendar.AM_PM);
            minusIndex = mCurCalendar.get(Calendar.MINUTE) - 1;
            secondIndex = mCurCalendar.get(Calendar.SECOND) - 1;
            current = mCurCalendar.getTime();

            if (hourIndex == -1) {
                hourIndex = 23;
            }

            if (minusIndex == -1) {
                minusIndex = 59;
            }

            if (secondIndex == -1) {
                secondIndex = 59;
            }

            //处理动画
            float sd = (System.currentTimeMillis() % 1000) * 1.0f / (schedule * 25);

            //静止时不再绘制，降低功耗
            if (secondDelta == 0 && sd > 1 && !changeConf) {
                return;
            }
            changeConf = false;
            secondDelta = sd;
            if (sd >= 1f) {
                secondDelta = 1f;
            }
            secondDelta = secondDelta - 1;
            if (start.get()) {
                onDraw();
            }
        }
    };

    private void onDraw() {
        Canvas canvas = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && userHardCanvas) {

                // miui 清理最近任务时会报错，蛋疼
                // canvas = surfaceHolder.lockHardwareCanvas();

                canvas = surfaceHolder.lockCanvas();
            } else {
                canvas = surfaceHolder.lockCanvas();
            }
            if (canvas == null) {
                return;
            }
            if (bgBitmap != null) {
                canvas.drawBitmap(bgBitmap, bgMatrix, bgPaint);
            } else {
                canvas.drawColor(bgColor);
            }
            drawCenter(canvas);
            drawCircle(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (canvas != null) {
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    private void drawCenter(Canvas canvas) {
        centerPaint.setTextSize(drawBean.getCenterTextSize() * 1.0f / drawBean.getCenterText().size());
        centerPaint.setColor(textColor);
        Paint.FontMetrics fontMetrics = centerPaint.getFontMetrics();
        float dis = (fontMetrics.top - fontMetrics.bottom) * 2;
        int i = 0;
        for (TextBean textBean : drawBean.getCenterText()) {
            if (textBean.getArray().isEmpty()) {
                continue;
            }
            String centerText = "";
            try {
                if ("dateformat".equals(textBean.getType())) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(textBean.getArray().get(0));
                    centerText = simpleDateFormat.format(current);
                } else {
                    Index index = getIndex(textBean);
                    if (index.index < textBean.getArray().size()) {
                        centerText = textBean.getArray().get(index.index);
                    }
                }
            } catch (Exception e) {
                continue;
            }
            centerPaint.setTypeface(textBean.getUseCusFont() == 1 ? cusTypeFace : null);
            float strLength = centerPaint.measureText(centerText);
            matrix.reset();
            float scale = 0.52f * (canvas.getWidth() / 1080f);
            matrix.postTranslate(canvas.getWidth() * horizontalPos, canvas.getHeight() * verticalPos);
            matrix.postTranslate(-strLength / 2, 0);
            matrix.postRotate(rotate * 360, canvas.getWidth() * horizontalPos, canvas.getHeight() * verticalPos);
            matrix.postScale(scale * this.scale, scale * this.scale, canvas.getWidth() * horizontalPos, canvas.getHeight() * verticalPos);
            canvas.setMatrix(matrix);
            float h = i * dis - (dis / 2) * (drawBean.getCenterText().size() - 1);
            float baseline = (h - (fontMetrics.descent - fontMetrics.ascent)) / 2 - fontMetrics.ascent;
            centerPaint.setFakeBoldText(textBean.getBold() == 1);
            canvas.drawText(centerText, 0, baseline, centerPaint);
            i++;
        }

    }

    /**
     * @param canvas
     */
    private void drawCircle(Canvas canvas) {
        for (TextBean textBean : drawBean.getCircleText()) {
            float radius = textBean.getDis();
            Index index = getIndex(textBean);
            clockPaint.setFakeBoldText(textBean.getBold() == 1);
            clockPaint.setTextSize(drawBean.getCircleTextSize());
            float scale = 0.52f * (canvas.getWidth() / 1080f);
            int width = canvas.getWidth();
            int height = canvas.getHeight();
            float addD = 360f / textBean.getArray().size();
            float degree = 0f - addD * index.index - addD * index.delta;
            int i = 0;
            Paint.FontMetrics fontMetrics = clockPaint.getFontMetrics();
            float baseline = (0 - (fontMetrics.descent -
                    fontMetrics.ascent)) / 2 - fontMetrics.ascent;
            for (String str : textBean.getArray()) {
                matrix.reset();
                matrix.postTranslate(width * horizontalPos, height * verticalPos);
                matrix.postTranslate(radius, 0);
                matrix.postRotate(degree + rotate * 360, width * horizontalPos, height * verticalPos);
                matrix.postScale(scale * this.scale, scale * this.scale, width * horizontalPos, height * verticalPos);
                canvas.setMatrix(matrix);
                if ("text".equals(textBean.getType())) {
                    clockPaint.setColor(textColor);
                } else {
                    if (index.delta == -1f || index.delta == 0f) {
                        clockPaint.setColor(i == index.index ? textColor : darkenTextColor);
                    } else {
                        clockPaint.setColor(darkenTextColor);
                    }
                }
                clockPaint.setTypeface(textBean.getUseCusFont() == 1 ? cusTypeFace : null);
                canvas.drawText(str, 0, baseline, clockPaint);
                degree += addD;
                i++;
            }
        }
    }

    private Index getIndex(TextBean textBean) {
        int curIndex = 0;
        float delta = 0;
        if ("month".equals(textBean.getType())) {
            curIndex = monthIndex;
        } else if ("day".equals(textBean.getType())) {
            curIndex = dayIndex;
        } else if ("hour".equals(textBean.getType())) {
            curIndex = hourIndex % textBean.getArray().size();
        } else if ("hour_23_23".equals(textBean.getType())) {
            hourIndex = hourIndex + 1;
            if (hourIndex == 23) {
                curIndex = 0;
            } else if (hourIndex % 2 == 1) {
                curIndex = (hourIndex + 1) / 2;
            } else {
                curIndex = hourIndex / 2;
            }
        } else if ("minute".equals(textBean.getType())) {
            curIndex = minusIndex;
            //secondIndex==-1表示到达下一分钟，处理分钟动画
            delta = secondIndex == -1 ? secondDelta : 0;
        } else if ("second".equals(textBean.getType())) {
            delta = secondDelta;
            curIndex = secondIndex;
        } else if ("week".equals(textBean.getType())) {
            curIndex = weekIndex;
        } else if ("ampm".equals(textBean.getType())) {
            curIndex = amOrPm;
        } else {
            curIndex = 0;
        }
        return new Index(curIndex, delta);
    }

    class Index {
        int index;
        float delta;

        public Index(int index, float delta) {
            this.index = index;
            this.delta = delta;
        }
    }

    /**
     * 初始化
     *
     * @param holder
     * @param context
     * @param userHardCanvas
     */
    public void init(SurfaceHolder holder, Context context, boolean userHardCanvas) {
        this.userHardCanvas = userHardCanvas;
        clockPaint.setAntiAlias(true);
        clockPaint.setDither(true);

        this.context = context;
        surfaceHolder = holder;
    }

    /**
     * 重新配置
     */
    public void resetConf(boolean force) {
        start.set(false);
        verticalPos = (float) SharedPreferencesUtil.getData(SP_VERTICAL_POS, 0.5f);
        horizontalPos = (float) SharedPreferencesUtil.getData(SP_HORIZONTAL_POS, 0.5f);
        rotate = (float) SharedPreferencesUtil.getData(SP_ROTATE, 0f);
        scale = 2 * (float) SharedPreferencesUtil.getData(SP_SCALE, 0.25f) + 0.5f;
        textColor = (int) SharedPreferencesUtil.getData(SP_TEXT_COLOR, Color.WHITE);
        darkenTextColor = darkenColor(textColor);
        if (force) {
            drawConfName = "";
        }
        resetJsonConf();
        setBg();
        changeConf = true;
        start.set(true);
    }

    private void resetJsonConf() {
        String cus = (String) SharedPreferencesUtil.getData(SP_CUS_CONF, "");
        if (!TextUtils.isEmpty(cus)) {
            if (!cus.equals(drawConfName)) {
                drawConfName = cus;
                try {
                    drawBean = JSON.parseObject(FileUtil.getFromFile(new File(drawConfName)), DrawBean.class);
                } catch (Exception e) {
                    Toast.makeText(context, "您的配置有问题", Toast.LENGTH_SHORT).show();
                }
                if (drawBean == null) {
                    drawBean = JSON.parseObject(AssetsUtil.getFromAssets("default1.json", context), DrawBean.class);
                }
                setCircleTextAndCalDis();
            }
        } else {
            Boolean numFormat = (boolean) SharedPreferencesUtil.getData(SP_NUM_FORMAT, true);
            if (numFormat) {
                cus = "default1.json";
            } else {
                cus = "default2.json";
            }
            if (!cus.equals(drawConfName)) {
                drawBean = JSON.parseObject(AssetsUtil.getFromAssets(cus, context), DrawBean.class);
                drawConfName = cus;
            }
            setCircleTextAndCalDis();
        }
    }

    /**
     * 计算文字及距离
     */
    private void setCircleTextAndCalDis() {
        if (!TextUtils.isEmpty(drawBean.getCusFont())) {
            File file = new File(FileUtil.getBaseFile(), drawBean.getCusFont());
            if (file.exists()) {
                cusTypeFace = Typeface.createFromFile(file);
            } else {
                cusTypeFace = null;
            }
        } else {
            cusTypeFace = null;
        }

        //计算绘制距离
        float dis = 90;
        for (TextBean textBean : drawBean.getCircleText()) {
            textBean.setDis(dis);
            float length = 0;
            clockPaint.setTextSize(drawBean.getCircleTextSize());
            clockPaint.setTypeface(textBean.getUseCusFont() == 1 ? cusTypeFace : null);
            for (String drawStr : textBean.getArray()) {
                length = Math.max(length, clockPaint.measureText(drawStr));
            }
            dis += length + 4;
        }
    }

    /**
     * 设置背景
     */
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

    /**
     * 显示隐藏
     *
     * @param visible
     */
    public void onVisibilityChanged(boolean visible) {
        if (context == null) {
            return;
        }
        if (visible) {
            resetConf(true);
            scheduledFuture = scheduledThreadPool.scheduleAtFixedRate(refreshTask, 0, schedule, TimeUnit.MILLISECONDS);
        } else {
            if (scheduledFuture != null) {
                scheduledFuture.cancel(true);
                bgImg = "";
                bgBitmap = null;
            }
        }
    }

    /**
     * 加深颜色
     *
     * @param color
     * @return
     */
    private int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = hsv[2] * 0.5f;
        return Color.HSVToColor(hsv);
    }
}
