package com.sesac.sesacscheduler.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "schedule")
data class ScheduleInfo (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "scheduleId")
    var id: Int = 0,
    var title: String = "내일정",
    var startDate: String = "",
    var lastDate: String = "",
    var startTime: String = "",
    var endTime: String = "",
    var repeatDays: Int = 0,
    var appointmentPlace: String = "",
    var latitude: Double = 0.0, //위도
    var longitude: Double = 0.0, //경도
    var appointmentAlarm: Boolean = false,
    var appointmentAlarmTime: Int = -1,
    var color: Int = 0
){
    constructor(
        title: String,
        startDate: String,
        lastDate: String,
        startTime: String,
        endTime: String,
        repeatDays: Int,
        appointmentPlace: String,
        latitude: Double,
        longitude: Double,
        appointmentAlarm: Boolean,
        appointmentAlarmTime: Int,
        color: Int
    ) : this() {
        this.title = title
        this.startDate = startDate
        this.lastDate = lastDate
        this.startTime = startTime
        this.endTime = endTime
        this.repeatDays = repeatDays
        this.appointmentPlace = appointmentPlace
        this.latitude = latitude
        this.longitude = longitude
        this.appointmentAlarm = appointmentAlarm
        this.appointmentAlarmTime = appointmentAlarmTime
        this.color = color
    }
}

