package com.example.ozturkse.sinebu.detail

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.ozturkse.sinebu.R
import com.example.ozturkse.sinebu.model.Movie
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.toolbar.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        setToolbar()

        val posterUrl = intent.getStringExtra(POSTER)
        Glide.with(this).load(posterUrl).into(movie_poster)

        val rating = intent.getStringExtra(RATING)

        val overview = intent.getStringExtra(OVERVIEW)


        detail_movie_name.text = intent.getStringExtra(TITLE)
        detail_movie_rating.text = rating
        detail_overview.text = overview
        detail_movie_year.text = intent.getStringExtra(YEAR)

    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setToolbar(){
        setSupportActionBar(toolbar)

        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowTitleEnabled(true)
        }
    }

    companion object {
        private val TITLE = "title"
        private val POSTER = "poster"
        private val YEAR = "year"
        private val RATING = "rating"
        private val OVERVIEW = "overview"


        fun newIntent(context: Context, movie: Movie): Intent {
            val intent = Intent(context, DetailActivity::class.java)

            val posterUrl = movie.getPosterUrl()
            intent.putExtra(TITLE, movie.title)
            intent.putExtra(OVERVIEW, movie.overview)

            intent.putExtra(POSTER, posterUrl)
            intent.putExtra(YEAR, movie.releaseDate!!.split("-")[0])
            intent.putExtra(RATING, movie.rating.toString())


            return intent
        }
    }

}
