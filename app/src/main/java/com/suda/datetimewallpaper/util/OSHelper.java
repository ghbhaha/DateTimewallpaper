package com.suda.datetimewallpaper.util;

/**
 * 设备系统辅助类
 *
 * @author liyunlong
 * @date 2017/5/27 11:36
 */

import android.os.Build;

import java.lang.reflect.Method;


public final class OSHelper {

    private static final String KEY_EMUI_VERSION_CODE = "ro.build.version.emui";
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";

    public static boolean isFlyme() {
        try {
            final Method method = Build.class.getMethod("hasSmartBar");
            return method != null;
        } catch (final Exception e) {
            return false;
        }
    }

    public static boolean isEMUI() {
        return isPropertiesExist(KEY_EMUI_VERSION_CODE);
    }

    public static boolean isMIUI() {
        return isPropertiesExist(KEY_MIUI_VERSION_CODE, KEY_MIUI_VERSION_NAME, KEY_MIUI_INTERNAL_STORAGE);
    }

    private static boolean isPropertiesExist(String... keys) {
        if (keys == null || keys.length == 0) {
            return false;
        }
        try {

            for (String key : keys) {
                String value = getProperty(key);
                if (value != null) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getProperty(String key) {
        String value = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            value = (String) (get.invoke(c, key, "unknown"));
            if (value.equals("unknown")) {
                return null;
            } else {
                return value;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return null;
    }
}