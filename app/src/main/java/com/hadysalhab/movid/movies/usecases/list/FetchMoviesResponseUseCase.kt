package com.hadysalhab.movid.movies.usecases.list

import com.hadysalhab.movid.common.usecases.UseCaseSyncResults
import com.hadysalhab.movid.common.utils.BaseBusyObservable
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.usecases.nowplaying.FetchNowPlayingMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.popular.FetchPopularMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.recommended.FetchRecommendedMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.similar.FetchSimilarMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.toprated.FetchTopRatedMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.upcoming.FetchUpcomingMoviesUseCaseSync
import com.techyourchance.threadposter.BackgroundThreadPoster
import com.techyourchance.threadposter.UiThreadPoster

class FetchMoviesResponseUseCase(
    private val fetchPopularMoviesUseCaseSync: FetchPopularMoviesUseCaseSync,
    private val fetchTopRatedMoviesUseCaseSync: FetchTopRatedMoviesUseCaseSync,
    private val fetchUpcomingMoviesUseCaseSync: FetchUpcomingMoviesUseCaseSync,
    private val fetchNowPlayingMoviesUseCaseSync: FetchNowPlayingMoviesUseCaseSync,
    private val fetchSimilarMoviesUseCaseSync: FetchSimilarMoviesUseCaseSync,
    private val fetchRecommendedMoviesUseCaseSync: FetchRecommendedMoviesUseCaseSync,
    private val backgroundThreadPoster: BackgroundThreadPoster,
    private val uiThreadPoster: UiThreadPoster
) :
    BaseBusyObservable<FetchMoviesResponseUseCase.Listener>() {
    interface Listener {
        fun onFetchMoviesResponseSuccess(movies: MoviesResponse)
        fun onFetchMoviesResponseFailure(msg: String)
        fun onFetchMoviesResponse()
    }

    private lateinit var errorMessage: String
    private var movieId: Int? = null
    private var page = 1
    private val LOCK = Object()
    private val computations: Map<GroupType, () -> Unit> = mapOf(
        GroupType.POPULAR to this::fetchPopularMovies,
        GroupType.TOP_RATED to this::fetchTopRatedMovies,
        GroupType.NOW_PLAYING to this::fetchNowPlayingMovies,
        GroupType.UPCOMING to this::fetchUpcomingMovies,
        GroupType.SIMILAR_MOVIES to this::fetchSimilarMovies,
        GroupType.RECOMMENDED_MOVIES to this::fetchRecommendedMovies
    )

    fun fetchMoviesResponseUseCase(groupType: GroupType, page: Int, movieId: Int?) {
        assertNotBusyAndBecomeBusy()
        listeners.forEach {
            it.onFetchMoviesResponse()
        }
        if ((groupType == GroupType.SIMILAR_MOVIES || groupType == GroupType.RECOMMENDED_MOVIES) && movieId == null) {
            throw RuntimeException("Cannot fetch similar movies or recommended movies with null movie id")
        }
        synchronized(LOCK) {
            errorMessage = ""
            this.movieId = movieId
            this.page = page
        }
        backgroundThreadPoster.post {
            val computation = computations[groupType]
                ?: throw java.lang.RuntimeException("GroupType $groupType is not supported in FetchMoviesResponseUseCase")
            computation.invoke()
        }

    }

    private fun fetchPopularMovies() {
        val result = fetchPopularMoviesUseCaseSync.fetchPopularMoviesUseCaseSync(page = page)
        handleResult(result)
    }

    private fun fetchTopRatedMovies() {
        val result = fetchTopRatedMoviesUseCaseSync.fetchTopRatedMoviesUseCaseSync(page = page)
        handleResult(result)
    }

    private fun fetchUpcomingMovies() {
        val result = fetchUpcomingMoviesUseCaseSync.fetchUpcomingMoviesUseCaseSync(page = page)
        handleResult(result)
    }

    private fun fetchNowPlayingMovies() {
        val result = fetchNowPlayingMoviesUseCaseSync.fetchNowPlayingMoviesUseCaseSync(page = page)
        handleResult(result)
    }

    private fun fetchSimilarMovies() {
        val result =
            fetchSimilarMoviesUseCaseSync.fetchSimilarMoviesUseCaseSync(
                page = page,
                movieId = movieId!!
            )
        handleResult(result)
    }

    private fun fetchRecommendedMovies() {
        val result = fetchRecommendedMoviesUseCaseSync.fetchRecommendedMoviesUseCaseSync(
            page = page,
            movieId = movieId!!
        )
        handleResult(result)
    }

    private fun handleResult(result: UseCaseSyncResults<MoviesResponse>) {
        when (result) {
            is UseCaseSyncResults.Results -> {
                notifySuccess(result.data)
            }
            is UseCaseSyncResults.Error -> {
                notifyFailure(result.errMessage)
            }
        }
    }

    // notify controller
    private fun notifyFailure(error: String) {
        uiThreadPoster.post {
            listeners.forEach {
                it.onFetchMoviesResponseFailure(error)
            }
        }
        becomeNotBusy()
    }

    // notify controller
    private fun notifySuccess(movies: MoviesResponse) {
        uiThreadPoster.post {
            listeners.forEach {
                it.onFetchMoviesResponseSuccess(movies)
            }
        }
        becomeNotBusy()
    }
}