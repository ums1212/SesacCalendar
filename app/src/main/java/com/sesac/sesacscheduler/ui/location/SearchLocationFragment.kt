package com.sesac.sesacscheduler.ui.location

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sesac.sesacscheduler.R
import com.sesac.sesacscheduler.common.toastShort
import com.sesac.sesacscheduler.databinding.FragmentSearchLocationBinding
import com.sesac.sesacscheduler.ui.common.BaseFragment
import com.sesac.sesacscheduler.ui.location.manager.RetrofitInstance
import com.sesac.sesacscheduler.ui.location.model.TmapResponse
import retrofit2.Call
import retrofit2.Response

class SearchLocationFragment : BaseFragment<FragmentSearchLocationBinding>(FragmentSearchLocationBinding::inflate) {
    private val apiKey = "l7xx7daab04e0de142cf800ce73e929f55e3"
    private val navController by lazy {
        findNavController()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding){
            recyclerViewPoi.layoutManager = LinearLayoutManager(requireContext())
            val searchAdapter = SearchAdapter { selectedPlace ->
                val place = selectedPlace.name
                val latitude = selectedPlace.noorLat
                val longitude = selectedPlace.noorLon
                toastShort("장소: $place, 경도: $longitude, 위도: $latitude")
                val bundle = bundleOf("latitude" to latitude, "longitude" to longitude, "place" to place)
                navController.navigate(R.id.action_searchLocationFragment_to_addSchedulerFragment,bundle)
            }
            recyclerViewPoi.adapter = searchAdapter

            searchBtn.setOnClickListener{
                val place = keywordET.text.toString()
                if(place.isNotEmpty()){
                    searchPlaces(place,searchAdapter)
                } else {
                    keywordET.requestFocus()
                    toastShort("검색어를 입력해주세요.")
                }
            }
        }
    }
    private fun searchPlaces(place: String,adapter: SearchAdapter) {
        val call = RetrofitInstance.api.searchPlaces(keyword = place, apiKey = apiKey)

        call.enqueue(object : retrofit2.Callback<TmapResponse> {
            override fun onResponse(call: Call<TmapResponse>, response: Response<TmapResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    adapter.submitList(response.body()!!.searchPoiInfo.pois.poi)
                }
            }
            override fun onFailure(call: Call<TmapResponse>, t: Throwable) {
                toastShort("장소 검색 실패: ${t.message}")
            }
        })
    }
}

