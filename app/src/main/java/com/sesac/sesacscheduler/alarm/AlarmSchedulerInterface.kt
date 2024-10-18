package com.sesac.sesacscheduler.alarm

import com.sesac.sesacscheduler.model.ScheduleInfo

interface AlarmSchedulerInterface {
    fun scheduleAlarm(schedule: ScheduleInfo)
    fun cancel()
}