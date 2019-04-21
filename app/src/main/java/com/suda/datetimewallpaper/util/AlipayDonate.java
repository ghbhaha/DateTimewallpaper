package com.suda.datetimewallpaper.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.service.quicksettings.TileService;
import android.view.View;
import android.widget.Toast;

import com.suda.datetimewallpaper.R;

import java.net.URISyntaxException;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by didikee on 2017/7/21.
 * 使用支付宝捐赠
 */

public class AlipayDonate {

    public static void donateTip(String key, int maxClick, final Context context) {

        String key1 = "DONATE:" + key;
        final String key2 = "lastClickTime:" + key;

        int clickTime = (int) SharedPreferencesUtil.getData(key1, 0);
        SharedPreferencesUtil.putData(key1, clickTime + 1);

        long lastDonateTime = (long) SharedPreferencesUtil.getData(key2, 0L);

        //每两天会弹
        if (clickTime > 0 && clickTime % maxClick == 0 && System.currentTimeMillis() - lastDonateTime > 2 * 24 * 3600 * 1000) {
            SharedPreferencesUtil.putData(key2, System.currentTimeMillis());
            final MaterialDialog outDialog = new MaterialDialog(context);
            outDialog.setTitle(R.string.thanks_for_support);
            outDialog.setMessage(R.string.donate_tip);
            outDialog.setCanceledOnTouchOutside(true);
            outDialog.setPositiveButton(R.string.want_donate, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean hasInstalledAlipayClient = AlipayDonate.hasInstalledAlipayClient(context);
                    if (hasInstalledAlipayClient) {
                        Toast.makeText(context, "感谢支持,时间轮盘将越来越好", Toast.LENGTH_SHORT).show();
                        AlipayDonate.startAlipayClient((Activity) context, "apqiqql0hgh5pmv54d");
                    }
                    Toast.makeText(context, R.string.thanks_for_support, Toast.LENGTH_SHORT).show();
                }
            });

            outDialog.setNegativeButton(R.string.copy_code, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData mClipData = ClipData.newPlainText("红包码", "521043031");
                    cm.setPrimaryClip(mClipData);
                    Toast.makeText(context, "复制成功，快去支付宝领取吧", Toast.LENGTH_SHORT).show();
                    outDialog.dismiss();
                }
            });
            outDialog.show();
        }
    }


    // 支付宝包名
    private static final String ALIPAY_PACKAGE_NAME = "com.eg.android.AlipayGphone";

    // 旧版支付宝二维码通用 Intent Scheme Url 格式
    private static final String INTENT_URL_FORMAT = "intent://platformapi/startapp?saId=10000007&" +
            "clientVersion=3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2F{payCode}%3F_s" +
            "%3Dweb-other&_t=1472443966571#Intent;" +
            "scheme=alipayqr;package=com.eg.android.AlipayGphone;end";

    /**
     * 打开转账窗口
     * 旧版支付宝二维码方法，需要使用 https://fama.alipay.com/qrcode/index.htm 网站生成的二维码
     * 这个方法最好，但在 2016 年 8 月发现新用户可能无法使用
     *
     * @param activity Parent Activity
     * @param urlCode  手动解析二维码获得地址中的参数，例如 https://qr.alipay.com/aehvyvf4taua18zo6e 最后那段
     * @return 是否成功调用
     */
    public static boolean startAlipayClient(Activity activity, String payCode) {
        return startIntentUrl(activity, INTENT_URL_FORMAT.replace("{payCode}", payCode));
    }

    /**
     * 打开 Intent Scheme Url
     *
     * @param activity      Parent Activity
     * @param intentFullUrl Intent 跳转地址
     * @return 是否成功调用
     */
    public static boolean startIntentUrl(Activity activity, String intentFullUrl) {
        try {
            Intent intent = Intent.parseUri(
                    intentFullUrl,
                    Intent.URI_INTENT_SCHEME
            );
            activity.startActivity(intent);
            return true;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return false;
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断支付宝客户端是否已安装，建议调用转账前检查
     *
     * @param context Context
     * @return 支付宝客户端是否已安装
     */
    public static boolean hasInstalledAlipayClient(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(ALIPAY_PACKAGE_NAME, 0);
            return info != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取支付宝客户端版本名称，作用不大
     *
     * @param context Context
     * @return 版本名称
     */
    public static String getAlipayClientVersion(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(ALIPAY_PACKAGE_NAME, 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 打开支付宝扫一扫界面
     *
     * @param context Context
     * @return 是否成功打开 Activity
     */
    public static boolean openAlipayScan(Context context) {
        try {
            Uri uri = Uri.parse("alipayqr://platformapi/startapp?saId=10000007");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (context instanceof TileService) {

                    ((TileService) context).startActivityAndCollapse(intent);
                }
            } else {
                context.startActivity(intent);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 打开支付宝付款码
     *
     * @param context Context
     * @return 是否成功打开 Activity
     */
    public static boolean openAlipayBarcode(Context context) {
        try {
            Uri uri = Uri.parse("alipayqr://platformapi/startapp?saId=20000056");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (context instanceof TileService) {
                    ((TileService) context).startActivityAndCollapse(intent);
                }
            } else {
                context.startActivity(intent);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
