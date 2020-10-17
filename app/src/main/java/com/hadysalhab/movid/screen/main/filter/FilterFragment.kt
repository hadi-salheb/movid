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
import com.hadysalhab.movid.screen.common.toasthelper.ToastHelper
import com.hadysalhab.movid.screen.common.viewmodels.ViewModelFactory
import com.zhuinden.eventemitter.EventSource
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
    lateinit var toastHelper: ToastHelper

    @Inject
    lateinit var mainNavigator: MainNavigator

    private var subscription: EventSource.NotificationToken? = null

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
        subscription = filterViewModel.screenEvents.startListening { event ->
            handleEvents(event)
        }
    }

    private fun handleEvents(event: FilterScreenEvents) {
        when (event) {
            is FilterScreenEvents.PopFragment -> mainNavigator.popFragment()
            is FilterScreenEvents.ShowToast -> toastHelper.displayMessage(event.msg)
        }
    }

    private fun unregisterObservers() {
        filterView.unregisterListener(this)
        subscription?.stopListening()
        subscription = null
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
    }

    override fun onVoteAverageGteChanged(voteAverageGte: Float?) {
        filterViewModel.onVoteAverageGteChanged(voteAverageGte)
    }

    override fun onVoteAverageLteChanged(voteAverageLte: Float?) {
        filterViewModel.onVoteAverageLteChanged(voteAverageLte)
    }

    override fun onVoteCountGteChanged(voteCountGte: Int?) {
        filterViewModel.onVoteCountGteChanged(voteCountGte)
    }

    override fun onVoteCountLteChanged(voteCountLte: Int?) {
        filterViewModel.onVoteCountLteChanged(voteCountLte)
    }

    override fun onRuntimeGteChanged(withRuntimeGte: Int?) {
        filterViewModel.onRuntimeGteChanged(withRuntimeGte)
    }

    override fun onRuntimeLteChanged(withRuntimeLte: Int?) {
        filterViewModel.onRuntimeLteChanged(withRuntimeLte)
    }

    override fun onResetClick() {
        filterViewModel.onResetClick()
    }
}