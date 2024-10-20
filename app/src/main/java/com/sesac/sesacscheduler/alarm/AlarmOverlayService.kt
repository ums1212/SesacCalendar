package com.sesac.sesacscheduler.alarm

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import com.sesac.sesacscheduler.R
import com.sesac.sesacscheduler.databinding.NotificationOverlayBinding
import com.sesac.sesacscheduler.tmap.TMapInfo
import com.sesac.sesacscheduler.weather.WeatherInfo

class AlarmOverlayService: Service() {

    companion object {
        private const val TAG = "AlarmOverlayService"
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private lateinit var windowManager: WindowManager
    private lateinit var layoutInflater: LayoutInflater

    private var screenWidth = 0
    private var screenHeight = 0

    private lateinit var notiView: NotificationOverlayBinding

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        getScreenSize()

        layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        notiView = NotificationOverlayBinding.inflate(layoutInflater)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 리시버에서 서비스 실행할때 전달받은 값 불러오기
        val weather = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra("weather", WeatherInfo::class.java)
        } else {
            intent?.getParcelableExtra("weather") as? WeatherInfo
        }
        val tmap = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra("tmap", TMapInfo::class.java)
        } else {
            intent?.getParcelableExtra("tmap") as? TMapInfo
        }
        val title = intent?.getStringExtra("title") ?: ""
        val place = intent?.getStringExtra("place") ?: ""

        settingNotiView(weather!!, tmap!!, title, place)
        showOverlay()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun showOverlay(){
        val params = WindowManager.LayoutParams(
            screenWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = android.view.Gravity.TOP
        windowManager.addView(notiView.root, params)
    }

    private fun settingNotiView(weather: WeatherInfo, tmap: TMapInfo, title: String, place: String){
        with(notiView){
            // 나중에 title, place를 scheduleInfo 객체를 가져오는걸로 수정하면 좋을듯
//            val scheduleDate = LocalDate.parse(schedule.startDate).format(DateTimeFormatter.ofPattern("MM월 dd일"))
//            val scheduleLocation = schedule.appointmentPlace
//            val scheduleTime = LocalDateTime.parse(schedule.startTime).format(DateTimeFormatter.ofPattern("a h시"))
//            expandedNotificationTitle.text = "${scheduleDate} ${scheduleLocation}"
//            expandedNotificationInfo.text = scheduleTime
            expandedNotificationTitle.text = title
            expandedNotificationInfo.text = place
            textViewTemperature.text = "${weather.temperature}℃"
            textViewCarDistance.text = "${tmap.distance}m"
            textViewCarTime.text = "${tmap.carTime/60}분"
            textViewWalkDistance.text = "${tmap.distance}m"
            textViewWalkTime.text = "${tmap.walkTime/60}분"
            when(weather.sky){
                "1" -> skyImage.setImageResource(R.drawable.sky1)
                "3" -> skyImage.setImageResource(R.drawable.sky3)
                "4" -> skyImage.setImageResource(R.drawable.sky4)
            }
            carIcon.setImageResource(R.drawable.car_icon)
            walkIcon.setImageResource(R.drawable.walk_icon)
            closeIcon.apply {
                setOnClickListener {
                    removeViews()
                }
            }
        }
    }

    /**
     * api 레벨에 따라서 화면 사이즈 불러오기
     */
    private fun getScreenSize() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val screenRect = windowManager.currentWindowMetrics.bounds
            screenWidth = screenRect.right
            screenHeight = screenRect.bottom
        } else {
            val metrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(metrics)
            screenWidth = metrics.widthPixels
            screenHeight = metrics.heightPixels
        }
    }

    /**
     * OverlayActivity에서 onDestroy 가 호출 될 때 Service를 종료시키는 로직을 추가했습니다.
     * Service를 종료시킬 때 view들을 제거해줘야 완전히 화면에서 사라집니다.
     */
    override fun onDestroy() {
        removeViews()
        super.onDestroy()
    }

    private fun removeViews() {
        windowManager.removeView(notiView.root)
    }
}