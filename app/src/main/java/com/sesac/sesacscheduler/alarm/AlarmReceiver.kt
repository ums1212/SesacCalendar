package com.sesac.sesacscheduler.alarm

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Icon
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.sesac.sesacscheduler.R
import com.sesac.sesacscheduler.common.RetrofitManager
import com.sesac.sesacscheduler.common.logE
import com.sesac.sesacscheduler.tmap.TMapInfo
import com.sesac.sesacscheduler.weather.WeatherInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import com.google.android.gms.tasks.Task
import android.location.Location
import com.google.android.gms.location.Priority

class AlarmReceiver : BroadcastReceiver() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onReceive(context: Context, intent: Intent) {
        val scheduleId = intent.getIntExtra("scheduleId", -1)
        val scheduleTitle = intent.getStringExtra("scheduleTitle")
        val appointmentPlace = intent.getStringExtra("appointmentPlace")
        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)

        logE(
            "알람리시버 도착",
            "id: $scheduleId, 제목: $scheduleTitle, 장소: $appointmentPlace, 위도: $latitude, 경도: $longitude"
        )

        getCurrentLocation(context) { currentLatitude, currentLongitude ->
            logE("현재", "위도: $currentLatitude, 경도: $currentLongitude")

            CoroutineScope(Dispatchers.IO).launch {
                val weatherInfo = getWeatherInfo(latitude, longitude).await()
                val tmapInfo = getTMapInfo(currentLatitude, currentLongitude, latitude, longitude).await()

                if (latitude != 0.0) {
                    showRouteNotification(
                        context,
                        weatherInfo,
                        tmapInfo,
                        scheduleTitle!!,
                        appointmentPlace!!
                    )
                } else {
                    showNotification(context, weatherInfo, scheduleTitle!!)
                }
            }
        }
    }

    private fun showNotification(context: Context, weather: WeatherInfo, title: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "schedule_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Schedule Notifications", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        // 특정 intent로 가기 - tmap을 넣어볼까?
//        val mainIntent = Intent(context, MainActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("일정 시간이 얼마 남지 않았어요!!!")
            .setContentText(
                "$title 일정이 있습니다!\n" +
                "기온: ${weather.temperature}"
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            //.setContentIntent(pendingIntent) - 특정 intent로 가기
            .build()
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)

    }

    private fun showRouteNotification(context: Context, weather: WeatherInfo, tmap: TMapInfo, title: String, place: String){
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Notification Channel 아이디, 이름, 설명, 중요도 설정
        val channelId = "channel_one"
        val channelName = "첫 번째 채널"
        val channelDescription = "첫 번째 채널에 대한 설명입니다."
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        // NotificationChannel 객체 생성
        val notificationChannel = NotificationChannel(channelId, channelName, importance)
        // 설명 설정
        notificationChannel.description = channelDescription

        // 채널에 대한 각종 설정(불빛, 진동 등)
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(true)
        notificationChannel.vibrationPattern = longArrayOf(100L, 200L, 300L)
        // 시스템에 notificationChannel 등록
        notificationManager.createNotificationChannel(notificationChannel)

        val notificationCompatBuilder = NotificationCompat.Builder(context, channelId)

        notificationCompatBuilder.let {
            // 작은 아이콘 설정
            it.setSmallIcon(android.R.drawable.ic_notification_overlay)
            // 시간 설정
            it.setWhen(System.currentTimeMillis())
            // 알림 메시지 설정
            it.setContentTitle("$title 일정이 있습니다.")
            // 알림 내용 설정
            it.setContentText(
                "장소: $place\n" +
                "기온: ${weather.temperature}\n" +
                        "자동차: ${tmap.carTime} \n" +
                        "도보: ${tmap.walkTime}\n" +
                        "거리: ${tmap.distance}"
            )
            // 날씨 아이콘
            when(weather.sky){
                "1" -> it.setLargeIcon(Icon.createWithResource(context, R.drawable.sky1))
                "3" -> it.setLargeIcon(Icon.createWithResource(context, R.drawable.sky3))
                "4" -> it.setLargeIcon(Icon.createWithResource(context, R.drawable.sky4))
            }
            // 알림과 동시에 진동 설정(권한 필요(
            it.setDefaults(Notification.DEFAULT_VIBRATE)
            // 클릭 시 알림이 삭제되도록 설정
            it.setAutoCancel(true)
        }

        val notification = notificationCompatBuilder.build()
        // Notification 식별자 값, Notification 객체
        notificationManager.notify(0, notification)
    }
    private fun getCurrentLocation(context: Context, callback: (Double, Double) -> Unit) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        // 위치 권한 확인
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            logE("위치 권한이 필요합니다.", "")
            return
        }

        // 위치 정보 가져오기
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnCompleteListener { task: Task<Location> ->
            if (task.isSuccessful && task.result != null) {
                val location: Location = task.result
                val latitude = location.latitude
                val longitude = location.longitude
                // 위치 값을 콜백으로 전달
                callback(latitude, longitude)
            } else {
                logE("현재 위치를 가져오지 못했습니다.", "")
                callback(0.0, 0.0) // 위치를 가져오지 못한 경우 기본 값 전달
            }
        }
    }

    private fun getWeatherInfo(latitude: Double, longitude: Double): Deferred<WeatherInfo>
            = CoroutineScope(Dispatchers.IO).async {
        val result = RetrofitManager.WeatherService.weatherService.getWeatherForecast(
            nx = latitude.toInt(),
            ny = longitude.toInt()
        )
        result.let {
            if(it.isSuccessful){
                val firstItemSKY = it.body()?.response?.body?.items?.item?.filter { item ->
                    item.category == "SKY"
                }?.sortedBy { item ->
                    item.fcstTime.toInt()
                }?.first()

                val firstItemT1H = it.body()?.response?.body?.items?.item?.filter { item ->
                    item.category == "T1H"
                }?.sortedBy { item ->
                    item.fcstTime.toInt()
                }?.first()

                // 하늘상태(SKY) 코드 : 맑음(1), 구름많음(3), 흐림(4)
                WeatherInfo(firstItemSKY?.fcstValue?:"1", firstItemT1H?.fcstValue?:"2")
            }else{
                Log.e("test1234", "실패 ${it.code()}")
                throw Exception("getWeatherInfo: 실패")
            }
        }
    }

    private fun getTMapInfo(currentLatitude: Double, currentLongitude: Double, latitude: Double, longitude: Double): Deferred<TMapInfo>
            = CoroutineScope(Dispatchers.IO).async{
        val carInfo = CoroutineScope(Dispatchers.IO).async {
            val result = RetrofitManager.TMapService.tMapService.getCarResult(
                startX = currentLongitude,
                startY = currentLatitude,
                endX = longitude,
                endY = latitude
            )
            result.let {
                if(it.isSuccessful){
                    val totalTime = it.body()?.features?.first()?.properties?.totalTime
                    val totalDistance = it.body()?.features?.first()?.properties?.totalDistance
                    logE("차량 정보","차량 걸리는 시간: $totalTime,차량 걸리는 거리: $totalDistance")
                    Pair(totalDistance!!, totalTime!!)
                }else{
                    Log.e("test1234", "실패 ${it.code()}")
                    throw Exception("getWeatherInfo: 실패")
                }
            }
        }
        val walkInfo = CoroutineScope(Dispatchers.IO).async {
            val result = RetrofitManager.TMapService.tMapService.getWalkResult(
                startX = 126.92365493654832,
                startY = 37.556770374096615,
                endX = longitude,
                endY = latitude,
                startName = "%EC%B6%9C%EB%B0%9C",
                endName = "%EB%B3%B8%EC%82%AC"
            )
            result.let {
                if(it.isSuccessful){
                    val totalDistance = it.body()?.features?.first()?.properties?.totalDistance
                    val totalTime = it.body()?.features?.first()?.properties?.totalTime
                    logE("도보정보","도보 걸리는 시간: $totalTime,도보 걸리는 거리: $totalDistance")
                    Pair(totalDistance!!, totalTime!!)
                }else{
                    Log.e("test1234", "실패 ${it.code()}")
                    throw Exception("getWeatherInfo: 실패")
                }
            }
        }
        //거리, 시간, 시간
        TMapInfo(carInfo.await().first, carInfo.await().second, walkInfo.await().second)
    }
}