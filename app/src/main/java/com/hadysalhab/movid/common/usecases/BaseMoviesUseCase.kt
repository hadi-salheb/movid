package com.hadysalhab.movid.common.usecases

import com.hadysalhab.movid.common.DeviceConfigManager
import com.hadysalhab.movid.common.datavalidator.DataValidator
import com.hadysalhab.movid.common.time.TimeProvider
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.MoviesStateManager
import com.hadysalhab.movid.movies.SchemaToModelHelper
import com.hadysalhab.movid.networking.ApiEmptyResponse
import com.hadysalhab.movid.networking.ApiErrorResponse
import com.hadysalhab.movid.networking.ApiResponse
import com.hadysalhab.movid.networking.ApiSuccessResponse
import com.hadysalhab.movid.networking.responses.MoviesResponseSchema
import retrofit2.Response

abstract class BaseMoviesUseCase(
    private val deviceConfigManager: DeviceConfigManager,
    private val dataValidator: DataValidator,
    private val timeProvider: TimeProvider,
    private val moviesStateManager: MoviesStateManager,
    private val errorMessageHandler: ErrorMessageHandler,
    private val schemaToModelHelper: SchemaToModelHelper
) {

    private val region = deviceConfigManager.getISO3166CountryCodeOrUS()
    private var page = 1
    private var moviesID: Int? = null
    private val groupType: GroupType = getGroupType()
    fun fetchMoviesUseCase(
        page: Int = 1,
        movieId: Int? = null
    ): UseCaseSyncResults<MoviesResponse> {
        this.moviesID = moviesID
        this.page = page
        return if (page == 1) {
            if (shouldCheckMovieStore()) {
                val moviesStore = getMoviesStore()
                if (isValid(moviesStore)) {
                    notifySuccess(moviesStore)
                } else {
                    fetchMovies()
                }
            } else {
                fetchMovies()
            }
        } else {
            fetchMovies()
        }
    }

    private fun fetchMovies(): UseCaseSyncResults<MoviesResponse> = try {
        val res = fetchMoviesFromApi()
        handleResponse(ApiResponse.create(res))
    } catch (err: Throwable) {
        handleResponse(ApiResponse.create(err))
    }

    //hook
    protected fun shouldCheckMovieStore() = true
    protected abstract fun fetchMoviesFromApi(): Response<MoviesResponseSchema>
    protected abstract fun getMoviesStore(): MoviesResponse
    protected abstract fun getGroupType(): GroupType
    private fun isValid(moviesResponse: MoviesResponse) =
        dataValidator.isMoviesResponseValid(moviesResponse)

    private fun handleResponse(res: ApiResponse<MoviesResponseSchema>) = when (res) {
        is ApiSuccessResponse -> {
            val moviesResponse = convertSchemaToResponseAndReturn(res.body).also {
                if (page == 1) {
                    setTimeStamp(it)
                    if (shouldCheckMovieStore()) {
                        updateMovieStore(it)
                    }
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

    private fun notifySuccess(moviesResponse: MoviesResponse) =
        UseCaseSyncResults.Results(data = moviesResponse)

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
        schemaToModelHelper.getMoviesResponseFromSchema(this.groupType, moviesResponseSchema)
}