package com.suda.datetimewallpaper.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import com.suda.datetimewallpaper.bean.City;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ghbha on 2016/4/27.
 */
public class CityDao {

    private Context context;
    private DBOpenHelper dbOpenHelper;

    public CityDao(Context context) {
        this.context = context;
        dbOpenHelper = new DBOpenHelper(context);
    }

    public List<City> getCityByCityOrArea(String areaName) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        List<City> cities = new ArrayList<>();
        if (TextUtils.isEmpty(areaName)){
            return cities;
        }
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select * from citys where " +
                    "`areaName` !='' and (cityName like '%" + areaName + "%' or areaName like '%" + areaName + "%')", null);
            while (cursor.moveToNext()) {
                City city = new City();
                city.setAreaName(cursor.getString(cursor.getColumnIndex("areaName")));
                city.setProvinceName(cursor.getString(cursor.getColumnIndex("provinceName")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("cityName")));
                city.setWeatherId(cursor.getString(cursor.getColumnIndex("weatherId")));
                city.setAreaId(cursor.getString(cursor.getColumnIndex("areaId")));
                cities.add(city);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                cursor.close();
            } catch (Exception e) {

            }
        }

        return cities;
    }


    public List<City> getCityByArea(String areaName) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        List<City> cities = new ArrayList<>();
        if (TextUtils.isEmpty(areaName)) {
            return cities;
        }
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select * from citys where " +
                    "`areaName` ='" + areaName + "'", null);
            while (cursor.moveToNext()) {
                City city = new City();
                city.setAreaName(cursor.getString(cursor.getColumnIndex("areaName")));
                city.setProvinceName(cursor.getString(cursor.getColumnIndex("provinceName")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("cityName")));
                city.setWeatherId(cursor.getString(cursor.getColumnIndex("weatherId")));
                city.setAreaId(cursor.getString(cursor.getColumnIndex("areaId")));
                cities.add(city);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                cursor.close();
            } catch (Exception e) {

            }
        }

        return cities;
    }

    public List<City> getCityByCity(String cityName) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        List<City> cities = new ArrayList<>();
        if (TextUtils.isEmpty(cityName)) {
            return cities;
        }
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select * from citys where " +
                    "`cityName` ='" + cityName + "'", null);
            while (cursor.moveToNext()) {
                City city = new City();
                city.setAreaName(cursor.getString(cursor.getColumnIndex("areaName")));
                city.setProvinceName(cursor.getString(cursor.getColumnIndex("provinceName")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("cityName")));
                city.setWeatherId(cursor.getString(cursor.getColumnIndex("weatherId")));
                city.setAreaId(cursor.getString(cursor.getColumnIndex("areaId")));
                cities.add(city);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                cursor.close();
            } catch (Exception e) {

            }
        }

        return cities;
    }


    public City getCityByCityAndArea(String cityName, String areaName) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from citys where cityName = '" + cityName + "' and areaName = '" + areaName + "'", null);
        City city = new City();
        if (cursor.moveToNext()) {
            city.setAreaName(cursor.getString(cursor.getColumnIndex("cityName")));
            city.setProvinceName(cursor.getString(cursor.getColumnIndex("provinceName")));
            city.setCityName(cursor.getString(cursor.getColumnIndex("cityName")));
            city.setWeatherId(cursor.getString(cursor.getColumnIndex("weatherId")));
            city.setAreaId(cursor.getString(cursor.getColumnIndex("areaId")));
            cursor.close();
        } else {
            cursor.close();
            return null;
        }

        return city;
    }

    public City getCityByWeatherID(String weather_id) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from citys where weatherId ='" + weather_id + "'", null);
        City city = new City();
        if (cursor.moveToNext()) {
            city.setAreaName(cursor.getString(cursor.getColumnIndex("areaName")));
            city.setProvinceName(cursor.getString(cursor.getColumnIndex("provinceName")));
            city.setCityName(cursor.getString(cursor.getColumnIndex("cityName")));
            city.setWeatherId(cursor.getString(cursor.getColumnIndex("weatherId")));
            city.setAreaId(cursor.getString(cursor.getColumnIndex("areaId")));
            cursor.close();
        } else {
            cursor.close();
            return null;
        }

        return city;
    }

}
