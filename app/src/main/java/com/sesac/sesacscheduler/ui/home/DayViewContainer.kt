package com.sesac.sesacscheduler.ui.home

import android.view.View
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.view.ViewContainer
import com.sesac.sesacscheduler.databinding.CalendarDayLayoutBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import reactivecircus.flowbinding.android.view.clicks

class DayViewContainer(view: View, scope: LifecycleCoroutineScope, clickEvent: (day: CalendarDay) -> Unit) : ViewContainer(view) {
//    val textView = view.findViewById<TextView>(R.id.calendarDayText)

    // With ViewBinding
    val textView = CalendarDayLayoutBinding.bind(view).calendarDayText
    lateinit var day: CalendarDay

    init {
        view.clicks().onEach {
            clickEvent(day)
        }.launchIn(scope)
    }
}