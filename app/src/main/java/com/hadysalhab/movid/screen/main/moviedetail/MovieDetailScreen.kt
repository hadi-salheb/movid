package com.hadysalhab.movid.screen.main.moviedetail

import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.MovieDetail
import com.hadysalhab.movid.movies.VideosResponse
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

abstract class MovieDetailScreen : BaseObservableViewMvc<MovieDetailScreen.Listener>() {
    interface Listener {
        fun onSeeAllClicked(groupType: GroupType)
        fun onCastClicked(castId: Int)
        fun onMovieClicked(movieId: Int)
        fun onSeeReviewsClicked(movieID: Int)
        fun onSeeTrailerClicked(videosResponse: VideosResponse)
        fun onFavBtnClick()
        fun onWatchlistBtnClick()
        fun onRefresh()
        fun onRetry()
    }

    abstract fun showLoadingIndicator()
    abstract fun hideLoadingIndicator()
    abstract fun showRefreshIndicator()
    abstract fun hideRefreshIndicator()
    abstract fun displayMovieDetail(movieDetail: MovieDetail)
    abstract fun hideMovieDetail()
    abstract fun showErrorScreen(error: String)
    abstract fun hideErrorScreen()
    abstract fun disablePullRefresh()
    abstract fun enablePullRefresh()
    abstract fun showTrailerIndicator()
    abstract fun hideTrailerIndicator()
}