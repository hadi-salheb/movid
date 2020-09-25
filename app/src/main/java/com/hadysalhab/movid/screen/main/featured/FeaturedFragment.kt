package com.hadysalhab.movid.screen.main.featured

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("FeaturedFragment", "onViewCreated!")
        featuredViewModel.data.observe(viewLifecycleOwner) { featuredMovies ->
            val sortedMovies = sortMoviesAndReturn(featuredMovies)
            featuredView.displayMovieGroups(sortedMovies)
        }
        featuredViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                featuredView.showLoadingIndicator()
            } else {
                featuredView.hideLoadingIndicator()
            }
        }
        featuredViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                featuredView.showErrorScreen(errorMessage)
            } else {
                featuredView.hideErrorScreen()
            }
        }
        featuredViewModel.isPowerMenuOpen.observe(viewLifecycleOwner) { isPowerMenuOpen ->
            Log.d("FeaturedFragment", "registerLiveDataObservers: $isPowerMenuOpen ")
            if (isPowerMenuOpen) featuredView.showPowerMenu() else featuredView.hidePowerMenu()
        }
        featuredViewModel.powerMenuItem.observe(viewLifecycleOwner) { powerMenuItem ->
            featuredView.setPowerMenuItem(powerMenuItem)
        }
    }

    override fun onStart() {
        super.onStart()
        featuredView.registerListener(this)
        featuredViewModel.onStart()
        subscription = featuredViewModel.events.startListening { event ->
            handleFeaturedEvents(event)
        }
    }

    override fun onStop() {
        super.onStop()
        featuredView.hidePowerMenu() //can cause memory leaks.!!!
        featuredView.unregisterListener(this)
        subscription?.stopListening()
        subscription = null
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


    private fun handleFeaturedEvents(event: FeaturedEvents) {
        when (event) {
            is ShowUserToastMessage -> toastHelper.displayMessage(event.toastMessage)
        }
    }

    private fun sortMoviesAndReturn(movieGroups: List<MoviesResponse>) =
        movieGroups.sortedBy { item -> item.tag.ordinal }
            .filter { !it.movies.isNullOrEmpty() }

}