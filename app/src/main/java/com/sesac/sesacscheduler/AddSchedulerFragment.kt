package com.sesac.sesacscheduler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.sesac.sesacscheduler.databinding.FragmentAddSchedulerBinding

class AddSchedulerFragment : BaseFragment<FragmentAddSchedulerBinding>(FragmentAddSchedulerBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.iconPlace.setOnClickListener {
            findNavController().navigate(R.id.action_addSchedulerFragment_to_searchLocationFragment)
        }
    }

}