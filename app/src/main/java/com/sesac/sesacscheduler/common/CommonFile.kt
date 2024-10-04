package com.sesac.sesacscheduler.common

import android.util.Log
import android.widget.Toast

const val MOCK_DELAY_TIME = 2000L
fun toastShort(message: String){
    Toast.makeText(SchedulerApplication.getSchedulerApplication(), message, Toast.LENGTH_SHORT).show()
}
fun logE(tag: String, message: String){
    Log.e(tag, message)
}
