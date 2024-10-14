package com.sesac.sesacscheduler.ui.scheduleadd

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.imageview.ShapeableImageView
import com.sesac.sesacscheduler.R
import com.sesac.sesacscheduler.common.EnumAlarmTime
import com.sesac.sesacscheduler.common.EnumColor
import com.sesac.sesacscheduler.common.EnumRepeat
import com.sesac.sesacscheduler.common.formatCurrentDate
import com.sesac.sesacscheduler.common.formatCurrentTime
import com.sesac.sesacscheduler.common.formatDate
import com.sesac.sesacscheduler.common.formatTime
import com.sesac.sesacscheduler.common.logE
import com.sesac.sesacscheduler.databinding.FragmentAddSchedulerBinding
import com.sesac.sesacscheduler.factory.scheduleViewModelFactory
import com.sesac.sesacscheduler.model.ScheduleInfo
import com.sesac.sesacscheduler.ui.common.BaseFragment
import com.sesac.sesacscheduler.viewmodel.ScheduleViewModel

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
    private var place = "약속 장소"

    private val navController by lazy {
        findNavController()
    }

    //데이터 저장
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
//        with(binding){
//            outState.putString("title",etTitle.text.toString())
//            outState.putString("startDate",tvStartDate.text.toString())
//            outState.putString("lastDate",tvLastDate.text.toString())
//            outState.putString("startTime",tvStartTime.text.toString())
//            outState.putString("endTime",tvEndTime.text.toString())
//            outState.putInt("repeatDays",repeatDays)
//            outState.putString("appointmentPlace",tvAppointmentPlace.text.toString())
//            outState.putBoolean("appointmentAlarm",switchAppointmentAlarm.isChecked)
//            outState.putInt("appointmentAlarmTime",appointmentAlarmTime)
//            outState.putInt("color",color)
//        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logE("AddSchedulerFragment","onViewCreated시작")
        //데이터 불러오기
