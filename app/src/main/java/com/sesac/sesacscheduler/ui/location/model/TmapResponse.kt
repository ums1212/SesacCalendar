package com.sesac.sesacscheduler.ui.location.model

data class TmapResponse(
    val searchPoiInfo: SearchPoiInfo
)

data class SearchPoiInfo(
    val pois: Pois
)

data class Pois(
    val poi: MutableList<PoiItem>
)

data class PoiItem(
    val name: String,
    val noorLat: String, // 위도
    val noorLon: String  // 경도
)