package com.hadysalhab.movid.screen.common.listheader

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.hadysalhab.movid.R
import com.hadysalhab.movid.screen.common.views.BaseViewMvc

class ListHeader(layoutInflater: LayoutInflater, parent: ViewGroup?) : BaseViewMvc() {
    private val headerText: TextView

    init {
        setRootView(layoutInflater.inflate(R.layout.component_list_header, parent, false))
        headerText = findViewById(R.id.header_text)
    }

    fun setText(text: String) {
        headerText.text = text
    }
}