package com.sesac.sesacscheduler.common

enum class EnumRepeat(val order: Int, val repeat: Int) {
    NO(0,0),
    EVERY_DAY(1,-1),
    EVERY_WEEK(2,-2),
    EVERY_MONTH(3,-3)
}