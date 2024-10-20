package com.sesac.sesacscheduler.ui.home

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.sesac.sesacscheduler.R
import com.sesac.sesacscheduler.common.ScheduleResult
import com.sesac.sesacscheduler.common.collectWhenStarted
import com.sesac.sesacscheduler.common.getScheduleColorResource
import com.sesac.sesacscheduler.common.hideSoftInput
import com.sesac.sesacscheduler.common.toastShort
import com.sesac.sesacscheduler.databinding.FragmentMainBinding
import com.sesac.sesacscheduler.databinding.ScheduleBoxBinding
import com.sesac.sesacscheduler.model.ScheduleInfo
import com.sesac.sesacscheduler.ui.common.BaseFragment
import com.sesac.sesacscheduler.viewmodel.ScheduleViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import reactivecircus.flowbinding.android.view.clicks
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class MainFragment : BaseFragment<FragmentMainBinding>(FragmentMainBinding::inflate) {

    private var selectedDate: LocalDate = LocalDate.now()
    private var selectedDayView: LinearLayout? = null

    private val viewModel: ScheduleViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 일정 추가 버튼
        settingAddScheduleButton()

        // 커스텀 캘린더 셋팅
        settingKizitonwoseCalendar()
    }

    private fun settingAddScheduleButton(){
        // 일정 추가 버튼 FlowBinding으로 UI이벤트 등록
        binding.buttonAdd.clicks()
            .onEach {
                if(binding.editTextSchedule.text.toString()==""){
                    // 일정 텍스트를 직접 입력하지 않고 추가하려고 할때
                    findNavController().navigate(R.id.action_mainFragment_to_addSchedulerFragment)
                }else{
                    // 일정 텍스트를 직접 입력했을 때 자동으로 일정 추가
                    // 달력에서 선택한 날짜 기준으로 저장됨
                    addSchedule()
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun addSchedule(){
        val title = binding.editTextSchedule.text.toString()
        if(title.isNotEmpty()){
            val schedule = ScheduleInfo(
                title = title,
                startDate = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                lastDate = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            )
            viewModel.insertSchedule(schedule)
            binding.editTextSchedule.text?.clear()
            requireActivity().hideSoftInput()

            collectWhenStarted(viewModel.scheduleComplete){
                if(it) settingKizitonwoseCalendar()
            }
        }
    }

    private fun setCalendarMonth(currentMonth: YearMonth){
        // 초기 editTextView 힌트 설정
        binding.editTextSchedule.hint = "${selectedDate.monthValue}월 ${selectedDate.dayOfMonth}일에 일정 추가"
        // 초기 달력뷰 셋팅
        with(binding.calendarView){
            val startMonth = currentMonth.minusMonths(100) // Adjust as needed
            val endMonth = currentMonth.plusMonths(100) // Adjust as needed
            val firstDayOfWeek = firstDayOfWeekFromLocale() // Available from the library
            setup(startMonth, endMonth, firstDayOfWeek)
            scrollToMonth(currentMonth)
        }
    }

    private fun settingKizitonwoseCalendar(){
        setCalendarMonth(YearMonth.of(selectedDate.year, selectedDate.month))
        // kizitonwose 라이브러리 사용
        with(binding.calendarView){
            // 달력 스크롤을 할 때마다 해당 월의 스케줄을 가져옴
            binding.calendarView.monthScrollListener = {
                if(scrollPaged) viewModel.findScheduleByMonth(it.yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM")))
            }
            // 일 레이아웃 바인딩
            dayBinder = object : MonthDayBinder<DayViewContainer> {
                // Called only when a new container is needed.
                override fun create(view: View) = DayViewContainer(view)
                // Called every time we need to reuse a container.
                override fun bind(container: DayViewContainer, data: CalendarDay) {
                    // Set text for the day of the week.
                    container.day = data
                    container.textView.text = data.date.dayOfMonth.toString()
                    container.textView.setTextColor(
                        resources.getColor(
                            if(data.position==DayPosition.MonthDate){
                                // 평일과 주말 색 셋팅
                                when(data.date.dayOfWeek){
                                    DayOfWeek.SUNDAY -> R.color.sc_red
                                    DayOfWeek.SATURDAY -> R.color.sc_blue
                                    else -> R.color.white
                                }
                            }else{
                                // 해당 월의 날짜가 아닌 경우에는 회색
                                R.color.sc_black
                            },
                            null
                        )
                    )
                    // 초기 선택일 표시
                    if(data.date==selectedDate){
                        selectedDayView(data, container)
                    }
                    // 일정 데이터 불러와서 표시
                    showScheduleOnContainer(data, container)
                    // 일정 클릭 이벤트
                    settingContainerClickEvent(container, data)
                }

                fun settingContainerClickEvent(
                    container: DayViewContainer,
                    data: CalendarDay
                ) {
                    container.view.clicks().onEach {
                        // 클릭 이벤트를 여기서 작성
                        if (data.date == selectedDate) {
                            // 날짜를 한번 더 클릭하면 일정 리스트로 이동
                            moveToScheduleList(data.date)
                        } else {
                            selectedDayView(data, container)
                        }
                    }.launchIn(viewLifecycleOwner.lifecycleScope)
                }

                fun selectedDayView(data: CalendarDay, container: DayViewContainer){
                    if (selectedDayView != null) selectedDayView?.background = null
                    selectedDayView = container.view as LinearLayout
                    selectedDate = data.date
                    // 날짜가 변경될 때마다 editText의 hint내용 변경
                    binding.editTextSchedule.hint =
                        "${data.date.monthValue}월 ${data.date.dayOfMonth}일에 일정 추가"
                    // 선택 날짜 테두리 표시
                    selectedDayView?.background =
                        resources.getDrawable(R.drawable.calendar_day_layout_selected, null)
                }

                fun showScheduleOnContainer(
                    data: CalendarDay,
                    container: DayViewContainer
                ) {
                    collectWhenStarted(viewModel.scheduleList){ result ->
                        when (result) {
                            is ScheduleResult.Success -> {
                                // 일정이 있을 경우 추가
                                result.resultData.filter { data.date == LocalDate.parse(it.startDate) }
                                    .also { filteredList ->
                                        if (filteredList.isNotEmpty()) {
                                            container.scheduleBox.removeAllViews()
                                            filteredList.forEach { schedule ->
                                                val scheduleView =
                                                    ScheduleBoxBinding.inflate(
                                                        layoutInflater
                                                    )
                                                scheduleView.textViewScheduleTitle.text =
                                                    schedule.title
                                                scheduleView.textViewScheduleTitle.setBackgroundColor(
                                                    resources.getColor(
                                                        getScheduleColorResource(schedule.color),
                                                        null
                                                    )
                                                )
                                                container.scheduleBox.addView(scheduleView.root)
                                            }
                                        }
                                    }
                            }

                            is ScheduleResult.Loading -> {}
                            is ScheduleResult.RoomDBError -> {
                                toastShort("일정을 불러오는데 실패했습니다.")
                            }

                            else -> {}
                        }
                    }
                } // showScheduleOnContainer 끝

            } // MonthDayBinder 끝

            // 요일, 월 레이아웃 바인딩
            monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: View) = MonthViewContainer(view)
                override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                    val daysOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.SUNDAY)
                    // Remember that the header is reused so this will be called for each month.
                    // However, the first day of the week will not change so no need to bind
                    // the same view every time it is reused.
                    if (container.titlesContainer.tag == null) {
                        container.titlesContainer.tag = data.yearMonth
                        // 월 셋팅
                        (container.titlesContainer.children.first() as TextView).text =
                            data.yearMonth.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                        // 요일 셋팅
                        (container.titlesContainer.children.last() as LinearLayout)
                            .children.map { it as TextView }
                            .forEachIndexed { index, textView ->
                                val dayOfWeek = daysOfWeek[index]
                                val title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                                textView.text = title
                                when(index){
                                    0 -> textView.setTextColor(resources.getColor(R.color.sc_red, null))
                                    6 -> textView.setTextColor(resources.getColor(R.color.sc_blue, null))
                                    else -> textView.setTextColor(resources.getColor(R.color.white, null))
                                }
                                // In the code above, we use the same `daysOfWeek` list
                                // that was created when we set up the calendar.
                                // However, we can also get the `daysOfWeek` list from the month data:
                                // val daysOfWeek = data.weekDays.first().map { it.date.dayOfWeek }
                                // Alternatively, you can get the value for this specific index:
                                // val dayOfWeek = data.weekDays.first()[index].date.dayOfWeek
                            }
                    }
                }
            } // monthHeaderBinder 끝
        }
    }

    private fun moveToScheduleList(date: LocalDate) {
        val bundle = Bundle().also {
            it.putString("selectedDate", date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
        }
        findNavController().navigate(R.id.action_mainFragment_to_scheduleListFragment, bundle)
    }

}