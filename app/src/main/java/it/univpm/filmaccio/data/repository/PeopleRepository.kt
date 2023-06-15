package it.univpm.filmaccio.data.repository

import it.univpm.filmaccio.data.api.TmdbApiClient
import it.univpm.filmaccio.data.models.Person

class PeopleRepository {
    private val tmdbApi = TmdbApiClient.TMDB_API

    suspend fun getPersonDetails(personId: Int): Person {
        return tmdbApi.getPersonDetails(personId = personId)
    }
}