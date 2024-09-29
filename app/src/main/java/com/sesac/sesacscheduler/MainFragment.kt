package com.sesac.sesacscheduler

import android.os.Bundle
import android.view.View
import com.sesac.sesacscheduler.databinding.FragmentMainBinding
import java.util.Calendar
import java.util.Date

class MainFragment : BaseFragment<FragmentMainBinding>(FragmentMainBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 초기 editTextView 힌트 설정
        Calendar.getInstance().apply {
            time = Date(binding.calendarView.date)
            binding.editTextSchedule.hint = "${get(Calendar.MONTH)+1}월 ${get(Calendar.DAY_OF_MONTH)}일에 일정 추가"
        }
        // 날짜가 변경될 때마다 hint내용 변경
        binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            binding.editTextSchedule.hint = "${month+1}월 ${dayOfMonth}일에 일정 추가"
        }
    }

}