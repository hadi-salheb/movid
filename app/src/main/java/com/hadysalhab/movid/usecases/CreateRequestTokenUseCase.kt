package com.hadysalhab.movid.usecases

import com.hadysalhab.movid.networking.ApiResponse
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.networking.responses.CreateAndSignTokenResponse

class CreateRequestTokenUseCase(private val tmdbApi: TmdbApi) {
    fun createRequestTokenSync(): ApiResponse<CreateAndSignTokenResponse> = try {
        val res = tmdbApi.createRequestToken().execute()
        ApiResponse.create<CreateAndSignTokenResponse>(res)
    } catch (err: Throwable) {
        ApiResponse.create(err)
    }
}