package com.hadysalhab.movid.movies

data class CastGroup(
    val groupType: GroupType = GroupType.CAST,
    val casts: List<Cast>
)