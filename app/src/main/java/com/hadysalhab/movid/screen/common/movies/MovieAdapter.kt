package com.hadysalhab.movid.screen.common.movies

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.hadysalhab.movid.movies.Movie
import com.hadysalhab.movid.screen.common.ViewFactory
import com.hadysalhab.movid.screen.common.seeall.SeeAll

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
    }


    //template method
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder =
        when (viewType) {
            ViewType.SEE_ALL.viewType -> {
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
    override fun getItemCount(): Int = (currentList.size + 1)

    //template method
    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        if (position >= currentList.size) {
            return
        }
        holder.bind(getItem(position))
    }

    //template method
    override fun getItemViewType(position: Int): Int = when (position) {
        in (0..itemCount - 2) -> ViewType.CARD_MOVIE.viewType
        else -> ViewType.SEE_ALL.viewType
    }

    //template method
    override fun onMovieCardClicked(movieID: Int) {
        listener.onMovieCardClicked(movieID)
    }

    override fun onSeeAllClicked() {
        listener.onSeeAllClicked()
    }
}