package com.suda.datetimewallpaper;

import android.app.Application;
import com.suda.datetimewallpaper.util.AssetsCopyUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import okhttp3.OkHttpClient;

/**
 * @author guhaibo
 * @date 2019/4/9
 */
public class App extends Application {

    public final static OkHttpClient GLOBAL_OK_HTTP = new OkHttpClient();

    @Override
    public void onCreate() {
        super.onCreate();
        AssetsCopyUtil.copyEmbassy2Databases(this, getDatabasePath("location.db"));
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, null);
    }
}
