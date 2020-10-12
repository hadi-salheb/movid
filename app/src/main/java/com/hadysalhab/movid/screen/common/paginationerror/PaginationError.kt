package com.hadysalhab.movid.screen.common.paginationerror

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import com.hadysalhab.movid.R
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

class PaginationError(layoutInflater: LayoutInflater, container: ViewGroup?) :
    BaseObservableViewMvc<PaginationError.Listener>() {
    interface Listener {
        fun onPaginationErrorClick()
    }

    init {
        setRootView(layoutInflater.inflate(R.layout.component_pagination_error, container, false))
        findViewById<ImageButton>(R.id.pagination_error_button).setOnClickListener {
            listeners.forEach {
                it.onPaginationErrorClick()
            }
        }
    }
}