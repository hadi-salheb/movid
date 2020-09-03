package com.hadysalhab.movid.common.usecases

import com.hadysalhab.movid.movies.GroupType
import com.hadysalhab.movid.movies.MoviesResponse
import com.hadysalhab.movid.movies.SchemaToModelHelper
import com.hadysalhab.movid.networking.ApiEmptyResponse
import com.hadysalhab.movid.networking.ApiErrorResponse
import com.hadysalhab.movid.networking.ApiResponse
import com.hadysalhab.movid.networking.ApiSuccessResponse
import com.hadysalhab.movid.networking.responses.MoviesResponseSchema
import retrofit2.Response

//Similar,Recommended
abstract class BaseSimilarRecommendedMoviesUseCase(
    private val errorMessageHandler: ErrorMessageHandler,
    private val schemaToModelHelper: SchemaToModelHelper
) {
    fun fetchSimilarRecommendedMoviesUseCase(
        page: Int = 1,
        movieId: Int
    ): UseCaseSyncResults<MoviesResponse> {
        return fetchSimilarMoviesAndReturn(page, movieId)
    }

    private fun fetchSimilarMoviesAndReturn(
        page: Int,
        movieId: Int
    ): UseCaseSyncResults<MoviesResponse> = try {
        val res = fetchApi(page, movieId)
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
            getGroupType(),
            moviesResponseSchema
        )

    protected abstract fun getGroupType(): GroupType
    protected abstract fun fetchApi(page: Int, movieId: Int): Response<MoviesResponseSchema>
}