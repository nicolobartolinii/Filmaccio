package it.univpm.filmaccio.main.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import it.univpm.filmaccio.data.repository.MovieRepository
import kotlinx.coroutines.Dispatchers

class HomeViewModel : ViewModel() {
    private val movieRepository = MovieRepository()

    val nowPlayingMovies = liveData(Dispatchers.IO) {
        val movies = movieRepository.getNowPlayingMovies()
        emit(movies)
    }
}