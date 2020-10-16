package com.hadysalhab.movid.screen.main.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.BaseFragment
import com.hadysalhab.movid.screen.common.screensnavigator.MainNavigator
import com.hadysalhab.movid.screen.common.viewmodels.ViewModelFactory
import javax.inject.Inject

class FilterFragment : BaseFragment(), FilterView.Listener {
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

    @Inject
    lateinit var mainNavigator: MainNavigator

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

    override fun onStart() {
        super.onStart()
        registerObservers()
    }

    override fun onStop() {
        super.onStop()
        unregisterObservers()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        filterViewModel.onSavedInstanceState()
    }

    private val filterScreenStateObserver = Observer<FilterState> {
        filterView.handleState(it)
    }

    private fun registerObservers() {
        filterView.registerListener(this)
        filterViewModel.currentScreenState.observeForever(filterScreenStateObserver)
    }

    private fun unregisterObservers() {
        filterView.unregisterListener(this)
        filterViewModel.currentScreenState.removeObserver(filterScreenStateObserver)
    }

    //User Interactions-----------------------------------------------------------------------------

    override fun onSortByChanged(sortBy: String) {
        filterViewModel.onSortByChanged(sortBy)
    }

    override fun onIncludeAdultChanged(includeAdult: Boolean) {
        filterViewModel.onIncludeAdultChanged(includeAdult)
    }

    override fun onPrimaryReleaseYearGteChanged(primaryReleaseYearGte: String?) {
        filterViewModel.onPrimaryReleaseYearGteChanged(primaryReleaseYearGte)
    }

    override fun onPrimaryReleaseYearLteChanged(primaryReleaseYearLte: String?) {
        filterViewModel.onPrimaryReleaseYearLteChanged(primaryReleaseYearLte)
    }

    override fun onFilterSubmit() {
        filterViewModel.onFilterSubmit()
        mainNavigator.popFragment()
    }

    override fun onVoteAverageGteChanged(voteAverageGte: Float?) {
        filterViewModel.onVoteAverageGteChanged(voteAverageGte)
    }

    override fun onVoteAverageLteChanged(voteAverageLte: Float?) {
        filterViewModel.onVoteAverageLteChanged(voteAverageLte)
    }
}