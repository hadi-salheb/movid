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
    lateinit var viewMvc: SearchView

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
        if (!this::viewMvc.isInitialized) {
            viewMvc = viewFactory.getSearchView(container)
        }
        return viewMvc.getRootView()
    }

    override fun onStart() {
        super.onStart()
        viewMvc.registerListener(this)
        searchViewModel.viewState.observe(viewLifecycleOwner, Observer {
            render(it)
        })
    }

    override fun onStop() {
        super.onStop()
        viewMvc.unregisterListener(this)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SearchFragment()
    }

    override fun onSearchConfirmed(text: CharSequence) {
        if (text.isEmpty()) {
            return
        }
        searchViewModel.searchMovie(text)
    }

    override fun onSearchBackBtnClick() {
        searchViewModel.setGenresState()
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

    private fun render(viewState: SearchViewState) {
        when (viewState) {
            Loading -> viewMvc.displayLoadingIndicator()
            PaginationLoading -> viewMvc.displayPaginationLoading()
            is Error -> {
            }
            is SearchLoaded -> {
                if (viewState.movies.isEmpty()) {
                    viewMvc.displayEmptyListIndicator("No Results Found")
                } else {
                    viewMvc.displayMovies(viewState.movies)
                }
            }
            is Genres -> {
                viewMvc.renderGenres()
            }
        }
    }

}