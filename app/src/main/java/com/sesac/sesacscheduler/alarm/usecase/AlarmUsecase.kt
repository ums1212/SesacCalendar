package com.sesac.sesacscheduler.alarm.usecase

import com.sesac.sesacscheduler.alarm.repository.AlarmRepository
import com.sesac.sesacscheduler.common.logE
import com.sesac.sesacscheduler.model.ScheduleInfo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AlarmUsecase(private val alarmRepository: AlarmRepository) {
    suspend fun getValidAlarms(): List<ScheduleInfo> {
        // 현재 시간을 가져옴
        val currentTime = LocalDateTime.now()

        // Repo에서 알람이 켜져 있는 스케줄을 모두 가져옴
        val schedulesWithAlarm = alarmRepository.getSchedulesWithAlarm()

        // 알람 시간이 현재보다 이후인 스케줄만 필터링
        return schedulesWithAlarm.filter { schedule ->
            logE("Before Filter","id: ${schedule.id}, ${schedule.startDate} ${schedule.startTime}, 알람유무 : ${schedule.appointmentAlarm}")
            val alarmDateTime = convertToLocalDateTime(schedule.startDate, schedule.startTime)
            val filteringAlarm = alarmDateTime.isAfter(currentTime)
            logE("After Filter", "울릴 유무 : $filteringAlarm")
            filteringAlarm
        }
    }

    // startDate와 startTime을 LocalDateTime으로 변환하는 헬퍼 메서드
    private fun convertToLocalDateTime(date: String, time: String): LocalDateTime {
        // date: "3월2일", time: "21시25분"을 처리
        val dateFormatter = DateTimeFormatter.ofPattern("M월d일")
        val timeFormatter = DateTimeFormatter.ofPattern("H시m분")

        // 각각 LocalDate와 LocalTime으로 파싱
        val localDate = LocalDateTime.now().withMonth(parseMonth(date)).withDayOfMonth(parseDay(date))
        val localTime = LocalDateTime.now().withHour(parseHour(time)).withMinute(parseMinute(time))

        // LocalDateTime으로 결합
        return localDate.with(localTime.toLocalTime())
    }

    private fun parseMonth(date: String): Int {
        return date.substringBefore("월").toInt()
    }

    private fun parseDay(date: String): Int {
        return date.substringAfter("월").substringBefore("일").toInt()
    }

    private fun parseHour(time: String): Int {
        return time.substringBefore("시").toInt()
    }

    private fun parseMinute(time: String): Int {
        return time.substringAfter("시").substringBefore("분").toInt()
    }
}