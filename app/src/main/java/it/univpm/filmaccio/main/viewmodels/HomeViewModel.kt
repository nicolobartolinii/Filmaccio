package it.univpm.filmaccio.main.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import it.univpm.filmaccio.data.models.Movie
import it.univpm.filmaccio.data.models.Series
import it.univpm.filmaccio.data.repository.MovieRepository
import it.univpm.filmaccio.data.repository.SeriesRepository
import it.univpm.filmaccio.main.utils.FirestoreService
import kotlinx.coroutines.Dispatchers

// Questa classe è il ViewModel della schermata home dell'applicazione, quindi si occupa di gestire
// i dati relativi ai film che vengono mostrati nella schermata home.
class HomeViewModel : ViewModel() {
    // Qui creiamo un oggetto che contiene il repository per i film
    private val movieRepository = MovieRepository()

    // Qui creiamo un oggetto che contiene il repository per le serie tv
    private val seriesRepository = SeriesRepository()

    // Qui creiamo un oggetto LiveData che contiene i film popolari. Questo oggetto LiveData viene
    // creato utilizzando un CoroutineScope che esegue la chiamata al repository in un thread separato
    // e poi emette i dati ottenuti. Così, osservando questa variabile nella schermata home, possiamo
    // eseguire del codice ogni volta che questa variabile cambia.
    val nowPlayingMovies = liveData(Dispatchers.IO) {
        val movies = movieRepository.getNowPlayingMovies()
        emit(movies)
    }

    val topRatedMovies = liveData(Dispatchers.IO) {
        var movies = FirestoreService.getAllRatings("movies")
        movies = movies.sortedBy { -it.second }
        val moviesList = mutableListOf<Movie>()
        for (movie in movies) {
            moviesList.add(movieRepository.getMovieDetails(movie.first))
        }
        emit(moviesList)
    }

    val topRatedSeries = liveData(Dispatchers.IO) {
        var series = FirestoreService.getAllRatings("series")
        series = series.sortedBy { -it.second }
        val seriesList = mutableListOf<Series>()
        for (serie in series) {
            seriesList.add(seriesRepository.getSeriesDetails(serie.first))
        }
        emit(seriesList)
    }
}