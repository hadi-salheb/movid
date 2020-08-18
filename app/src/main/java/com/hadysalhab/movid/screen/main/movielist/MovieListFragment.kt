package com.hadysalhab.movid.screen.main.movielist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hadysalhab.movid.common.DeviceConfigManager
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.usecases.list.FetchMovieListUseCase
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.BaseFragment
import com.hadysalhab.movid.screen.common.screensnavigator.MainNavigator
import javax.inject.Inject

private const val ARG_GROUP_KEY = "arg_group_key"

class MovieListFragment : BaseFragment(), MovieListView.Listener, FetchMovieListUseCase.Listener {
    lateinit var groupType: String

    @Inject
    lateinit var viewFactory: ViewFactory

    @Inject
    lateinit var activityContext: Context

    @Inject
    lateinit var mainNavigator: MainNavigator

    @Inject
    lateinit var deviceConfigManager: DeviceConfigManager

    @Inject
    lateinit var fetchMovieListUseCase: FetchMovieListUseCase

    private lateinit var view: MovieListView

    private enum class ScreenState {
        LOADING_SCREEN, ERROR_SCREEN, DATA_SCREEN
    }

    //on first time created -> loading screen is the default screen
    private var screenState = ScreenState.LOADING_SCREEN
    private var currentPage: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        arguments?.let {
            groupType = it.getString(ARG_GROUP_KEY)
                ?: throw RuntimeException("Cannot Start MovieListFragment without group type key")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::view.isInitialized) {
            view = viewFactory.getMovieListView(container)
        }
        // Inflate the layout for this fragment
        return view.getRootView()
    }

    override fun onStart() {
        super.onStart()
        view.registerListener(this)
        fetchMovieListUseCase.registerListener(this)
        fetchMovieListUseCase.fetchMovieListAndNotify(deviceConfigManager.getISO3166CountryCodeOrUS(),GroupType.POPULAR,currentPage)
    }

    override fun onStop() {
        super.onStop()
        view.unregisterListener(this)
        fetchMovieListUseCase.unregisterListener(this)
    }

    companion object {
        @JvmStatic
        fun newInstance(groupType: String) =
            MovieListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_GROUP_KEY, groupType)
                }
            }
    }

    override fun onMovieItemClicked(movieID: Int) {
        Log.d("MovieListFragment", "onMovieItemClicked: $movieID ")
    }

    override fun loadMoreItems() {
        fetchMovieListUseCase.fetchMovieListAndNotify(deviceConfigManager.getISO3166CountryCodeOrUS(),GroupType.POPULAR,currentPage+10)
    }

    override fun onFetchingMovieList(page: Int) {
        if (page == 1) {
            view.displayPaginationLoading()
        } else {
            view.displayPaginationLoading()
        }
    }

    override fun onFetchMovieListSuccess(moviesResponse: MoviesResponse) {
        currentPage = moviesResponse.page
        view.displayMovies(moviesResponse.movies!!)
    }

    override fun onFetchMovieListFailed(msg: String) {
        TODO("Not yet implemented")
    }

}