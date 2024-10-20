package com.sesac.sesacscheduler.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sesac.sesacscheduler.alarm.AlarmScheduler
import com.sesac.sesacscheduler.alarm.repository.AlarmRepository
import com.sesac.sesacscheduler.alarm.usecase.AlarmUsecase
import com.sesac.sesacscheduler.common.ScheduleResult
import com.sesac.sesacscheduler.common.SchedulerApplication
import com.sesac.sesacscheduler.common.logE
import com.sesac.sesacscheduler.common.toastShort
import com.sesac.sesacscheduler.database.ScheduleRoomDatabase
import com.sesac.sesacscheduler.model.ScheduleInfo
import com.sesac.sesacscheduler.repo.ScheduleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScheduleViewModel : ViewModel() {

    private val database = ScheduleRoomDatabase.getDatabase(SchedulerApplication.getSchedulerApplication())
    private val scheduleDAO = database.scheduleDao()

    private val repository: ScheduleRepository by lazy {
        ScheduleRepository(scheduleDAO)
    }
    private var _scheduleList =
        MutableStateFlow<ScheduleResult<MutableList<ScheduleInfo>>>(ScheduleResult.NoConstructor)
    val scheduleList get() = _scheduleList.asStateFlow()

    private var _schedule =
        MutableStateFlow<ScheduleResult<ScheduleInfo>>(ScheduleResult.NoConstructor)
    val schedule get() = _schedule.asStateFlow()

    private var _removeSchedule =
        MutableStateFlow<ScheduleResult<Int>>(ScheduleResult.NoConstructor)
    val removeSchedule get() = _removeSchedule.asStateFlow()

    private val ioDispatchers = CoroutineScope(Dispatchers.IO)

    private fun <T> handleResult(result: ScheduleResult<T>, successMessage: String) {
        when (result) {
            is ScheduleResult.Success -> {
                viewModelScope.launch {
                    toastShort(successMessage)
                    logE("성공", result.toString())
                }
            }
            is ScheduleResult.Loading -> {
                viewModelScope.launch {
                    logE("로딩", result.toString())
                }
            }
            is ScheduleResult.RoomDBError -> {
                logE("DB 에러", result.exception.toString())
            }
            else -> {
                logE("예상치 못한 상태", result.toString())
            }
        }
    }
    private fun <T> fetchAndUpdateState(
        fetch: suspend () -> Flow<ScheduleResult<T>>,
        stateFlow: MutableStateFlow<ScheduleResult<T>>
    ) {
        viewModelScope.launch {
            withContext(ioDispatchers.coroutineContext) {
                fetch().collectLatest {
                    stateFlow.value = it
                }
            }
        }
    }

    fun insertSchedule(newSchedule: ScheduleInfo) {
        viewModelScope.launch {
            withContext(ioDispatchers.coroutineContext) {
                repository.insertSchedule(newSchedule).collectLatest {
                    handleResult(it, "스케줄 저장")
                    if (it is ScheduleResult.Success) {
                        AlarmScheduler(SchedulerApplication.getSchedulerApplication(), AlarmUsecase(AlarmRepository(scheduleDAO)))
                            .scheduleAlarmsForAppointments()
                    }
                }
            }
        }
    }

    fun updateSchedule(updatedSchedule: ScheduleInfo) {
        viewModelScope.launch {
            withContext(ioDispatchers.coroutineContext) {
                repository.updateSchedule(updatedSchedule).collectLatest {
                    handleResult(it, "스케줄 수정 완료")
                }
            }
        }
    }

    fun getSchedule(id: Int) {
        fetchAndUpdateState({ repository.getSchedule(id) }, _schedule)
    }

    fun findAllSchedule() {
        fetchAndUpdateState({ repository.findAllSchedule() }, _scheduleList)
    }

    fun findScheduleByMonth(month: String) {
        fetchAndUpdateState({ repository.getScheduleMonth(month) }, _scheduleList)
    }

    fun deleteSchedule(id: Int) {
        viewModelScope.launch {
            withContext(ioDispatchers.coroutineContext) {
                repository.deleteSchedule(id)
            }
        }
    }
}
