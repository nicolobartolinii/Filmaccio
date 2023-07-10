package it.univpm.filmaccio.main.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import it.univpm.filmaccio.data.models.Movie
import it.univpm.filmaccio.data.models.Series
import it.univpm.filmaccio.data.repository.MovieRepository
import it.univpm.filmaccio.data.repository.SeriesRepository
import it.univpm.filmaccio.main.utils.FirestoreService
import kotlinx.coroutines.Dispatchers

/**
 * Questa classe è il ViewModel della schermata home dell'applicazione, quindi si occupa di gestire
 * i dati relativi ai film e alle serie TV che vengono mostrati nella schermata home.
 *
 * @author nicolobartolinii
 */
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

    // Qui otteniamo i film con una media di valutazione più alta. Per fare questo, otteniamo tutti i
    // film con le rispettive valutazioni dal database e poi ordiniamo la lista in base alla media valutazione.
    val topRatedMovies = liveData(Dispatchers.IO) {
        var movies = FirestoreService.getAllRatings("movies")
        movies = movies.sortedBy { -it.second }
        val moviesList = mutableListOf<Movie>()
        for (movie in movies) {
            moviesList.add(movieRepository.getMovieDetails(movie.first))
        }
        emit(moviesList)
    }

    // Analogo a quanto fatto per i film, otteniamo le serie tv con una media di valutazione più alta.
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