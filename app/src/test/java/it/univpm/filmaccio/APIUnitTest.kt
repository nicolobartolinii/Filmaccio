package it.univpm.filmaccio

import io.mockk.coEvery
import io.mockk.mockk
import it.univpm.filmaccio.data.models.SearchResponse
import it.univpm.filmaccio.data.models.TmdbEntity
import it.univpm.filmaccio.data.repository.SearchRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Questa classe contiene i test unitari per le API.
 *
 * @author nicolobartolinii
 */
class APIUnitTest {

    @Test
    // Testa che la funzione di ricerca ritorni i film corretti.
    // runBlocking è necessario perché all'interno del test viene eseguita una coroutine.
    fun searchMovies_returnsCorrectMovies() = runBlocking {
        // Creiamo un oggetto "falso" di tipo SearchRepository che simula il comportamento di un vero SearchRepository.
        val mockSearchRepository = mockk<SearchRepository>()
        // coEvery è una funzione di Mockk che permette di simulare il comportamento di una funzione asincrona.
        // In questo caso diciamo che quando la funzione searchMulti viene chiamata con il parametro "Guardiani della galassia vol.3"
        // deve ritornare una SearchResponse contenente un solo film.
        coEvery { mockSearchRepository.searchMulti("Guardiani della galassia vol.3") } returns SearchResponse(
            listOf(
                TmdbEntity(
                    447365,
                    "Guardiani della Galassia Vol.3",
                    "/nrKFSB9Xfe3HSEbpNyIOtGYCycj.jpg",
                    "movie"
                ),
            ), 1, 1, 1
        )

        // Qui stiamo effettivamente chiamando la funzione searchMulti del mockSearchRepository e salvando il risultato in una variabile.
        val result = mockSearchRepository.searchMulti("Guardiani della galassia vol.3")

        // Qui stiamo verificando che il risultato della funzione sia quello che ci aspettiamo. Se non lo fosse il test fallirebbe.
        assertEquals(
            SearchResponse(
                listOf(
                    TmdbEntity(
                        447365,
                        "Guardiani della Galassia Vol.3",
                        "/nrKFSB9Xfe3HSEbpNyIOtGYCycj.jpg",
                        "movie"
                    ),
                ), 1, 1, 1
            ), result
        )
    }
}