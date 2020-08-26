package com.hadysalhab.movid.movies.usecases.list

import com.hadysalhab.movid.movies.GroupType

interface FetchMovieListUseCaseFactory {
    fun makeFetchListUseCase(groupType: GroupType): FetchMovieListUseCase
}
