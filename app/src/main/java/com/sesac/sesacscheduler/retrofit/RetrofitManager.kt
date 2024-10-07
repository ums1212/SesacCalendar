package com.sesac.sesacscheduler.retrofit

import com.sesac.sesacscheduler.common.BASE_URL
import com.sesac.sesacscheduler.common.WeatherRestService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitManager {
    object WeatherService{
        var weatherService: WeatherRestService = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(WeatherRestService::class.java)
    }
}