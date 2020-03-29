package com.example.ozturkse.awesomelogin

import android.app.Application
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Start home activity
        startActivity(Intent(this@SplashActivity, SignInActivity::class.java))
        // close splash activity
        finish()
    }
}
