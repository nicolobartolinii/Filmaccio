package it.univpm.filmaccio.main.viewmodels

import android.util.Log
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

class SearchViewModel : ViewModel() {
    private val movieRepository = MovieRepository()
    private val seriesRepository = SeriesRepository()
    private val searchRepository = SearchRepository()
    var searchResults = MutableLiveData<List<Any>>()

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

    fun search(query: String) = viewModelScope.launch(Dispatchers.IO) {
        Log.e("Search", "Searching for $query in TMDB and Firestore")

        val multiTmdbSearch = async { searchRepository.searchMulti(query) }
        val usersSearch = FirestoreService.searchUsers(query).toList()[0]

        val combinedSearchResults = multiTmdbSearch.await().entities + usersSearch

        withContext(Dispatchers.Main) {
            Log.e("Search", "Search results: $combinedSearchResults")
            searchResults.value = combinedSearchResults
        }
    }
}