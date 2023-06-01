package it.univpm.filmaccio.main.viewmodels

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import it.univpm.filmaccio.data.models.User
import it.univpm.filmaccio.data.repository.MovieRepository
import it.univpm.filmaccio.data.repository.SearchRepository
import it.univpm.filmaccio.data.repository.SeriesRepository
import it.univpm.filmaccio.main.utils.FirestoreService
import kotlinx.coroutines.Dispatchers

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

    suspend fun search(query: String) {
        Log.e("Search", "Searching for $query")
        val multiTmdbSearchLiveData = liveData(Dispatchers.Default) {
            try {
                Log.e("Search", "Searching for $query in TMDB")
                val response = searchRepository.searchMulti(query)
                emit(response.entities)
            } catch (e: Exception) {
                Log.e("Search", "Error searching for $query in TMDB: ${e.message}")
            }
        }

        Log.e("Search", "Searching for $query a metà")

        val usersLiveData = liveData(Dispatchers.Default) {
            Log.e("Search", "Searching for $query in Firestore")
            val response = FirestoreService.searchUsers(query)
            emit(response)
        }

        searchResults = multiTmdbSearchLiveData.switchMap { multiTmdbSearch ->
            usersLiveData.map { users ->
                multiTmdbSearch + users
            }
        } as MutableLiveData<List<Any>>
    }



}