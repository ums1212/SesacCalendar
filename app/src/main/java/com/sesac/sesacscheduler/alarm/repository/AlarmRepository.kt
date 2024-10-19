package com.sesac.sesacscheduler.alarm.repository

import com.sesac.sesacscheduler.common.SchedulerApplication
import com.sesac.sesacscheduler.dao.ScheduleDAO
import com.sesac.sesacscheduler.database.ScheduleRoomDatabase
import com.sesac.sesacscheduler.model.ScheduleInfo

class AlarmRepository(private var scheduleDAO: ScheduleDAO) {
    init {
        val scheduleDatabase = ScheduleRoomDatabase.getDatabase(SchedulerApplication.getSchedulerApplication())
        scheduleDAO = scheduleDatabase.scheduleDao()
    }
    suspend fun getSchedulesWithAlarm(): MutableList<ScheduleInfo> = scheduleDAO.getSchedulesWithAlarm()
}