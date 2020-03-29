package com.example.ozturkse.awesomelogin

import android.app.Application
import android.content.Context
import android.content.Intent

import android.os.AsyncTask
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView

import android.support.v7.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_sign_in.*


/**
 * A login screen that offers login via email/password.
 */

class SignInActivity : AppCompatActivity() {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private var authTask: UserLoginTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val loginUtil = LoginUtil()
        val (savedEmail, savedPassword) = loginUtil.getSavedLoginInfo(this@SignInActivity)

        // log in if username and password matches
        if (savedPassword != null && savedEmail != null) {
            startActivity(MainActivity.newIntent(this@SignInActivity, savedEmail, savedPassword))
        }


        signin_edittext_password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })

        sign_in_button.setOnClickListener { attemptLogin() }
        sign_in_create_account_button.setOnClickListener { startActivity(SignUpActivity.newIntent(this)) }

    }


    // cancel login on stop
    override fun onStop() {
        super.onStop()
        authTask?.cancel(true)

    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun attemptLogin() {
        if (authTask != null) {
            return
        }

        // Reset errors.
        signin_edittext_email.error = null
        signin_edittext_password.error = null

        val email = signin_edittext_email.text.toString()
        val password = signin_edittext_password.text.toString()

        var cancel = false
        var focusView: View? = null


        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            signin_edittext_email.error = getString(R.string.error_field_required)
            focusView = signin_edittext_email
            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView?.requestFocus()
        } else {
            authTask = UserLoginTask(email, password)
            authTask!!.execute(null as Void?)
        }
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    inner class UserLoginTask internal constructor(private val email: String, private val password: String) : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void): Boolean? {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(1000)
                val loginUtil = LoginUtil()
                return loginUtil.isLoginInfoCorrect(this@SignInActivity, email, password)

            } catch (e: InterruptedException) {
                return false
            }


        }


        override fun onPostExecute(success: Boolean?) {
            authTask = null

            if (success!!) {
                finish()
                startActivity(MainActivity.newIntent(this@SignInActivity, email, password))
            } else {
                signin_edittext_password.error = getString(R.string.error_login)
            }
        }

        override fun onCancelled() {
            authTask = null
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            val intent = Intent(context, SignInActivity::class.java)
            return intent
        }
    }


}

