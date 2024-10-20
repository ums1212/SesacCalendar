package com.sesac.sesacscheduler.weather

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeatherInfo(
    val sky: String,
    val temperature: String,) : Parcelable
