package com.hadysalhab.movid.screen.main.movielist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.BaseFragment
import com.hadysalhab.movid.screen.common.screensnavigator.MainNavigator
import com.hadysalhab.movid.screen.common.viewmodels.ViewModelFactory
import javax.inject.Inject

private const val ARG_GROUP_KEY = "arg_group_key"
private const val ARG_MOVIE_ID = "arg_movie_id"

class MovieListFragment : BaseFragment(), MovieListView.Listener {
    lateinit var groupType: GroupType
    private var movieID: Int? = null

    @Inject
    lateinit var viewFactory: ViewFactory

    @Inject
    lateinit var activityContext: Context

    @Inject
    lateinit var mainNavigator: MainNavigator

    private lateinit var view: MovieListView

    private lateinit var movieListViewModel: MovieListViewModel

    @Inject
    lateinit var myViewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        injector.inject(this)

        arguments?.let {
            groupType = it.getParcelable(ARG_GROUP_KEY)
                ?: throw RuntimeException("Cannot Start MovieListFragment without group type key")
            movieID = it.getInt(ARG_MOVIE_ID)
        }
        if((groupType == GroupType.RECOMMENDED_MOVIES || groupType == GroupType.SIMILAR_MOVIES) && movieID == null){
            throw RuntimeException("Cannot get recommended movies or similar movies without providing a movie id!")
        }
        movieListViewModel =
            ViewModelProvider(this, myViewModelFactory).get(MovieListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::view.isInitialized) {
            view = viewFactory.getMovieListView(container)
        }
        // Inflate the layout for this fragment
        return view.getRootView()
    }

    override fun onStart() {
        super.onStart()
        view.registerListener(this)
        movieListViewModel.init(groupType,movieID)
        movieListViewModel.viewState.observe(viewLifecycleOwner, Observer {
            render(it)
        })
    }

    override fun onStop() {
        super.onStop()
        view.unregisterListener(this)
    }

    companion object {
        @JvmStatic
        fun newInstance(groupType: GroupType, movieID: Int?) =
            MovieListFragment().apply {
                arguments = Bundle().apply {
                    movieID?.let {
                        putInt(ARG_MOVIE_ID, movieID)
                    }
                    putParcelable(ARG_GROUP_KEY, groupType)
                }
            }
    }

    override fun onMovieItemClicked(movieID: Int) {

    }

    override fun loadMoreItems() {
        movieListViewModel.loadMore()
    }

    private fun render(viewState: MovieListViewState) {
        when (viewState) {
            Loading -> view.displayLoadingIndicator()
            PaginationLoading -> view.displayPaginationLoading()
            is Error -> {
            }
            is MovieListLoaded -> view.displayMovies(viewState.movies)
        }
    }


}