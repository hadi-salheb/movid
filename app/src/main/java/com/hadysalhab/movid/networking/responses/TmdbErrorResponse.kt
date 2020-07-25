package com.hadysalhab.movid.networking.responses

import com.google.gson.annotations.SerializedName

data class TmdbErrorResponse(
    @SerializedName("status_message")
    val statusMessage: String
)