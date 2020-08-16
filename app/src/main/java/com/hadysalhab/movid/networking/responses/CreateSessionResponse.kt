package com.hadysalhab.movid.networking.responses

import com.google.gson.annotations.SerializedName

data class CreateSessionResponse(
    @SerializedName("session_id")
    val sessionId: String
)