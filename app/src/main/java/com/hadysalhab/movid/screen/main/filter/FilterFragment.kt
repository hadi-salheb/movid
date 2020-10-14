package com.hadysalhab.movid.screen.main.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.BaseFragment
import com.hadysalhab.movid.screen.common.viewmodels.ViewModelFactory
import javax.inject.Inject

class FilterFragment : BaseFragment() {
    companion object {
        @JvmStatic
        fun newInstance() =
            FilterFragment()
    }

    @Inject
    lateinit var myViewModelFactory: ViewModelFactory

    @Inject
    lateinit var viewFactory: ViewFactory

    private lateinit var filterView: FilterView

    private lateinit var filterViewModel: FilterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
        filterViewModel =
            ViewModelProvider(this, myViewModelFactory).get(FilterViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::filterView.isInitialized) {
            filterView = viewFactory.getFilterView(container)
        }
        // Inflate the layout for this fragment
        return filterView.getRootView()
    }
}