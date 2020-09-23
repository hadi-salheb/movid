package com.hadysalhab.movid.screen.main.discover

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.BaseFragment
import com.hadysalhab.movid.screen.common.screensnavigator.MainNavigator
import com.hadysalhab.movid.screen.common.viewmodels.ViewModelFactory
import com.hadysalhab.movid.screen.main.search.Genre
import javax.inject.Inject

private const val ARG_GENRE_ID = "genre_id"

class DiscoverFragment : BaseFragment(), DiscoverView.Listener {

    private lateinit var genre: Genre

    @Inject
    lateinit var viewFactory: ViewFactory

    @Inject
    lateinit var activityContext: Context

    @Inject
    lateinit var mainNavigator: MainNavigator
    private lateinit var discoverView: DiscoverView

    private lateinit var discoverViewModel: DiscoverViewModel

    @Inject
    lateinit var myViewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
        arguments?.let {
            genre =
                it.getParcelable(ARG_GENRE_ID) ?: throw RuntimeException("Genre Id cannot be null")
        }
        discoverViewModel =
            ViewModelProvider(this, myViewModelFactory).get(DiscoverViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::discoverView.isInitialized) {
            discoverView = viewFactory.getDiscoverView(container)
        }
        // Inflate the layout for this fragment
        return discoverView.getRootView()
    }

    override fun onStart() {
        super.onStart()
        discoverView.setGenreDetail(this.genre)
        discoverViewModel.onStart(this.genre.id.toString())
        discoverViewModel.viewState.observe(viewLifecycleOwner, Observer {
            render(it)
        })
        discoverView.registerListener(this)
    }

    override fun onStop() {
        super.onStop()
        discoverView.unregisterListener(this)
    }

    companion object {
        @JvmStatic
        fun newInstance(genre: Genre) =
            DiscoverFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_GENRE_ID, genre)
                }
            }
    }

    override fun onMovieItemClicked(movieID: Int) {
        mainNavigator.toDetailFragment(movieID)
    }

    override fun loadMoreItems() {
        discoverViewModel.loadMore()
    }

    private fun render(viewState: DiscoverMoviesViewState) {
        when (viewState) {
            Loading -> discoverView.displayLoadingIndicator()
            PaginationLoading -> discoverView.displayPaginationLoading()
            is Error -> {
            }
            is DiscoverMoviesLoaded -> {
                if (viewState.movies.isEmpty()) {
                    discoverView.displayEmptyListIndicator("No Movies Found")
                } else {
                    discoverView.displayMovies(viewState.movies)
                }
            }
        }
    }
}