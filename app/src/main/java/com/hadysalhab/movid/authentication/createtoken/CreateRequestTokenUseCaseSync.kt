package com.hadysalhab.movid.authentication.createtoken

import com.hadysalhab.movid.networking.ApiResponse
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.networking.responses.CreateAndSignTokenResponse

class CreateRequestTokenUseCaseSync(
    private val tmdbApi: TmdbApi
) {
    fun createRequestTokenSync(): ApiResponse<CreateAndSignTokenResponse> = try {
        val res = tmdbApi.createRequestToken().execute()
        ApiResponse.create(res)
    } catch (err: Throwable) {
        ApiResponse.create(err)
    }
}