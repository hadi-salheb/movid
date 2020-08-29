package com.hadysalhab.movid.screen.main.movielist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hadysalhab.movid.R
import com.hadysalhab.movid.common.utils.convertDpToPixel
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.scrolllistener.OnVerticalScrollListener


class MovieListViewImpl(
    layoutInflater: LayoutInflater,
    parent: ViewGroup?,
    viewFactory: ViewFactory
) : MovieListView(), MovieListAdapter.Listener {
    private val recyclerView: RecyclerView
    private val adapter: MovieListAdapter
    private val progressBar: ProgressBar
    private val paginationProgressBar: ProgressBar

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_list_data, parent, false))
        recyclerView = findViewById(R.id.rv_movies)
        adapter = MovieListAdapter(this, viewFactory)
        progressBar = findViewById(R.id.loading_indicator)
        paginationProgressBar = findViewById(R.id.pagination_loading_indicator)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = this@MovieListViewImpl.adapter
        }
        recyclerView.apply {
            val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            itemDecoration.setDrawable(ContextCompat.getDrawable(context, R.drawable.divider)!!)
            addItemDecoration(itemDecoration)
            addOnScrollListener(object : OnVerticalScrollListener() {
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
            })
            setPadding(0, 0, 0, 0)
        }
    }

    override fun displayMovies(movies: List<Movie>) {
        adapter.submitList(movies)

        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        if (paginationProgressBar.visibility == View.VISIBLE) {
            android.os.Handler().postDelayed({
                paginationProgressBar.visibility = View.GONE
                recyclerView.suppressLayout(false)
                recyclerView.post {
                    recyclerView.smoothScrollBy(0, convertDpToPixel(16, getContext()))
                }
            }, 300)
        }
    }

    override fun displayPaginationLoading() {
        paginationProgressBar.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        recyclerView.suppressLayout(true)
    }

    override fun displayLoadingIndicator() {
        paginationProgressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    override fun onMovieItemClicked(movieID: Int) {
        listeners.forEach {
            it.onMovieItemClicked(movieID)
        }
    }
}
