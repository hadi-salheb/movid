package com.hadysalhab.movid.networking.responses

import com.google.gson.annotations.SerializedName

data class CreateAndSignTokenResponse (
    @SerializedName("request_token")
    val requestToken:String
)