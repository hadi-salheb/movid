package com.hadysalhab.movid.movies.usecases.recommended

import com.hadysalhab.movid.common.usecases.UseCaseSyncResults
import com.hadysalhab.movid.common.usecases.ErrorMessageHandler
import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.SchemaToModelHelper
import com.hadysalhab.movid.networking.*
import com.hadysalhab.movid.networking.responses.MoviesResponseSchema

class FetchRecommendedMoviesUseCaseSync(
    private val tmdbApi: TmdbApi,
    private val errorMessageHandler: ErrorMessageHandler,
    private val schemaToModelHelper: SchemaToModelHelper
) {
    fun fetchRecommendedMoviesUseCaseSync(page: Int = 1, movieId: Int): UseCaseSyncResults<MoviesResponse> {
         return   fetchRecommendedMovies(page, movieId)
    }

    private fun fetchRecommendedMovies(page: Int, movieId: Int): UseCaseSyncResults<MoviesResponse> = try {
        val res = tmdbApi.fetchRecommendedMovies(page = page, movieId = movieId).execute()
        handleResponse(ApiResponse.create(res))
    } catch (err: Throwable) {
        handleResponse(ApiResponse.create(err))
    }

    private fun handleResponse(res: ApiResponse<MoviesResponseSchema>) = when (res) {
        is ApiSuccessResponse -> {
            val moviesResponse = convertSchemaToResponseAndReturn(res.body)
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

    private fun convertSchemaToResponseAndReturn(moviesResponseSchema: MoviesResponseSchema): MoviesResponse =
        schemaToModelHelper.getMoviesResponseFromSchema(
            GroupType.RECOMMENDED_MOVIES,
            moviesResponseSchema
        )
}