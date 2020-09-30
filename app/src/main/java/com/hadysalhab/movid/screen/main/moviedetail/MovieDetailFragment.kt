package com.hadysalhab.movid.screen.main.moviedetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.MovieDetail
import com.hadysalhab.movid.movies.VideosResponse
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.BaseFragment
import com.hadysalhab.movid.screen.common.intenthandler.IntentHandler
import com.hadysalhab.movid.screen.common.screensnavigator.MainNavigator
import com.hadysalhab.movid.screen.common.toasthelper.ToastHelper
import com.hadysalhab.movid.screen.common.viewmodels.ViewModelFactory
import com.zhuinden.eventemitter.EventSource
import com.zhuinden.tupleskt.Tuple4
import javax.inject.Inject

private const val MOVIE_ID = "MOVIE_ID"


class MovieDetailFragment : BaseFragment(),
    MovieDetailView.Listener {

    //----------------------------------------------------------------------------------------------
    private val movieDetailObserver = Observer<MovieDetail> { movieDetail ->
        if (movieDetail != null) {
            movieDetailView.displayMovieDetail(movieDetail)
        } else {
            movieDetailView.hideMovieDetail() //hide will disable pull to refresh!
        }
    }
    private val loadingObserver = Observer<Boolean> { isLoading ->
        if (isLoading) {
            movieDetailView.showLoadingIndicator()
        } else {
            movieDetailView.hideLoadingIndicator()
        }
    }

    private val errorObserver = Observer<String?> { errorMessage ->
        if (errorMessage != null) {
            movieDetailView.showErrorScreen(errorMessage)
        } else {
            movieDetailView.hideErrorScreen()
        }
    }
    private val isRefreshingObserver = Observer<Boolean> { isRefreshing ->
        if (isRefreshing) {
            movieDetailView.showRefreshIndicator()
        } else {
            movieDetailView.hideRefreshIndicator()
        }
    }
    private val combinedStateObserver =
        Observer<Tuple4<MovieDetail?, Boolean?, String?, Boolean?>> { (movieDetail, loading, errorMessage, isRefreshing) ->
            if (!isRefreshing!!) {
                if (loading!! || errorMessage != null) {
                    movieDetailView.disablePullToRefresh()
                } else {
                    movieDetailView.enablePullToRefresh()
                }
            }

        }
    //---------------------------------------------------------------------------------------------

    private var subscription: EventSource.NotificationToken? = null
    private var movieID: Int? = null

    @Inject
    lateinit var viewFactory: ViewFactory

    private lateinit var movieDetailView: MovieDetailView
    private lateinit var movieDetailViewModel: MovieDetailViewModel

    @Inject
    lateinit var myViewModelFactory: ViewModelFactory

    @Inject
    lateinit var mainNavigator: MainNavigator

    @Inject
    lateinit var toastHelper: ToastHelper

    @Inject
    lateinit var intentHandler: IntentHandler

    companion object {
        @JvmStatic
        fun newInstance(movieID: Int) =
            MovieDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(MOVIE_ID, movieID)
                }
            }
    }

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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::movieDetailView.isInitialized) {
            movieDetailView = viewFactory.getMovieDetailView(container)
        }
        return movieDetailView.getRootView()
    }

    override fun onStart() {
        super.onStart()
        movieDetailViewModel.onStart(movieID!!)
        registerObservers()
    }

    override fun onStop() {
        super.onStop()
        unregisterObservers()
    }

    //UserActions-----------------------------------------------------------------------------------
    //SeeAll can be for cast or movies!!
    override fun onSeeAllClicked(groupType: GroupType) {
        if (groupType != GroupType.CAST) {
            mainNavigator.toMovieListFragment(groupType, movieID, null)
        }
    }

    override fun onCastClicked(castId: Int) {
        toastHelper.displayMessage("On Cast card clicked $castId")
    }

    override fun onMovieClicked(movieId: Int) {
        mainNavigator.toDetailFragment(movieId)
    }

    override fun onSeeReviewsClicked(movieID: Int) {
        mainNavigator.toReviewsFragment(movieID)
    }

    override fun onSeeTrailerClicked(videosResponse: VideosResponse) {
        intentHandler.handleTrailerIntent(videosResponse)
    }

    override fun onFavBtnClick() {
        movieDetailViewModel.onAddRemoveFavoritesClicked()
    }

    override fun onWatchlistBtnClick() {
        movieDetailViewModel.onAddRemoveWatchlistClicked()
    }

    override fun onRefresh() {
        movieDetailViewModel.onRefresh()
    }

    override fun onRetry() {
        movieDetailViewModel.retry()
    }

    //----------------------------------------------------------------------------------------------

    private fun handleFeaturedEvents(event: MovieDetailEvents) {
        when (event) {
            is ShowUserToastMessage -> toastHelper.displayMessage(event.toastMessage)
        }
    }

    private fun registerObservers() {
        movieDetailView.registerListener(this)
        subscription = movieDetailViewModel.events.startListening { event ->
            handleFeaturedEvents(event)
        }
        movieDetailViewModel.combinedState.observe(this, combinedStateObserver)
        movieDetailViewModel.data.observe(this, movieDetailObserver)
        movieDetailViewModel.isLoading.observe(this, loadingObserver)
        movieDetailViewModel.errorMessage.observe(this, errorObserver)
        movieDetailViewModel.refresh.observe(this, isRefreshingObserver)

    }

    private fun unregisterObservers() {
        movieDetailViewModel.combinedState.removeObserver(combinedStateObserver)
        movieDetailViewModel.data.removeObserver(movieDetailObserver)
        movieDetailViewModel.isLoading.removeObserver(loadingObserver)
        movieDetailViewModel.errorMessage.removeObserver(errorObserver)
        movieDetailViewModel.refresh.removeObserver(isRefreshingObserver)
        movieDetailView.unregisterListener(this)
        subscription?.stopListening()
        subscription = null
    }

}