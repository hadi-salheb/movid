package com.hadysalhab.movid.screen.main.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.BaseFragment
import javax.inject.Inject


class SearchFragment : BaseFragment() {
    lateinit var viewMvc: SearchView

    @Inject
    lateinit var viewFactory: ViewFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::viewMvc.isInitialized) {
            viewMvc = viewFactory.getSearchView(container)
        }
        return viewMvc.getRootView()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SearchFragment()
    }
}