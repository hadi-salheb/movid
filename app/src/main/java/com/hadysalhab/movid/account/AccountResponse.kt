package com.hadysalhab.movid.account

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey


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
)

data class Avatar(
    @Embedded
    val gravatar: Gravatar
)

data class Gravatar(
    val hash: String
)