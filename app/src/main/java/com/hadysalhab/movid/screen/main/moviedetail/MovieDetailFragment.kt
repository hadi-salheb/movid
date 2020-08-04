package com.hadysalhab.movid.screen.main.moviedetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hadysalhab.movid.movies.FetchMovieDetailUseCase
import com.hadysalhab.movid.movies.MovieDetail
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.BaseFragment
import com.hadysalhab.movid.user.UserStateManager
import javax.inject.Inject

private const val MOVIE_ID = "MOVIE_ID"


class MovieDetailFragment : BaseFragment(), FetchMovieDetailUseCase.Listener,
    MovieDetailView.Listener {
    private var movieID: Int? = null

    @Inject
    lateinit var viewFactory: ViewFactory

    @Inject
    lateinit var fetchMovieDetailUseCase: FetchMovieDetailUseCase

    @Inject
    lateinit var userStateManager: UserStateManager

    private lateinit var viewMvc: MovieDetailView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        arguments?.let {
            movieID = it.getInt(MOVIE_ID)
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
        fetchMovieDetailUseCase.fetchMovieDetailAndNotify(movieID!!, userStateManager.sessionId)
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
    }

    override fun onFetchMovieDetailSuccess(movieDetail: MovieDetail) {
        Log.d("MovieDetailFragment", "onFetchMovieDetailSuccess: $movieDetail ")
        viewMvc.apply {
            displayCarouselImages(movieDetail.images.backdrops)
            displayPosterImage(movieDetail.details.posterPath)
        }

    }

    override fun onFetchMovieDetailFailed(msg: String) {
        TODO("Not yet implemented")
    }


}