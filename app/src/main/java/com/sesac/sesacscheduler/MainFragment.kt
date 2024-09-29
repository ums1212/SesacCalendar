package com.sesac.sesacscheduler

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.sesac.sesacscheduler.databinding.FragmentMainBinding

class MainFragment : BaseFragment<FragmentMainBinding>(FragmentMainBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findNavController().navigate(R.id.addSchedulerFragment)
    }

}