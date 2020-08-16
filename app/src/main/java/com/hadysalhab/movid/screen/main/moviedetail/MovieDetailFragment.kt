package com.hadysalhab.movid.screen.main.moviedetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hadysalhab.movid.movies.FetchMovieDetailUseCase
import com.hadysalhab.movid.movies.MovieDetail
import com.hadysalhab.movid.movies.MoviesStateManager
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.BaseFragment
import com.hadysalhab.movid.user.UserStateManager
import javax.inject.Inject

private const val MOVIE_ID = "MOVIE_ID"


class MovieDetailFragment : BaseFragment(), FetchMovieDetailUseCase.Listener,
    MovieDetailView.Listener {

    private enum class ScreenState {
        LOADING_SCREEN, ERROR_SCREEN, DATA_SCREEN
    }

    private var movieID: Int? = null

    @Inject
    lateinit var viewFactory: ViewFactory

    @Inject
    lateinit var fetchMovieDetailUseCase: FetchMovieDetailUseCase

    @Inject
    lateinit var userStateManager: UserStateManager

    @Inject
    lateinit var moviesStateManager: MoviesStateManager

    private lateinit var viewMvc: MovieDetailView

    //on first time created -> loading screen is the default screen
    private var screenState = ScreenState.LOADING_SCREEN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        arguments?.let {
            movieID = it.getInt(MOVIE_ID)
        }
        if (savedInstanceState != null) {
            screenState = savedInstanceState.getSerializable(SCREEN_STATE) as ScreenState
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewMvc = viewFactory.getMovieDetailView(container)
        return viewMvc.getRootView()
    }

    override fun onStart() {
        super.onStart()
        viewMvc.registerListener(this)
        fetchMovieDetailUseCase.registerListener(this)
        when (screenState) {
            ScreenState.LOADING_SCREEN -> {
                if (fetchMovieDetailUseCase.isBusy) {
                    // fetching movies hasn't finished yet. Wait for it
                    // keep the loading screen showing
                } else {
                    // we missed it. re-fetch
                    if (moviesStateManager.movies.any { movie -> movie.details.id == movieID }) {
                        screenState = ScreenState.DATA_SCREEN
                        val movie = moviesStateManager.movies.find { it.details.id == movieID }
                        viewMvc.displayMovieDetail(movie!!)
                    } else {
                        fetchMovieDetailUseCase.fetchMovieDetailAndNotify(
                            movieID!!,
                            userStateManager.sessionId
                        )
                    }
                }
            }
            ScreenState.DATA_SCREEN -> {
                if (moviesStateManager.movies.any { movie -> movie.details.id == movieID }) {
                    screenState = ScreenState.DATA_SCREEN
                    val movie = moviesStateManager.movies.find { it.details.id == movieID }
                    viewMvc.displayMovieDetail(movie!!)
                } else {
                    screenState = ScreenState.LOADING_SCREEN
                    viewMvc.displayLoadingScreen()
                    fetchMovieDetailUseCase.fetchMovieDetailAndNotify(
                        movieID!!,
                        userStateManager.sessionId
                    )
                }
            }
            ScreenState.ERROR_SCREEN -> {
                //wait for the user response
            }
        }

    }

    override fun onStop() {
        super.onStop()
        viewMvc.unregisterListener(this)
        fetchMovieDetailUseCase.unregisterListener(this)
    }

    companion object {
        @JvmStatic
        fun newInstance(movieID: Int) =
            MovieDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(MOVIE_ID, movieID)
                }
            }

        private const val SCREEN_STATE = "MOVIE_DETAIL_SCREEN_STATE"
    }

    override fun onFetchMovieDetailSuccess(movieDetail: MovieDetail) {
        screenState = ScreenState.DATA_SCREEN
        viewMvc.apply {
            displayMovieDetail(movieDetail)
        }

    }

    override fun onFetchMovieDetailFailed(msg: String) {
        screenState = ScreenState.ERROR_SCREEN
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(SCREEN_STATE, screenState)
    }


}