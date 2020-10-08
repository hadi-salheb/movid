package com.hadysalhab.movid.screen.common.toolbar

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.hadysalhab.movid.R
import com.hadysalhab.movid.screen.common.views.BaseViewMvc

class TitleToolbarLayout(layoutInflater: LayoutInflater, parent: ViewGroup?) :
    BaseViewMvc() {


    private val titleToolbarTextView: TextView

    init {
        setRootView(layoutInflater.inflate(R.layout.component_title_toolbar, parent, false))
        titleToolbarTextView = findViewById(R.id.title_toolbar_textView)
    }

    fun setToolbarTitle(title: String) {
        titleToolbarTextView.text = title
    }
}