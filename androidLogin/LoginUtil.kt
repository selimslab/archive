package com.example.ozturkse.awesomelogin

import android.content.Context
import android.support.v7.app.AppCompatActivity


class LoginUtil {

    private val EMAIL = "email_shared_pref"
    private val PASSWORD = "password_shared_pref"
    private val LOGIN_PREFS_FILENAME = "Login"


    fun isLoginInfoCorrect(context: Context, email: String, password: String): Boolean {
        val (savedEmail, savedPassword) = getSavedLoginInfo(context)

        if (email == savedEmail && password == savedPassword) {
            return true
        }

        return false
    }

    fun saveLoginInfo(context: Context, email: String, password: String) {
        val sharedPrefs = context.getSharedPreferences(LOGIN_PREFS_FILENAME, Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putString(EMAIL, email)
        editor.putString(PASSWORD, password)
        editor.apply()
    }

    data class LoginInfo(val email: String?, val password: String?)

    fun getSavedLoginInfo(context: Context): LoginInfo {
        val sharedPrefs = context.getSharedPreferences(LOGIN_PREFS_FILENAME, Context.MODE_PRIVATE)
        val savedEmail = sharedPrefs.getString(EMAIL, null)
        val savedPassword = sharedPrefs.getString(PASSWORD, null)
        return LoginInfo(savedEmail, savedPassword)
    }

    fun clearLoginInfo(context: Context) {
        val sharedPrefs = context.getSharedPreferences(LOGIN_PREFS_FILENAME, Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.clear()
        editor.apply()
    }


}