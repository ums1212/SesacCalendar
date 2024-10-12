package com.sesac.sesacscheduler.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey


@Entity(tableName = "schedule")
class ScheduleInfo {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "scheduleId")
    var id: Int = 0
    var title: String = ""
    var startDate: String = ""
    var lastDate: String = ""
    var startTime: String = ""
    var endTime: String = ""
    var repeatDays: Int = 0
    var appointmentPlace: String = ""
    var latitude: Double = 0.0 //위도
    var longitude: Double = 0.0 //경도
    var appointmentAlarm: Boolean = false
    var appointmentAlarmTime: Int = -1
    var color: Int = 0

    constructor(){}

    constructor(
        title: String,
        startDate: String,
        lastDate: String,
        startTime: String,
        endTime: String,
        repeatDays: Int,
        appointmentPlace: String,
        longitude: Double,
        latitude: Double,
        appointmentAlarm: Boolean,
        appointmentAlarmTime: Int,
        color: Int
    ) {
        this.title = title
        this.startDate = startDate
        this.lastDate = lastDate
        this.startTime = startTime
        this.endTime = endTime
        this.repeatDays = repeatDays
        this.appointmentPlace = appointmentPlace
        this.longitude = longitude
        this.latitude = latitude
        this.appointmentAlarm = appointmentAlarm
        this.appointmentAlarmTime = appointmentAlarmTime
        this.color = color
    }
}

