package com.sesac.sesacscheduler.ui.home

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
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
import com.sesac.sesacscheduler.databinding.FragmentMainBinding
import com.sesac.sesacscheduler.ui.common.BaseFragment
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import reactivecircus.flowbinding.android.view.clicks
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

class MainFragment : BaseFragment<FragmentMainBinding>(FragmentMainBinding::inflate) {

    private var selectedDate: LocalDate = LocalDate.now()
    private var selectedDayView: LinearLayout? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 초기 editTextView 힌트 설정
        binding.editTextSchedule.hint = "${LocalDateTime.now().monthValue}월 ${LocalDateTime.now().dayOfMonth}일에 일정 추가"

        // 일정 추가 버튼 FlowBinding으로 UI이벤트 등록
        binding.buttonAdd.clicks()
            .onEach {
                if(binding.editTextSchedule.text.toString()==""){
                    // 일정 텍스트를 직접 입력하지 않고 추가하려고 할때
                    findNavController().navigate(R.id.action_mainFragment_to_addSchedulerFragment)
                }else{
                    // 일정 텍스트를 직접 입력했을 때 자동으로 일정 추가
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)

        // 커스텀 캘린더 셋팅
        settingKizitonwoseCalendar()
    }

    private fun settingKizitonwoseCalendar(){
        // kizitonwose 라이브러리 사용
        with(binding.calendarView){
            dayBinder = object : MonthDayBinder<DayViewContainer> {
                // Called only when a new container is needed.
                override fun create(view: View) = DayViewContainer(view, viewLifecycleOwner.lifecycleScope){ day ->
                    notifyDateChanged(day.date)
                    if(day.date == selectedDate){
                        // 날짜를 한번 더 클릭하면 일정 리스트로 이동
                        findNavController().navigate(R.id.action_mainFragment_to_scheduleListFragment)
                    }else{
                        if(selectedDayView!=null) selectedDayView?.background = null
                        selectedDayView = view as LinearLayout
                        selectedDate = day.date
                        // 날짜가 변경될 때마다 editText의 hint내용 변경
                        binding.editTextSchedule.hint = "${day.date.monthValue}월 ${day.date.dayOfMonth}일에 일정 추가"
                        // 선택 날짜 테두리 표시
                        selectedDayView?.background = resources.getDrawable(R.drawable.calendar_day_layout_selected, null)
                    }
                }

                // Called every time we need to reuse a container.
                override fun bind(container: DayViewContainer, data: CalendarDay) {
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
                }
            }
            val currentMonth = YearMonth.now()
            val startMonth = currentMonth.minusMonths(100) // Adjust as needed
            val endMonth = currentMonth.plusMonths(100) // Adjust as needed
            val firstDayOfWeek = firstDayOfWeekFromLocale() // Available from the library
            val daysOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.SUNDAY)
            setup(startMonth, endMonth, firstDayOfWeek)
            scrollToMonth(currentMonth)

            monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: View) = MonthViewContainer(view)
                override fun bind(container: MonthViewContainer, data: CalendarMonth) {
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
            }
        }
    }
}