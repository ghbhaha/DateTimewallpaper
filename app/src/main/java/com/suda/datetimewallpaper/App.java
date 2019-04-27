package com.suda.datetimewallpaper;

import android.app.Application;
import com.suda.datetimewallpaper.util.SharedPreferencesUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

/**
 * @author guhaibo
 * @date 2019/4/9
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, null);
    }
}
