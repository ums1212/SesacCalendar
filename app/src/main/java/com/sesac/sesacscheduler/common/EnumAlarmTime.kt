package com.sesac.sesacscheduler.common


enum class EnumAlarmTime(val time: Int) {
    BEFORE_1_HOUR(60),
    BEFORE_1_HALF_HOUR(90),
    BEFORE_2_HOUR(120),
    BEFORE_2_HALF_HOUR(150),
    BEFORE_3_HOUR(180)
}