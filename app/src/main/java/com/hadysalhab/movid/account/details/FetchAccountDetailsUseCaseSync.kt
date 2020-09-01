package com.hadysalhab.movid.account.details

import com.hadysalhab.movid.networking.ApiResponse
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.networking.responses.AccountSchema

class FetchAccountDetailsUseCaseSync(private val tmdbApi: TmdbApi) {
    fun getAccountDetailsUseCaseSync(
        sessionID: String
    ): ApiResponse<AccountSchema> = try {
        val res = tmdbApi.getAccountDetail(sessionID).execute()
        ApiResponse.create<AccountSchema>(res)
    } catch (err: Throwable) {
        ApiResponse.create(err)
    }
}