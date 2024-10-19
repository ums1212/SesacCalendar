package com.sesac.sesacscheduler.weather

import com.sesac.sesacscheduler.common.WEATHER_API_KEY
import com.sesac.sesacscheduler.common.WEATHER_REST_URL
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

interface WeatherRestService {
    @GET(WEATHER_REST_URL)
    suspend fun getWeatherForecast(
        @Query("ServiceKey") serviceKey: String = WEATHER_API_KEY,
        @Query("pageNo") pageNo: Int = 1,
        @Query("numOfRows") numOfRows: Int = 1000,
        @Query("dataType") dataType: String = "JSON",
        @Query("base_date") baseDate: String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
        @Query("base_time") baseTime: String = LocalTime.now().format(DateTimeFormatter.ofPattern("HHmm")),
        @Query("nx") nx: Int,
        @Query("ny") ny: Int,
    ): Response<WeatherRestInfo>
}