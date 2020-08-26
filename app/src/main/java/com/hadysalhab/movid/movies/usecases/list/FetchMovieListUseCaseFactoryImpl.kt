package com.hadysalhab.movid.movies.usecases.list

import com.hadysalhab.movid.common.datavalidator.DataValidator
import com.hadysalhab.movid.common.time.TimeProvider
import com.hadysalhab.movid.movies.ErrorMessageHandler
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.MoviesStateManager
import com.hadysalhab.movid.movies.SchemaToModelHelper
import com.hadysalhab.movid.movies.usecases.nowplaying.FetchMovieNowPlayingMoviesUseCase
import com.hadysalhab.movid.movies.usecases.nowplaying.FetchNowPlayingMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.popular.FetchMoviePopularMoviesUseCase
import com.hadysalhab.movid.movies.usecases.popular.FetchPopularMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.recommended.FetchMovieRecommendedMoviesUseCase
import com.hadysalhab.movid.movies.usecases.recommended.FetchRecommendedMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.similar.FetchMovieSimilarMoviesUseCase
import com.hadysalhab.movid.movies.usecases.similar.FetchSimilarMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.toprated.FetchMovieTopRatedMoviesUseCase
import com.hadysalhab.movid.movies.usecases.toprated.FetchTopRatedMoviesUseCaseSync
import com.hadysalhab.movid.movies.usecases.upcoming.FetchMovieUpComingMoviesUseCase
import com.hadysalhab.movid.movies.usecases.upcoming.FetchUpcomingMoviesUseCaseSync
import com.techyourchance.threadposter.BackgroundThreadPoster
import com.techyourchance.threadposter.UiThreadPoster

class FetchMovieListUseCaseFactoryImpl(
    private val fetchPopularMoviesUseCaseSync: FetchPopularMoviesUseCaseSync,
    private val fetchTopRatedMoviesUseCaseSync: FetchTopRatedMoviesUseCaseSync,
    private val fetchUpcomingMoviesUseCaseSync: FetchUpcomingMoviesUseCaseSync,
    private val fetchNowPlayingMoviesUseCaseSync: FetchNowPlayingMoviesUseCaseSync,
    private val fetchSimilarMoviesUseCaseSync: FetchSimilarMoviesUseCaseSync,
    private val fetchRecommendedMoviesUseCaseSync: FetchRecommendedMoviesUseCaseSync,
    private val backgroundThreadPoster: BackgroundThreadPoster,
    private val uiThreadPoster: UiThreadPoster,
    private val schemaToModelHelper: SchemaToModelHelper,
    private val timeProvider: TimeProvider,
    private val errorMessageHandler: ErrorMessageHandler,
    private val dataValidator: DataValidator,
    private val moviesStateManager: MoviesStateManager
) : FetchMovieListUseCaseFactory {
    override fun makeFetchListUseCase(groupType: GroupType): FetchMovieListUseCase = when (groupType) {
        GroupType.POPULAR -> FetchMoviePopularMoviesUseCase(
            fetchPopularMoviesUseCaseSync,
            backgroundThreadPoster,
            uiThreadPoster,
            schemaToModelHelper,
            timeProvider,
            errorMessageHandler,
            dataValidator,
            moviesStateManager
        )
        GroupType.NOW_PLAYING -> FetchMovieNowPlayingMoviesUseCase(
            fetchNowPlayingMoviesUseCaseSync,
            backgroundThreadPoster,
            uiThreadPoster,
            schemaToModelHelper,
            timeProvider,
            errorMessageHandler,
            dataValidator,
            moviesStateManager
        )
        GroupType.UPCOMING -> FetchMovieUpComingMoviesUseCase(
            fetchUpcomingMoviesUseCaseSync,
            backgroundThreadPoster,
            uiThreadPoster,
            schemaToModelHelper,
            timeProvider,
            errorMessageHandler,
            dataValidator,
            moviesStateManager
        )
        GroupType.TOP_RATED -> FetchMovieTopRatedMoviesUseCase(
            fetchTopRatedMoviesUseCaseSync,
            backgroundThreadPoster,
            uiThreadPoster,
            schemaToModelHelper,
            timeProvider,
            errorMessageHandler,
            dataValidator,
            moviesStateManager
        )
        GroupType.SIMILAR_MOVIES -> FetchMovieSimilarMoviesUseCase(
            fetchSimilarMoviesUseCaseSync,
            backgroundThreadPoster,
            uiThreadPoster,
            schemaToModelHelper,
            timeProvider,
            errorMessageHandler,
            dataValidator,
            moviesStateManager
        )
        GroupType.RECOMMENDED_MOVIES
        -> FetchMovieRecommendedMoviesUseCase(
            fetchRecommendedMoviesUseCaseSync,
            backgroundThreadPoster,
            uiThreadPoster,
            schemaToModelHelper,
            timeProvider,
            errorMessageHandler,
            dataValidator,
            moviesStateManager
        )
        else -> throw RuntimeException("ListUseCase for $groupType is not supported")
    }
}
