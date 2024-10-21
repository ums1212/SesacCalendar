package com.sesac.sesacscheduler.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TemporaryViewModel : ViewModel() {
    // LiveData로 각 데이터를 관리
    private val _title = MutableLiveData<String>()
    val title: LiveData<String> get() = _title

    private val _startDate = MutableLiveData<String>()
    val startDate: LiveData<String> get() = _startDate

    private val _lastDate = MutableLiveData<String>()
    val lastDate: LiveData<String> get() = _lastDate

    private val _startTime = MutableLiveData<String>()
    val startTime: LiveData<String> get() = _startTime

    private val _endTime = MutableLiveData<String>()
    val endTime: LiveData<String> get() = _endTime

    private val _chooseRepeat = MutableLiveData<Int>()
    val chooseRepeat: LiveData<Int> get() = _chooseRepeat

    private val _alarmState = MutableLiveData<Boolean>()
    val alarmState: LiveData<Boolean> get() = _alarmState

    // 데이터 저장
    fun saveData(
        title: String,
        startDate: String,
        lastDate: String,
        startTime: String,
        endTime: String,
        chooseRepeat: Int,
        alarmState: Boolean
    ) {
        _title.value = title
        _startDate.value = startDate
        _lastDate.value = lastDate
        _startTime.value = startTime
        _endTime.value = endTime
        _chooseRepeat.value = chooseRepeat
        _alarmState.value = alarmState
    }
    // 데이터 조회
    fun getSavedData(): Map<String, Any?> {
        return mapOf(
            "title" to _title.value,
            "startDate" to _startDate.value,
            "lastDate" to _lastDate.value,
            "startTime" to _startTime.value,
            "endTime" to _endTime.value,
            "chooseRepeat" to _chooseRepeat.value,
            "alarmState" to _alarmState.value
        )
    }
}