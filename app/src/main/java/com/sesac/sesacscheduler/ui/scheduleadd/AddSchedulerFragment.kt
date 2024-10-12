package com.sesac.sesacscheduler.ui.scheduleadd

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.navigation.fragment.findNavController
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import com.jakewharton.rxbinding4.view.clicks
import com.sesac.sesacscheduler.R
import com.sesac.sesacscheduler.common.BEFORE_1_HALF_HOUR
import com.sesac.sesacscheduler.common.BEFORE_1_HOUR
import com.sesac.sesacscheduler.common.BEFORE_2_HALF_HOUR
import com.sesac.sesacscheduler.common.BEFORE_2_HOUR
import com.sesac.sesacscheduler.common.BEFORE_3_HOUR
import com.sesac.sesacscheduler.common.EVERY_DAY
import com.sesac.sesacscheduler.common.EVERY_MONTH
import com.sesac.sesacscheduler.common.EVERY_WEEK
import com.sesac.sesacscheduler.common.EnumAlarmTime
import com.sesac.sesacscheduler.common.EnumColor
import com.sesac.sesacscheduler.common.EnumRepeat
import com.sesac.sesacscheduler.common.NO
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
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class AddSchedulerFragment : BaseFragment<FragmentAddSchedulerBinding>(FragmentAddSchedulerBinding::inflate) {


    private val viewModel by lazy{
        scheduleViewModelFactory.create(ScheduleViewModel::class.java)
    }
    private val compositeDisposable = CompositeDisposable()
    private var calendarVisible = false
    private var timeVisible = false
    private var appointmentImageVisible = false
    private var alarmSpinnerVisible = false
    private var colorVisible = false

    private var repeatDays = 0
    private var appointmentPlace = ""
    private var longitude = 0.0
    private var latitude = 0.0
    private var appointmentAlarm = false
    private var appointmentAlarmTime = 0
    private var color = 0

    private val navController by lazy {
        findNavController()
    }
//    val args: AddSchedulerFragmentArgs by navArgs()
    private var scheduleInfo = ScheduleInfo()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        compositeDisposable.add(binding.tvSave
            .clicks()
            .subscribe {
                if(checkValidate()){
                    addSchedule()
                }
            }
        )
    }

    private fun initView(){
        getCurrentDataAndTime()
        with(binding){
            chooseDate(tvStartDate)
            chooseDate(tvLastDate)
            chooseTime()
            chooseTime()
        }
        chooseRepeat()
        choosePlace()
        chooseAlarmOnOff()
        chooseColor()
        addSchedule()
        cancelSchedule()
    }

    private fun chooseDate(date: MaterialTextView){
        with(binding) {
            compositeDisposable
                .add(date.clicks()
//                    .observeOn(Schedulers.io())
                    .throttleFirst(300, TimeUnit.MILLISECONDS)
                    .subscribe
                        {
                            if(calendarView.visibility == View.GONE){
                                calendarView.visibility = View.VISIBLE
                            } else {
                                calendarView.visibility = View.GONE
                            }
                            //calendarVisible = changedVisibility(calendarView, calendarVisible)
                            with(calendarView) {
                                setOnDateChangeListener { _, _, month, dayOfMonth ->
                                    when(date){
                                        tvStartDate -> {
                                            tvStartDate.text = formatDate(month, dayOfMonth)
                                        }
                                        tvLastDate -> {
                                            tvLastDate.text = formatDate(month, dayOfMonth)
                                        }
                                    }
                                    visibility = View.GONE
                                    calendarVisible = false
                                }
                            }
                        }
                )
        }
    }
    private fun chooseTime(){
        with(binding) {
            compositeDisposable
                .add(tvStartTime.clicks()
                    .observeOn(Schedulers.io())
                    .throttleFirst(300, TimeUnit.MILLISECONDS)
                    .subscribe
                        ({
                            if(timePicker.visibility == View.GONE){
                                timePicker.visibility = View.VISIBLE
                            } else {
                                timePicker.visibility = View.GONE
                            }
                            //timeVisible = changedVisibility(timePicker, timeVisible)
                            with(timePicker) {
                                setOnTimeChangedListener { _, hour, minute ->
                                    when(tvStartTime){
                                        tvStartTime -> {
                                            tvStartTime.text = formatTime(hour, minute)
                                            scheduleInfo.startTime = formatTime(hour, minute)
                                        }
                                        tvEndTime -> {
                                            tvEndTime.text = formatTime(hour, minute)
                                            scheduleInfo.endTime = formatTime(hour, minute)
                                        }
                                    }
                                    visibility = View.GONE
                                    timeVisible = false
                                }
                            }
                        },
                        {
                            logE("시간error", it.toString())
                        })
                )
        }
    }
    private fun chooseRepeat(){
        binding.spinnerRepeat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                repeatDays = when (position) {
                    NO -> EnumRepeat.NO.repeat
                    EVERY_DAY -> EnumRepeat.EVERY_DAY.repeat
                    EVERY_WEEK -> EnumRepeat.EVERY_WEEK.repeat
                    EVERY_MONTH -> EnumRepeat.EVERY_MONTH.repeat
                    else -> 0
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }
    private fun choosePlace(){
        //금천캠퍼스 37.4749-위도, 126.8911-경도 long
        //사진 추가되면 visible로
//        val latitude = args.latitude
//        val longitude = args.longitude
        binding.iconPlace.setOnClickListener {
            navController.navigate(R.id.action_addSchedulerFragment_to_searchLocationFragment)
        }
//        scheduleInfo.latitude = latitude.toDouble()
//        scheduleInfo.longitude = longitude.toDouble()
    }
    private fun chooseAlarmOnOff(){
        with(binding) {
            binding.switchAppointmentAlarm.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    appointmentAlarm = true
                    changedVisibility(spinnerAlarm, alarmSpinnerVisible)
                    chooseAlarmTime()
                } else {
                    appointmentAlarm = false
                    spinnerAlarm.visibility = View.GONE
                    alarmSpinnerVisible = false
                }
            }
        }
    }
    private fun chooseAlarmTime(){
        binding.spinnerAlarm.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                appointmentAlarmTime = when (position) {
                    BEFORE_1_HOUR -> EnumAlarmTime.BEFORE_1_HOUR.time
                    BEFORE_1_HALF_HOUR -> EnumAlarmTime.BEFORE_1_HALF_HOUR.time
                    BEFORE_2_HOUR -> EnumAlarmTime.BEFORE_2_HOUR.time
                    BEFORE_2_HALF_HOUR -> EnumAlarmTime.BEFORE_2_HALF_HOUR.time
                    BEFORE_3_HOUR -> EnumAlarmTime.BEFORE_3_HOUR.time
                    else -> 0
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }
    private fun chooseColor() {
        with(binding){
            iconColor.setOnClickListener {
                colorVisible = changedVisibility(flowColor, colorVisible)
                chooseColorFromFlow(iconColorLightpurple)
                chooseColorFromFlow(iconColorBlue)
                chooseColorFromFlow(iconColorGreen)
                chooseColorFromFlow(iconColorYellow)
                chooseColorFromFlow(iconColorRed)
                chooseColorFromFlow(iconColorSkyblue)
                chooseColorFromFlow(iconColorPurple)
                chooseColorFromFlow(iconColorRedviolet)
                chooseColorFromFlow(iconColorGray)
                chooseColorFromFlow(iconColorPink)
            }
        }
    }
    private fun chooseColorFromFlow(choosecolor: ShapeableImageView){
        with(binding) {
            choosecolor.setOnClickListener {
                color = when (choosecolor) {
                    iconColorLightpurple -> EnumColor.LIGHT_PURPLE.color
                    iconColorBlue -> EnumColor.BLUE.color
                    iconColorGreen -> EnumColor.GREEN.color
                    iconColorYellow -> EnumColor.YELLOW.color
                    iconColorRed -> EnumColor.RED.color
                    iconColorSkyblue -> EnumColor.SKY_BLUE.color
                    iconColorPurple -> EnumColor.PURPLE.color
                    iconColorRedviolet -> EnumColor.RED_VIOLET.color
                    iconColorGray -> EnumColor.GRAY.color
                    iconColorPink -> EnumColor.PINK.color
                    else -> 0
                }
            }
            colorVisible = false
        }
    }
    /*
    false -> visible
    리턴 true

    진짜 -> gone
    리턴 false
     */

    private fun changedVisibility(view: View, isVisible: Boolean): Boolean {
        view.visibility = if (isVisible) {
            View.GONE
        } else {
            View.VISIBLE
        }
        return !isVisible
    }

    private fun checkValidate():Boolean{
        return true
    }

    private fun cancelSchedule(){
        compositeDisposable
            .add(binding.tvCancel
                .clicks()
                .throttleFirst(300, TimeUnit.MILLISECONDS)
                .subscribe {
                    findNavController().navigate(R.id.action_addSchedulerFragment_to_mainFragment)
                }
            )
    }
    private fun addSchedule() {
        with(binding) {
            compositeDisposable
                .add(tvSave
                    .clicks()
                    .observeOn(Schedulers.io())
                    .throttleFirst(300, TimeUnit.MILLISECONDS)
                    .subscribe(
                        {
                            val title = etTitle.text.toString()
                            val startDate = tvStartDate.text.toString()
                            val lastDate = tvLastDate.text.toString()
                            val startTime = tvStartTime.text.toString()
                            val endTime = tvEndTime.text.toString()
                            val repeatDays = scheduleInfo.repeatDays
                            val appointmentPlace = tvAppointmentPlace.text.toString()
                            val longitude = 31.5
                            val latitude = 55.5
                            val appointmentAlarm = appointmentAlarm
                            val appointmentAlarmTime = appointmentAlarmTime
                            val color = color
                            viewModel.insertSchedule(
                                ScheduleInfo(title, startDate, lastDate, startTime, endTime, repeatDays, appointmentPlace, longitude, latitude, appointmentAlarm, appointmentAlarmTime, color)
                            )
                            Log.d("저장? - 프래그먼트 ", "")
                        },
                        {
                            logE("RX에러 : 스케줄저장", it.toString())
                        }
                    )
                )
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

        override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}