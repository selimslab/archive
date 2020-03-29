package com.example.ozturkse.sinebu.listing

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.ozturkse.sinebu.R
import com.example.ozturkse.sinebu.api.TheMovieDbApiService
import com.example.ozturkse.sinebu.detail.DetailActivity
import com.example.ozturkse.sinebu.model.Movie
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_movie_grid.view.*
import android.os.Parcelable



class MoviesFragment:Fragment(){

    private val apiService by lazy {
        TheMovieDbApiService.create()
    }

    private var disposable: Disposable? = null

    private var data: List<Movie>? = emptyList()

    private var clickListener = { movie: Movie -> movieItemClicked(movie) }


    private lateinit var recyclerView : RecyclerView

    private lateinit var adapter: MovieRecyclerViewAdapter

    private lateinit var layoutManager: RecyclerView.LayoutManager

    private lateinit var recyclerViewState : Parcelable



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDataset()

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_movie_grid, container, false)

        recyclerView = rootView.fragment_movie_grid_recycler_view

        layoutManager = GridLayoutManager(activity, 2)

        recyclerView.layoutManager = layoutManager

        adapter = MovieRecyclerViewAdapter(data, clickListener)

        recyclerView.adapter = adapter

        return rootView

    }


    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        // Save currently selected layout manager.
        savedInstanceState.putParcelable(KEY_SCROLL_STATE, recyclerView.layoutManager!!.onSaveInstanceState() )
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            recyclerViewState = savedInstanceState.getParcelable<Parcelable>(KEY_SCROLL_STATE)
            recyclerView.layoutManager!!.onRestoreInstanceState(recyclerViewState)

        }
        super.onViewStateRestored(savedInstanceState)

    }

    fun initDataset() {
        val sectionNumber = arguments!!.getInt(KEY_SECTION_NUMBER)
        when (sectionNumber) {
            0 -> getNowPlayingMovies()
            1 -> getPopularMovies()
            2 -> getTopRatedMovies()
            3 -> getUpcomingMovies()
        }

    }

    fun getUpcomingMovies() {
        disposable = apiService.getUpcomingMovies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result -> displayMovies(result.movies) },
                        { error -> Toast.makeText(context, error.message, Toast.LENGTH_LONG).show() }
                )
    }

    fun getTopRatedMovies() {
        disposable = apiService.getTopRated()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result -> displayMovies(result.movies) },
                        { error -> Toast.makeText(context, error.message, Toast.LENGTH_LONG).show() }
                )
    }

    fun getPopularMovies() {
        disposable = apiService.getPopular()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result -> displayMovies(result.movies) },
                        { error -> Toast.makeText(context, error.message, Toast.LENGTH_LONG).show() }
                )
    }

    fun getNowPlayingMovies() {
        disposable = apiService.getNowPlayingMovies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result -> displayMovies(result.movies) },
                        { error -> Toast.makeText(context, error.message, Toast.LENGTH_LONG).show() }
                )
    }

    fun displayMovies(movies: List<Movie>?) {
        adapter.updateList(movies)
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
                        { error -> Toast.makeText(context, error.message, Toast.LENGTH_LONG).show() }
                )
    }

    fun displayMovieDetails(movie: Movie) {
        startActivity(DetailActivity.newIntent(context!!, movie))
    }

    companion object {
        // keys
        private val TAG = "RecyclerViewFragment"

        private val KEY_SCROLL_STATE = "scroll_state"

        private val KEY_SECTION_NUMBER = "section_number"

        fun newInstance( sectionNumber: Int): MoviesFragment {
            val fragment = MoviesFragment()
            val args = Bundle()
            args.putInt(KEY_SECTION_NUMBER, sectionNumber)
            fragment.arguments = args
            return fragment
        }
    }


}