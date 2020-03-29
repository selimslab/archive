package com.example.ozturkse.sinebu.api

import com.example.ozturkse.sinebu.model.Movie
import com.google.gson.annotations.SerializedName

data class TheMovieDbApiResponse (

    @SerializedName("results")
    var movies: List<Movie>? = null

)