package com.hadysalhab.movid.screen.main.reviews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hadysalhab.movid.R
import com.hadysalhab.movid.movies.Review
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.scrolllistener.OnVerticalScrollListener


class ReviewListViewImpl(
    layoutInflater: LayoutInflater,
    parent: ViewGroup?,
    viewFactory: ViewFactory
) : ReviewListView() {
    private val recyclerView: RecyclerView
    private val adapter: ReviewListAdapter
    private val progressBar: ProgressBar
//    private val paginationProgressBar: ProgressBar

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_list_data, parent, false))
        recyclerView = findViewById(R.id.rv_movies)
        adapter = ReviewListAdapter(viewFactory)
        progressBar = findViewById(R.id.loading_indicator)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = this@ReviewListViewImpl.adapter
        }
        recyclerView.addOnScrollListener(object : OnVerticalScrollListener() {
            override fun onScrolledUp() {

            }

            override fun onScrolledDown() {
            }

            override fun onScrolledToTop() {
            }

            override fun onScrolledToBottom() {
                listeners.forEach {
                    it.loadMoreItems()
                }
            }

        }
        )
    }

    override fun displayReviews(reviews: List<Review>) {
        adapter.submitList(reviews)
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE

    }

    override fun displayPaginationLoading() {
        progressBar.visibility = View.GONE
        recyclerView.suppressLayout(true)
    }

    override fun displayLoadingIndicator() {
        recyclerView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

}
