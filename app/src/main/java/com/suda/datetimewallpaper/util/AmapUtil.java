package com.suda.datetimewallpaper.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.amap.api.location.*;
import com.suda.datetimewallpaper.bean.City;
import com.suda.datetimewallpaper.model.CityDao;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * @author guhaibo
 * @date 2019/5/8
 */
public class AmapUtil {

    public static Observable<String> getLocation(final Context context) {

        return Observable.create(new ObservableOnSubscribe<String>() {

            @Override
            public void subscribe(final ObservableEmitter<String> e) throws Exception {

                final AMapLocationClient locationClient = new AMapLocationClient(context.getApplicationContext());
                AMapLocationClientOption locationOption = new AMapLocationClientOption();
                locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
                locationOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
                locationOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
                locationOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
                locationOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
                AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTPS);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
                locationOption.setSensorEnable(true);//可选，设置是否使用传感器。默认是false
                locationOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
                locationOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
                locationOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.DEFAULT);//可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
                locationClient.setLocationOption(locationOption);
                locationClient.setLocationListener(new AMapLocationListener() {
                    @Override
                    public void onLocationChanged(AMapLocation location) {
                        if (null != location) {
                            //解析定位结果，
                            printLoc(location);

                            City city = getCity(location, context);
                            if (city != null) {
                                e.onNext(city.getWeatherId());
                            } else {
                                e.onNext("");
                            }
                        } else {
                            e.onNext("");
                            Log.d("AmapUtil", "定位失败");
                        }
                        e.onComplete();
                        locationClient.stopLocation();
                        locationClient.onDestroy();
                    }
                });
                locationClient.startLocation();
            }
        }).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread());


    }

    private static City getCity(AMapLocation aMapLocation, Context context) {
        if (aMapLocation == null) {
            return null;
        }
        CityDao cityDao = new CityDao(context);
        List<City> cityByAreas = cityDao.getCityByArea(aMapLocation.getDistrict());
        List<City> arrayList = cityByAreas.size() == 0 ? cityDao.getCityByArea(repalceLast(aMapLocation.getDistrict())) : cityByAreas;
        if (arrayList.size() != 0) {
            for (int i = 0; i < arrayList.size(); i++) {
                City city = arrayList.get(i);
                if (aMapLocation.getCity().contains(city.getCityName())) {
                    return city;
                }
            }
        }
        List<City> cityByCity = cityDao.getCityByCity(aMapLocation.getCity());
        List<City> arrayList2 = cityByCity.size() == 0 ? cityDao.getCityByCity(repalceLast(aMapLocation.getCity())) : cityByCity;
        if (arrayList2.size() != 0) {
            for (int i2 = 0; i2 < arrayList2.size(); i2++) {
                City area2 = (City) arrayList2.get(i2);
                if (aMapLocation.getCity().contains(area2.getCityName())) {
                    return area2;
                }
            }
        }
        return null;
    }

    private static String repalceLast(String str) {
        return str.length() > 1 ? str.substring(0, str.length() - 1) : str;
    }

    private static void printLoc(AMapLocation location) {
        StringBuffer sb = new StringBuffer();
        //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
        if (location.getErrorCode() == 0) {
            sb.append("定位成功" + "\n");
            sb.append("定位类型: " + location.getLocationType() + "\n");
            sb.append("经    度    : " + location.getLongitude() + "\n");
            sb.append("纬    度    : " + location.getLatitude() + "\n");
            sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
            sb.append("提 供 者    : " + location.getProvider() + "\n");

            sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
            sb.append("角    度    : " + location.getBearing() + "\n");
            // 获取当前提供定位服务的卫星个数
            sb.append("星    数    : " + location.getSatellites() + "\n");
            sb.append("国    家    : " + location.getCountry() + "\n");
            sb.append("省            : " + location.getProvince() + "\n");
            sb.append("市            : " + location.getCity() + "\n");
            sb.append("城市编码 : " + location.getCityCode() + "\n");
            sb.append("区            : " + location.getDistrict() + "\n");
            sb.append("区域 码   : " + location.getAdCode() + "\n");
            sb.append("地    址    : " + location.getAddress() + "\n");
            sb.append("兴趣点    : " + location.getPoiName() + "\n");
            //定位完成的时间
            sb.append("定位时间: " + formatUTC(location.getTime(), "yyyy-MM-dd HH:mm:ss") + "\n");
        } else {
            //定位失败
            sb.append("定位失败" + "\n");
            sb.append("错误码:" + location.getErrorCode() + "\n");
            sb.append("错误信息:" + location.getErrorInfo() + "\n");
            sb.append("错误描述:" + location.getLocationDetail() + "\n");
        }
        sb.append("***定位质量报告***").append("\n");
        sb.append("* WIFI开关：").append(location.getLocationQualityReport().isWifiAble() ? "开启" : "关闭").append("\n");
        sb.append("* GPS状态：").append(getGPSStatusString(location.getLocationQualityReport().getGPSStatus())).append("\n");
        sb.append("* GPS星数：").append(location.getLocationQualityReport().getGPSSatellites()).append("\n");
        sb.append("* 网络类型：" + location.getLocationQualityReport().getNetworkType()).append("\n");
        sb.append("* 网络耗时：" + location.getLocationQualityReport().getNetUseTime()).append("\n");
        sb.append("****************").append("\n");
        //定位之后的回调时间
        sb.append("回调时间: " + formatUTC(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + "\n");
        Log.d("AmapUtil", sb.toString());
    }

    /**
     * 获取GPS状态的字符串
     *
     * @param statusCode GPS状态码
     * @return
     */
    private static String getGPSStatusString(int statusCode) {
        String str = "";
        switch (statusCode) {
            case AMapLocationQualityReport.GPS_STATUS_OK:
                str = "GPS状态正常";
                break;
            case AMapLocationQualityReport.GPS_STATUS_NOGPSPROVIDER:
                str = "手机中没有GPS Provider，无法进行GPS定位";
                break;
            case AMapLocationQualityReport.GPS_STATUS_OFF:
                str = "GPS关闭，建议开启GPS，提高定位质量";
                break;
            case AMapLocationQualityReport.GPS_STATUS_MODE_SAVING:
                str = "选择的定位模式中不包含GPS定位，建议选择包含GPS定位的模式，提高定位质量";
                break;
            case AMapLocationQualityReport.GPS_STATUS_NOGPSPERMISSION:
                str = "没有GPS定位权限，建议开启gps定位权限";
                break;
        }
        return str;
    }

    private static SimpleDateFormat sdf = null;

    private static String formatUTC(long l, String strPattern) {
        if (TextUtils.isEmpty(strPattern)) {
            strPattern = "yyyy-MM-dd HH:mm:ss";
        }
        if (sdf == null) {
            try {
                sdf = new SimpleDateFormat(strPattern, Locale.CHINA);
            } catch (Throwable e) {
            }
        } else {
            sdf.applyPattern(strPattern);
        }
        return sdf == null ? "NULL" : sdf.format(l);
    }


}
