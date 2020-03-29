package com.example.ozturkse.sinebu.model

import com.google.gson.annotations.SerializedName

data class Movie(

        @SerializedName("id")
        var id: Int = 0,

        @SerializedName("overview")
        val overview: String? = null,

        @SerializedName("title")
        var title: String? = null,

        @SerializedName("poster_path")
        var posterPath: String? = null,

        @SerializedName("release_date")
        var releaseDate: String? = null,

        @SerializedName("backdrop_path")
        var backDropPath: String? = null,


        @SerializedName("vote_average")
        var rating: Float = 0.toFloat(),

        @SerializedName("genre_ids")
        var genreIds: List<Int>? = null


        ) {
    private val BASE_POSTER_URL = "https://image.tmdb.org/t/p/w342"
    private val BASE_BACKDROP_URL = "httpS://image.tmdb.org/t/p/w780"

    fun getPosterUrl(): String {
        return "$BASE_POSTER_URL$posterPath"
    }

    fun getBackDropUrl(): String {
        return "$BASE_BACKDROP_URL$backDropPath"
    }
}

