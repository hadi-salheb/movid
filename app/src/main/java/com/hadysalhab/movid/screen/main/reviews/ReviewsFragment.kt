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
import com.hadysalhab.movid.screen.common.viewmodels.ViewModelFactory
import javax.inject.Inject

private const val ARG_MOVIE_ID = "movie_id"

class ReviewsFragment : BaseFragment(), ReviewListView.Listener {
    private var movieID: Int? = null

    @Inject
    lateinit var viewFactory: ViewFactory

    @Inject
    lateinit var activityContext: Context

    private lateinit var view: ReviewListView

    private lateinit var reviewsViewModel: ReviewsViewModel

    @Inject
    lateinit var myViewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
        arguments?.let {
            movieID = it.getInt(ARG_MOVIE_ID)
        }
        if (movieID == null) {
            throw RuntimeException("Cannot retrieve reviews without movieID")
        }
        reviewsViewModel =
            ViewModelProvider(this, myViewModelFactory).get(ReviewsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::view.isInitialized) {
            view = viewFactory.getReviewsView(container)
        }
        // Inflate the layout for this fragment
        return view.getRootView()
    }

    override fun onStart() {
        super.onStart()
        view.registerListener(this)
        reviewsViewModel.init(movieID!!)
        reviewsViewModel.viewState.observe(viewLifecycleOwner, Observer {
            render(it)
        })
    }

    override fun onStop() {
        super.onStop()
        view.unregisterListener(this)
    }

    companion object {
        @JvmStatic
        fun newInstance(movieID: Int) =
            ReviewsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_MOVIE_ID, movieID)
                }
            }
    }

    private fun render(viewState: ReviewListViewState) {
        when (viewState) {
            Loading -> view.displayLoadingIndicator()
            PaginationLoading -> view.displayPaginationLoading()
            is Error -> {
            }
            is ReviewListLoaded -> view.displayReviews(viewState.reviews)
        }
    }

    override fun loadMoreItems() {
        reviewsViewModel.loadMore()
    }
}