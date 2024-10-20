package com.sesac.sesacscheduler.common

import android.util.Log
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

const val MOCK_DELAY_TIME = 2000L
const val NO_SCHEDULE_ID = 0

fun toastShort(message: String){
    Toast.makeText(SchedulerApplication.getSchedulerApplication(), message, Toast.LENGTH_SHORT).show()
}
fun logE(tag: String, message: String) {
    Log.e(tag, message)
}
fun formatDate(year: Int, month: Int, dayOfMonth: Int): String = "$year-${String.format("%02d", month + 1)}-${String.format("%02d", dayOfMonth)}"
fun formatTimeToString(hour: Int, minute: Int): String = "${String.format("%02d", hour)}:${String.format("%02d", minute)}"
fun formatCurrentDate(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
fun formatCurrentTime(): String = SimpleDateFormat("hh:mm", Locale.getDefault()).format(Calendar.getInstance().time)
fun getAlarmTime(date: String, time: String): Calendar {
    // 날짜 포맷 (yyyy-MM-dd)
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
    val parsedDate: Date = dateFormat.parse(date)
    // 시간 포맷 (HH:mm)
    val timeFormat = SimpleDateFormat("HH:mm", Locale.KOREA)
    val parsedTime: Date = timeFormat.parse(time)
    // Calendar 객체 생성
    val calendar = Calendar.getInstance()
    // 날짜 설정
    parsedDate.let {
        val dateCalendar = Calendar.getInstance()
        dateCalendar.time = it
        calendar.set(Calendar.YEAR, dateCalendar.get(Calendar.YEAR))  // 연도 설정
        calendar.set(Calendar.MONTH, dateCalendar.get(Calendar.MONTH))  // 월 설정
        calendar.set(Calendar.DAY_OF_MONTH, dateCalendar.get(Calendar.DAY_OF_MONTH))  // 일 설정
    }
    // 시간 설정
    parsedTime.let {
        val timeCalendar = Calendar.getInstance()
        timeCalendar.time = it
        calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))  // 시간 설정
        calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))  // 분 설정
        calendar.set(Calendar.SECOND, 0)  // 초는 0으로 설정
    }
    // 알람 시간 50초 전으로 설정
    calendar.add(Calendar.SECOND, -50)
    return calendar
}

