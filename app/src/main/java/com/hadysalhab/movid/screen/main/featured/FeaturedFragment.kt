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


class FeaturedFragment : BaseFragment(), FeaturedView.Listener {
    companion object {
        @JvmStatic
        fun newInstance() = FeaturedFragment()
    }

    //---------------------------------------------------------------------------------------------
    private val featureMoviesObserver = Observer<List<MoviesResponse>> { featuredMovies ->
        val sortedMovies = sortMoviesAndReturn(featuredMovies)
        featuredView.displayMovieGroups(sortedMovies)
    }
    private val loadingObserver = Observer<Boolean> { isLoading ->
        if (isLoading) featuredView.showLoadingIndicator() else featuredView.hideLoadingIndicator()
    }

    private val errorObserver = Observer<String?> { errorMessage ->
        if (errorMessage != null) featuredView.showErrorScreen(errorMessage) else featuredView.hideErrorScreen()
    }
    private val powerMenuItemObserver = Observer<ToolbarCountryItems> { powerMenuItem ->
        featuredView.setPowerMenuItem(powerMenuItem)
    }
    private val isPowerMenuOpenObserver = Observer<Boolean> { isPowerMenuOpen ->
        if (isPowerMenuOpen) featuredView.showPowerMenu() else featuredView.hidePowerMenu()
    }
    private val isRefreshingObserver = Observer<Boolean> { isRefreshing ->
        if (isRefreshing) {
            featuredView.showRefreshIndicator()
        } else {
            featuredView.hideRefreshIndicator()
        }
    }

    //---------------------------------------------------------------------------------------------

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
    private lateinit var featuredView: FeaturedView
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
        if (!this::featuredView.isInitialized) {
            featuredView = viewFactory.getFeaturedView(container)
        }
        return featuredView.getRootView()
    }

    override fun onStart() {
        super.onStart()
        featuredViewModel.onStart()
        registerObservers()
    }

    override fun onStop() {
        super.onStop()
        featuredView.hidePowerMenu() //can cause memory leaks.!!!
        unregisterObservers()
    }

    override fun onMovieCardClicked(movieID: Int) {
        if (featuredViewModel.isPowerMenuOpen.value!!) {
            return
        }
        mainNavigator.toDetailFragment(movieID)
    }

    override fun onSeeAllClicked(groupType: GroupType) {
        if (featuredViewModel.isPowerMenuOpen.value!!) {
            return
        }
        mainNavigator.toMovieListFragment(
            groupType,
            null,
            featuredViewModel.powerMenuItem.value!!.region
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


    private fun handleFeaturedEvents(event: FeaturedEvents) {
        when (event) {
            is ShowUserToastMessage -> toastHelper.displayMessage(event.toastMessage)
        }
    }

    private fun registerObservers() {
        featuredView.registerListener(this)
        subscription = featuredViewModel.events.startListening { event ->
            handleFeaturedEvents(event)
        }
        featuredViewModel.data.observe(this, featureMoviesObserver)
        featuredViewModel.isLoading.observe(this, loadingObserver)
        featuredViewModel.errorMessage.observe(this, errorObserver)
        featuredViewModel.isPowerMenuOpen.observe(this, isPowerMenuOpenObserver)
        featuredViewModel.powerMenuItem.observe(this, powerMenuItemObserver)
        featuredViewModel.refresh.observe(this, isRefreshingObserver)
    }

    private fun unregisterObservers() {
        featuredViewModel.data.removeObserver(featureMoviesObserver)
        featuredViewModel.isLoading.removeObserver(loadingObserver)
        featuredViewModel.errorMessage.removeObserver(errorObserver)
        featuredViewModel.isPowerMenuOpen.removeObserver(isPowerMenuOpenObserver)
        featuredViewModel.powerMenuItem.removeObserver(powerMenuItemObserver)
        featuredViewModel.refresh.removeObserver(isRefreshingObserver)
        featuredView.unregisterListener(this)
        subscription?.stopListening()
        subscription = null
    }

    private fun sortMoviesAndReturn(movieGroups: List<MoviesResponse>) =
        movieGroups.sortedBy { item -> item.tag.ordinal }
            .filter { !it.movies.isNullOrEmpty() }

}