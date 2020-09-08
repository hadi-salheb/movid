package com.hadysalhab.movid.account.usecases.watchlist

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class WatchlistHttpBodyRequest(
    @field:Expose
    @field:SerializedName("media_type") private val mediaType: String = "movie",
    @field:Expose @field:SerializedName("media_id") private val mediaId: Int,
    @field:Expose @field:SerializedName("watchlist") private val watchlist: Boolean
)