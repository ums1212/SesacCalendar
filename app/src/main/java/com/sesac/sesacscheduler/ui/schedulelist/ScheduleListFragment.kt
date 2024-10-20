package com.sesac.sesacscheduler.ui.schedulelist

import android.R
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sesac.sesacscheduler.common.ScheduleResult
import com.sesac.sesacscheduler.databinding.FragmentScheduleListBinding
import com.sesac.sesacscheduler.model.ScheduleInfo
import com.sesac.sesacscheduler.ui.common.BaseFragment
import com.sesac.sesacscheduler.viewmodel.ScheduleViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ScheduleListFragment :
    BaseFragment<FragmentScheduleListBinding>(FragmentScheduleListBinding::inflate) {

    private val viewModel: ScheduleViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 데이터 준비 (예시)
        val scheduleList = mutableListOf("일정1", "일정2", "일정3")

        // Adapter 생성
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_list_item_1, scheduleList)
//        val adapter = ArrayAdapter(requireContext(), com.sesac.sesacscheduler.R.layout.fragment_schedule_list, scheduleList)


        // ListView에 Adapter 설정
        binding.scheduleList.adapter = adapter

        // 데이터 추가 버튼 클릭 이벤트 처리
        binding.buttonAdd.setOnClickListener {
            findNavController().navigate(com.sesac.sesacscheduler.R.id.action_scheduleListFragment_to_addSchedulerFragment)
        }

        observeScheduleList(adapter)
    }

    override fun onResume() {
        super.onResume()
        viewModel.findAllSchedule() // 저장된 일정 불러오기
    }

    private fun observeScheduleList(adapter: ArrayAdapter<String>) {
        lifecycleScope.launch {
            viewModel.scheduleList.collectLatest { scheduleResult ->
                scheduleResult?.let {
                    when (it) {
                        is ScheduleResult.Success<*> -> {
                            val schedules = it.resultData as? List<ScheduleInfo> ?: return@let
                            adapter.clear()
                            adapter.addAll(schedules.map { schedule -> schedule.title })
                            // ListView 아이템 클릭 이벤트 처리
                            binding.scheduleList.setOnItemClickListener { parent, view, position, id ->
                                val selectedSchedule = schedules[position]
                                val bundle = Bundle()
                                bundle.putInt("scheduleId", selectedSchedule.id) // 선택된 일정의 ID를 Bundle에 담아 전달
                                // 일정 클릭 시 AddSchedulerFragment 로 이동
//                                findNavController().navigate(com.sesac.sesacscheduler.R.id.action_scheduleListFragment_to_addSchedulerFragment, bundle)
                            }
                        }

                        is ScheduleResult.Loading -> {
                            // 로딩 중 처리
                        }
                        is ScheduleResult.RoomDBError -> {
                            // 에러 처리
                        }
                        else -> { /* 기타 처리 */ }
                    }
                }
            }
        }
    }
}