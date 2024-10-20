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
    }
}