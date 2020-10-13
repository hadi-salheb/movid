package com.hadysalhab.movid.screen.main.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hadysalhab.movid.movies.DiscoverMoviesFilterStateStore
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.BaseFragment
import com.hadysalhab.movid.screen.common.screensnavigator.MainNavigator
import com.hadysalhab.movid.screen.common.viewmodels.ViewModelFactory
import javax.inject.Inject


class SearchFragment : BaseFragment(), SearchView.Listener {
    lateinit var searchView: SearchView

    @Inject
    lateinit var viewFactory: ViewFactory

    @Inject
    lateinit var myViewModelFactory: ViewModelFactory

    private lateinit var searchViewModel: SearchViewModel

    @Inject
    lateinit var mainNavigator: MainNavigator

    @Inject
    lateinit var discoverMoviesFilterStateStore: DiscoverMoviesFilterStateStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
        searchViewModel =
            ViewModelProvider(this, myViewModelFactory).get(SearchViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::searchView.isInitialized) {
            searchView = viewFactory.getSearchView(container)
        }
        return searchView.getRootView()
    }

    override fun onStart() {
        super.onStart()
        registerObservers()
    }

    override fun onStop() {
        super.onStop()
        unregisterObservers()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SearchFragment()
    }

    //Callbacks-------------------------------------------------------------------------------------
    override fun onSearchConfirmed(query: String) {
        searchViewModel.onSearchConfirmed(query)
    }

    override fun onSearchBackBtnClick() {
        searchViewModel.onSearchBackBtnClick()
    }

    override fun loadMoreItems() {
        searchViewModel.loadMoreItems()
    }

    override fun onMovieItemClicked(movieID: Int) {
        mainNavigator.toDetailFragment(movieID)
    }

    override fun onGenreListItemClick(genre: Genre) {
        discoverMoviesFilterStateStore.reset()
        mainNavigator.toDiscoverFragment(genre)
    }

    override fun onRetryClicked() {
        searchViewModel.onErrorRetryClicked()
    }

    override fun onPaginationErrorClicked() {
        searchViewModel.onPaginationErrorClicked()
    }
    //----------------------------------------------------------------------------------------------

    private val searchScreenStateObserver =
        Observer<SearchScreenState> { searchScreenState ->
            searchView.handleState(searchScreenState)
        }


    private fun registerObservers() {
        searchViewModel.state.observeForever(searchScreenStateObserver)
        searchView.registerListener(this)
    }

    private fun unregisterObservers() {
        searchViewModel.state.removeObserver(searchScreenStateObserver)
        searchView.unregisterListener(this)
    }


}