package com.sesac.sesacscheduler.common

enum class EnumRepeat(val repeat: Int) {
    NO(0),
    EVERY_DAY(-1),
    EVERY_WEEK(-2),
    EVERY_MONTH(-3)
}