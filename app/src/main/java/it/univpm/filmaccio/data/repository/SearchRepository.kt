package it.univpm.filmaccio.data.repository

import android.util.Log
import it.univpm.filmaccio.data.api.TmdbApiClient
import it.univpm.filmaccio.data.models.SearchResponse

class SearchRepository {
    private val tmdbApi = TmdbApiClient.TMDB_API

    suspend fun searchMulti(
        query: String,
        page: Int = 1,
        language: String = "it-IT",
        includeAdult: Boolean = false
    ): SearchResponse {
        Log.e("Search", "Searching for $query in TMDB API")
        return tmdbApi.searchMulti(
            query = query,
            page = page,
            language = language,
            includeAdult = includeAdult
        )
    }
}