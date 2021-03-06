package com.suda.datetimewallpaper.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @author guhaibo
 * @date 2019/4/8
 */
final public class DateTimeView extends SurfaceView implements SurfaceHolder.Callback {
    DateTimeDrawer dateTimeDrawer;

    public DateTimeView(Context context) {
        super(context);
        init();
    }

    public DateTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        dateTimeDrawer = new DateTimeDrawer();
        dateTimeDrawer.init(getHolder(), getContext(), false, 0);
        this.getHolder().addCallback(this);
    }

    public void resetPaperId(long paperId) {
        dateTimeDrawer.resetPaperId(paperId, true);
    }

    public void resetConf(boolean force) {
        dateTimeDrawer.resetConf(force);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        dateTimeDrawer.onSurfaceChange(getWidth(), getHeight());
        dateTimeDrawer.onVisibilityChanged(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            dateTimeDrawer.resetCameraRotate(event);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            dateTimeDrawer.resetCameraRotate(event);
            dateTimeDrawer.startShakeAnim();
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        dateTimeDrawer.onVisibilityChanged(false);
    }
}
