package com.sesac.sesacscheduler

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.sesac.sesacscheduler.databinding.FragmentMainBinding
import java.util.Calendar
import java.util.Date

class MainFragment : BaseFragment<FragmentMainBinding>(FragmentMainBinding::inflate) {

    private var selectedYear = 0
    private var selectedMonth = 0
    private var selectedDate = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 초기 editTextView 힌트 설정
        Calendar.getInstance().apply {
            time = Date(binding.calendarView.date)
            binding.editTextSchedule.hint = "${get(Calendar.MONTH)+1}월 ${get(Calendar.DAY_OF_MONTH)}일에 일정 추가"
        }
        
        // 캘린더의 날짜 변경 시
        binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            if(year+month+dayOfMonth == selectedYear+selectedMonth+selectedDate){
                // 날짜를 한번 더 클릭하면 일정 리스트로 이동
                findNavController().navigate(R.id.action_mainFragment_to_scheduleListFragment)
            }else{
                // 날짜가 변경될 때마다 editText의 hint내용 변경
                binding.editTextSchedule.hint = "${month+1}월 ${dayOfMonth}일에 일정 추가"
                selectedYear = year
                selectedMonth = month
                selectedDate = dayOfMonth
            }
        }

        // 일정 추가 버튼
        binding.buttonAdd.setOnClickListener {
            if(binding.editTextSchedule.text.toString()==""){
                // 일정 텍스트를 직접 입력하지 않고 추가하려고 할때
                findNavController().navigate(R.id.action_mainFragment_to_addSchedulerFragment)
            }else{
                // 일정 텍스트를 직접 입력했을 때 자동으로 일정 추가
            }
        }
    }

}