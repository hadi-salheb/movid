package com.hadysalhab.movid.account

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson


@Entity(tableName = "account")
data class AccountResponse(
    @PrimaryKey
    val id: Int,
    val name: String,
    val username: String,
    val includeAdult: Boolean,
    @Embedded
    val avatar: Avatar,
    val language: String,
    val country: String
) {
    fun deepCopy(gson: Gson): AccountResponse {
        val json = gson.toJson(this)
        return gson.fromJson(json, AccountResponse::class.java)
    }
}

data class Avatar(
    @Embedded
    val gravatar: Gravatar
)

data class Gravatar(
    val hash: String
)