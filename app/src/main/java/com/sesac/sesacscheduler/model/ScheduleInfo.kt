package com.sesac.sesacscheduler.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "schedule")
class ScheduleInfo(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "scheduleId")
    val id: Int,

    @ColumnInfo(name = "scheduleTitle")
    val title: String,

    val startDate: String,

    val lastDate: String,

    val startTime: String,

    val endTime: String,

    val repeat: Boolean = false,

    val repeatDays: Int = 0,

    val appointmentPlace: String,

    val longitude: Double = 0.0,

    val latitude: Double = 0.0,

    val appointmentAlarm: Boolean = false,

    val appointmentAlarmTime: String = "",

    val color: Int = 1
)
