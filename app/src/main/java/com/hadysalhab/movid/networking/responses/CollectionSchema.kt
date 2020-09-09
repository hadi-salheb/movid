package com.hadysalhab.movid.networking.responses

data class CollectionSchema(
    val id: Int,
    val name: String,
    val overview: String,
    val parts: List<MovieSchema>
)