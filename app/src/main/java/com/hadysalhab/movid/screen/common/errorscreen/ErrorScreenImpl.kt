package com.hadysalhab.movid.screen.common.errorscreen

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.hadysalhab.movid.R

class ErrorScreenImpl(layoutInflater: LayoutInflater, parent: ViewGroup?) : ErrorScreen() {
    private val tryAgainBtn: Button
    private val errorMessageTextView: TextView

    init {
        setRootView(layoutInflater.inflate(R.layout.component_error_screen, parent, false))
        tryAgainBtn = findViewById(R.id.button_try_again)
        errorMessageTextView = findViewById(R.id.text_custom_error_message)
        tryAgainBtn.setOnClickListener {
            listeners.forEach {
                it.onRetryClicked()
            }
        }
    }

    override fun displayErrorMessage(errorMessage: String) {
        errorMessageTextView.text = errorMessage
    }
}