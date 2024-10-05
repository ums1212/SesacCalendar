package com.sesac.sesacscheduler.common

sealed class ScheduleResult<out T> {
    data object NoConstructor : ScheduleResult<Nothing>()
    data object Loading : ScheduleResult<Nothing>()

    data class Success<T>(val resultData: T) : ScheduleResult<T>()

    data class NetworkError(val exception: Throwable) : ScheduleResult<Nothing>()
    data class RoomDBError(val exception: Throwable) : ScheduleResult<Nothing>()
}