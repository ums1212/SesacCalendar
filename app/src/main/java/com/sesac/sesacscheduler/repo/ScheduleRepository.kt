package com.sesac.sesacscheduler.repo

sealed class ScheduleRepository<out T> {
    data object NoConstructor : ScheduleRepository<Nothing>()
    data object Loading : ScheduleRepository<Nothing>()

    data class Success<T>(val resultData: T) : ScheduleRepository<T>()

    data class NetworkError(val exception: Throwable) : ScheduleRepository<Nothing>()
    data class RoomDBError(val exception: Throwable) : ScheduleRepository<Nothing>()
}
