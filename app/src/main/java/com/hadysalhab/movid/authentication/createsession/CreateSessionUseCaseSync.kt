package com.hadysalhab.movid.authentication.createsession

import com.hadysalhab.movid.common.usecases.UseCaseSyncResults
import com.hadysalhab.movid.common.usecases.ErrorMessageHandler
import com.hadysalhab.movid.networking.*
import com.hadysalhab.movid.networking.responses.CreateSessionResponse


class CreateSessionUseCaseSync(
    private val tmdbApi: TmdbApi,
    private val errorMessageHandler: ErrorMessageHandler
) {
    fun createSessionSync(token: String): UseCaseSyncResults<CreateSessionResponse> = try {
        val res = tmdbApi.createSession(token).execute()
        handleResponse(ApiResponse.create(res))
    } catch (err: Throwable) {
        handleResponse(ApiResponse.create(err))
    }

    private fun handleResponse(res: ApiResponse<CreateSessionResponse>) = when (res) {
        is ApiSuccessResponse -> {
            notifySuccess(res.body)
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

    private fun createErrorResultsAndReturn(msg: String?): String =
        errorMessageHandler.createErrorMessage(msg)

    private fun notifySuccess(response: CreateSessionResponse) =
        UseCaseSyncResults.Results(data = response)

    private fun notifyError(msg: String) = UseCaseSyncResults.Error<CreateSessionResponse>(msg)
}