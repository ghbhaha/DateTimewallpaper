package com.suda.datetimewallpaper.service;

import android.service.dreams.DreamService;

import android.view.View;
import com.suda.datetimewallpaper.R;

/**
 * @author guhaibo
 * @date 2019/4/9
 */
public class DateTimeDreamService extends DreamService {
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setFullscreen(true);
        setContentView(R.layout.dream_service_layout);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
}
