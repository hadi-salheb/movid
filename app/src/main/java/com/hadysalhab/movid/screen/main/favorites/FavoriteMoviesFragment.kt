package com.hadysalhab.movid.screen.main.favorites

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.BaseFragment
import com.hadysalhab.movid.screen.common.movielist.MovieListScreen
import com.hadysalhab.movid.screen.common.screensnavigator.MainNavigator
import com.hadysalhab.movid.screen.common.viewmodels.ViewModelFactory
import javax.inject.Inject

class FavoriteMoviesFragment : BaseFragment(), MovieListScreen.Listener {
    @Inject
    lateinit var viewFactory: ViewFactory

    @Inject
    lateinit var activityContext: Context

    @Inject
    lateinit var mainNavigator: MainNavigator
    private lateinit var screen: MovieListScreen

    private lateinit var favoriteMoviesViewModel: FavoriteMoviesViewModel

    @Inject
    lateinit var myViewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        injector.inject(this)

        favoriteMoviesViewModel =
            ViewModelProvider(this, myViewModelFactory).get(FavoriteMoviesViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::screen.isInitialized) {
            screen = viewFactory.getMovieScreen(container)
        }
        // Inflate the layout for this fragment
        return screen.getRootView()
    }

    override fun onStart() {
        super.onStart()
        screen.registerListener(this)
        favoriteMoviesViewModel.onStart()
        favoriteMoviesViewModel.viewState.observe(viewLifecycleOwner, Observer {
            render(it)
        })
    }

    override fun onStop() {
        super.onStop()
        screen.unregisterListener(this)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            FavoriteMoviesFragment()
    }

    override fun onMovieItemClicked(movieID: Int) {
        mainNavigator.toDetailFragment(movieID)
    }

    override fun loadMoreItems() {
        favoriteMoviesViewModel.loadMore()
    }

    private fun render(viewState: FavoriteMoviesViewState) {
        when (viewState) {
            Loading -> screen.showLoadingIndicator()
            PaginationLoading -> screen.showPaginationIndicator()
            is Error -> {
            }
            is FavoriteMoviesLoaded -> {
                if (viewState.movies.isEmpty()) {
                    screen.displayEmptyListIndicator("No Favorite Movies")
                } else {
                    screen.displayMovies(viewState.movies)
                }
            }
        }
    }
}