package com.suda.datetimewallpaper.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import com.alibaba.fastjson.JSON;
import com.suda.datetimewallpaper.bean.WallPaperModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author guhaibo
 * @date 2019/4/9
 */
public class SharedPreferencesUtil {

    public static final String SP_NUM_FORMAT = "num_format";
    public static final String SP_VERTICAL_POS = "verticalPos";
    public static final String SP_HORIZONTAL_POS = "horizontalPos";
    public static final String SP_SCALE = "scale";
    public static final String SP_ROTATE = "rotate";
    public static final String SP_TEXT_COLOR = "text_color";
    public static final String SP_TEXT_COLOR_DARK = "text_color_dark";
    public static final String SP_BG_COLOR = "bg_color";
    public static final String SP_BG_IMAGE = "SP_BG_IMAGE";
    public static final String SP_CUS_CONF = "SP_CUS_CONF";

    public static final String SP_CONFS = "SP_CONFS";

    public static final String SP_HIDE_ACT = "hide_from_recent";

    public static final String AREA_NAME = "AREA_NAME";
    public static final String AREA_CODE = "AREA_CODE";
    public static final String AREA_WEATHER = "AREA_WEATHER";

    private SharedPreferences sp;
    private Context context;

    public SharedPreferencesUtil(Context context) {
        this.context = context;
        sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    public SharedPreferencesUtil(Context context, Long paperId) {
        this.context = context;
        sp = context.getSharedPreferences(context.getPackageName() + ":" + paperId, Context.MODE_PRIVATE);
    }

    public SharedPreferencesUtil(Context context, String key) {
        this.context = context;
        sp = context.getSharedPreferences(key, Context.MODE_PRIVATE);
    }


    public static SharedPreferencesUtil getAppDefault(Context context){
        return new SharedPreferencesUtil(context,context.getPackageName()+"_preferences");
    }

    /**
     * 保存数据到SharedPreferences
     *
     * @param key   键
     * @param value 需要保存的数据
     * @return 保存结果
     */
    public boolean putData(String key, Object value) {
        boolean result;
        SharedPreferences.Editor editor = sp.edit();
        String type = value.getClass().getSimpleName();
        try {
            switch (type) {
                case "Boolean":
                    editor.putBoolean(key, (Boolean) value);
                    break;
                case "Long":
                    editor.putLong(key, (Long) value);
                    break;
                case "Float":
                    editor.putFloat(key, (Float) value);
                    break;
                case "String":
                    editor.putString(key, (String) value);
                    break;
                case "Integer":
                    editor.putInt(key, (Integer) value);
                    break;
                default:
                    break;
            }
            result = true;
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        }
        editor.apply();
        return result;
    }

    /**
     * 获取SharedPreferences中保存的数据
     *
     * @param key          键
     * @param defaultValue 获取失败默认值
     * @return 从SharedPreferences读取的数据
     */
    public <T> T getData(String key, T defaultValue) {
        Object result;
        String type = defaultValue.getClass().getSimpleName();
        try {
            switch (type) {
                case "Boolean":
                    result = sp.getBoolean(key, (Boolean) defaultValue);
                    break;
                case "Long":
                    result = sp.getLong(key, (Long) defaultValue);
                    break;
                case "Float":
                    result = sp.getFloat(key, (Float) defaultValue);
                    break;
                case "String":
                    result = sp.getString(key, (String) defaultValue);
                    break;
                case "Integer":
                    result = sp.getInt(key, (Integer) defaultValue);
                    break;
                default:
                    result = null;
                    break;
            }
        } catch (Exception e) {
            result = null;
            e.printStackTrace();
        }
        return (T) result;
    }

    public List<WallPaperModel> getWallpapermodels() {
        String confs = getData(SharedPreferencesUtil.SP_CONFS, "");
        if (confs.isEmpty()) {
            WallPaperModel wallPaperModel = new WallPaperModel("默认样式", 0, true, 0);


            SharedPreferencesUtil _0sp = new SharedPreferencesUtil(context, 0L);
            SharedPreferencesUtil _def_sp = new SharedPreferencesUtil(context);

            _0sp.putData(SP_NUM_FORMAT, _def_sp.getData(SP_NUM_FORMAT, true));
            _0sp.putData(SP_VERTICAL_POS, _def_sp.getData(SP_VERTICAL_POS, 0.5f));
            _0sp.putData(SP_HORIZONTAL_POS, _def_sp.getData(SP_HORIZONTAL_POS, 0.5f));
            _0sp.putData(SP_ROTATE, _def_sp.getData(SP_ROTATE, 0f));
            _0sp.putData(SP_SCALE, _def_sp.getData(SP_SCALE, 0.25f));
            _0sp.putData(SP_TEXT_COLOR_DARK, _def_sp.getData(SP_TEXT_COLOR_DARK, Color.GRAY));
            _0sp.putData(SP_TEXT_COLOR, _def_sp.getData(SP_TEXT_COLOR, Color.WHITE));
            _0sp.putData(SP_BG_COLOR, _def_sp.getData(SP_BG_COLOR, Color.BLACK));
            _0sp.putData(SP_BG_IMAGE, _def_sp.getData(SP_BG_IMAGE, ""));
            _0sp.putData(SP_CUS_CONF, _def_sp.getData(SP_CUS_CONF, ""));

            List modelList = new ArrayList();
            modelList.add(wallPaperModel);
            confs = JSON.toJSONString(modelList);
            putData(SharedPreferencesUtil.SP_CONFS, confs);
        }
        return JSON.parseArray(confs, WallPaperModel.class);
    }

    public WallPaperModel addNewDefaultModel() {
        List<WallPaperModel> wallPaperModels = getWallpapermodels();
        WallPaperModel wallPaperModel = new WallPaperModel("默认样式", System.currentTimeMillis(), false, wallPaperModels.get(wallPaperModels.size() - 1).getOrderId() + 1);
        wallPaperModels.add(wallPaperModel);
        putData(SharedPreferencesUtil.SP_CONFS, JSON.toJSONString(wallPaperModels));
        return wallPaperModel;
    }

    public void deleteConf(long paperId) {
        List<WallPaperModel> wallPaperModels = getWallpapermodels();
        Iterator<WallPaperModel> iterator = wallPaperModels.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getPaperId() == paperId) {
                iterator.remove();
            }
        }
        putData(SharedPreferencesUtil.SP_CONFS, JSON.toJSONString(wallPaperModels));
    }

    public void editName(long paperId, String paperName) {
        List<WallPaperModel> wallPaperModels = getWallpapermodels();
        Iterator<WallPaperModel> iterator = wallPaperModels.iterator();
        while (iterator.hasNext()) {
            WallPaperModel wallPaperModel = iterator.next();
            if (wallPaperModel.getPaperId() == paperId) {
                wallPaperModel.setModelName(paperName);
            }
        }
        putData(SharedPreferencesUtil.SP_CONFS, JSON.toJSONString(wallPaperModels));
    }

    public void setWallPaperId(long paperId) {
        sp.edit().putLong("last", paperId).apply();
    }

    public long getLastPaperId() {
        long last = sp.getLong("last", -1L);
        if (last == -1) {
            List<WallPaperModel> wallPaperModels = getWallpapermodels();
            last = wallPaperModels.get(0).getPaperId();
        }
        return last;
    }

    public long getNextWallPaper() {
        List<WallPaperModel> wallPaperModels = getWallpapermodels();
        long last = sp.getLong("last", -1L);
        long tmp = last;
        Iterator<WallPaperModel> iterator = wallPaperModels.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getPaperId() == last) {
                if (iterator.hasNext()) {
                    tmp = iterator.next().getPaperId();
                }
            }
        }
        if (last == tmp) {
            last = wallPaperModels.get(0).getPaperId();
        } else {
            last = tmp;
        }

        sp.edit().putLong("last", last).apply();
        return last;
    }

    public long getNextWallPaperDream() {
        List<WallPaperModel> wallPaperModels = getWallpapermodels();
        long last = sp.getLong("lastDream", -1L);
        long tmp = last;
        Iterator<WallPaperModel> iterator = wallPaperModels.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getPaperId() == last) {
                if (iterator.hasNext()) {
                    tmp = iterator.next().getPaperId();
                }
            }
        }
        if (last == tmp) {
            last = wallPaperModels.get(0).getPaperId();
        } else {
            last = tmp;
        }
        sp.edit().putLong("lastDream", last).apply();
        return last;
    }
}
