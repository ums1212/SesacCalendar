package com.sesac.sesacscheduler.common

import com.sesac.sesacscheduler.tmap.TMapRestService
import com.sesac.sesacscheduler.weather.WeatherRestService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitManager {
    object WeatherService{
        var weatherService: WeatherRestService = Retrofit.Builder()
            .baseUrl(WEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(WeatherRestService::class.java)
    }

    object TMapService{
        var tMapService: TMapRestService = Retrofit.Builder()
            .baseUrl(TMAP_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(TMapRestService::class.java)
    }
}