package it.univpm.filmaccio.data.repository

import it.univpm.filmaccio.data.api.TmdbApiClient
import it.univpm.filmaccio.data.models.Person

class PeopleRepository {
    private val tmdbApi = TmdbApiClient.TMDB_API

    suspend fun getPersonDetails(personId: Long): Person {
        val person = tmdbApi.getPersonDetails(personId = personId)
        val products = when (person.knownFor) {
            "Acting" -> person.combinedCredits.cast
            "Directing" -> person.combinedCredits.crew
            else -> person.combinedCredits.cast + person.combinedCredits.crew
        }.distinct().sortedBy { it.popularity }.reversed().take(30)
        person.products = products
        person.combinedCredits = Person.CombinedCredits(emptyList(), emptyList())
        return person
    }
}