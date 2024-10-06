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
    var repeat: Boolean = false
    var repeatDays: Int = 0
    var appointmentPlace: String = ""
    var longitude: Double = 0.0
    var latitude: Double = 0.0
    var appointmentAlarm: Boolean = false
    var appointmentAlarmTime: String = ""
    var color: Int = 1

    constructor(){}

    constructor(
        title: String,
        startDate: String,
        lastDate: String,
        startTime: String,
        endTime: String,
        repeat: Boolean,
        repeatDays: Int,
        appointmentPlace: String,
        longitude: Double,
        latitude: Double,
        appointmentAlarm: Boolean,
        appointmentAlarmTime: String,
        color: Int
    ) {
        this.title = title
        this.startDate = startDate
        this.lastDate = lastDate
        this.startTime = startTime
        this.endTime = endTime
        this.repeat = repeat
        this.repeatDays = repeatDays
        this.appointmentPlace = appointmentPlace
        this.longitude = longitude
        this.latitude = latitude
        this.appointmentAlarm = appointmentAlarm
        this.appointmentAlarmTime = appointmentAlarmTime
        this.color = color
    }
}

