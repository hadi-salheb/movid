package com.hadysalhab.movid.screen.common.emptyresults

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import com.hadysalhab.movid.R
import com.hadysalhab.movid.screen.common.views.BaseViewMvc


data class EmptyResultsState(
    @DrawableRes val iconDrawable: Int,
    val emptyResultMessage: String
)

class EmptyResults(layoutInflater: LayoutInflater, container: ViewGroup?) : BaseViewMvc() {
    private val emptyResultIcon by lazy {
        findViewById<ImageView>(R.id.empty_result_icon)
    }
    private val emptyResultsMessage by lazy {
        findViewById<TextView>(R.id.empty_result_message)
    }

    init {
        setRootView(layoutInflater.inflate(R.layout.component_empty_screen, container, false))
    }

    fun render(emptyResultsState: EmptyResultsState) {
        emptyResultIcon.setImageResource(emptyResultsState.iconDrawable)
        emptyResultsMessage.text = emptyResultsState.emptyResultMessage
    }

}