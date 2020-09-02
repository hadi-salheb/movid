package com.hadysalhab.movid.account.usecases.details

import com.hadysalhab.movid.account.AccountResponse
import com.hadysalhab.movid.account.UserStateManager
import com.hadysalhab.movid.common.SharedPreferencesManager
import com.hadysalhab.movid.common.usecases.ErrorMessageHandler
import com.hadysalhab.movid.common.usecases.UseCaseSyncResults
import com.hadysalhab.movid.movies.SchemaToModelHelper
import com.hadysalhab.movid.networking.*
import com.hadysalhab.movid.networking.responses.AccountSchema
import com.hadysalhab.movid.persistence.AccountDao

class FetchAccountDetailsUseCaseSync(
    private val tmdbApi: TmdbApi,
    private val errorMessageHandler: ErrorMessageHandler,
    private val sharedPreferencesManager: SharedPreferencesManager,
    private val accountDao: AccountDao,
    private val userStateManager: UserStateManager,
    private val schemaToModelHelper: SchemaToModelHelper
) {
    private lateinit var sessionID: String
    fun getAccountDetailsUseCaseSync(
        sessionID: String
    ): UseCaseSyncResults<AccountResponse> = try {
        this.sessionID = sessionID
        val res = tmdbApi.getAccountDetail(sessionID).execute()
        handleResponse(ApiResponse.create(res))
    } catch (err: Throwable) {
        handleResponse(ApiResponse.create(err))
    }

    private fun handleResponse(res: ApiResponse<AccountSchema>) = when (res) {
        is ApiSuccessResponse -> {
            val accountResponse = getResponseFromSchema(res.body)
            saveSessionIdToSharedPref()
            saveAccountResponseToDb(accountResponse)
            updateUserStateManager(accountResponse)
            notifySuccess(accountResponse)
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

    private fun updateUserStateManager(accountResponse: AccountResponse) = userStateManager.apply {
        updateSessionId(sessionID)
        updateAccountResponse(accountResponse)
    }

    private fun saveAccountResponseToDb(accountResponse: AccountResponse) =
        accountDao.insertAccountResponse(accountResponse)

    private fun saveSessionIdToSharedPref() = sharedPreferencesManager.setStoredSessionId(sessionID)
    private fun getResponseFromSchema(accountSchema: AccountSchema) =
        schemaToModelHelper.getAccountResponse(accountSchema)

    private fun createErrorResultsAndReturn(msg: String?): String =
        errorMessageHandler.createErrorMessage(msg)

    private fun notifySuccess(response: AccountResponse) =
        UseCaseSyncResults.Results(data = response)

    private fun notifyError(msg: String) = UseCaseSyncResults.Error<AccountResponse>(msg)

}


