package com.sesac.sesacscheduler.ui.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sesac.sesacscheduler.R
import com.sesac.sesacscheduler.databinding.FragmentSearchLocationBinding
import com.sesac.sesacscheduler.ui.common.BaseFragment

class SearchLocationFragment : BaseFragment<FragmentSearchLocationBinding>(FragmentSearchLocationBinding::inflate) {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_location, container, false)
    }

}