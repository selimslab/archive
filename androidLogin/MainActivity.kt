package com.example.ozturkse.awesomelogin

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val email = intent.getStringExtra(EMAIL_INTENT_KEY)
        val password = intent.getStringExtra(PASSWORD_INTENT_KEY)

        main_textview_email.text = email
        main_textview_password.text = password

        // notify login
        val toast = Toast.makeText(this@MainActivity, R.string.login_successful, Toast.LENGTH_SHORT)
        toast.show()

    }

    fun logout(view: View) {
        val loginUtil = LoginUtil()
        loginUtil.clearLoginInfo(this@MainActivity)
        startActivity(SignInActivity.newIntent(this))
    }


    companion object {

        private val EMAIL_INTENT_KEY = "email_intent"
        private val PASSWORD_INTENT_KEY = "password_intent"

        fun newIntent(context: Context, email: String, password: String): Intent {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(EMAIL_INTENT_KEY, email)
            intent.putExtra(PASSWORD_INTENT_KEY, password)
            return intent
        }


    }
}
