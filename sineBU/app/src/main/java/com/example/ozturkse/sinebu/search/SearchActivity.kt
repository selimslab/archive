package com.example.ozturkse.sinebu.search

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.ozturkse.sinebu.R
import com.example.ozturkse.sinebu.api.TheMovieDbApiService
import io.reactivex.disposables.Disposable
import android.app.SearchManager
import android.content.Intent
import android.view.Menu
import android.widget.ListView
import com.example.ozturkse.sinebu.detail.DetailActivity
import com.example.ozturkse.sinebu.model.Movie
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_search.*
import android.support.v7.widget.SearchView

class SearchActivity : AppCompatActivity() {
    private val apiService by lazy {
        TheMovieDbApiService.create()
    }

    private var disposable: Disposable? = null

    private lateinit var listView: ListView

    private lateinit var searchAdapter: SearchResultsListAdapter

    private var movieslist: List<Movie>? = emptyList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        setSupportActionBar(activity_search_toolbar)

        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowTitleEnabled(true)
        }

        // Get the intent, verify the action and get the query
        val intent = intent
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            searchMovie(query)
        }

        listView = activity_search_list_view
        searchAdapter = SearchResultsListAdapter(this, movieslist, { movie: Movie -> movieItemClicked(movie) })
        listView.adapter = searchAdapter

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        // Search
        val searchView = menu.findItem(R.id.home_search).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
            override fun onQueryTextSubmit(query: String): Boolean {
                searchMovie(query)
                return false
            }
        })

        return true
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun searchMovie(query: String) {
        disposable = apiService.SearchMovies(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result -> displaySearchResults(result.movies) },
                        { error -> Toast.makeText(this, error.message, Toast.LENGTH_LONG).show() }
                )
    }

    fun displaySearchResults(movies: List<Movie>?) {
        movieslist = movies
        searchAdapter.updateList(movieslist)

    }

    private fun movieItemClicked(movie: Movie) {
        getMovieDetails(movie)
    }

    fun getMovieDetails(movie: Movie) {
        disposable = apiService.getMovie(movie.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result -> displayMovieDetails(result) },
                        { error -> Toast.makeText(this, error.message, Toast.LENGTH_LONG).show() }
                )
    }

    fun displayMovieDetails(movie: Movie) {
        startActivity(DetailActivity.newIntent(this@SearchActivity, movie))
    }


}
