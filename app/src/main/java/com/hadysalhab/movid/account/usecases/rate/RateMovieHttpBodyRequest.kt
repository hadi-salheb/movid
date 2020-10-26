package com.hadysalhab.movid.account.usecases.rate

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RateMovieHttpBodyRequest(
    @field:Expose
    @field:SerializedName("value") private val rate: Double
)