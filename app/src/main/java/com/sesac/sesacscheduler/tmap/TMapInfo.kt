package com.sesac.sesacscheduler.tmap

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TMapInfo(
    val distance: Long,
    val carTime: Long,
    val walkTime: Long,
) : Parcelable
