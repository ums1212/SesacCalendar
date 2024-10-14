package com.sesac.sesacscheduler.ui.home

import android.view.View
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.view.ViewContainer
import com.sesac.sesacscheduler.databinding.CalendarDayLayoutBinding

class DayViewContainer(view: View) : ViewContainer(view) {
//    val textView = view.findViewById<TextView>(R.id.calendarDayText)

    // With ViewBinding
    val root = CalendarDayLayoutBinding.bind(view).insideCalendarDayLayout
    val textView = CalendarDayLayoutBinding.bind(view).calendarDayText
    lateinit var day: CalendarDay

}