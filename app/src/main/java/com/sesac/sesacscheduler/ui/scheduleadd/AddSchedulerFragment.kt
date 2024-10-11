package com.sesac.sesacscheduler.ui.scheduleadd

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.navigation.fragment.findNavController
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import com.jakewharton.rxbinding4.view.clicks
import com.sesac.sesacscheduler.R
import com.sesac.sesacscheduler.common.EVERY_DAY
import com.sesac.sesacscheduler.common.EVERY_MONTH
import com.sesac.sesacscheduler.common.EVERY_WEEK
import com.sesac.sesacscheduler.common.EnumColor
import com.sesac.sesacscheduler.common.NO
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
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class AddSchedulerFragment : BaseFragment<FragmentAddSchedulerBinding>(FragmentAddSchedulerBinding::inflate) {


    /*private val viewModel by lazy{
        scheduleViewModelFactory.create(ScheduleViewModel::class.java)
    }
    private val compositeDisposable = CompositeDisposable()
    private var calendarVisible = false
    private var timeVisible = false
    private var appointmentImageVisible = false
    private var alarm = false
    private var appointmentVisible = false
    private var colorVisible = false
    private var repeat = 0
    private var color = 1
    private lateinit var shapeableImageView: ShapeableImageView
    private var image = 1

    private var scheduleInfo = ScheduleInfo()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initView()
        return binding.root
    }

    private fun initView(){
        getCurrentDataAndTime()
        with(binding){
            chooseDate(tvStartDate)
            chooseDate(tvLastDate)
            chooseTime(tvStartTime)
            chooseTime(tvEndTime)
        }
        chooseRepeat()
        choosePlace()
        chooseAlarm()
        chooseColor()
        addSchedule()
        cancelSchedule()
    }

    private fun chooseDate(date: MaterialTextView){
        with(binding) {
            compositeDisposable
                .add(date.clicks()
                    .observeOn(Schedulers.io())
                    //.throttleFirst(300, TimeUnit.MILLISECONDS)3333
                    .subscribe
                        ({
                        calendarVisible = changedVisibility(calendarView, calendarVisible)
                        with(calendarView) {
                            setOnDateChangeListener { _, _, month, dayOfMonth ->
                                date.text = formatDate(month, dayOfMonth)
                                scheduleInfo.startDate = date.text.toString()
                                visibility = View.GONE
                                calendarVisible = false
                            }
                        }

                    },
                        {   logE("날짜선택error", it.toString()) })

                )
        }
    }
    private fun chooseTime(time: MaterialTextView){
        with(binding) {
            compositeDisposable
                .add(time.clicks()
                    .observeOn(Schedulers.io())
                    //.throttleFirst(300, TimeUnit.MILLISECONDS)
                    .subscribe ({
                        timeVisible = changedVisibility(timePicker, timeVisible)
                        with(timePicker) {
                            setOnTimeChangedListener { _, hour, minute ->
                                val formattedTime = formatTime(hour, minute)
                                time.text = formattedTime
                                visibility = View.GONE
                                timeVisible = false
                            }
                        }
                    },
                        {   logE("시간error", it.toString()) })
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
                repeat = when (position) {
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
    private fun getCurrentDataAndTime(){
        with(binding){
            tvStartDate.text = formatCurrentDate()
            tvLastDate.text = formatCurrentDate()
            tvStartTime.text = formatCurrentTime()
            tvEndTime.text = formatCurrentTime()
        }
    }
    private fun choosePlace(){
        //금천캠퍼스 37.4749-위도, 126.8911-경도

    }
    private fun chooseAlarm(){

    }
    private fun chooseColor() {
        with(binding){
            iconColor.setOnClickListener{
                colorVisible = changedVisibility(flowColor, colorVisible)
                iconColorLightpurple.setOnClickListener{
                    image = EnumColor.LIGHT_PURPLE.color
                    flowColor.visibility = View.GONE
                    colorVisible = false
                }
                iconColorBlue.setOnClickListener{image = EnumColor.BLUE.color}
                iconColorGreen.setOnClickListener{image = EnumColor.GREEN.color}
                iconColorYellow.setOnClickListener{image = EnumColor.YELLOW.color}
                iconColorRed.setOnClickListener{image = EnumColor.RED.color}
                iconColorSkyblue.setOnClickListener{image = EnumColor.SKY_BLUE.color}
                iconColorPurple.setOnClickListener{image = EnumColor.PURPLE.color}
                iconColorRedviolet.setOnClickListener{image = EnumColor.RED_VIOLET.color}
                iconColorGray.setOnClickListener{image = EnumColor.GRAY.color}
                iconColorPink.setOnClickListener{image = EnumColor.PINK.color}
            }
        }
    }
    private fun changedVisibility(view: View, isVisible: Boolean): Boolean {
        view.visibility = if (isVisible) View.GONE else View.VISIBLE
        return !isVisible
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        compositeDisposable.add(binding.tvSave
            .clicks()
            .subscribe {
                if(checkValidate()){
                    addSchedule()
                }
            }
        )
    }
    private fun checkValidate():Boolean{

        return true
    }

    private fun cancelSchedule(){
        compositeDisposable
            .add(binding.tvCancel
                .clicks()
                //.debounce(300, TimeUnit.MILLISECONDS)
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
                    //.throttleFirst(300, TimeUnit.MILLISECONDS)
                    .subscribe(
                        {
                            val title = etTitle.text.toString()
                            val startDate = tvStartDate.text.toString()
                            val lastDate = tvLastDate.text.toString()
                            val startTime = tvStartTime.text.toString()
                            val endTime = tvEndTime.text.toString()
                            val repeatDays = repeat
                            val appointmentPlace = tvAppointmentPlace.text.toString()
                            val longitude = 0.0
                            val latitude = 0.0
                            val appointmentAlarm = alarm
                            val alarmTime = tvAlarmTime.text.toString()
                            var color: Int
//                            viewModel.insertSchedule(ScheduleInfo(title, startDate, lastDate, startTime, endTime, repeatDays, appointmentPlace, longitude, latitude, appointmentAlarm, alarmTime, color))

                            viewModel.insertSchedule(
                                ScheduleInfo(
                                    "ㅇ",
                                    "ㅇ",
                                    "ㅇ",
                                    "ㅇ",
                                    "ㅇ",
                                    0,
                                    "ㅇ",
                                    0.0,
                                    0.0,
                                    true,
                                    "ㅇ",
                                    1
                                )
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

        override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }*/
}