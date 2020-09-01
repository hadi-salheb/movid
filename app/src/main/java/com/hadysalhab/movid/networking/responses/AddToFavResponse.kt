package com.hadysalhab.movid.networking.responses

import com.google.gson.annotations.SerializedName

data class AddToFavResponse(
    @SerializedName("status_code")
    val statusCode: Int,
    @SerializedName("status_message")
    val statusMessage: String
)