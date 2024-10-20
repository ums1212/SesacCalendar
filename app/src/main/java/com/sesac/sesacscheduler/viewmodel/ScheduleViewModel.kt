package com.sesac.sesacscheduler.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.sesac.sesacscheduler.alarm.AlarmScheduler
import com.sesac.sesacscheduler.alarm.repository.AlarmRepository
import com.sesac.sesacscheduler.alarm.usecase.AlarmUsecase
import com.sesac.sesacscheduler.common.ScheduleResult
import com.sesac.sesacscheduler.common.SchedulerApplication
import com.sesac.sesacscheduler.common.logE
import com.sesac.sesacscheduler.common.toastShort
import com.sesac.sesacscheduler.dao.ScheduleDAO
import com.sesac.sesacscheduler.database.ScheduleRoomDatabase
import com.sesac.sesacscheduler.model.ScheduleInfo
import com.sesac.sesacscheduler.repo.ScheduleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

    private var _scheduleComplete = MutableStateFlow(false)
    val scheduleComplete get() = _scheduleComplete.asStateFlow()

    fun insertSchedule(newSchedule: ScheduleInfo) {
        viewModelScope.launch {
            withContext(ioDispatchers.coroutineContext) {
                repository.insertSchedule(newSchedule).collectLatest {
                    when (it) {
                        is ScheduleResult.Success -> {
                            viewModelScope.launch {
                                toastShort("스케줄 저장")
                                logE("스케줄 저장(성공)", it.toString())
                                AlarmScheduler(SchedulerApplication.getSchedulerApplication(), AlarmUsecase(AlarmRepository(scheduleDAO)))
                                    .scheduleAlarmsForAppointments()
                                _scheduleComplete.emit(true)
                            }
                        }
                        is ScheduleResult.Loading -> {
                            viewModelScope.launch {
                                toastShort("스케줄 저장중...")
                                logE("스케줄 저장(로딩)", it.toString())
                            }
                        }

                        is ScheduleResult.RoomDBError -> {
                            logE("스케줄 저장(DB에러)", it.exception.toString())
                        }

                        else -> {
                            logE("스케줄 저장(너가 왜뜨니?)", it.toString())
                        }
                    }
                }
            }
        }
    }

    fun getSchedule(id: Int) {
        viewModelScope.launch {
            withContext(ioDispatchers.coroutineContext) {
                repository.getSchedule(id).collectLatest {
                    _schedule.value = it
                }
            }
        }
    }

    fun deleteSchedule(id: Int) {
        viewModelScope.launch {
            withContext(ioDispatchers.coroutineContext) {
                repository.deleteSchedule(id)/*.collectLatest {
                    _schedule.value = it
            }*/
            }
        }
    }
    fun findAllSchedule() = viewModelScope.launch {
        withContext(ioDispatchers.coroutineContext) {
            repository.findAllSchedule().collectLatest {
                _scheduleList.value = it
            }
        }
    }
    fun findScheduleByMonth(month: String) = viewModelScope.launch {
        withContext(ioDispatchers.coroutineContext) {
            repository.getScheduleMonth(month).collectLatest {
                _scheduleList.value = it
            }
        }
    }
    fun updateSchedule(updatedSchedule: ScheduleInfo) {
        viewModelScope.launch {
            withContext(ioDispatchers.coroutineContext) {
                repository.updateSchedule(updatedSchedule).collectLatest {
                    when (it) {
                        is ScheduleResult.Success -> {
                            viewModelScope.launch {
                                toastShort("스케줄 수정 완료")
                                logE("스케줄 수정(성공)", it.toString())

                            }
                        }
                        is ScheduleResult.Loading -> {
                            logE("스케줄 수정(로딩)", it.toString())
                        }

                        is ScheduleResult.RoomDBError -> {
                            logE("스케줄 수정(DB에러)", it.exception.toString())
                        }

                        else -> {
                            logE("스케줄 수정(너가 왜뜨니?)", it.toString())
                        }
                    }
                }
            }
        }
    }
}
