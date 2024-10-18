package com.sesac.sesacscheduler.common

import android.util.Log
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

const val MOCK_DELAY_TIME = 2000L

fun toastShort(message: String){
    Toast.makeText(SchedulerApplication.getSchedulerApplication(), message, Toast.LENGTH_SHORT).show()
}
fun logE(tag: String, message: String) {
    Log.e(tag, message)
}
fun formatDate(month: Int, dayOfMonth: Int): String = "${month + 1}월${dayOfMonth}일"
fun formatTimeToString(hour: Int, minute: Int): String = "${hour}시${minute}분"
fun formatCurrentDate(): String = SimpleDateFormat("M월d일", Locale.getDefault()).format(Calendar.getInstance().time)
fun formatCurrentTime(): String = SimpleDateFormat("HH시mm분", Locale.getDefault()).format(Calendar.getInstance().time)
fun getAlarmTime(date: String, time: String): Calendar {
    // 날짜 포맷 (MM월dd일)
    val dateFormat = SimpleDateFormat("MM월dd일", Locale.KOREA)
    val parsedDate: Date = dateFormat.parse(date)
    // 시간 포맷 (HH시mm분)
    val timeFormat = SimpleDateFormat("HH시mm분", Locale.KOREA)
    val parsedTime: Date = timeFormat.parse(time)
    // Calendar 객체 생성
    val calendar = Calendar.getInstance()
    // 날짜 설정
    parsedDate.let {
        val dateCalendar = Calendar.getInstance()
        dateCalendar.time = it
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
    // 알람 시간 10초 전으로 설정
    calendar.add(Calendar.SECOND, -10)
    return calendar
}

