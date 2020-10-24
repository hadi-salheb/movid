package com.hadysalhab.movid.screen.authentication

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.hadysalhab.movid.R

class LoginViewImpl(inflater: LayoutInflater, parent: ViewGroup?) : LoginView() {
    private val usernameEditText: TextInputEditText
    private val usernameInputLayout: TextInputLayout
    private val passwordEditText: TextInputEditText
    private val passwordInputLayout: TextInputLayout
    private val loginBtn: Button
    private val signUpBtn: TextView
    private val browseBtn: Button

    init {
        setRootView(inflater.inflate(R.layout.layout_authentication, parent, false))
        usernameEditText = findViewById(R.id.username_editText)
        usernameInputLayout = findViewById(R.id.username_input_layout)
        passwordEditText = findViewById(R.id.password_editText)
        passwordInputLayout = findViewById(R.id.password_input_layout)
        loginBtn = findViewById(R.id.login_btn)
        signUpBtn = findViewById(R.id.signUp_btn)
        browseBtn = findViewById(R.id.browse_btn)
        setupListeners()
    }

    private fun setupListeners() {
        loginBtn.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            val validationResult: Boolean
            validationResult = handleValidation(username, password)
            if (validationResult) {
                listeners.forEach { listener ->
                    listener.onLoginClicked(username, password)
                }
            }
        }
        signUpBtn.setOnClickListener {
            listeners.forEach { listener ->
                listener.onSignUpClicked()
            }
        }
        browseBtn.setOnClickListener {
            listeners.forEach { listener ->
                listener.onBrowseClicked()
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

    override fun showProgressState() {
        loginBtn.isEnabled = false
        signUpBtn.isEnabled = false
        usernameInputLayout.isEnabled = false
        passwordInputLayout.isEnabled = false
        browseBtn.isEnabled = false
    }

    override fun hideProgressState() {
        showIdleScreen()
    }

    override fun showIdleScreen() {
        loginBtn.isEnabled = true
        signUpBtn.isEnabled = true
        browseBtn.isEnabled = true
        usernameInputLayout.isEnabled = true
        passwordInputLayout.isEnabled = true
    }
}
