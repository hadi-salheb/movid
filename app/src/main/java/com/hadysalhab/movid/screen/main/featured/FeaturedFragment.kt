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
    private lateinit var view: FeaturedView
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
        if (!this::view.isInitialized) {
            view = viewFactory.getFeaturedView(container)
        }
        return view.getRootView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        featuredViewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            render(viewState)
        })
    }

    override fun onStart() {
        super.onStart()
        view.registerListener(this)
        featuredViewModel.onStart()
        subscription = featuredViewModel.events.startListening { event ->
            handleFeaturedEvents(event)
        }
    }

    override fun onStop() {
        super.onStop()
        view.unregisterListener(this)
        subscription?.stopListening()
        subscription = null
    }

    override fun onMovieCardClicked(movieID: Int) {
        if (featuredViewModel.isPowerMenuOpen()) {
            return
        }
        mainNavigator.toDetailFragment(movieID)
    }

    override fun onSeeAllClicked(groupType: GroupType) {
        if (featuredViewModel.isPowerMenuOpen()) {
            return
        }
        mainNavigator.toMovieListFragment(
            groupType,
            null,
            featuredViewModel.getCurrentPowerMenuItem().region
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

    private fun render(viewState: FeaturedViewState) {
        val sortedMovies = sortMoviesAndReturn(viewState.data)
        view.displayMovieGroups(sortedMovies)
        view.setPowerMenuItem(viewState.powerMenuItem)
        if (viewState.isPowerMenuOpen) view.showPowerMenu() else view.hidePowerMenu()
        if (viewState.isLoading) {
            view.showLoadingIndicator()
        } else {
            view.hideLoadingIndicator()
        }
        if (viewState.errorMessage != null) {
            view.showErrorScreen(viewState.errorMessage)
        } else {
            view.hideErrorScreen()
        }
    }

    private fun handleFeaturedEvents(event: FeaturedEvents) {
        when (event) {
            is ShowUserToastMessage -> toastHelper.displayMessage(event.toastMessage)
        }
    }

    private fun sortMoviesAndReturn(movieGroups: List<MoviesResponse>) =
        movieGroups.sortedBy { item -> item.tag.ordinal }
            .filter { !it.movies.isNullOrEmpty() }

}