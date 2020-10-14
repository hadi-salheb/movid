package com.hadysalhab.movid.screen.main.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hadysalhab.movid.R
import com.hadysalhab.movid.screen.common.controllers.BaseFragment

class FilterFragment : BaseFragment() {
    companion object {
        @JvmStatic
        fun newInstance() =
            FilterFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.layout_filter, container, false)
    }
}