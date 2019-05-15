package com.suda.datetimewallpaper.model

import com.alibaba.fastjson.JSON
import com.suda.datetimewallpaper.App
import com.suda.datetimewallpaper.bean.RealWeather
import io.reactivex.Observable
import okhttp3.Request


const val WEATHER_URL = "http://res.aider.meizu.com/1.0/weather/%s.json"

/**
 * @author guhaibo
 * @date 2019/5/3
 */
class WeatherModel {

    fun getWeather(areaid: String): Observable<RealWeather> {

        return Observable.create<RealWeather> {
            try {
                val request = Request.Builder().url(String.format(WEATHER_URL, areaid)).get().build()
                val call = App.GLOBAL_OK_HTTP.newCall(request)
                val response = call.execute()
                val result = JSON.parseObject(response.body()!!.string())

                val realtime = result.getJSONObject("realtime")
                val realWeather = RealWeather()
                realWeather.areaName = result.getString("city")
                realWeather.lastUpdate = System.currentTimeMillis()
                realWeather.areaid = areaid
                realWeather.fj = realtime.getString("wS")
                realWeather.fx = realtime.getString("wD")
                realWeather.temp = realtime.getInteger("temp")
                realWeather.weatherCondition = realtime.getString("weather")
                realWeather.feeltemp = realtime.getInteger("sendibleTemp")
                realWeather.weatherCondition = realtime.getString("weather")

                it.onNext(realWeather)
            } catch (e: Exception) {
                it.onError(e)
            }
        }
    }

}