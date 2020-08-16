package com.hadysalhab.movid.authentication

import com.hadysalhab.movid.networking.ApiResponse
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.networking.responses.CreateAndSignTokenResponse

class SignTokenUseCase(
    private val tmdbApi: TmdbApi
) {
    fun signTokenSync(
        username: String,
        password: String,
        requestToken: String
    ): ApiResponse<CreateAndSignTokenResponse> = try {
        val res = tmdbApi.signToken(username, password, requestToken).execute()
        ApiResponse.create<CreateAndSignTokenResponse>(res)
    } catch (err: Throwable) {
        ApiResponse.create(err)
    }
}