package it.univpm.filmaccio.data.repository

import android.util.Log
import it.univpm.filmaccio.data.api.TmdbApiClient
import it.univpm.filmaccio.data.models.SearchResponse

// Questa classe è un repository che si occupa di gestire i dati relativi alla ricerca. Effettua
// semplicemente la chiamata all'endpoint search/multi dell'API di TMDB.
class SearchRepository {
    private val tmdbApi = TmdbApiClient.TMDB_API

    suspend fun searchMulti(
        query: String, page: Int = 1, language: String = "it-IT", includeAdult: Boolean = false
    ): SearchResponse {
        return tmdbApi.searchMulti(
            query = query, page = page, language = language, includeAdult = includeAdult
        )
    }
}