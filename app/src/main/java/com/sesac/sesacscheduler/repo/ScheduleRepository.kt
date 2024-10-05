package com.sesac.sesacscheduler.repo

import com.sesac.sesacscheduler.common.MOCK_DELAY_TIME
import com.sesac.sesacscheduler.common.ScheduleResult
import com.sesac.sesacscheduler.common.SchedulerApplication
import com.sesac.sesacscheduler.dao.ScheduleDAO
import com.sesac.sesacscheduler.database.ScheduleRoomDatabase
import com.sesac.sesacscheduler.model.ScheduleInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlin.math.exp

class ScheduleRepository {

    private var scheduleDAO: ScheduleDAO

    init {
        val scheduleDatabase = ScheduleRoomDatabase.getDatabase(SchedulerApplication.getSchedulerApplication())
        scheduleDAO = scheduleDatabase.scheduleDao()
    }

    fun insertSchedule(newSchedule: ScheduleInfo) = flow {
        emit(ScheduleResult.Loading)
        delay(MOCK_DELAY_TIME)
        emit(ScheduleResult.Success(scheduleDAO.insertSchedule(newSchedule)))
    }.catch { exception -> emit(ScheduleResult.RoomDBError(exception)) }

    fun updateSchedule(newSchedule: ScheduleInfo) = flow {
        emit(ScheduleResult.Loading)
        delay(MOCK_DELAY_TIME)
        emit(ScheduleResult.Success(scheduleDAO.updateSchedule(newSchedule)))
    }.catch { exception -> emit(ScheduleResult.RoomDBError(exception)) }

    fun deleteSchedule(id: Int) = flow {
        emit(ScheduleResult.Loading)
        delay(MOCK_DELAY_TIME)
        emit(ScheduleResult.Success(scheduleDAO.deleteSchedule(id)))
    }.catch { exception -> emit(ScheduleResult.NetworkError(exception)) }

    fun getSchedule(id: Int) = flow {
        emit(ScheduleResult.Loading)
        delay(MOCK_DELAY_TIME)
        emit(ScheduleResult.Success(scheduleDAO.getSchedule(id)))
    }.catch { exception -> emit(ScheduleResult.NetworkError(exception)) }

    fun getScheduleDate(date: String) = flow {
        emit(ScheduleResult.Loading)
        delay(MOCK_DELAY_TIME)
        emit(ScheduleResult.Success(scheduleDAO.getScheduleDate(date)))
    }.catch { exception -> emit(ScheduleResult.NetworkError(exception)) }

    fun getScheduleMonth(month: String) = flow {
        emit(ScheduleResult.Loading)
        delay(MOCK_DELAY_TIME)
        emit(ScheduleResult.Success(scheduleDAO.getScheduleMonth(month)))
    }.catch { exception -> emit(ScheduleResult.NetworkError(exception)) }
}
