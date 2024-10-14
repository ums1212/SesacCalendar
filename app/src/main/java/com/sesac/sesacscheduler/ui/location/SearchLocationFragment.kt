package com.sesac.sesacscheduler.ui.location

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.sesac.sesacscheduler.R
import com.sesac.sesacscheduler.databinding.FragmentSearchLocationBinding
import com.sesac.sesacscheduler.ui.common.BaseFragment

class SearchLocationFragment : BaseFragment<FragmentSearchLocationBinding>(FragmentSearchLocationBinding::inflate) {
    private var latitude :String ="31.5"
    private var longitude : String ="31.5"
    private var place: String = "금천캠퍼스"
    private val navController by lazy {
        findNavController()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        latitude = "37.4749"
        longitude = "126.8911"
        place = "금천캠퍼스2"
        val bundle = bundleOf("latitude" to latitude, "longitude" to longitude, "place" to place)
        binding.btnTest.setOnClickListener{
            navController.navigate(R.id.action_searchLocationFragment_to_addSchedulerFragment,bundle)
        }
    }

}

