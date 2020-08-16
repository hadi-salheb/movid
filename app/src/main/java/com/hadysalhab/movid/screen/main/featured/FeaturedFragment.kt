package com.hadysalhab.movid.screen.main.featured

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.hadysalhab.movid.common.DeviceConfigManager
import com.hadysalhab.movid.movies.FetchMovieGroupsUseCase
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.MovieGroup
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
        private const val SCREEN_STATE = "FEATURED_SCREEN_STATE"
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
        if (!this::view.isInitialized) {
            view = viewFactory.getFeaturedView(container)
        }
        return view.getRootView()
    }

    override fun onStart() {
        super.onStart()
        view.registerListener(this)
        fetchMovieGroupsUseCase.registerListener(this)
        //screenState represents the last screen was displayed to the user
        //maybe the user navigated away (can trigger process death) or configuration change
        when (screenState) {
            ScreenState.LOADING_SCREEN -> {
                if (fetchMovieGroupsUseCase.isBusy) {
                    // fetching movies hasn't finished yet. Wait for it
                    // keep the loading screen showing
                } else {
                    fetchMovieGroupsUseCase.fetchMovieGroupsAndNotify(deviceConfigManager.getISO3166CountryCodeOrUS())
                }
            }
            ScreenState.DATA_SCREEN -> {
                    fetchMovieGroupsUseCase.fetchMovieGroupsAndNotify(deviceConfigManager.getISO3166CountryCodeOrUS())
            }
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

    override fun onSeeAllClicked(groupType: GroupType) {
        Toast.makeText(activityContext, "See All for $groupType is clicked", Toast.LENGTH_LONG)
            .show()
        mainNavigator.toMovieListFragment(groupType.value)
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

    override fun onFetching() {
        screenState = ScreenState.LOADING_SCREEN
        view.displayLoadingScreen()
    }

    private fun displayMovies() {
        view.displayMovieGroups(moviesStateManager.moviesGroup.sortedBy { item -> item.groupType.ordinal }
            .filter { it.movies.isNotEmpty() })
    }
}