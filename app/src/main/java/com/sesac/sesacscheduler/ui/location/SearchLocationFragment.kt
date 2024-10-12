package com.sesac.sesacscheduler.ui.location

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.sesac.sesacscheduler.databinding.FragmentSearchLocationBinding
import com.sesac.sesacscheduler.ui.common.BaseFragment

class SearchLocationFragment : BaseFragment<FragmentSearchLocationBinding>(FragmentSearchLocationBinding::inflate) {
    private var latitude : Double = 0.0
    private var longitude : Double = 0.0
    private val navController by lazy {
        findNavController()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        latitude =  37.4749
        longitude = 126.8911
//        binding.btnTest.setOnClickListener{
//            val action = SearchLocationFragmentDirections.actionSearchLocationFragmentToAddSchedulerFragment(latitude.toString(), longitude.toString())
//            navController.navigate(action)
//        }
    }

}