//        with(binding){
//            savedInstanceState?.let {
//                etTitle.setText(it.getString("title"))
//                tvStartDate.text = it.getString("startDate")
//                tvLastDate.text = it.getString("lastDate")
//                tvStartTime.text = it.getString("startTime")
//                tvEndTime.text = it.getString("endTime")
//                repeatDays = it.getInt("repeatDays")
//                tvAppointmentPlace.text = it.getString("appointmentPlace")
//                switchAppointmentAlarm.isChecked = it.getBoolean("appointmentAlarm")
//                appointmentAlarmTime = it.getInt("appointmentAlarmTime")
//                color = it.getInt("color")
//            }
//        }
        latitude = if(arguments?.getString("latitude").isNullOrEmpty()) 0.0 else arguments?.getString("latitude")!!.toDouble()
        longitude = if(arguments?.getString("longitude").isNullOrEmpty()) 0.0 else arguments?.getString("longitude")!!.toDouble()
        binding.tvAppointmentPlace.text = if(arguments?.getString("place").isNullOrEmpty()) "약속 장소" else arguments?.getString("place")!!

        getCurrentDataAndTime()
        scheduleId = arguments?.getInt("scheduleId")
        scheduleId?.let {
            viewModel.getSchedule(it)
        }
        setupUI()

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
            tvSave.setOnClickListener { addSchedule() }
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
    private fun showTimePicker(targetView: TextView) {
        with(binding.timePicker) {
            visibility = View.VISIBLE
            setOnTimeChangedListener { _, hourOfDay, minute ->
                targetView.text = formatTime(hourOfDay, minute)
                visibility = View.GONE
            }
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
    private fun addSchedule() {
        with(binding) {
            val title = etTitle.text.toString()
            val startDate = tvStartDate.text.toString()
            val lastDate = tvLastDate.text.toString()
            val startTime = tvStartTime.text.toString()
            val endTime = tvEndTime.text.toString()
            val appointmentPlace = tvAppointmentPlace.text.toString()
            val appointmentAlarm = alarmOnOff()
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
    private fun chooseRepeat(){
        binding.spinnerRepeat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                repeatDays = when (position) {
                    EnumRepeat.NO.order -> EnumRepeat.NO.repeat
                    EnumRepeat.EVERY_DAY.order -> EnumRepeat.EVERY_DAY.repeat
                    EnumRepeat.EVERY_WEEK.order -> EnumRepeat.EVERY_WEEK.repeat
                    EnumRepeat.EVERY_MONTH.order -> EnumRepeat.EVERY_MONTH.repeat
                    else -> EnumRepeat.NO.repeat
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }
    private fun choosePlace(){
        findNavController().navigate(R.id.action_addSchedulerFragment_to_searchLocationFragment)
    }
    private fun alarmOnOff() = binding.switchAppointmentAlarm.isChecked
    private fun chooseAlarmTime(){
        binding.spinnerAlarm.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                appointmentAlarmTime = when (position) {
                    EnumAlarmTime.BEFORE_1_HOUR.order -> EnumAlarmTime.BEFORE_1_HOUR.time
                    EnumAlarmTime.BEFORE_1_HALF_HOUR.order -> EnumAlarmTime.BEFORE_1_HALF_HOUR.time
                    EnumAlarmTime.BEFORE_2_HOUR.order -> EnumAlarmTime.BEFORE_2_HOUR.time
                    EnumAlarmTime.BEFORE_2_HALF_HOUR.order -> EnumAlarmTime.BEFORE_2_HALF_HOUR.time
                    EnumAlarmTime.BEFORE_3_HOUR.order -> EnumAlarmTime.BEFORE_3_HOUR.time
                    else -> EnumAlarmTime.BEFORE_1_HOUR.time
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
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

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        scheduleId = arguments?.getInt("scheduleId")
//
//        scheduleId?.let {
//            viewModel.getSchedule(it)
//        }
//
//        initView()
//        compositeDisposable.add(binding.tvSave
//            .clicks()
//            .subscribe {
//                if(checkValidate()){
//                    addSchedule()
//                }
//            }
//        )
//    }
//
//    private fun initView(){
//        getCurrentDataAndTime()
//        with(binding){
//            chooseDate(tvStartDate)
//            chooseDate(tvLastDate)
//            chooseTime()
//            chooseTime()
//        }
//        chooseRepeat()
//        choosePlace()
//        chooseAlarmOnOff()
//        chooseColor()
//        addSchedule()
//        cancelSchedule()
//    }
//
//    private fun chooseDate(date: MaterialTextView){
//        with(binding) {
//            compositeDisposable
//                .add(date.clicks()
////                    .observeOn(Schedulers.io())
//                    .throttleFirst(300, TimeUnit.MILLISECONDS)
//                    .subscribe
//                        {
//                            if(calendarView.visibility == View.GONE){
//                                calendarView.visibility = View.VISIBLE
//                            } else {
//                                calendarView.visibility = View.GONE
//                            }
//                            //calendarVisible = changedVisibility(calendarView, calendarVisible)
//                            with(calendarView) {
//                                setOnDateChangeListener { _, _, month, dayOfMonth ->
//                                    when(date){
//                                        tvStartDate -> {
//                                            tvStartDate.text = formatDate(month, dayOfMonth)
//                                        }
//                                        tvLastDate -> {
//                                            tvLastDate.text = formatDate(month, dayOfMonth)
//                                        }
//                                    }
//                                    visibility = View.GONE
//                                    calendarVisible = false
//                                }
//                            }
//                        }
//                )
//        }
//    }
//    private fun chooseTime(){
//        with(binding) {
//            compositeDisposable
//                .add(tvStartTime.clicks()
//                    .observeOn(Schedulers.io())
//                    .throttleFirst(300, TimeUnit.MILLISECONDS)
//                    .subscribe
//                        ({
//                            if(timePicker.visibility == View.GONE){
//                                timePicker.visibility = View.VISIBLE
//                            } else {
//                                timePicker.visibility = View.GONE
//                            }
//                            //timeVisible = changedVisibility(timePicker, timeVisible)
//                            with(timePicker) {
//                                setOnTimeChangedListener { _, hour, minute ->
//                                    when(tvStartTime){
//                                        tvStartTime -> {
//                                            tvStartTime.text = formatTime(hour, minute)
//                                            scheduleInfo.startTime = formatTime(hour, minute)
//                                        }
//                                        tvEndTime -> {
//                                            tvEndTime.text = formatTime(hour, minute)
//                                            scheduleInfo.endTime = formatTime(hour, minute)
//                                        }
//                                    }
//                                    visibility = View.GONE
//                                    timeVisible = false
//                                }
//                            }
//                        },
//                        {
//                            logE("시간error", it.toString())
//                        })
//                )
//        }
//    }
//    private fun chooseRepeat(){
//        binding.spinnerRepeat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
//            override fun onItemSelected(
//                parent: AdapterView<*>,
//                view: View,
//                position: Int,
//                id: Long
//            ) {
//                repeatDays = when (position) {
//                    NO -> EnumRepeat.NO.repeat
//                    EVERY_DAY -> EnumRepeat.EVERY_DAY.repeat
//                    EVERY_WEEK -> EnumRepeat.EVERY_WEEK.repeat
//                    EVERY_MONTH -> EnumRepeat.EVERY_MONTH.repeat
//                    else -> 0
//                }
//            }
//
//            override fun onNothingSelected(p0: AdapterView<*>?) {}
//        }
//    }
//    private fun choosePlace(){
//        //금천캠퍼스 37.4749-위도, 126.8911-경도 long
//        //사진 추가되면 visible로
////        val latitude = args.latitude
////        val longitude = args.longitude
//        binding.iconPlace.setOnClickListener {
//            navController.navigate(R.id.action_addSchedulerFragment_to_searchLocationFragment)
//        }
////        scheduleInfo.latitude = latitude.toDouble()
////        scheduleInfo.longitude = longitude.toDouble()
//    }
//    private fun chooseAlarmOnOff(){
//        with(binding) {
//            binding.switchAppointmentAlarm.setOnCheckedChangeListener { _, isChecked ->
//                if (isChecked) {
//                    appointmentAlarm = true
//                    changedVisibility(spinnerAlarm, alarmSpinnerVisible)
//                    chooseAlarmTime()
//                } else {
//                    appointmentAlarm = false
//                    spinnerAlarm.visibility = View.GONE
//                    alarmSpinnerVisible = false
//                }
//            }
//        }
//    }
//    private fun chooseAlarmTime(){
//        binding.spinnerAlarm.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
//            override fun onItemSelected(
//                parent: AdapterView<*>,
//                view: View,
//                position: Int,
//                id: Long
//            ) {
//                appointmentAlarmTime = when (position) {
//                    BEFORE_1_HOUR -> EnumAlarmTime.BEFORE_1_HOUR.time
//                    BEFORE_1_HALF_HOUR -> EnumAlarmTime.BEFORE_1_HALF_HOUR.time
//                    BEFORE_2_HOUR -> EnumAlarmTime.BEFORE_2_HOUR.time
//                    BEFORE_2_HALF_HOUR -> EnumAlarmTime.BEFORE_2_HALF_HOUR.time
//                    BEFORE_3_HOUR -> EnumAlarmTime.BEFORE_3_HOUR.time
//                    else -> 0
//                }
//            }
//            override fun onNothingSelected(p0: AdapterView<*>?) {}
//        }
//    }
//    private fun chooseColor() {
//        with(binding){
//            iconColor.setOnClickListener {
//                colorVisible = changedVisibility(flowColor, colorVisible)
//                chooseColorFromFlow(iconColorLightpurple)
//                chooseColorFromFlow(iconColorBlue)
//                chooseColorFromFlow(iconColorGreen)
//                chooseColorFromFlow(iconColorYellow)
//                chooseColorFromFlow(iconColorRed)
//                chooseColorFromFlow(iconColorSkyblue)
//                chooseColorFromFlow(iconColorPurple)
//                chooseColorFromFlow(iconColorRedviolet)
//                chooseColorFromFlow(iconColorGray)
//                chooseColorFromFlow(iconColorPink)
//            }
//        }
//    }
//    private fun chooseColorFromFlow(choosecolor: ShapeableImageView){
//        with(binding) {
//            choosecolor.setOnClickListener {
//                color = when (choosecolor) {
//                    iconColorLightpurple -> EnumColor.LIGHT_PURPLE.color
//                    iconColorBlue -> EnumColor.BLUE.color
//                    iconColorGreen -> EnumColor.GREEN.color
//                    iconColorYellow -> EnumColor.YELLOW.color
//                    iconColorRed -> EnumColor.RED.color
//                    iconColorSkyblue -> EnumColor.SKY_BLUE.color
//                    iconColorPurple -> EnumColor.PURPLE.color
//                    iconColorRedviolet -> EnumColor.RED_VIOLET.color
//                    iconColorGray -> EnumColor.GRAY.color
//                    iconColorPink -> EnumColor.PINK.color
//                    else -> 0
//                }
//            }
//            colorVisible = false
//        }
//    }
//    /*
//    false -> visible
//    리턴 true
//
//    진짜 -> gone
//    리턴 false
//     */
//
//    private fun changedVisibility(view: View, isVisible: Boolean): Boolean {
//        view.visibility = if (isVisible) {
//            View.GONE
//        } else {
//            View.VISIBLE
//        }
//        return !isVisible
//    }
//
//    private fun checkValidate():Boolean{
//        return true
//    }
//
//    private fun cancelSchedule(){
//        compositeDisposable
//            .add(binding.tvCancel
//                .clicks()
//                .throttleFirst(300, TimeUnit.MILLISECONDS)
//                .subscribe {
//                    findNavController().navigate(R.id.action_addSchedulerFragment_to_mainFragment)
//                }
//            )
//    }
//    private fun addSchedule() {
//        with(binding) {
//            compositeDisposable
//                .add(tvSave
//                    .clicks()
//                    .observeOn(Schedulers.io())
//                    .throttleFirst(300, TimeUnit.MILLISECONDS)
//                    .subscribe(
//                        {
//                            val title = etTitle.text.toString()
//                            val startDate = tvStartDate.text.toString()
//                            val lastDate = tvLastDate.text.toString()
//                            val startTime = tvStartTime.text.toString()
//                            val endTime = tvEndTime.text.toString()
//                            val repeatDays = scheduleInfo.repeatDays
//                            val appointmentPlace = tvAppointmentPlace.text.toString()
//                            val longitude = 31.5
//                            val latitude = 55.5
//                            val appointmentAlarm = appointmentAlarm
//                            val appointmentAlarmTime = appointmentAlarmTime
//                            val color = color
//                            viewModel.insertSchedule(
//                                ScheduleInfo(title, startDate, lastDate, startTime, endTime, repeatDays, appointmentPlace, longitude, latitude, appointmentAlarm, appointmentAlarmTime, color)
//                            )
//                            Log.d("저장? - 프래그먼트 ", "")
//                        },
//                        {
//                            logE("RX에러 : 스케줄저장", it.toString())
//                        }
//                    )
//                )
//        }
//    }
//    private fun getCurrentDataAndTime(){
//        with(binding){
//            tvStartDate.text = formatCurrentDate()
//            tvLastDate.text = formatCurrentDate()
//            tvStartTime.text = formatCurrentTime()
//            tvEndTime.text = formatCurrentTime()
//        }
//    }
//
//        override fun onDestroy() {
//        super.onDestroy()
//        compositeDisposable.dispose()
//    }
//}