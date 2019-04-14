package com.suda.datetimewallpaper.util;

import android.app.Activity;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import com.suda.datetimewallpaper.service.LiveWallPaperService;

/**
 * @author guhaibo
 * @date 2019/4/9
 */
final public class WallpaperUtil {
    /**
     * 跳转到系统设置壁纸界面
     *
     * @param activity
     */
    public static void setLiveWallpaper(Activity activity, int requestCode) {
        try {
            Intent localIntent = new Intent();
            localIntent.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
            localIntent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    new ComponentName(activity.getApplicationContext().getPackageName(), LiveWallPaperService.class.getCanonicalName()));
            activity.startActivityForResult(localIntent, requestCode);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    /**
     * 判断是否是使用我们的壁纸
     *
     * @param context
     * @return
     */
    public static boolean wallpaperIsUsed(Context context) {
        WallpaperInfo localWallpaperInfo = WallpaperManager.getInstance(context).getWallpaperInfo();
        return ((localWallpaperInfo != null) &&
                (localWallpaperInfo.getPackageName().equals(context.getPackageName())) &&
                (localWallpaperInfo.getServiceName().equals(LiveWallPaperService.class.getCanonicalName())));
    }
}
