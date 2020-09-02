package com.hadysalhab.movid.account

import com.hadysalhab.movid.networking.ApiResponse
import com.hadysalhab.movid.networking.TmdbApi
import com.hadysalhab.movid.networking.responses.AddToFavResponse

class AddRemoveFavUseCaseSync(private val tmdbApi: TmdbApi) {
    fun addToFavoriteUseCaseSync(
        accountID: Int,
        mediaID: Int,
        sessionId: String,
        favorite: Boolean
    ): ApiResponse<AddToFavResponse> = try {
        val res = tmdbApi.markAsFavorite(
            accountID = accountID,
            media_id = mediaID,
            favorite = favorite,
            sessionID = sessionId
        ).execute()
        ApiResponse.create<AddToFavResponse>(res)
    } catch (err: Throwable) {
        ApiResponse.create(err)
    }
}