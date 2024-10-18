package com.sesac.sesacscheduler.ui.scheduleadd

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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

    private var scheduleId: Int? = null

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

        latitude = if(arguments?.getString("latitude").isNullOrEmpty()) 0.0 else arguments?.getString("latitude")!!.toDouble()
        longitude = if(arguments?.getString("longitude").isNullOrEmpty()) 0.0 else arguments?.getString("longitude")!!.toDouble()
        binding.tvAppointmentPlace.text = if(arguments?.getString("place").isNullOrEmpty()) "약속 장소" else arguments?.getString("place")!!

        val adapter = ArrayAdapter.createFromResource(requireContext(),R.array.repeat_choice,android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerRepeat.adapter = adapter
        val adapter2 =  ArrayAdapter.createFromResource(requireContext(),R.array.alarm_choice,android.R.layout.simple_spinner_item)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerAlarm.adapter = adapter2

        getCurrentDataAndTime()
        scheduleId = arguments?.getInt("scheduleId")

        if(scheduleId != 0) {
            logE("schedule이 null아님","$scheduleId")
            viewModel.getSchedule(scheduleId!!)
            lifecycleScope.launch {
                viewModel.schedule.collectLatest { schedule ->
                    binding.tvSave.text = "수정"
                    reSetupUI(schedule)
                }
            }
        }
        setupUI()

    }
    private fun reSetupUI(it: ScheduleResult<ScheduleInfo>) {
        with(binding) {
            when (it) {
                is ScheduleResult.Success -> {
                    etTitle.setText(it.resultData.title)
                    tvStartDate.text = it.resultData.startDate
                    tvLastDate.text = it.resultData.lastDate
                    tvStartTime.text = it.resultData.startTime
                    tvEndTime.text = it.resultData.endTime
                    spinnerRepeat.setSelection(it.resultData.repeatDays)
                    tvAppointmentPlace.text = it.resultData.appointmentPlace
                    switchAppointmentAlarm.isChecked = it.resultData.appointmentAlarm
                    spinnerAlarm.setSelection(it.resultData.appointmentAlarmTime)
                    iconColor.setColorFilter(ContextCompat.getColor(requireContext(), it.resultData.color))
                    color = it.resultData.color
                }
                is ScheduleResult.Loading -> toastShort("스케줄 세팅중 : $it")
                is ScheduleResult.RoomDBError -> toastShort("스케줄 세팅 룸DB에러 : $it")
                else ->  {
                    toastShort("하잇! : $it")
                    logE("ReSetupUI문제", "$it")
                }
            }
            setupUI()
        }
    }
    private fun setupUI() {
        with(binding) {
            // 날짜 선택
            tvStartDate.setOnClickListener { showCalendarView(tvStartDate) }
            tvLastDate.setOnClickListener { showCalendarView(tvLastDate) }
            // 시간 선택
            tvStartTime.setOnClickListener { showTimePicker(tvStartTime) }
            tvEndTime.setOnClickListener { showTimePicker(tvEndTime) }
            //반복 선택
            chooseRepeat()
            // 장소 선택
            iconPlace.setOnClickListener {
                choosePlace()
            }
            // 알람 설정 스위치
            switchAppointmentAlarm.setOnCheckedChangeListener { _, isChecked ->
                spinnerAlarm.visibility = if (isChecked) View.VISIBLE else View.GONE
            }
            //알람 시간 선택
            chooseAlarmTime()
            // 색상 선택
            iconColor.setOnClickListener { toggleColorFlow() }
            // 취소 및 저장 버튼
            tvCancel.setOnClickListener { navController.popBackStack() }
            tvSave.setOnClickListener { saveSchedule() }

        }
    }
    private fun showCalendarView(targetView: TextView) {
        with(binding.calendarView) {
            visibility = View.VISIBLE
            setOnDateChangeListener { _, _, month, dayOfMonth ->
                targetView.text = formatDate(month, dayOfMonth)
                visibility = View.GONE
            }
        }
    }
//    private fun showTimePicker(targetView: TextView) {
//        with(binding.timePicker) {
//            visibility = View.VISIBLE
//            setOnTimeChangedListener { _, hourOfDay, minute ->
//                targetView.text = formatTimeToString(hourOfDay, minute)
//                visibility = View.GONE
//            }
//        }
//    }
    private fun showTimePicker(targetView: TextView) {
        showMaterialTimePicker { selectedTime ->
            targetView.text = selectedTime
        }
    }
    // TimePicker 다이얼로그를 표시하는 함수
    private fun showMaterialTimePicker(onTimeSelected: (String) -> Unit) {
        // MaterialTimePicker 빌더 설정
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)  // 24시간 형식
            .setHour(12)  // 기본 시간 설정
            .setMinute(0) // 기본 분 설정
            .setTitleText("Select Time")  // 다이얼로그 제목
            .build()

        // 다이얼로그를 화면에 표시
        picker.show(parentFragmentManager, "MaterialTimePicker")

        // 사용자가 시간을 선택했을 때 콜백 처리
        picker.addOnPositiveButtonClickListener {
            // 선택한 시간과 분 가져오기
            val selectedHour = picker.hour
            val selectedMinute = picker.minute

            // 시간 형식에 맞게 포맷 (예: 12:05)
            val formattedTime = formatTimeToString(selectedHour, selectedMinute)
            // 콜백으로 선택된 시간 전달
            onTimeSelected(formattedTime)
        }
    }
    private fun toggleColorFlow() {
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
            val appointmentAlarm = alarmOnOff()
            if(scheduleId != 0){
                viewModel.updateSchedule(
                    ScheduleInfo(
                        title,
                        startDate,
                        lastDate,
                        startTime,
                        endTime,
                        repeatDays,
                        appointmentPlace,
                        latitude,
                        longitude,
                        appointmentAlarm,
                        appointmentAlarmTime,
                        color
                    )
                )
            } else {
                viewModel.insertSchedule(
                    ScheduleInfo(
                        title,
                        startDate,
                        lastDate,
                        startTime,
                        endTime,
                        repeatDays,
                        appointmentPlace,
                        latitude,
                        longitude,
                        appointmentAlarm,
                        appointmentAlarmTime,
                        color
                    )
                )
            }
        }

    }
    private fun chooseRepeat(){

        binding.spinnerRepeat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long,
            ) {
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
    private fun alarmOnOff() = binding.switchAppointmentAlarm.isChecked
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