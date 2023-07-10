package it.univpm.filmaccio.data.repository

import it.univpm.filmaccio.data.api.TmdbApiClient
import it.univpm.filmaccio.data.models.Person
import it.univpm.filmaccio.main.utils.FirestoreService
import kotlinx.coroutines.flow.first

/**
 * Questa classe è un repository che si occupa di gestire i dati relativi alle persone.
 * In particolare si occupa di fare le chiamate all'API di TMDB per ottenere i dati relativi alle persone.
 *
 * @author nicolobartolinii
 */
class PeopleRepository {
    // Qui creiamo un oggetto che contiene il client per le chiamate alle API di TMDB
    private val tmdbApi = TmdbApiClient.TMDB_API

    // Questo metodo implementa la chiamata all'endpoint person/{personId} in modo da poterlo usare in altre classi
    suspend fun getPersonDetails(personId: Long): Person {
        // Qui otteniamo i dettagli di una persona
        val person = tmdbApi.getPersonDetails(personId = personId)
        // Qui gestiamo la lista di prodotti della persona. Nello specifico verifichiamo il ruolo per cui
        // la persona è conosciuta e in base a quello prendiamo i prodotti corrispondenti. Quindi se la persona
        // è un attore, prendiamo solo i film in cui ha recitato, se è un regista prendiamo solo i film che ha
        // diretto, altrimenti prendiamo sia i film in cui ha recitato che quelli che ha diretto. Dopo aver ottenuto questa
        // lista, la ordiniamo per popolarità e prendiamo i primi 30 elementi in modo da non sovraffollare la pagina.
        val products = when (person.knownFor) {
            "Acting" -> person.combinedCredits.cast
            "Directing" -> person.combinedCredits.crew
            else -> person.combinedCredits.cast + person.combinedCredits.crew
        }.distinct().sortedBy { it.popularity }.reversed().take(30)
        // Qui aggiorniamo i dati della persona con la lista di prodotti che abbiamo ottenuto
        person.products = products
        // Qui, dopo aver utilizzato i combinedCredits per creare la lista di prodotti che ci interessa
        // puliamo i combinedCredits in modo da non occupare memoria inutilmente
        person.combinedCredits = Person.CombinedCredits(emptyList(), emptyList())
        if (personHasMissingDetails(person)) {
            val personInEnglish = tmdbApi.getPersonDetails(personId = personId, language = "en-US")
            fillMissingDetails(person, personInEnglish)
        }
        return person
    }

    // Questo metodo si occupa di verificare se una persona ha dei dettagli mancanti in italiano
    private fun personHasMissingDetails(person: Person): Boolean {
        return person.biography.isEmpty()
    }

    // Questo metodo si occupa di riempire i dettagli mancanti di una persona in italiano con quelli in inglese
    private fun fillMissingDetails(person: Person, personInEnglish: Person) {
        if (person.biography.isEmpty()) {
            person.biography = personInEnglish.biography
        }
    }

    // Questo metodo si aggancia all'oggetto FirestoreService per verificare se una persona è seguita da un utente
    suspend fun isPersonFollowed(userId: String, personId: Long): Boolean {
        val peopleFollowed: List<Long> = FirestoreService.getPeopleFollowed(userId).first()
        return personId in peopleFollowed
    }

    // Questo metodo si aggancia all'oggetto FirestoreService per seguire una persona
    fun followPerson(userId: String, personId: Long) {
        FirestoreService.followPerson(userId, personId)
    }

    // Questo metodo si aggancia all'oggetto FirestoreService per smettere di seguire una persona
    fun unfollowPerson(userId: String, personId: Long) {
        FirestoreService.unfollowPerson(userId, personId)
    }

    // Questo metodo si aggancia all'oggetto FirestoreService per ottenere la lista di persone seguite da un utente
    suspend fun getFollowedPeople(userId: String): List<Person> {
        val followdPeopleIds = FirestoreService.getPeopleFollowed(userId).first()
        val followedPeople = mutableListOf<Person>()
        for (personId in followdPeopleIds) {
            val person = getPersonDetails(personId)
            followedPeople.add(person)
        }
        return followedPeople
    }
}