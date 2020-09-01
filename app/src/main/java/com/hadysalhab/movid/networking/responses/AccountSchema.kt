package com.hadysalhab.movid.networking.responses

import com.google.gson.annotations.SerializedName

data class AccountSchema(
    val id: Int,
    val name: String,
    val username: String,
    @SerializedName("include_adult")
    val includeAdult: Boolean,
    @SerializedName("avatar")
    val avatarSchema: AvatarSchema,
    @SerializedName("iso_639_1")
    val language: String,
    @SerializedName("iso_3166_1")
    val country: String
)

data class AvatarSchema(
    @SerializedName("gravatar")
    val gravatarSchema: GravatarSchema
)

data class GravatarSchema(
    val hash: String
)