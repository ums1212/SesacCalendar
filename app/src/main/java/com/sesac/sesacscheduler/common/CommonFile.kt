package com.sesac.sesacscheduler.common

import android.util.Log
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

const val MOCK_DELAY_TIME = 2000L

const val NO = 0
const val EVERY_DAY = 1
const val EVERY_WEEK = 2
const val EVERY_MONTH = 3

fun toastShort(message: String){
    Toast.makeText(SchedulerApplication.getSchedulerApplication(), message, Toast.LENGTH_SHORT).show()
}
fun logE(tag: String, message: String){
    Log.e(tag, message)
}
fun formatDate(month: Int, dayOfMonth: Int): String = "${month + 1}월${dayOfMonth}일"
fun formatTime(hour: Int, minute: Int): String {
    val hourIn12 = if (hour % 12 == 0) 12 else hour % 12
    return "${hourIn12}시${minute}분"
}
fun formatCurrentDate(): String = SimpleDateFormat("M월d일", Locale.getDefault()).format(Calendar.getInstance().time)
fun formatCurrentTime(): String = SimpleDateFormat("HH시mm분", Locale.getDefault()).format(Calendar.getInstance().time)

