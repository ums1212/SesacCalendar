package com.sesac.sesacscheduler

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sesac.sesacscheduler.alarm.AlarmScheduler
import com.sesac.sesacscheduler.alarm.repository.AlarmRepository
import com.sesac.sesacscheduler.alarm.usecase.AlarmUsecase
import com.sesac.sesacscheduler.common.logE
import com.sesac.sesacscheduler.database.ScheduleRoomDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var repository: AlarmRepository
    private lateinit var alarmScheduler: AlarmScheduler
    private lateinit var alarmUsecase: AlarmUsecase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        logE("mainActivity","onCreate")
//        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
//
//        val scheduler = AndroidAlarmScheduler(this)
//
//        val alarmItem = AlarmItem(
//            time = LocalDateTime.now()
//                .plusSeconds(10),
//            message = "Alarm! 10 seconds",
//            true
//        )
//
//        binding.button.setOnClickListener {
//            alarmItem.let(scheduler::schedule)
//        }

        val scheduleDAO = ScheduleRoomDatabase.getDatabase(this).scheduleDao()
        repository = AlarmRepository(scheduleDAO)
        alarmUsecase = AlarmUsecase(repository)
        alarmScheduler = AlarmScheduler(this, alarmUsecase)

        // 앱 시작 시 알람을 설정한거 -> 이거를 어디다 넣어야 할까..?
        alarmScheduler.scheduleAlarmsForAppointments()
    }
//    private val requestPermissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) { isGranted: Boolean ->
//        if (isGranted) {
//            Log.d("MainActivity", "알림 권한이 허용되었습니다.")
//            // 푸시 알림을 수신하고 배지를 표시하는 로직 추가 가능
//        } else {
//            Log.d("MainActivity", "알림 권한이 거부되었습니다.")
//            // 알림 권한이 거부되었을 때의 처리 로직 추가 가능
//        }
//    }
}