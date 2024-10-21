package com.sesac.sesacscheduler.common

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri

const val WEATHER_BASE_URL = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/"
const val WEATHER_REST_URL = "getUltraSrtFcst"
const val WEATHER_API_KEY = "CCyHbj1LLKwj5YjI7ycFHNZJNNlRmz06H4SBg0PHzStJzHDKzYVc/qMWsM95qQkuUy90qT7NHjJRxDnL4DdldQ=="

const val TMAP_BASE_URL = "https://apis.openapi.sk.com/tmap/"
const val TMAP_CAR_URL = "routes"
const val TMAP_WALK_URL = "routes/pedestrian"
const val TMAP_API_KEY = "MmTMX9uAtAa9Y9AaRy3GO9Qz0pO3HIba37xmRpTn"

const val TMAP_PACKAGE_NAME = "com.skt.tmap.ku"
const val TMAP_ROUTE_URL = "tmap://route?goalname=%s&goalx=%s&goaly=%s&startx=%s&starty=%s"
const val TMAP_MARKET_URL = "market://details?id=%s"