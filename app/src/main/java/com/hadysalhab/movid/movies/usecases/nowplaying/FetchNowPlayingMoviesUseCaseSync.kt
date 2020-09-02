package com.hadysalhab.movid.movies.usecases.nowplaying

import com.hadysalhab.movid.common.DeviceConfigManager
import com.hadysalhab.movid.common.datavalidator.DataValidator
import com.hadysalhab.movid.common.time.TimeProvider
import com.hadysalhab.movid.common.usecases.ErrorMessageHandler
import com.hadysalhab.movid.common.usecases.UseCaseSyncResults
import com.hadysalhab.movid.movies.*
import com.hadysalhab.movid.networking.*
import com.hadysalhab.movid.networking.responses.MoviesResponseSchema

class FetchNowPlayingMoviesUseCaseSync(
    private val tmdbApi: TmdbApi,
    private val errorMessageHandler: ErrorMessageHandler,
    private val moviesStateManager: MoviesStateManager,
    private val schemaToModelHelper: SchemaToModelHelper,
    private val timeProvider: TimeProvider,
    private val dataValidator: DataValidator,
    private val deviceConfigManager: DeviceConfigManager
) {
    private val region = deviceConfigManager.getISO3166CountryCodeOrUS()
    private var page = 1
    fun fetchNowPlayingMoviesUseCaseSync(page: Int = 1): UseCaseSyncResults<MoviesResponse> {
        this.page = page
        return if (page == 1) {
            val nowPlayingMoviesInStore = getNowPlayingMoviesInStore()
            if (isValid(nowPlayingMoviesInStore)) {
                notifySuccess(nowPlayingMoviesInStore)
            } else {
                fetchNowPlayingMoviesAndReturn()
            }
        } else {
            fetchNowPlayingMoviesAndReturn()
        }
    }

    private fun fetchNowPlayingMoviesAndReturn(): UseCaseSyncResults<MoviesResponse> = try {
        val res = tmdbApi.fetchNowPlayingMovies(region = region, page = page).execute()
        handleResponse(ApiResponse.create(res))
    } catch (err: Throwable) {
        handleResponse(ApiResponse.create(err))
    }

    private fun getNowPlayingMoviesInStore(): MoviesResponse = moviesStateManager.getNowPlayingMovies()
    private fun isValid(moviesResponse: MoviesResponse) =
        dataValidator.isMoviesResponseValid(moviesResponse)

    private fun handleResponse(res: ApiResponse<MoviesResponseSchema>) = when (res) {
        is ApiSuccessResponse -> {
            val moviesResponse = convertSchemaToResponseAndReturn(res.body).also {
                if (page == 1) {
                    setTimeStamp(it)
                    updateMovieStore(it)
                }
            }
            notifySuccess(moviesResponse)
        }
        is ApiEmptyResponse -> {
            val error = createErrorResultsAndReturn(null)
            notifyError(error)
        }
        is ApiErrorResponse -> {
            val error = createErrorResultsAndReturn(res.errorMessage)
            notifyError(error)
        }
    }
    private fun notifySuccess(moviesResponse: MoviesResponse) = UseCaseSyncResults.Results(data = moviesResponse)
    private fun notifyError(msg: String) = UseCaseSyncResults.Error<MoviesResponse>(msg)

    private fun createErrorResultsAndReturn(msg: String?): String =
        errorMessageHandler.createErrorMessage(msg)

    private fun updateMovieStore(moviesResponse: MoviesResponse) {
        moviesStateManager.updateMoviesResponseByGroupType(moviesResponse)
    }

    private fun setTimeStamp(moviesResponse: MoviesResponse) {
        moviesResponse.timeStamp = timeProvider.currentTimestamp
    }

    private fun convertSchemaToResponseAndReturn(moviesResponseSchema: MoviesResponseSchema): MoviesResponse =
        schemaToModelHelper.getMoviesResponseFromSchema(GroupType.NOW_PLAYING, moviesResponseSchema)
}