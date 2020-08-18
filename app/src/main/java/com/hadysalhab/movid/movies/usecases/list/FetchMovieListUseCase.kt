package com.hadysalhab.movid.movies.usecases.list

import com.hadysalhab.movid.common.utils.BaseBusyObservable
import com.hadysalhab.movid.movies.GroupType

class FetchMovieListUseCase : BaseBusyObservable<FetchMovieListUseCase.Listener>() {
    interface Listener{
        fun onFetchingMovieList()
        fun onFetchMovieListSuccess()
        fun onFetchMovieListFailed(msg:String)
    }

    fun fetchMovieListAndNotify(groupType: GroupType,page:Int){

    }
}