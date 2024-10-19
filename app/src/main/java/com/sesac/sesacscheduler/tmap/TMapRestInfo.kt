package com.sesac.sesacscheduler.tmap

import com.google.gson.annotations.SerializedName

data class TMapRestInfo(
    @SerializedName("features")
    val features: List<FeatureItems>
){

    data class FeatureItems(
        @SerializedName("properties")
        val properties: Properties,
    )

    data class Properties(
        @SerializedName("totalDistance")
        val totalDistance: Long,
        @SerializedName("totalTime")
        val totalTime: Long,
    )
}
