package com.suda.datetimewallpaper.util;

import android.app.Activity;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
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
            Intent intent = new Intent();
            intent.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    new ComponentName(activity.getApplicationContext().getPackageName(), LiveWallPaperService.class.getCanonicalName()));
            activity.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            e.printStackTrace();
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction("android.service.wallpaper.LIVE_WALLPAPER_CHOOSER");
            try {
                activity.startActivity(intent);
            } catch (Exception e2) {
                e2.printStackTrace();
                Toast.makeText(activity, "您的系统壁纸功能貌似被阉割了...", Toast.LENGTH_SHORT).show();
            }
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
