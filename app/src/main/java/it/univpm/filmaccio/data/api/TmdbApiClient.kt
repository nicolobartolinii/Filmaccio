package it.univpm.filmaccio.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TmdbApiClient {
    private const val BASE_URL = "https://api.themoviedb.org/3/"

    private val rf = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val TMDB_API: TmdbApiService = rf.create(TmdbApiService::class.java)
}