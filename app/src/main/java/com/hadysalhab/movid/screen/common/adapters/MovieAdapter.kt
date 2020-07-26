package com.hadysalhab.movid.screen.common.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.components.moviecard.MovieCard
import com.hadysalhab.movid.screen.common.components.seeall.SeeAll

class MovieAdapter(
    private val listener: Listener,
    private val viewFactory: ViewFactory
) :
    ListAdapter<Movie, MovieViewHolder>(DIFF_CALLBACK), MovieCard.Listener, SeeAll.Listener {

    interface Listener {
        fun onMovieCardClicked(movieID: Int)
        fun onSeeAllClicked()
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Movie>() {

            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem == newItem
            }

        }
        private const val ITEM_COUNT = 6
    }


    //template method
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder =
        when (viewType) {
            ViewType.SEE_ALL.ordinal -> {
                val seeAll = viewFactory.getSeeAll(parent)
                seeAll.registerListener(this)
                MovieViewHolder.createSeeAll(seeAll)
            }
            else -> {
                val movieCard = viewFactory.getMovieCard(parent)
                movieCard.registerListener(this)
                MovieViewHolder.createMovieCard(movieCard)
            }
        }


    //template method
    override fun getItemCount(): Int = ITEM_COUNT

    //template method
    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    //template method
    override fun getItemViewType(position: Int): Int = when (position) {
        in (0 until ITEM_COUNT) -> ViewType.CARD_MOVIE.ordinal
        else -> ViewType.SEE_ALL.ordinal
    }

    //template method
    override fun onMovieCardClicked(movieID: Int) {
        listener.onMovieCardClicked(movieID)
    }

    override fun onSeeAllClicked() {
        listener.onSeeAllClicked()
    }
}