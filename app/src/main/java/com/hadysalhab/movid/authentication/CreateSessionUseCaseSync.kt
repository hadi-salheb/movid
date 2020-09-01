package com.hadysalhab.movid.authentication

import com.hadysalhab.movid.networking.ApiResponse
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.networking.responses.CreateSessionResponse

class CreateSessionUseCaseSync(private val tmdbApi: TmdbApi) {
    fun createSessionSync(token: String): ApiResponse<CreateSessionResponse> = try {
        val res = tmdbApi.createSession(token).execute()
        ApiResponse.create<CreateSessionResponse>(res)
    } catch (err: Throwable) {
        ApiResponse.create(err)
    }
}