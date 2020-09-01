package com.hadysalhab.movid.screen.main.moviedetail

import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.MovieDetail
import com.hadysalhab.movid.movies.VideosResponse
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

abstract class MovieDetailView : BaseObservableViewMvc<MovieDetailView.Listener>() {
    interface Listener {
        fun onSeeAllClicked(groupType: GroupType)
        fun onCastClicked(castId: Int)
        fun onMovieClicked(movieId: Int)
        fun onSeeReviewsClicked(movieID: Int)
        fun onSeeTrailerClicked(videosResponse: VideosResponse)
        fun onAddToFavoritesClick(movieId: Int)
    }

    abstract fun displayMovieDetail(movieDetail: MovieDetail)
    abstract fun displayLoadingScreen()
    abstract fun hideTrailerButton()
}