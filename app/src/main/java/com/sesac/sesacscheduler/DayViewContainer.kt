package com.sesac.sesacscheduler

import android.view.View
import android.widget.TextView
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.view.ViewContainer
import com.sesac.sesacscheduler.databinding.CalendarDayLayoutBinding

class DayViewContainer(view: View, clickEvent: (day: CalendarDay) -> Unit) : ViewContainer(view) {
//    val textView = view.findViewById<TextView>(R.id.calendarDayText)

    // With ViewBinding
    val textView = CalendarDayLayoutBinding.bind(view).calendarDayText
    lateinit var day: CalendarDay

    init {
        view.setOnClickListener{
            clickEvent(day)
        }
    }
}