package com.sesac.sesacscheduler.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.sesac.sesacscheduler.alarm.usecase.AlarmUsecase
import com.sesac.sesacscheduler.common.getAlarmTime
import com.sesac.sesacscheduler.common.logE
import com.sesac.sesacscheduler.model.ScheduleInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class AlarmScheduler(private val context: Context, private val alarmUsecase: AlarmUsecase): AlarmSchedulerInterface {

    fun scheduleAlarmsForAppointments() {
        // 코루틴을 사용해 비동기로 Room 데이터베이스에서 데이터를 가져옴
        CoroutineScope(Dispatchers.IO).launch {
            val schedulesWithAlarm = alarmUsecase.getValidAlarms()

            for (schedule in schedulesWithAlarm) {
                scheduleAlarm(schedule)
                logE("필터링 된 schedule들","제목 : ${schedule.title}, 시작날짜 : ${schedule.startDate}, 시작시간 : ${schedule.startTime}, 알람유무 : ${schedule.appointmentAlarm}")
            }
        }

    }
    @SuppressLint("ScheduleExactAlarm")
    override fun scheduleAlarm(schedule: ScheduleInfo) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("scheduleId", schedule.id)
            putExtra("scheduleTitle", schedule.title)
            putExtra("appointmentPlace", schedule.appointmentPlace)
            putExtra("latitude", schedule.latitude)
            putExtra("longitude", schedule.longitude)
        }
        // 에러가 나서 PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE 이걸로 수정함
        val pendingIntent = PendingIntent.getBroadcast(
            context, schedule.id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = getAlarmTime(schedule.startDate,schedule.startTime)
        triggerTime.add(Calendar.SECOND, -50) //50초 전으로 설정
        logE("알람 시간", triggerTime.toString())

        // 알람 설정
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime.timeInMillis, pendingIntent)
    }

    override fun cancel() {
    }
}