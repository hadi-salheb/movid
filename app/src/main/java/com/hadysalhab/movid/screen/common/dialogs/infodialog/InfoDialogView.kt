package com.hadysalhab.movid.screen.common.dialogs.infodialog

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.hadysalhab.movid.R
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

data class InfoDialogViewState(
    val title: String = "",
    val message: String = "",
    val caption: String = ""
)

class InfoDialogView(layoutInflater: LayoutInflater, viewGroup: ViewGroup?) :
    BaseObservableViewMvc<InfoDialogView.Listener>() {
    interface Listener {
        fun onPositiveButtonClicked()
    }

    private val titleTV: TextView
    private val messageTV: TextView
    private val button: MaterialButton

    init {
        setRootView(layoutInflater.inflate(R.layout.component_info_dialog, viewGroup, false))
        titleTV = findViewById(R.id.txt_title)
        messageTV = findViewById(R.id.txt_message)
        button = findViewById(R.id.btn_positive)
        button.setOnClickListener {
            listeners.forEach {
                it.onPositiveButtonClicked()
            }
        }
    }

    fun handleState(infoDialogViewState: InfoDialogViewState) {
        with(infoDialogViewState) {
            titleTV.text = title
            messageTV.text = message
            button.text = caption
        }
    }
}