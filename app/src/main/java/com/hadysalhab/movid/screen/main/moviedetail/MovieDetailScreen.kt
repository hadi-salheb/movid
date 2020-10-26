package com.hadysalhab.movid.screen.main.moviedetail

import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.MovieDetail
import com.hadysalhab.movid.movies.VideosResponse
import com.hadysalhab.movid.screen.common.people.PeopleType
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

data class MovieDetailScreenState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val data: MovieDetail? = null,
    val error: String? = null
)

abstract class MovieDetailScreen : BaseObservableViewMvc<MovieDetailScreen.Listener>() {
    interface Listener {
        fun onPeopleSeeAllClicked(movieID: Int, movieName: String, peopleType: PeopleType)
        fun onSeeAllRecommendedSimilarMoviesClicked(
            groupType: GroupType,
            movieID: Int,
            movieName: String
        )

        fun onMovieClicked(movieId: Int)
        fun onSeeReviewsClicked(movieID: Int, movieName: String)
        fun onSeeTrailerClicked(videosResponse: VideosResponse)
        fun onFavBtnClick()
        fun onWatchlistBtnClick()
        fun onRefresh()
        fun onRetry()
        fun onBackArrowClicked()
        fun onPeopleCardClicked(peopleID: Int, peopleType: PeopleType)
        fun onRateBtnClick()
    }

    protected abstract fun showLoadingIndicator()
    protected abstract fun hideLoadingIndicator()
    protected abstract fun showRefreshIndicator()
    protected abstract fun hideRefreshIndicator()
    protected abstract fun displayMovieDetail(movieDetail: MovieDetail)
    protected abstract fun hideMovieDetail()
    protected abstract fun showErrorScreen(error: String)
    protected abstract fun hideErrorScreen()
    protected abstract fun disablePullRefresh()
    protected abstract fun enablePullRefresh()
    protected abstract fun showTrailerIndicator()
    protected abstract fun hideTrailerIndicator()
    abstract fun handleScreenState(movieDetailScreenState: MovieDetailScreenState)
}