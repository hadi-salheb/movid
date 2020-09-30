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
        fun onFavBtnClick()
        fun onWatchlistBtnClick()
        fun onRefresh()
        fun onRetry()
    }

    abstract fun displayMovieDetail(movieDetail: MovieDetail)
    abstract fun hideTrailerButton()
    abstract fun showLoadingIndicator()
    abstract fun hideLoadingIndicator()
    abstract fun showErrorScreen(errorMessage: String)
    abstract fun hideErrorScreen()
    abstract fun showRefreshIndicator()
    abstract fun hideRefreshIndicator()
    abstract fun hideMovieDetail()
    abstract fun disablePullToRefresh()
    abstract fun enablePullToRefresh()
}