package com.hadysalhab.movid.screen.main.featured

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.hadysalhab.movid.common.DeviceConfigManager
import com.hadysalhab.movid.movies.FetchMovieGroupsUseCase
import com.hadysalhab.movid.movies.MovieGroup
import com.hadysalhab.movid.movies.MovieGroupType
import com.hadysalhab.movid.movies.MoviesStateManager
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.BaseFragment
import com.hadysalhab.movid.screen.common.screensnavigator.MainNavigator
import javax.inject.Inject


class FeaturedFragment : BaseFragment(), FeaturedView.Listener, FetchMovieGroupsUseCase.Listener {
    private enum class ScreenState {
        LOADING_SCREEN, ERROR_SCREEN, DATA_SCREEN
    }

    //on first time created -> loading screen is the default screen
    private var screenState = ScreenState.LOADING_SCREEN

    companion object {
        @JvmStatic
        fun newInstance() = FeaturedFragment()
        const val SCREEN_STATE = "FEATURED_SCREEN_STATE"
    }

    @Inject
    lateinit var viewFactory: ViewFactory

    @Inject
    lateinit var activityContext: Context

    @Inject
    lateinit var moviesStateManager: MoviesStateManager

    @Inject
    lateinit var mainNavigator: MainNavigator

    @Inject
    lateinit var deviceConfigManager: DeviceConfigManager

    private lateinit var view: FeaturedView

    @Inject
    lateinit var fetchMovieGroupsUseCase: FetchMovieGroupsUseCase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        if (savedInstanceState != null) {
            screenState = savedInstanceState.getSerializable(SCREEN_STATE) as ScreenState
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = viewFactory.getFeaturedView(container)
        return view.getRootView()
    }

    override fun onStart() {
        super.onStart()
        view.registerListener(this)
        fetchMovieGroupsUseCase.registerListener(this)
        //screenState represents the last screen was displayed to the user
        //maybe the user navigated away (can trigger process death) or configuration change
        when (screenState) {
            //1) configuration change: isBusy can be false or true (UseCase is not destroyed)
            //2) if process death happens: isBusy = false, re-fetching is triggered
            //3) if none of the above: isBusy can be false or true (UseCase is not destroyed)
            ScreenState.LOADING_SCREEN -> {
                if (fetchMovieGroupsUseCase.isBusy) {
                    // fetching movies hasn't finished yet. Wait for it
                    // keep the loading screen showing
                } else {
                    // we missed it. re-fetch
                    fetchMovieGroupsUseCase.fetchMovieGroupsAndNotify(deviceConfigManager.getISO3166CountryCodeOrUS())
                }
            }
            //1) configuration change: MovieStore is not destroyed (AppScope) data should be available
            //2) process death: MovieStore data might not be available
            //3) none of the above: MovieStore should contain the data
            ScreenState.DATA_SCREEN -> {
                if (moviesStateManager.areMoviesAvailabe()) {
                    displayMovies()
                } else {
                    screenState = ScreenState.LOADING_SCREEN
                    view.displayLoadingScreen()
                    fetchMovieGroupsUseCase.fetchMovieGroupsAndNotify(deviceConfigManager.getISO3166CountryCodeOrUS())
                }
            }
            //1) configuration change: Dialog is recreated by the system
            //2) process death: Dialog is recreated by the system
            //3) none of the above: Dialog is visible
            ScreenState.ERROR_SCREEN -> {
                //wait for the user response
            }
        }
    }

    override fun onStop() {
        super.onStop()
        view.unregisterListener(this)
        fetchMovieGroupsUseCase.unregisterListener(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(SCREEN_STATE, screenState)
    }

    override fun onMovieCardClicked(movieID: Int) {
        mainNavigator.toDetailFragment(movieID)
    }

    override fun onSeeAllClicked(movieGroupType: MovieGroupType) {
        Toast.makeText(activityContext, "See All for $movieGroupType is clicked", Toast.LENGTH_LONG)
            .show()
    }

    override fun onFetchMovieGroupsSucceeded(movieGroups: List<MovieGroup>) {
        screenState = ScreenState.DATA_SCREEN
        displayMovies()
    }

    override fun onFetchMovieGroupsFailed(msg: String) {
        screenState = ScreenState.ERROR_SCREEN
        Toast.makeText(activityContext, msg, Toast.LENGTH_LONG)
            .show()
    }

    private fun displayMovies() {
        view.displayMovieGroups(moviesStateManager.moviesGroup.sortedBy { item->item.movieGroupType.ordinal }.filter { it.movies.isNotEmpty() })
    }
}