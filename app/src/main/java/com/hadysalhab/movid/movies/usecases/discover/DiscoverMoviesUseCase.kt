package com.hadysalhab.movid.movies.usecases.discover

import com.hadysalhab.movid.common.usecases.ErrorMessageHandler
import com.hadysalhab.movid.common.utils.BaseBusyObservable
import com.hadysalhab.movid.movies.DiscoverMoviesFilterStateStore
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.SchemaToModelHelper
import com.hadysalhab.movid.networking.*
import com.hadysalhab.movid.networking.responses.MoviesResponseSchema
import com.hadysalhab.movid.screen.main.filter.FilterState
import com.techyourchance.threadposter.BackgroundThreadPoster
import com.techyourchance.threadposter.UiThreadPoster

class DiscoverMoviesUseCase(
    private val backgroundThreadPoster: BackgroundThreadPoster,
    private val errorMessageHandler: ErrorMessageHandler,
    private val schemaToModelHelper: SchemaToModelHelper,
    private val uiThreadPoster: UiThreadPoster,
    private val discoverMoviesFilterStateStore: DiscoverMoviesFilterStateStore,
    private val tmdbApi: TmdbApi
) : BaseBusyObservable<DiscoverMoviesUseCase.Listener>() {
    interface Listener {
        fun onFetchDiscoverMoviesSuccess(movies: MoviesResponse)
        fun onFetchDiscoverMoviesFailure(msg: String)
    }

    fun discoverMoviesUseCase(page: Int, genreIds: String) {
        assertNotBusyAndBecomeBusy()
        backgroundThreadPoster.post {
            when (val response =
                fireRequest(page, genreIds, discoverMoviesFilterStateStore.currentFilterState)) {
                is ApiSuccessResponse -> {
                    val moviesResponse =
                        schemaToModelHelper.getMoviesResponseFromSchema(
                            GroupType.FAVORITES,
                            response.body
                        )
                    notifySuccess(moviesResponse)
                }
                is ApiEmptyResponse, is ApiErrorResponse -> {
                    notifyFailure(errorMessageHandler.getErrorMessageFromApiResponse(response))
                }
            }
        }
    }

    private fun fireRequest(
        page: Int,
        genre: String,
        filterState: FilterState
    ): ApiResponse<MoviesResponseSchema> = try {
        with(filterState) {
            val res = tmdbApi.discover(
                page = page,
                sortBy = sortBy,
                includeAdult = includeAdult,
                primaryReleaseDateGte = if (primaryReleaseYearGte == null) null else "$primaryReleaseYearGte-01-01",
                primaryReleaseDateLte = if (primaryReleaseYearLte == null) null else "$primaryReleaseYearLte-12-31",
                voteCountGte = voteCountGte,
                voteCountLte = voteCountLte,
                voteAverageGte = voteAverageGte,
                voteAverageLte = voteAverageLte,
                withGenres = genre,
                withRuntimeGte = withRuntimeGte,
                withRuntimeLte = withRuntimeLte
            ).execute()
            ApiResponse.create<MoviesResponseSchema>(res)
        }
    } catch (err: Throwable) {
        ApiResponse.create(err)
    }

    private fun notifySuccess(movies: MoviesResponse) {
        uiThreadPoster.post {
            listeners.forEach { it.onFetchDiscoverMoviesSuccess(movies) }
            becomeNotBusy()
        }
    }

    private fun notifyFailure(err: String) {
        uiThreadPoster.post {
            listeners.forEach { it.onFetchDiscoverMoviesFailure(err) }
            becomeNotBusy()
        }
    }
}