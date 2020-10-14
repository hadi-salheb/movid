package com.hadysalhab.movid.screen.main.reviews

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.hadysalhab.movid.movies.Review
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.paginationerror.PaginationError

class ReviewListAdapter(
    private val listener: Listener,
    private val viewFactory: ViewFactory
) :
    ListAdapter<Review, ReviewListViewHolder>(DIFF_CALLBACK),
    PaginationError.Listener {
    interface Listener {
        fun onPaginationErrorClicked()
    }

    val LOADING = 0
    val REVIEW = 1
    val ERROR = 2

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Review>() {
            override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewListViewHolder =
        when (viewType) {
            REVIEW -> {
                val view = viewFactory.getReviewListItem(parent)
                ReviewListViewHolder.ReviewViewHolder(view)
            }
            LOADING -> {
                val view = viewFactory.getLoadingView(parent)
                ReviewListViewHolder.LoadingViewHolder(view)
            }
            ERROR -> {
                val view = viewFactory.getPaginationErrorView(parent)
                view.registerListener(this)
                ReviewListViewHolder.PaginationErrorViewHolder(view)
            }
            else -> {
                throw RuntimeException("Unsupported viewType $viewType")
            }
        }

    override fun onBindViewHolder(holder: ReviewListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position).author) {
        "LOADING" -> LOADING
        "ERROR" -> ERROR
        else -> REVIEW
    }

    override fun onPaginationErrorClick() {
        listener.onPaginationErrorClicked()
    }
}