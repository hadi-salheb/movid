package com.hadysalhab.movid.screen.main.watchlist

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

class WatchlistMoviesFragment : BaseFragment(), MovieListScreen.Listener {
    @Inject
    lateinit var viewFactory: ViewFactory

    @Inject
    lateinit var activityContext: Context

    @Inject
    lateinit var mainNavigator: MainNavigator
    private lateinit var screen: MovieListScreen

    private lateinit var watchlistMoviesViewModel: WatchlistMoviesViewModel

    @Inject
    lateinit var myViewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        injector.inject(this)
        watchlistMoviesViewModel =
            ViewModelProvider(this, myViewModelFactory).get(WatchlistMoviesViewModel::class.java)
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
        watchlistMoviesViewModel.onStart()
        watchlistMoviesViewModel.viewState.observe(viewLifecycleOwner, Observer {
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
            WatchlistMoviesFragment()
    }

    override fun onMovieItemClicked(movieID: Int) {
        mainNavigator.toDetailFragment(movieID)
    }

    override fun loadMoreItems() {
        watchlistMoviesViewModel.loadMore()
    }

    private fun render(viewState: WishlistMoviesViewState) {
        when (viewState) {
            Loading -> screen.showLoadingIndicator()
            PaginationLoading -> screen.showPaginationIndicator()
            is Error -> {
            }
            is WatchlistMoviesLoaded -> {
                if (viewState.movies.isEmpty()) {
                    screen.displayEmptyListIndicator("No Watchlist Movies")
                } else {
                    screen.displayMovies(viewState.movies)
                }
            }
        }
    }
}