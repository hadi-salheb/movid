package com.hadysalhab.movid.screen.layout.launcher

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.hadysalhab.movid.R

class LauncherViewImpl(inflater: LayoutInflater, parent: ViewGroup?) : LauncherView() {
    private val usernameEditText: TextInputEditText
    private val usernameInputLayout: TextInputLayout
    private val passwordEditText: TextInputEditText
    private val passwordInputLayout: TextInputLayout
    private val loginBtn: Button
    private val signUpBtn: Button

    init {
        setRootView(inflater.inflate(R.layout.layout_auth_launcher, parent, false))
        usernameEditText = findViewById(R.id.username_editText)
        usernameInputLayout = findViewById(R.id.username_input_layout)
        passwordEditText = findViewById(R.id.password_editText)
        passwordInputLayout = findViewById(R.id.password_input_layout)
        loginBtn = findViewById(R.id.login_btn)
        signUpBtn = findViewById(R.id.signUp_btn)
        setupListeners()
    }

    private fun setupListeners() {
        loginBtn.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            var validationResult: Boolean
            validationResult = handleValidation(username, password)
            if (validationResult) {
                listeners.forEach { listener ->
                    listener.onLoginClicked()
                }
            }
        }
    }

    private fun handleValidation(username: String, password: String): Boolean {
        var result: Boolean = true
        if (username.isEmpty()) {
            usernameInputLayout.error = "Please add a username"
            result = false
        } else {
            usernameInputLayout.error = null
        }
        if (password.isEmpty()) {
            passwordInputLayout.error = "Please add a password"
            result = false
        } else {
            passwordInputLayout.error = null
        }
        return result
    }
}