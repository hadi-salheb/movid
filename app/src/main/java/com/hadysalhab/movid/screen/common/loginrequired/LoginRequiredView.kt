package com.hadysalhab.movid.screen.common.loginrequired

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.hadysalhab.movid.R
import com.hadysalhab.movid.screen.common.views.BaseObservableViewMvc

class LoginRequiredView(layoutInflater: LayoutInflater, viewGroup: ViewGroup?) :
    BaseObservableViewMvc<LoginRequiredView.Listener>() {
    interface Listener {
        fun onLoginRequiredBtnClicked()
    }

    private val loginRequiredBtn: Button
    private val loginRequiredTV: TextView

    init {
        setRootView(layoutInflater.inflate(R.layout.component_login_required, viewGroup, false))
        loginRequiredBtn = findViewById(R.id.login_required_btn)
        loginRequiredBtn.setOnClickListener {
            listeners.forEach {
                it.onLoginRequiredBtnClicked()
            }
        }
        loginRequiredTV = findViewById(R.id.login_required_text)
    }

    fun setText(text: String) {
        loginRequiredTV.text = text
    }
}