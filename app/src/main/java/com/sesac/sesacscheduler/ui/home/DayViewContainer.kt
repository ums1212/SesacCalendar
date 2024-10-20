package com.sesac.sesacscheduler.ui.home

import android.view.View
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.view.ViewContainer
import com.sesac.sesacscheduler.databinding.CalendarDayLayoutBinding

class DayViewContainer(view: View) : ViewContainer(view) {
    // With ViewBinding
    val scheduleBox = CalendarDayLayoutBinding.bind(view).insideScheduleLayout
    val textView = CalendarDayLayoutBinding.bind(view).calendarDayText
    lateinit var day: CalendarDay
}