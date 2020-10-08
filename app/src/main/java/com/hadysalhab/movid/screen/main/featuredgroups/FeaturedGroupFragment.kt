package com.hadysalhab.movid.screen.main.featuredgroups

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.BaseFragment
import com.hadysalhab.movid.screen.common.screensnavigator.MainNavigator
import com.hadysalhab.movid.screen.common.toasthelper.ToastHelper
import com.hadysalhab.movid.screen.common.viewmodels.ViewModelFactory
import com.zhuinden.eventemitter.EventSource
import javax.inject.Inject


class FeaturedGroupFragment : BaseFragment(), FeaturedGroupScreen.Listener {
    companion object {
        @JvmStatic
        fun newInstance() = FeaturedGroupFragment()
    }

    @Inject
    lateinit var viewFactory: ViewFactory

    @Inject
    lateinit var activityContext: Context

    @Inject
    lateinit var mainNavigator: MainNavigator

    @Inject
    lateinit var toastHelper: ToastHelper

    @Inject
    lateinit var myViewModelFactory: ViewModelFactory
    private lateinit var featuredGroupScreen: FeaturedGroupScreen
    private lateinit var featuredGroupViewModel: FeaturedGroupViewModel
    private var subscription: EventSource.NotificationToken? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
        featuredGroupViewModel =
            ViewModelProvider(this, myViewModelFactory).get(FeaturedGroupViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::featuredGroupScreen.isInitialized) {
            featuredGroupScreen = viewFactory.getFeaturedView(container)
        }
        return featuredGroupScreen.getRootView()
    }

    override fun onStart() {
        super.onStart()
        featuredGroupViewModel.onStart()
        registerObservers()
    }

    override fun onStop() {
        super.onStop()
        unregisterObservers()
    }

    override fun onMovieCardClicked(movieID: Int) {
        if (featuredGroupViewModel.state.value!!.isPowerMenuOpen) {
            return
        }
        mainNavigator.toDetailFragment(movieID)
    }

    override fun onSeeAllClicked(groupType: GroupType) {
        if (featuredGroupViewModel.state.value!!.isPowerMenuOpen) {
            return
        }
        mainNavigator.toFeaturedListFragment(
            groupType,
            null,
            featuredGroupViewModel.state.value!!.powerMenuItem.region
        )
    }

    override fun onCountryToolbarItemClicked(toolbarCountryItem: ToolbarCountryItems) {
        featuredGroupViewModel.onCountryToolbarItemClicked(toolbarCountryItem)
    }

    override fun onOverflowMenuIconClick() {
        featuredGroupViewModel.onOverflowMenuIconClick()
    }

    override fun onBackgroundClick() {
        featuredGroupViewModel.onBackgroundClick()
    }

    override fun onRetryClicked() {
        featuredGroupViewModel.onRetryClicked()
    }

    override fun onRefresh() {
        featuredGroupViewModel.onRefresh()
    }


    private fun handleFeaturedEvents(event: FeaturedScreenEvents) {
        when (event) {
            is ShowUserToastMessage -> toastHelper.displayMessage(event.toastMessage)
        }
    }

    private val featuredStateObserver = Observer<FeaturedScreenState> { featuredState ->
        featuredGroupScreen.handleScreenState(featuredState)
    }


    private fun registerObservers() {
        featuredGroupScreen.registerListener(this)
        subscription = featuredGroupViewModel.screenEvents.startListening { event ->
            handleFeaturedEvents(event)
        }
        featuredGroupViewModel.state.observe(this, featuredStateObserver)
    }

    private fun unregisterObservers() {
        featuredGroupViewModel.state.removeObserver(featuredStateObserver)
        featuredGroupScreen.unregisterListener(this)
        subscription?.stopListening()
        subscription = null
    }

}