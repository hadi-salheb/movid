package com.hadysalhab.movid.screen.main.moviedetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.BaseFragment
import com.hadysalhab.movid.screen.common.viewmodels.ViewModelFactory
import javax.inject.Inject

private const val MOVIE_ID = "MOVIE_ID"


class MovieDetailFragment : BaseFragment(),
    MovieDetailView.Listener {

    private var movieID: Int? = null

    @Inject
    lateinit var viewFactory: ViewFactory

    private lateinit var viewMvc: MovieDetailView
    private lateinit var movieDetailViewModel: MovieDetailViewModel

    @Inject
    lateinit var myViewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
        arguments?.let {
            movieID = it.getInt(MOVIE_ID)
            if (movieID == null) {
                throw RuntimeException("Cannot Start MovieDetailFragment without movie id")
            }
        }
        movieDetailViewModel =
            ViewModelProvider(this, myViewModelFactory).get(MovieDetailViewModel::class.java)
        if(savedInstanceState == null){
            movieDetailViewModel.getMovieDetail(movieID!!)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::viewMvc.isInitialized) {
            viewMvc = viewFactory.getMovieDetailView(container)
        }
        return viewMvc.getRootView()
    }

    override fun onStart() {
        super.onStart()
        viewMvc.registerListener(this)
        movieDetailViewModel.viewState.observe(viewLifecycleOwner, Observer {
            render(it)
        })
    }

    override fun onStop() {
        super.onStop()
        viewMvc.unregisterListener(this)
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

    private fun render(viewState: MovieDetailViewState) {
        when (viewState) {
            Loading -> viewMvc.displayLoadingScreen()
            is Error -> {
            }
            is DetailLoaded -> viewMvc.displayMovieDetail(viewState.movie)
        }
    }

}