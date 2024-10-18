package com.sesac.sesacscheduler.common


enum class EnumAlarmTime(val order: Int, val time: Int) {
    BEFORE_1_HOUR(0,60),
    BEFORE_1_HALF_HOUR(1,90),
    BEFORE_2_HOUR(2,120),
    BEFORE_2_HALF_HOUR(3,150),
    BEFORE_3_HOUR(4,180)
}