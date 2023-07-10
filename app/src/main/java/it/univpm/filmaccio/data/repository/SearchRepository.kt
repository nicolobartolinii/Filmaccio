package it.univpm.filmaccio.data.repository

import it.univpm.filmaccio.data.api.TmdbApiClient
import it.univpm.filmaccio.data.models.SearchResponse

/**
 * Questa classe è un repository che si occupa di gestire i dati relativi alla ricerca.
 * In particolare si occupa di fare le chiamate all'API di TMDB per ottenere i dati relativi alla ricerca.
 *
 * @author nicolobartolinii
 */
class SearchRepository {
    private val tmdbApi = TmdbApiClient.TMDB_API

    // Questo metodo implementa la chiamata all'endpoint search/multi in modo da poterlo usare in altre classi
    suspend fun searchMulti(
        query: String, page: Int = 1, language: String = "it-IT", includeAdult: Boolean = false
    ): SearchResponse {
        return tmdbApi.searchMulti(
            query = query, page = page, language = language, includeAdult = includeAdult
        )
    }
}