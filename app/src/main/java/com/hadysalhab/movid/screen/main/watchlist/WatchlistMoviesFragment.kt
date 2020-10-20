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
import com.hadysalhab.movid.screen.common.listtitletoolbar.ListWithToolbarTitle
import com.hadysalhab.movid.screen.common.listtitletoolbar.ListWithToolbarTitleState
import com.hadysalhab.movid.screen.common.movielist.MovieListScreen
import com.hadysalhab.movid.screen.common.screensnavigator.MainNavigator
import com.hadysalhab.movid.screen.common.viewmodels.ViewModelFactory
import javax.inject.Inject

class WatchlistMoviesFragment : BaseFragment(), MovieListScreen.Listener,
    ListWithToolbarTitle.Listener {
    @Inject
    lateinit var viewFactory: ViewFactory

    @Inject
    lateinit var activityContext: Context

    @Inject
    lateinit var mainNavigator: MainNavigator
    private lateinit var listWithToolbarTitle: ListWithToolbarTitle

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
        if (!this::listWithToolbarTitle.isInitialized) {
            listWithToolbarTitle = viewFactory.getListWithToolbarTitle(container)
        }
        // Inflate the layout for this fragment
        return listWithToolbarTitle.getRootView()
    }

    override fun onStart() {
        super.onStart()
        registerObservers()
        watchlistMoviesViewModel.onStart()

    }

    override fun onStop() {
        super.onStop()
        unregisterObservers()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            WatchlistMoviesFragment()
    }

    //UserInteractions-----------------------------------------------------------------------------

    override fun onMovieItemClicked(movieID: Int) {
        mainNavigator.toDetailFragment(movieID)
    }

    override fun loadMoreItems() {
        watchlistMoviesViewModel.loadMore()
    }

    override fun onRetryClicked() {
        watchlistMoviesViewModel.onRetry()
    }

    override fun onPaginationErrorClicked() {
        watchlistMoviesViewModel.loadMore()
    }

    override fun onMenuIconClicked() {

    }

    override fun onBackArrowClick() {

    }

    //----------------------------------------------------------------------------------------------

    private val listWithToolbarTitleStateObserver =
        Observer<ListWithToolbarTitleState> { listWithToolbarTitleState ->
            listWithToolbarTitle.handleScreenState(listWithToolbarTitleState)
        }


    private fun registerObservers() {
        watchlistMoviesViewModel.state.observeForever(listWithToolbarTitleStateObserver)
        listWithToolbarTitle.registerListener(this)
    }

    private fun unregisterObservers() {
        watchlistMoviesViewModel.state.removeObserver(listWithToolbarTitleStateObserver)
        listWithToolbarTitle.unregisterListener(this)
    }
}