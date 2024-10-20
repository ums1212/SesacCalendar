package com.sesac.sesacscheduler.ui.scheduleadd

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.sesac.sesacscheduler.R
import com.sesac.sesacscheduler.common.EnumAlarmTime
import com.sesac.sesacscheduler.common.EnumColor
import com.sesac.sesacscheduler.common.EnumRepeat
import com.sesac.sesacscheduler.common.NO_SCHEDULE_ID
import com.sesac.sesacscheduler.common.ScheduleResult
import com.sesac.sesacscheduler.common.formatCurrentDate
import com.sesac.sesacscheduler.common.formatCurrentTime
import com.sesac.sesacscheduler.common.formatDate
import com.sesac.sesacscheduler.common.formatTimeToString
import com.sesac.sesacscheduler.common.logE
import com.sesac.sesacscheduler.common.toastShort
import com.sesac.sesacscheduler.databinding.FragmentAddSchedulerBinding
import com.sesac.sesacscheduler.factory.scheduleViewModelFactory
import com.sesac.sesacscheduler.model.ScheduleInfo
import com.sesac.sesacscheduler.ui.common.BaseFragment
import com.sesac.sesacscheduler.viewmodel.ScheduleViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AddSchedulerFragment : BaseFragment<FragmentAddSchedulerBinding>(FragmentAddSchedulerBinding::inflate) {


    private val viewModel by lazy {
        scheduleViewModelFactory.create(ScheduleViewModel::class.java)
    }

    private var scheduleId: Int = NO_SCHEDULE_ID

    private var repeatDays = EnumRepeat.NO.repeat
    private var appointmentAlarmTime = EnumAlarmTime.BEFORE_1_HOUR.time
    private var color = EnumColor.LIGHT_PURPLE.color
    private var latitude = 0.0
    private var longitude = 0.0

    private val navController by lazy {
        findNavController()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logE("AddSchedulerFragment","onViewCreated시작")
        setupArgument()
        setupSpinnerAdapter()
        getCurrentDataAndTime()

        if(scheduleId != NO_SCHEDULE_ID) {
            logE("schedule이 null아님","$scheduleId")
            viewModel.getSchedule(scheduleId!!)
            observeSchedule()
            binding.tvDelete.visibility = View.VISIBLE
        }
        setupUI()
    }
    private fun setupArgument(){
        arguments?.let {
            latitude = it.getString("latitude")?.toDoubleOrNull() ?: 0.0
            longitude = it.getString("longitude")?.toDoubleOrNull() ?: 0.0
            binding.tvAppointmentPlace.text = it.getString("place") ?: "약속 장소"
            scheduleId = it.getInt("scheduleId")
        }
    }
    private fun observeSchedule() {
        lifecycleScope.launch {
            viewModel.schedule.collectLatest { schedule ->
                binding.tvSave.text = "수정"
                when (schedule) {
                    is ScheduleResult.Success -> reSetupUI(schedule.resultData)
                    is ScheduleResult.Loading -> toastShort("스케줄 세팅중")
                    is ScheduleResult.RoomDBError -> toastShort("스케줄 세팅 룸DB 에러")
                    else -> logE("ReSetupUI 문제", "$schedule")
                }
            }
        }
    }
    private fun setupSpinnerAdapter(){
        setupSpinner(binding.spinnerRepeat, R.array.repeat_choice)
        setupSpinner(binding.spinnerAlarm, R.array.alarm_choice)
    }
    private fun setupSpinner(spinner: Spinner, arrayResId: Int) {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            arrayResId,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }
    private fun reSetupUI(it: ScheduleInfo) {
        with(binding) {
            etTitle.setText(it.title)
            tvStartDate.text = it.startDate
            tvLastDate.text = it.lastDate
            tvStartTime.text = it.startTime
            tvEndTime.text = it.endTime
            spinnerRepeat.setSelection(it.repeatDays)
            tvAppointmentPlace.text = it.appointmentPlace
            switchAppointmentAlarm.isChecked = it.appointmentAlarm
            spinnerAlarm.setSelection(it.appointmentAlarmTime)
            iconColor.setColorFilter(ContextCompat.getColor(requireContext(), it.color))
            color = it.color
        }
    }
    private fun setupUI() {
        with(binding) {
            // 날짜 선택
            tvStartDate.setOnClickListener { chooseDate(tvStartDate) }
            tvLastDate.setOnClickListener { chooseDate(tvLastDate) }
            // 시간 선택
            tvStartTime.setOnClickListener { chooseTime(tvStartTime) }
            tvEndTime.setOnClickListener { chooseTime(tvEndTime) }
            //반복 선택
            chooseRepeat()
            // 장소 선택
            iconPlace.setOnClickListener { choosePlace() }
            // 알람 설정 스위치
            switchAppointmentAlarm.setOnCheckedChangeListener { _, isChecked ->
                spinnerAlarm.visibility = if (isChecked) View.VISIBLE else View.GONE
            }
            //알람 시간 선택
            chooseAlarmTime()
            // 색상 선택
            iconColor.setOnClickListener { chooseColor() }
            // 취소 및 저장 버튼
            tvCancel.setOnClickListener { navController.popBackStack() }
            tvSave.setOnClickListener { saveSchedule() }
            // 삭제 버튼
            tvDelete.setOnClickListener { deleteSchedule() }
        }
    }
    private fun chooseDate(targetView: TextView) {
        with(binding.calendarView) {
            visibility = View.VISIBLE
            setOnDateChangeListener { _, year, month, dayOfMonth ->
                targetView.text = formatDate(year, month, dayOfMonth)
                visibility = View.GONE
            }
        }
    }
    private fun chooseTime(targetView: TextView) {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("시간 선택해 보거라~~")
            .build()

        picker.show(parentFragmentManager, "MaterialTimePicker")
        picker.addOnPositiveButtonClickListener {
            val formattedTime = formatTimeToString(picker.hour, picker.minute)
            targetView.text = formattedTime
        }
    }
    private fun chooseColor() {
        with(binding){
            flowColor.visibility = View.VISIBLE
            setColorClickListener(iconColorLightpurple, R.color.sc_lightpurple,EnumColor.LIGHT_PURPLE.color)
            setColorClickListener(iconColorBlue, R.color.sc_blue,EnumColor.BLUE.color)
            setColorClickListener(iconColorGreen, R.color.sc_green,EnumColor.GREEN.color)
            setColorClickListener(iconColorYellow, R.color.sc_yellow,EnumColor.YELLOW.color)
            setColorClickListener(iconColorRed, R.color.sc_red,EnumColor.RED.color)
            setColorClickListener(iconColorSkyblue, R.color.sc_skyblue,EnumColor.SKY_BLUE.color)
            setColorClickListener(iconColorPurple, R.color.sc_purple,EnumColor.PURPLE.color)
            setColorClickListener(iconColorRedviolet, R.color.sc_redviolet,EnumColor.RED_VIOLET.color)
            setColorClickListener(iconColorGray, R.color.sc_gray,EnumColor.GRAY.color)
            setColorClickListener(iconColorPink, R.color.sc_pink,EnumColor.PINK.color)
        }
    }
    private fun setColorClickListener(imageView: ShapeableImageView, colorResId: Int, saveColor: Int) {
        imageView.setOnClickListener {
            with(binding){
                // icon_color의 tint 색상을 클릭된 아이콘의 색상으로 변경
                iconColor.setColorFilter(ContextCompat.getColor(requireContext(), colorResId))
                color = saveColor
                flowColor.visibility = View.GONE
            }
        }
    }
    private fun saveSchedule() {
        with(binding) {
            val title = etTitle.text.toString()
            val startDate = tvStartDate.text.toString()
            val lastDate = tvLastDate.text.toString()
            val startTime = tvStartTime.text.toString()
            val endTime = tvEndTime.text.toString()
            val appointmentPlace = tvAppointmentPlace.text.toString()
            val appointmentAlarm = switchAppointmentAlarm.isChecked

            val schedule = ScheduleInfo(
                title, startDate, lastDate, startTime, endTime,
                repeatDays, appointmentPlace, latitude, longitude,
                appointmentAlarm, appointmentAlarmTime, color
            )

            if (scheduleId != NO_SCHEDULE_ID) {
                viewModel.updateSchedule(schedule)
            } else {
                viewModel.insertSchedule(schedule)
            }
        }
    }
    private fun chooseRepeat(){
        binding.spinnerRepeat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long ) {
                repeatDays = when (position) {
                    EnumRepeat.NO.order -> EnumRepeat.NO.repeat
                    EnumRepeat.EVERY_DAY.order -> EnumRepeat.EVERY_DAY.repeat
                    EnumRepeat.EVERY_WEEK.order -> EnumRepeat.EVERY_WEEK.repeat
                    EnumRepeat.EVERY_MONTH.order -> EnumRepeat.EVERY_MONTH.repeat
                    else -> EnumRepeat.NO.repeat
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                repeatDays = EnumRepeat.NO.repeat
            }
        }
    }
    private fun choosePlace(){
        findNavController().navigate(R.id.action_addSchedulerFragment_to_searchLocationFragment)
    }
    private fun chooseAlarmTime(){
        binding.spinnerAlarm.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                appointmentAlarmTime = when (position) {
                    EnumAlarmTime.BEFORE_1_HOUR.order -> EnumAlarmTime.BEFORE_1_HOUR.time
                    EnumAlarmTime.BEFORE_1_HALF_HOUR.order -> EnumAlarmTime.BEFORE_1_HALF_HOUR.time
                    EnumAlarmTime.BEFORE_2_HOUR.order -> EnumAlarmTime.BEFORE_2_HOUR.time
                    EnumAlarmTime.BEFORE_2_HALF_HOUR.order -> EnumAlarmTime.BEFORE_2_HALF_HOUR.time
                    EnumAlarmTime.BEFORE_3_HOUR.order -> EnumAlarmTime.BEFORE_3_HOUR.time
                    else -> EnumAlarmTime.BEFORE_1_HOUR.time
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                appointmentAlarmTime = EnumAlarmTime.BEFORE_1_HOUR.time
            }
        }
    }
    private fun getCurrentDataAndTime(){
        with(binding){
            tvStartDate.text = formatCurrentDate()
            tvLastDate.text = formatCurrentDate()
            tvStartTime.text = formatCurrentTime()
            tvEndTime.text = formatCurrentTime()
        }
    }
    private fun deleteSchedule() {
        scheduleId?.let {
            viewModel.deleteSchedule(it)
            navController.popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        logE("AddSchedulerFragment","onResume 시작")
    }

    override fun onPause() {
        super.onPause()
        logE("AddSchedulerFragment","onPause시작")
    }

    override fun onStop() {
        super.onStop()
        logE("AddSchedulerFragment","onStop시작")
    }

}