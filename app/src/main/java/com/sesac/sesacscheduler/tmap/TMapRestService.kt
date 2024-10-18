package com.sesac.sesacscheduler.tmap

import com.sesac.sesacscheduler.common.TMAP_API_KEY
import com.sesac.sesacscheduler.common.TMAP_CAR_URL
import com.sesac.sesacscheduler.common.TMAP_WALK_URL
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TMapRestService {

    @GET(TMAP_CAR_URL)
    suspend fun getCarResult(
        @Query("version") version: String = "1",
        @Query("appKey") appKey: String = TMAP_API_KEY,
        @Query("totalValue") totalValue: Int = 2,
        @Query("startX") startX: Double,
        @Query("startY") startY: Double,
        @Query("endX") endX: Double,
        @Query("endY") endY: Double,
    ): Response<TMapRestInfo>

    @GET(TMAP_WALK_URL)
    suspend fun getWalkResult(
        @Query("version") version: String = "1",
        @Query("appKey") appKey: String = TMAP_API_KEY,
        @Query("startX") startX: Double,
        @Query("startY") startY: Double,
        @Query("endX") endX: Double,
        @Query("endY") endY: Double,
        @Query("startName") startName: String,
        @Query("endName") endName: String,
    ): Response<TMapRestInfo>

}