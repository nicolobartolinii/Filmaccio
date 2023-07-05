package it.univpm.filmaccio.main.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import it.univpm.filmaccio.data.repository.MovieRepository
import it.univpm.filmaccio.data.repository.SearchRepository
import it.univpm.filmaccio.data.repository.SeriesRepository
import it.univpm.filmaccio.main.utils.FirestoreService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Questa classe è il ViewModel della schermata di ricerca dell'applicazione, quindi si occupa di gestire
// i dati relativi ai film, serie TV, persone e utenti che vengono mostrati nella schermata di ricerca.
class SearchViewModel : ViewModel() {
    // Qui otteniamo i vari repository che ci servono
    private val movieRepository = MovieRepository()
    private val seriesRepository = SeriesRepository()
    private val searchRepository = SearchRepository()

    // Qui creiamo un oggetto LiveData che contiene i risultati della ricerca.
    var searchResults = MutableLiveData<List<Any>>()

    // Qui creiamo i vari oggetti LiveData che contengono la lista dei dati che ci servono per la schermata
    // di esplorazione
    val trendingMovies = liveData(Dispatchers.IO) {
        val movies = movieRepository.getTrendingMovies()
        emit(movies)
    }

    val trendingSeries = liveData(Dispatchers.IO) {
        val series = seriesRepository.getTrendingSeries()
        emit(series)
    }

    val topRatedMovies = liveData(Dispatchers.IO) {
        val movies = movieRepository.getTopRatedMovies()
        emit(movies)
    }

    val topRatedSeries = liveData(Dispatchers.IO) {
        val series = seriesRepository.getTopRatedSeries()
        emit(series)
    }

    // Qui abbiamo la funzione search che viene chiamata quando il testo nella barra di ricerca cambia.
    // Il funzionamento della funzionalità di ricerca intreccia due modi diversi di gestire
    // la programmazione asincrona: le coroutine e i flussi. Quindi questo codice può essere un po'
    // difficile da capire. Lo è anche per me, ci ho messo molto a trovare il modo di far funzionare questa
    // funzionalità, ma intrecciare coroutine e flussi è l'unico modo che ho trovato per farlo.
    fun search(query: String) = viewModelScope.launch(Dispatchers.IO) {

        // Qui creiamo un oggetto che contiene la chiamata al repository per la ricerca di film, serie TV e persone (entità TMDB)
        val multiTmdbSearch = async { searchRepository.searchMulti(query) }
        // Qui invece creiamo un oggetto che contiene la chiamata al repository per la ricerca di utenti (tramite Firestore, perché
        // i dati degli utenti non sono su TMDB ma su Firebase)
        val usersSearch = if (query.isNotEmpty()) {
            FirestoreService.searchUsers(query)
                .toList()[0] // Questo indice 0 è necessario perché la funzione toList() restituisce un flusso di liste, e noi vogliamo solo la prima (e unica) lista
        } else {
            listOf()
        }

        // Qui combiniamo i risultati delle due chiamate in un'unica lista
        val combinedSearchResults = multiTmdbSearch.await().entities + usersSearch

        // Qui aggiorniamo la variabile searchResults con la lista combinata
        withContext(Dispatchers.Main) {
            searchResults.value = combinedSearchResults
        }
    }
}