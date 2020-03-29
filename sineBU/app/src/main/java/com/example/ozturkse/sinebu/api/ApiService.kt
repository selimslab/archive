package com.example.ozturkse.sinebu.api

import com.example.ozturkse.sinebu.model.Movie
import com.example.ozturkse.sinebu.network.NetworkModule
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TheMovieDbApiService {

    @GET("discover/movie?sort_by=popularity.desc")
    fun getPopular(): Observable<TheMovieDbApiResponse>

    @GET("discover/movie?sort_by=vote_average.desc")
    fun getTopRated(): Observable<TheMovieDbApiResponse>

    @GET("movie/{movie_id}")
    fun getMovie(@Path("movie_id") id: Int): Observable<Movie>

    @GET("movie/upcoming")
    fun getUpcomingMovies(): Observable<TheMovieDbApiResponse>

    @GET("movie/now_playing")
    fun getNowPlayingMovies(): Observable<TheMovieDbApiResponse>

    @GET("search/movie")
    fun SearchMovies(@Query("query") query: String): Observable<TheMovieDbApiResponse>

    companion object {
        fun create(): TheMovieDbApiService {
            val baseUrl = "https://api.themoviedb.org/3/"
            val network = NetworkModule(baseUrl)
            return network.retrofit.create(TheMovieDbApiService::class.java)
        }
    }
}