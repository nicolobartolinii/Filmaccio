package it.univpm.filmaccio

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import it.univpm.filmaccio.data.models.User
import it.univpm.filmaccio.data.repository.SeriesRepository
import it.univpm.filmaccio.data.repository.UsersRepository
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Test

class FirestoreUnitTests {

    @Test
    fun checkIfSeriesIsRated_returnsCorrectValue() = runBlocking {
        // Creiamo un oggetto "falso" di tipo SeriesRepository che simula il comportamento di un vero SeriesRepository.
        val mockSeriesRepository = mockk<SeriesRepository>()
        // Definiamo il valore che ci aspettiamo venga ritornato dalla funzione isSeriesRated
        val expectedValue = true
        // Definiamo il comportamento della funzione isSeriesRated. Quando viene chiamata con i parametri specificati,
        // deve ritornare il valore che abbiamo definito prima.
        coEvery {
            mockSeriesRepository.isSeriesRated(
                "udXDfvyjAJQV7I6KfrhirHoHdZX2", 1396
            )
        } returns expectedValue

        // Qui stiamo effettivamente chiamando la funzione isSeriesRated del mockSeriesRepository e salvando il risultato in una variabile.
        val result = mockSeriesRepository.isSeriesRated("udXDfvyjAJQV7I6KfrhirHoHdZX2", 1396)

        // Qui stiamo verificando che il risultato della funzione sia quello che ci aspettiamo. Se non lo fosse il test fallirebbe.
        assertTrue(result)
    }

    @Test
    fun updateUserProfile_updatesCorrectly() = runBlocking {
        // Creiamo un oggetto "falso" di tipo UsersRepository.
        val mockUserRepository = mockk<UsersRepository>()
        // Definiamo i parametri che passeremo alla funzione updateUserField.
        val field = "nameShown"
        val value = "new name shown"

        // Definiamo il comportamento della funzione updateUserField. In questo caso non stiamo specificando un valore di ritorno,
        // quindi dovrebbe ritornare Unit.
        every {
            mockUserRepository.updateUserField(
                "udXDfvyjAJQV7I6KfrhirHoHdZX2", field, value
            ) {}
        }

        // Qui stiamo definendo il comportamento della funzione getUserDetails. Quando viene chiamata con un certo parametro,
        // deve ritornare un oggetto User con il campo nameShown uguale al valore che abbiamo definito sopra.
        // Non ci interessano gli altri campi, quindi non li definiamo.
        coEvery { mockUserRepository.getUserDetails("udXDfvyjAJQV7I6KfrhirHoHdZX2") } returns User(
            nameShown = value
        )

        // Chiamiamo effettivamente la funzione getUserDetails sul nostro mock e salviamo il risultato.
        val result = mockUserRepository.getUserDetails("udXDfvyjAJQV7I6KfrhirHoHdZX2")

        // Infine, controlliamo che il risultato sia quello che ci aspettiamo.
        assertEquals(User(nameShown = value).nameShown, result.nameShown)
    }
}