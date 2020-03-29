package com.example.ozturkse.awesomelogin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView

import kotlinx.android.synthetic.main.activity_sign_up.*
import android.content.Context
import android.content.Intent


class SignUpActivity : AppCompatActivity() {

    private val minPasswordLength = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        signup_edittext_confirm_password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })

        sign_up_button.setOnClickListener { attemptLogin() }
        signup_login_button.setOnClickListener{startActivity(SignInActivity.newIntent(this))}
    }



    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun attemptLogin() {

        // Reset errors.
        signup_edittext_email.error = null
        signup_edittext_password.error = null
        signup_edittext_confirm_password.error = null

        // Store values at the time of the login attempt.
        val email = signup_edittext_email.text.toString()
        val password = signup_edittext_password.text.toString()
        val confirmPassword = signup_edittext_confirm_password.text.toString()


        var cancel = false
        var focusView: View? = null

        if (password != confirmPassword) {
            signup_edittext_password.error = getString(R.string.error_password_unmatch)
            focusView = signup_edittext_password
            cancel = true
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            signup_edittext_password.error = getString(R.string.error_invalid_password)
            focusView = signup_edittext_password
            cancel = true
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            signup_edittext_email.error = getString(R.string.error_field_required)
            focusView = signup_edittext_email
            cancel = true
        } else if (!isEmailValid(email)) {
            signup_edittext_email.error = getString(R.string.error_invalid_email)
            focusView = signup_edittext_email
            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView?.requestFocus()
        } else {
            // login successful
            val loginUtil = LoginUtil()
            loginUtil.saveLoginInfo(this@SignUpActivity,email,password)
            startActivity(MainActivity.newIntent(this, email, password))
        }
    }


    private fun isEmailValid(email: String): Boolean {
        return email.contains("@")
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > minPasswordLength
    }

    companion object {
        fun newIntent(context: Context): Intent {
            val intent = Intent(context, SignUpActivity::class.java)
            return intent
        }
    }


}


