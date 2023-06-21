package it.univpm.filmaccio.main.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import it.univpm.filmaccio.data.repository.MovieRepository
import kotlinx.coroutines.Dispatchers

// Questa classe è il ViewModel della schermata home dell'applicazione, quindi si occupa di gestire
// i dati relativi ai film che vengono mostrati nella schermata home.
class HomeViewModel : ViewModel() {
    // Qui creiamo un oggetto che contiene il repository per i film
    private val movieRepository = MovieRepository()

    // Qui creiamo un oggetto LiveData che contiene i film popolari. Questo oggetto LiveData viene
    // creato utilizzando un CoroutineScope che esegue la chiamata al repository in un thread separato
    // e poi emette i dati ottenuti. Così, osservando questa variabile nella schermata home, possiamo
    // eseguire del codice ogni volta che questa variabile cambia.
    val nowPlayingMovies = liveData(Dispatchers.IO) {
        val movies = movieRepository.getNowPlayingMovies()
        emit(movies)
    }
}