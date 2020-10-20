package com.hadysalhab.movid.screen.main.reviews

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
import javax.inject.Inject

private const val ARG_MOVIE_ID = "movie_id"
private const val ARG_MOVIE_NAME = "movie_name"

class ReviewsFragment : BaseFragment(), ReviewListView.Listener {

    companion object {
        @JvmStatic
        fun newInstance(movieID: Int, movieName: String) =
            ReviewsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_MOVIE_ID, movieID)
                    putString(ARG_MOVIE_NAME, movieName)
                }
            }
    }

    private var movieID: Int = 0
    private lateinit var movieName: String

    @Inject
    lateinit var viewFactory: ViewFactory

    @Inject
    lateinit var activityContext: Context

    private lateinit var reviewListView: ReviewListView

    private lateinit var reviewsViewModel: ReviewsViewModel

    @Inject
    lateinit var myViewModelFactory: ViewModelFactory

    @Inject
    lateinit var mainNavigator: MainNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
        arguments?.let {
            movieID = it.getInt(ARG_MOVIE_ID)
            movieName = it.getString(ARG_MOVIE_NAME)
                ?: throw java.lang.RuntimeException("Movie Title should not be empty in ReviewFragment")
        }
        reviewsViewModel =
            ViewModelProvider(this, myViewModelFactory).get(ReviewsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::reviewListView.isInitialized) {
            reviewListView = viewFactory.getReviewsView(container)
        }
        // Inflate the layout for this fragment
        return reviewListView.getRootView()
    }

    override fun onStart() {
        super.onStart()
        registerObservers()
        reviewsViewModel.onStart(movieID, movieName)

    }

    override fun onStop() {
        super.onStop()
        unregisterObservers()
    }

    //UserInteractions------------------------------------------------------------------------------
    override fun loadMoreItems() {
        reviewsViewModel.loadMore()
    }

    override fun onRetryClicked() {
        reviewsViewModel.onRetryClicked()
    }

    override fun onPaginationErrorClicked() {
        reviewsViewModel.onPaginationErrorClick()
    }

    override fun onBackArrowClicked() {
        mainNavigator.popFragment()
    }

    //----------------------------------------------------------------------------------------------
    private val reviewListStateObserver =
        Observer<ReviewListState> { reviewListState ->
            reviewListView.handleState(reviewListState)
        }

    private fun registerObservers() {
        reviewListView.registerListener(this)
        reviewsViewModel.state.observeForever(reviewListStateObserver)
    }

    private fun unregisterObservers() {
        reviewsViewModel.state.removeObserver(reviewListStateObserver)
        reviewListView.unregisterListener(this)
    }
}