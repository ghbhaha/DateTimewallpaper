package com.suda.datetimewallpaper.service;

import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
import com.suda.datetimewallpaper.view.DateTimeDrawer;

/**
 * @author guhaibo
 * @date 2019/4/9
 */
final public class LiveWallPaperService extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new LiveWallpaperEngine();
    }

    final private class LiveWallpaperEngine extends WallpaperService.Engine {

        private DateTimeDrawer dateTimeDrawer;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            dateTimeDrawer = new DateTimeDrawer();
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            dateTimeDrawer.init(holder, LiveWallPaperService.this, true);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            dateTimeDrawer.onVisibilityChanged(visible);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            dateTimeDrawer.onVisibilityChanged(false);
        }
    }
}
