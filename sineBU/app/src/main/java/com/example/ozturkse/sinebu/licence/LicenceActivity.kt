package com.example.ozturkse.sinebu.licence

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.ozturkse.sinebu.R
import kotlinx.android.synthetic.main.activity_licence.*


class LicenceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_licence)
        setToolbar()

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setToolbar(){
        setSupportActionBar(toolbar_licence)

        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowTitleEnabled(true)
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, LicenceActivity::class.java)
        }
    }
}
