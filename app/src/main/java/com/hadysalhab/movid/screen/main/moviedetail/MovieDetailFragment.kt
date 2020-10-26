package com.hadysalhab.movid.screen.main.moviedetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hadysalhab.movid.common.firebase.FirebaseAnalyticsClient
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.VideosResponse
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.controllers.BaseFragment
import com.hadysalhab.movid.screen.common.dialogs.DialogManager
import com.hadysalhab.movid.screen.common.intenthandler.IntentHandler
import com.hadysalhab.movid.screen.common.people.PeopleType
import com.hadysalhab.movid.screen.common.screensnavigator.MainNavigator
import com.hadysalhab.movid.screen.common.toasthelper.ToastHelper
import com.hadysalhab.movid.screen.common.viewmodels.ViewModelFactory
import com.zhuinden.eventemitter.EventSource
import javax.inject.Inject

private const val RATE_DIALOG = "RATE_DIALOG"
private const val MOVIE_ID = "MOVIE_ID"


class MovieDetailFragment : BaseFragment(),
    MovieDetailScreen.Listener {

    private var subscription: EventSource.NotificationToken? = null
    private var movieID: Int? = null

    @Inject
    lateinit var viewFactory: ViewFactory

    private lateinit var movieDetailScreen: MovieDetailScreen
    private lateinit var movieDetailViewModel: MovieDetailViewModel

    @Inject
    lateinit var myViewModelFactory: ViewModelFactory

    @Inject
    lateinit var mainNavigator: MainNavigator

    @Inject
    lateinit var toastHelper: ToastHelper

    @Inject
    lateinit var intentHandler: IntentHandler

    @Inject
    lateinit var firebaseAnalyticsClient: FirebaseAnalyticsClient

    @Inject
    lateinit var dialogManager: DialogManager

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
        if (!this::movieDetailScreen.isInitialized) {
            movieDetailScreen = viewFactory.getMovieDetailView(container)
        }
        return movieDetailScreen.getRootView()
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


    override fun onPeopleSeeAllClicked(movieID: Int, movieName: String, peopleType: PeopleType) {
        mainNavigator.toPeopleListFragment(movieID, movieName, peopleType)
    }

    override fun onSeeAllRecommendedSimilarMoviesClicked(
        groupType: GroupType,
        movieID: Int,
        movieName: String
    ) {
        mainNavigator.toRecommendedSimilarMoviesFragment(groupType, movieName, movieID)
    }


    override fun onMovieClicked(movieId: Int) {
        mainNavigator.toDetailFragment(movieId)
    }

    override fun onSeeReviewsClicked(movieID: Int, movieName: String) {
        mainNavigator.toReviewsFragment(movieID, movieName)
    }

    override fun onSeeTrailerClicked(videosResponse: VideosResponse) {
        intentHandler.handleTrailerIntent(videosResponse)
    }

    override fun onFavBtnClick() {
        firebaseAnalyticsClient.logFavorite()
        movieDetailViewModel.onAddRemoveFavoritesClicked()
    }

    override fun onWatchlistBtnClick() {
        firebaseAnalyticsClient.logWatchlist()
        movieDetailViewModel.onAddRemoveWatchlistClicked()
    }

    override fun onRefresh() {
        movieDetailViewModel.onRefresh()
    }

    override fun onRetry() {
        movieDetailViewModel.retry()
    }

    override fun onBackArrowClicked() {
        mainNavigator.popFragment()
    }

    override fun onPeopleCardClicked(peopleID: Int, peopleType: PeopleType) {
        firebaseAnalyticsClient.logCastClick()
    }

    override fun onRateBtnClick() {
        movieDetailViewModel.onRateBtnClick()
    }

    //----------------------------------------------------------------------------------------------

    private fun handleMovieDetailEvents(event: MovieDetailScreenEvents) {
        when (event) {
            is ShowUserToastMessage -> toastHelper.displayMessage(event.toastMessage)
            is ShowRateMovieDialog -> dialogManager.showRateDialog(
                RATE_DIALOG,
                event.movieName,
                event.currentRating,
                event.movieId
            )
        }
    }

    private val movieDetailScreenStateObserver =
        Observer<MovieDetailScreenState> { movieDetailScreenState ->
            movieDetailScreen.handleScreenState(movieDetailScreenState)
        }


    private fun registerObservers() {
        movieDetailScreen.registerListener(this)
        subscription = movieDetailViewModel.screenEvents.startListening { event ->
            handleMovieDetailEvents(event)
        }
        movieDetailViewModel.state.observeForever(movieDetailScreenStateObserver)

    }

    private fun unregisterObservers() {
        movieDetailViewModel.state.removeObserver(movieDetailScreenStateObserver)
        movieDetailScreen.unregisterListener(this)
        subscription?.stopListening()
        subscription = null
    }

}