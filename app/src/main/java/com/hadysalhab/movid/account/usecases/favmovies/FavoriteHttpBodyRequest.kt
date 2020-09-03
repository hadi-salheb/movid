package com.hadysalhab.movid.account.usecases.favmovies

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class FavoriteHttpBodyRequest(
    @field:Expose
    @field:SerializedName("media_type") private val mediaType: String = "movie",
    @field:Expose @field:SerializedName("media_id") private val mediaId: Int,
    @field:Expose @field:SerializedName("favorite") private val favorite: Boolean
)