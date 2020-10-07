package com.hadysalhab.movid.screen.main.featured

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.BaseFragment
import com.hadysalhab.movid.screen.common.screensnavigator.MainNavigator
import com.hadysalhab.movid.screen.common.toasthelper.ToastHelper
import com.hadysalhab.movid.screen.common.viewmodels.ViewModelFactory
import com.zhuinden.eventemitter.EventSource
import javax.inject.Inject


class FeaturedFragment : BaseFragment(), FeaturedScreen.Listener {
    companion object {
        @JvmStatic
        fun newInstance() = FeaturedFragment()
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
    private lateinit var featuredScreen: FeaturedScreen
    private lateinit var featuredViewModel: FeaturedViewModel
    private var subscription: EventSource.NotificationToken? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
        featuredViewModel =
            ViewModelProvider(this, myViewModelFactory).get(FeaturedViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::featuredScreen.isInitialized) {
            featuredScreen = viewFactory.getFeaturedView(container)
        }
        return featuredScreen.getRootView()
    }

    override fun onStart() {
        super.onStart()
        featuredViewModel.onStart()
        registerObservers()
    }

    override fun onStop() {
        super.onStop()
        unregisterObservers()
    }

    override fun onMovieCardClicked(movieID: Int) {
        if (featuredViewModel.state.value!!.isPowerMenuOpen) {
            return
        }
        mainNavigator.toDetailFragment(movieID)
    }

    override fun onSeeAllClicked(groupType: GroupType) {
        if (featuredViewModel.state.value!!.isPowerMenuOpen) {
            return
        }
        mainNavigator.toMovieListFragment(
            groupType,
            null,
            featuredViewModel.state.value!!.powerMenuItem.region
        )
    }

    override fun onCountryToolbarItemClicked(toolbarCountryItem: ToolbarCountryItems) {
        featuredViewModel.onCountryToolbarItemClicked(toolbarCountryItem)
    }

    override fun onOverflowMenuIconClick() {
        featuredViewModel.onOverflowMenuIconClick()
    }

    override fun onBackgroundClick() {
        featuredViewModel.onBackgroundClick()
    }

    override fun onRetryClicked() {
        featuredViewModel.onRetryClicked()
    }

    override fun onRefresh() {
        featuredViewModel.onRefresh()
    }


    private fun handleFeaturedEvents(event: FeaturedScreenEvents) {
        when (event) {
            is ShowUserToastMessage -> toastHelper.displayMessage(event.toastMessage)
        }
    }

    private val featuredStateObserver = Observer<FeaturedScreenState> { featuredState ->
        val sortedMovies = sortMoviesAndReturn(featuredState.data)
        featuredScreen.displayFeaturedMovies(sortedMovies)
        if (featuredState.isLoading) {
            featuredScreen.showLoadingIndicator()
        } else {
            featuredScreen.hideLoadingIndicator()
        }

        if (featuredState.errorMessage != null) {
            featuredScreen.showErrorScreen(featuredState.errorMessage)
        } else featuredScreen.hideErrorScreen()
        featuredScreen.setPowerMenuItem(featuredState.powerMenuItem)
        if (featuredState.isPowerMenuOpen) {
            featuredScreen.showPowerMenu()
        } else {
            featuredScreen.hidePowerMenu()
        }
        if (featuredState.isRefreshing) {
            featuredScreen.showRefreshIndicator()
        } else {
            featuredScreen.hideRefreshIndicator()
        }
        if (!featuredState.isRefreshing) {
            if (featuredState.isLoading || featuredState.errorMessage != null) {
                featuredScreen.disablePullRefresh()
            } else {
                featuredScreen.enablePullRefresh()
            }
        }
    }

    private fun sortMoviesAndReturn(movieGroups: List<MoviesResponse>) =
        movieGroups.sortedBy { item -> item.tag.ordinal }
            .filter { !it.movies.isNullOrEmpty() }

    private fun registerObservers() {
        featuredScreen.registerListener(this)
        subscription = featuredViewModel.screenEvents.startListening { event ->
            handleFeaturedEvents(event)
        }
        featuredViewModel.state.observe(this, featuredStateObserver)
    }

    private fun unregisterObservers() {
        featuredViewModel.state.removeObserver(featuredStateObserver)
        featuredScreen.unregisterListener(this)
        subscription?.stopListening()
        subscription = null
    }

}