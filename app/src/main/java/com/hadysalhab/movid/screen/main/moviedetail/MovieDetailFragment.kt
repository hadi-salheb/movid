package com.hadysalhab.movid.screen.main.moviedetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hadysalhab.movid.movies.FetchMovieDetailUseCase
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.BaseFragment
import javax.inject.Inject

private const val MOVIE_ID = "MOVIE_ID"


class MovieDetailFragment : BaseFragment(), FetchMovieDetailUseCase.Listener,
    MovieDetailView.Listener {
    private var movieID: Int? = null

    @Inject
    lateinit var viewFactory: ViewFactory

    @Inject
    lateinit var fetchMovieDetailUseCase: FetchMovieDetailUseCase

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
        fetchMovieDetailUseCase.unregisterListener(this)
        fetchMovieDetailUseCase.fetchMovieDetailAndNotify()
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

    override fun onFetchMovieDetailSuccess() {
        TODO("Not yet implemented")
    }

    override fun onFetchMovieDetailFailed(msg: String) {
        TODO("Not yet implemented")
    }
}