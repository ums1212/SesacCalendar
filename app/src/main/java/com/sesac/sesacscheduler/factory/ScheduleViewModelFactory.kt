package com.sesac.sesacscheduler.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sesac.sesacscheduler.viewmodel.ScheduleViewModel

@Suppress("UNCHECKED_CAST")
val scheduleViewModelFactory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        with(modelClass){
            when{
                isAssignableFrom(ScheduleViewModel::class.java) -> ScheduleViewModel()
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
