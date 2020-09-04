package com.hadysalhab.movid.account.usecases.details

import com.hadysalhab.movid.networking.ApiResponse
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.networking.responses.AccountSchema

class FetchAccountDetailsUseCaseSync(
    private val tmdbApi: TmdbApi
) {
    private lateinit var sessionID: String
    fun getAccountDetailsUseCaseSync(
        sessionID: String
    ): ApiResponse<AccountSchema> = try {
        this.sessionID = sessionID
        val res = tmdbApi.getAccountDetail(sessionID).execute()
        ApiResponse.create(res)
    } catch (err: Throwable) {
        ApiResponse.create(err)
    }

}


